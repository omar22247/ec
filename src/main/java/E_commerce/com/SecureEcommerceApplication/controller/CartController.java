package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.request.CartItemRequest;
import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateCartItemRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.CartResponse;
import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
import E_commerce.com.SecureEcommerceApplication.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "APIs for managing the shopping cart")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart", description = "Retrieves the shopping cart for the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart retrieved successfully")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success(cartService.getCart(userDetails.getUser().getId()))
        );
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a new product to the cart or increases its quantity if it already exists")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item added to cart")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Item added to cart",
                        cartService.addItem(userDetails.getUser().getId(), request))
        );
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item", description = "Updates the quantity of a specific item in the cart")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart updated successfully")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the cart item") @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cart updated",
                        cartService.updateItem(userDetails.getUser().getId(), itemId, request))
        );
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the shopping cart")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item removed successfully")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the cart item to remove") @PathVariable Long itemId) {
        return ResponseEntity.ok(
                ApiResponse.success("Item removed",
                        cartService.removeItem(userDetails.getUser().getId(), itemId))
        );
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Removes all items from the current user's shopping cart")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart cleared successfully")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails) {
        cartService.clearCart(userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared"));
    }

    private Long userId(AppUserDetails u) {
        return u.getUser().getId();
    }
}