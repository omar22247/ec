package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.CreateOrderRequest;
import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateOrderStatusRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.*;
import E_commerce.com.SecureEcommerceApplication.entity.*;
import E_commerce.com.SecureEcommerceApplication.entity.enums.OrderStatus;
import E_commerce.com.SecureEcommerceApplication.entity.enums.ShipmentStatus;
import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository     orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository      cartRepository;
    private final CartItemRepository  cartItemRepository;
    private final AddressRepository   addressRepository;
    private final CouponRepository    couponRepository;
    private final InventoryRepository inventoryRepository;
    private final ShipmentRepository  shipmentRepository;
    private final EmailService  emailService;
    private final UserRepository  userRepository;


    // ════════════════════════════════════════════════════════
    //  CREATE ORDER
    //
    //  Q1  → findWithItemsByUserId    (cart + items + products + inventories)
    //  Q2  → findByIdAndUserId        (address)
    //  Q3  → findByCode               (coupon — optional)
    //  Q4  → decreaseStock × N        (unavoidable — one UPDATE per item)
    //  Q5  → save Order
    //  Q6  → saveAll OrderItems
    //  Q7  → save Shipment
    //       coupon.usedCount++
    //  Q8  → deleteAllByCartId
    //
    //  NO Q9 — response built from memory (all data already loaded)
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {

        // Q1 — cart + items + products + inventories in ONE JOIN
        Cart cart = cartRepository.findWithItemsByUserId(userId)
                .orElseThrow(() -> new BusinessException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot place order with an empty cart");
        }

        // Q2 — validate address belongs to user
        Address address = addressRepository.findByIdAndUserId(
                        request.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address", "id", request.getAddressId()));

        // Q3 — coupon (optional)
        Coupon coupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Coupon", "code", request.getCouponCode()));
            if (!coupon.isValid()) {
                throw new BusinessException("Coupon is not valid");
            }
        }

        // ── all calculations in memory ────────────────────
        BigDecimal originalPrice = cart.getItems().stream()
                .map(item -> item.getProduct().getBasePrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                        .setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (coupon != null) {
            if (originalPrice.compareTo(coupon.getMinOrderAmount()) < 0) {
                throw new BusinessException(
                        "Order total must be at least " + coupon.getMinOrderAmount() +
                                " to use this coupon");
            }
            discountAmount = coupon.calculateDiscount(originalPrice);
        }

        BigDecimal totalPrice = originalPrice.subtract(discountAmount)
                .setScale(2, RoundingMode.HALF_UP);

        // ── validate stock in memory — inventory from Q1 ──
        for (CartItem item : cart.getItems()) {
            item.getProduct().getInventory().validateStock(item.getQuantity());
        }

        // Q4 — decrease stock (direct UPDATE per item)
        // returns rows updated — 0 means race condition (stock taken by another request)
        for (CartItem item : cart.getItems()) {
            int updated = inventoryRepository.decreaseStock(
                    item.getProduct().getId(), item.getQuantity());
            if (updated == 0) {
                throw new BusinessException(
                        "'" + item.getProduct().getName() +
                                "' is no longer available in the requested quantity.");
            }
        }

        // Q5 — save Order
        Order order = Order.builder()
                .user(User.builder().id(userId).build())
                .address(address)
                .coupon(coupon)
                .status(OrderStatus.PENDING)
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .totalPrice(totalPrice)
                .build();
        order = orderRepository.save(order);

        // Q6 — save OrderItems
        // ArrayList (mutable) — never .toList() (immutable → Hibernate crash)
        // saveAll directly — never order.setItems() → UnsupportedOperationException
        List<OrderItem> savedItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            savedItems.add(OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(cartItem.getProduct().getBasePrice())
                    .build());
        }
        orderItemRepository.saveAll(savedItems);

        // Q7 — save Shipment
        Shipment shipment = shipmentRepository.save(Shipment.builder()
                .order(order)
                .status(ShipmentStatus.PREPARING).build());

        // increment coupon usage
        if (coupon != null) {
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }


        // Q8 — clear cart
        cartItemRepository.deleteAllByCartId(cart.getId());
        log.info("Order created: id={}, userId={}, total={}", order.getId(), userId, totalPrice);
        OrderResponse response = buildResponse(order, address, coupon, savedItems, shipment);
        User user = userRepository.findById(userId).orElseThrow();
        emailService.sendOrderConfirmationEmail(user.getEmail(),user.getName(),response);

        return response;
    }

    // ════════════════════════════════════════════════════════
    //  USER — LIST + DETAIL
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> getMyOrders(Long userId, int page, int size) {
        return PageResponse.of(
                orderRepository.findSummariesByUserId(userId, PageRequest.of(page, size))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long userId, Long orderId) {
        return toResponse(
                orderRepository.findDetailByIdAndUserId(orderId, userId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Order", "id", orderId))
        );
    }

    // ════════════════════════════════════════════════════════
    //  ADMIN — LIST + STATUS UPDATE
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> getAllOrders(int page, int size) {
        return PageResponse.of(
                orderRepository.findAllSummaries(PageRequest.of(page, size))
        );
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {

        Order order = orderRepository.findForStatusUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());

        switch (request.getStatus()) {
            case SHIPPED -> {
                order.getShipment().setStatus(ShipmentStatus.SHIPPED);
                order.getShipment().setShippedAt(LocalDateTime.now());
            }
            case DELIVERED ->
                    order.getShipment().setStatus(ShipmentStatus.DELIVERED);
            case CANCELLED ->
                // restore stock — direct UPDATE per item
                    order.getItems().forEach(item ->
                            inventoryRepository.increaseStock(
                                    item.getProduct().getId(), item.getQuantity())
                    );
            default -> { /* PENDING, PAID — no extra action */ }
        }

        orderRepository.save(order);
        log.info("Order status updated: id={}, status={}", orderId, request.getStatus());

        // fetch full detail for response (needs address + coupon not loaded by findForStatusUpdate)
        return toResponse(orderRepository.findDetailById(orderId).orElseThrow());
    }

    // ════════════════════════════════════════════════════════
    //  Private helpers
    // ════════════════════════════════════════════════════════

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING   -> next == OrderStatus.PAID   || next == OrderStatus.CANCELLED;
            case PAID      -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED   -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
        if (!valid) {
            throw new BusinessException(
                    "Invalid status transition: " + current + " → " + next);
        }
    }

    // used when data is already in memory (createOrder)
    // avoids the extra DB fetch (Q9)
    private OrderResponse buildResponse(Order order, Address address,
                                        Coupon coupon, List<OrderItem> items,
                                        Shipment shipment) {
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .address(toAddressResponse(address))
                .couponCode(coupon != null ? coupon.getCode() : null)
                .originalPrice(order.getOriginalPrice())
                .discountAmount(order.getDiscountAmount())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .items(items.stream().map(this::toItemResponse).toList())
                .shipment(toShipmentResponse(shipment))
                .build();
    }

    // used when loading from DB (getOrderById, updateOrderStatus)
    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .address(toAddressResponse(order.getAddress()))
                .couponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .originalPrice(order.getOriginalPrice())
                .discountAmount(order.getDiscountAmount())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .items(order.getItems() != null
                        ? order.getItems().stream().map(this::toItemResponse).toList()
                        : List.of())
                .shipment(toShipmentResponse(order.getShipment()))
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        // product can be null if soft-deleted — handle gracefully
        String name  = item.getProduct() != null ? item.getProduct().getName()     : "Deleted Product";
        String image = item.getProduct() != null ? item.getProduct().getImageUrl() : null;
        Long   pid   = item.getProduct() != null ? item.getProduct().getId()       : null;

        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(pid)
                .productName(name)
                .productImage(image)
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .subtotal(item.getPriceAtPurchase()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                        .setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    private AddressResponse toAddressResponse(Address a) {
        if (a == null) return null;
        return AddressResponse.builder()
                .id(a.getId())
                .fullName(a.getFullName())
                .phone(a.getPhone())
                .street(a.getStreet())
                .city(a.getCity())
                .country(a.getCountry())
                .zipCode(a.getZipCode())
                .isDefault(a.isDefault())
                .build();
    }

    private ShipmentResponse toShipmentResponse(Shipment s) {
        if (s == null) return null;
        return ShipmentResponse.builder()
                .id(s.getId())
                .status(s.getStatus())
                .carrier(s.getCarrier())
                .trackingNumber(s.getTrackingNumber())
                .shippedAt(s.getShippedAt())
                .estimatedDelivery(s.getEstimatedDelivery())
                .build();
    }
}