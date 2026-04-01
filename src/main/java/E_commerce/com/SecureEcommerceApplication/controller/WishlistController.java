package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.WishlistItemResponse;
import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
import E_commerce.com.SecureEcommerceApplication.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "APIs for managing the user's saved wishlist items")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {

    private final WishlistService wishlistService;

    // ── GET wishlist ───────────────────────────────────────────
    @GetMapping
    @Operation(summary = "Get wishlist",
            description = "Retrieves all products in the authenticated user's wishlist")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Wishlist retrieved successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<WishlistItemResponse>>> getWishlist(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        wishlistService.getWishlist(userId(userDetails)))
        );
    }

    // ── POST add to wishlist ───────────────────────────────────
    @PostMapping("/{productId}")
    @Operation(summary = "Add to wishlist",
            description = "Adds a specific product to the user's wishlist")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
            description = "Product added to wishlist")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<WishlistItemResponse>> addToWishlist(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the product to add") @PathVariable Long productId) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Added to wishlist",
                        wishlistService.addToWishlist(userId(userDetails), productId)));
    }

    // ── DELETE remove one item ─────────────────────────────────
    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove from wishlist",
            description = "Removes a specific product from the user's wishlist")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Product removed from wishlist")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the product to remove") @PathVariable Long productId) {

        wishlistService.removeFromWishlist(userId(userDetails), productId);
        return ResponseEntity.ok(
                ApiResponse.success("Removed from wishlist")
        );
    }

    // ── DELETE clear all ───────────────────────────────────────
    @DeleteMapping
    @Operation(summary = "Clear wishlist",
            description = "Removes all products from the user's wishlist")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Wishlist cleared successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> clearWishlist(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails) {

        wishlistService.clearWishlist(userId(userDetails));
        return ResponseEntity.ok(
                ApiResponse.success("Wishlist cleared")
        );
    }

    private Long userId(AppUserDetails u) {
        return u.getUser().getId();
    }
}