package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.ReviewRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.PageResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ReviewResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Product;
import E_commerce.com.SecureEcommerceApplication.entity.Review;
import E_commerce.com.SecureEcommerceApplication.entity.User;
import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.exception.UnauthorizedException;
import E_commerce.com.SecureEcommerceApplication.repository.ProductRepository;
import E_commerce.com.SecureEcommerceApplication.repository.ReviewRepository;
import E_commerce.com.SecureEcommerceApplication.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository  reviewRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getProductReviews(Long productId, int page, int size) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return PageResponse.of(
                reviewRepository.findByProductId(
                        productId,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
        );
    }

    @Override
    @Transactional
    public ReviewResponse createReview(Long userId, Long productId, ReviewRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!reviewRepository.hasUserPurchasedProduct(userId, productId)) {
            throw new BusinessException(
                    "You can only review products you have purchased and received");
        }

        if (reviewRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new BusinessException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .user(User.builder().id(userId).build())
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);

        // update product average rating
        product.addReview(request.getRating());
        productRepository.save(product);

        log.info("Review created: id={}, userId={}, productId={}, rating={}",
                review.getId(), userId, productId, request.getRating());

        return toResponse(review);
    }
    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {

        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new UnauthorizedException(
                        "Review not found or does not belong to you"));

        Product product = review.getProduct();
        int rating = review.getRating();
        reviewRepository.delete(review);

        // update product average rating
        product.removeReview(rating);
        productRepository.save(product);

        log.info("Review deleted: id={}, userId={}", reviewId, userId);
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userName(r.getUser().getName())
                .productId(r.getProduct().getId())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}