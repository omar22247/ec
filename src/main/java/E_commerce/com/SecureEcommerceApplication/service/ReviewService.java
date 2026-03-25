package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.ReviewRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.PageResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ReviewResponse;

public interface ReviewService {

    // GET /api/v1/products/{productId}/reviews  — public
    PageResponse<ReviewResponse> getProductReviews(Long productId, int page, int size);

    // POST /api/v1/products/{productId}/reviews — authenticated
    ReviewResponse createReview(Long userId, Long productId, ReviewRequest request);


    // DELETE /api/v1/products/{productId}/reviews/{reviewId} — owner or admin
    void deleteReview(Long userId, Long reviewId);
}