package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.CartItemRequest;
import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateCartItemRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.CartItemResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.CartResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Cart;
import E_commerce.com.SecureEcommerceApplication.entity.CartItem;
import E_commerce.com.SecureEcommerceApplication.entity.Product;
import E_commerce.com.SecureEcommerceApplication.entity.User;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.exception.UnauthorizedException;
import E_commerce.com.SecureEcommerceApplication.repository.CartItemRepository;
import E_commerce.com.SecureEcommerceApplication.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository     cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;


    // ════════════════════════════════════════════════════════
    //  GET CART
    //  Q1 → findByUserId
    //  Q2 → findCartItems (DTO projection)
    // ════════════════════════════════════════════════════════

    @Override
//    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return buildCartResponse(cart.getId());
    }



    @Override
    @Transactional
    public CartResponse addItem(Long userId, CartItemRequest request) {

        // Q1 — cart + ALL items + products + inventories in ONE JOIN
        Cart cart = getOrCreateCartWithItems(userId);

        // check in memory — no query
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            // ── Case A: product already in cart ─────────────
            // inventory already loaded from Q1 — no extra query
            CartItem item = existing.get();

            // validateStock in memory
            item.getProduct().getInventory()
                    .validateStock(item.getQuantity() + request.getQuantity());

            // Q2 — direct UPDATE
            cartItemRepository.updateQuantity(
                    item.getId(), item.getQuantity() + request.getQuantity());
            log.info("Qty updated: itemId={}, newQty={}",
                    item.getId(), item.getQuantity() + request.getQuantity());

        } else {
            // ── Case B: product NOT in cart ──────────────────
            // ONE query — product + inventory via @EntityGraph
            Product product = productService
                    .findWithInventoryByIdAndActiveTrue(request.getProductId());

            // all checks in memory — inventory already loaded
            product.getInventory().validateStock(request.getQuantity());

            // INSERT — getReferenceById = proxy, no SELECT
            cartItemRepository.save(CartItem.builder()
                    .cart(cartRepository.getReferenceById(cart.getId()))
                    .product(product)
                    .quantity(request.getQuantity())
                    .build());
            log.info("Item added: cartId={}, productId={}",
                    cart.getId(), request.getProductId());
        }

        return buildCartResponse(cart.getId());
    }

    // ════════════════════════════════════════════════════════
    //  UPDATE ITEM
    //  Q1 → findWithItemsByUserId   (cart + items + products + inventories)
    //  Q2 → updateQuantity          (direct UPDATE)
    //  Q3 → findCartItems           (response)
    //
    //  ownership + stock — all in memory from Q1
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public CartResponse updateItem(Long userId, Long itemId,
                                   UpdateCartItemRequest request) {

        // Q1 — everything loaded
        Cart cart = getCartWithItemsOrThrow(userId);

        // find item + verify ownership — in memory
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException(
                        "Cart item does not belong to your cart"));

        // stock check — inventory from Q1 — in memory
        item.getProduct().getInventory().validateStock(request.getQuantity());

        // Q2 — direct UPDATE
        cartItemRepository.updateQuantity(itemId, request.getQuantity());
        log.info("Item updated: itemId={}, qty={}", itemId, request.getQuantity());

        return buildCartResponse(cart.getId()); // Q3
    }

    // ════════════════════════════════════════════════════════
    //  REMOVE ITEM
    //  Q1 → findWithItemsByUserId   (cart + items)
    //  Q2 → deleteByItemId          (direct DELETE)
    //  Q3 → findCartItems           (response)
    //
    //  ownership — in memory from Q1
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {

        Cart cart = getCartWithItemsOrThrow(userId); // Q1

        boolean belongs = cart.getItems().stream()
                .anyMatch(i -> i.getId().equals(itemId));

        if (!belongs) {
            throw new UnauthorizedException("Cart item does not belong to your cart");
        }

        cartItemRepository.deleteByItemId(itemId);  // Q2
        log.info("Item removed: itemId={}, cartId={}", itemId, cart.getId());

        return buildCartResponse(cart.getId());      // Q3
    }

    // ════════════════════════════════════════════════════════
    //  CLEAR CART
    //  Q1 → findByUserId
    //  Q2 → deleteAllByCartId       (direct DELETE)
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartOrThrow(userId);
        cartItemRepository.deleteAllByCartId(cart.getId());
        log.info("Cart cleared: cartId={}", cart.getId());
    }

    // ════════════════════════════════════════════════════════
    //  Private helpers
    // ════════════════════════════════════════════════════════

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    private Cart getOrCreateCartWithItems(Long userId) {
        return cartRepository.findWithItemsByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    private Cart getCartOrThrow(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart", "userId", userId));
    }

    private Cart getCartWithItemsOrThrow(Long userId) {
        return cartRepository.findWithItemsByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart", "userId", userId));
    }

    private Cart createCart(Long userId) {
        log.info("Creating cart for userId={}", userId);
        return cartRepository.save(
                Cart.builder()
                        .user(User.builder().id(userId).build())
                        .build()
        );
    }

    private CartResponse buildCartResponse(Long cartId) {
        List<CartItemResponse> items = cartRepository.findCartItems(cartId);

        BigDecimal totalPrice = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cartId)
                .items(items)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .build();
    }
}