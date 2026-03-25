package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.request.CreateOrderRequest;
import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateOrderStatusRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.OrderResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.OrderSummaryResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.PageResponse;
import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
import E_commerce.com.SecureEcommerceApplication.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Orders", description = "APIs for order processing and management")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    // ── User ─────────────────────────────────────────────────

    @PostMapping("/api/v1/orders")
    @Operation(summary = "Create an order", description = "Places a new order for the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order placed successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully",
                        orderService.createOrder(userDetails.getUser().getId(), request)));
    }

    @GetMapping("/api/v1/orders")
    @Operation(summary = "Get my orders", description = "Retrieves a paginated list of order summaries for the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponse>>> getMyOrders(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        orderService.getMyOrders(userDetails.getUser().getId(), page, size))
        );
    }

    @GetMapping("/api/v1/orders/{id}")
    @Operation(summary = "Get order details", description = "Retrieves the full details of a specific order for the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order details retrieved")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the order") @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        orderService.getOrderById(userDetails.getUser().getId(), id))
        );
    }

    // ── Admin ─────────────────────────────────────────────────

    @GetMapping("/api/v1/admin/orders")
    @Operation(summary = "Get all orders", description = "Retrieves a paginated list of all orders in the system. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All orders retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponse>>> getAllOrders(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.getAllOrders(page, size))
        );
    }

    @PatchMapping("/api/v1/admin/orders/{id}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an existing order. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "ID of the order to update") @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Order status updated",
                        orderService.updateOrderStatus(id, request))
        );
    }

    private Long userId(AppUserDetails u) {
        return u.getUser().getId();
    }
}