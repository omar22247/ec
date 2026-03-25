package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.CreateOrderRequest;
import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateOrderStatusRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.OrderResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.OrderSummaryResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.PageResponse;

public interface OrderService {

    // ── User ────────────────────────────────────────────────

    // POST /api/v1/orders
    OrderResponse createOrder(Long userId, CreateOrderRequest request);

    // GET /api/v1/orders  → summary list
    PageResponse<OrderSummaryResponse> getMyOrders(Long userId, int page, int size);

    // GET /api/v1/orders/{id}  → full detail
    OrderResponse getOrderById(Long userId, Long orderId);

    // ── Admin ───────────────────────────────────────────────

    // GET /api/v1/admin/orders  → summary list
    PageResponse<OrderSummaryResponse> getAllOrders(int page, int size);

    // PATCH /api/v1/admin/orders/{id}/status
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
}