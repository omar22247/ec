package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.request.ReviewRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.PageResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ReviewResponse;
import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
import E_commerce.com.SecureEcommerceApplication.service.ReviewService;
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
@RequestMapping("/api/v1/products/{productId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "APIs for product reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    // ── GET reviews ────────────────────────────────────────────
    @GetMapping
    @Operation(summary = "Get product reviews",
            description = "Retrieves a paginated list of reviews for a specific product")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Reviews retrieved successfully")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProductReviews(
            @Parameter(description = "ID of the product") @PathVariable Long productId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Items per page")        @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reviewService.getProductReviews(productId, page, size))
        );
    }

    // ── POST create ────────────────────────────────────────────
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Submit review",
            description = "Submits a new review for a product. Requires authentication.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
            description = "Review submitted successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the product to review") @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully",
                        reviewService.createReview(userId(userDetails), productId, request)));
    }

    // ── DELETE review ──────────────────────────────────────────
    @DeleteMapping("/{reviewId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete review",
            description = "Deletes a review made by the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Review deleted successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the product")        @PathVariable Long productId,
            @Parameter(description = "ID of the review to delete") @PathVariable Long reviewId) {

        reviewService.deleteReview(userId(userDetails), reviewId);
        return ResponseEntity.ok(
                ApiResponse.success("Review deleted successfully")
        );
    }

    private Long userId(AppUserDetails u) {
        return u.getUser().getId();
    }
}