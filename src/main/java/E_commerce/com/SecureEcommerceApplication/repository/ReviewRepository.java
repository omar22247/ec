package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.dto.response.ReviewResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // ── DTO Projection ──────────────────────────────────────
    @Query("""
        SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.ReviewResponse(
            r.id,
            r.user.id,
            r.user.name,
            r.product.id,
            r.rating,
            r.comment,
            r.createdAt
        )
        FROM Review r
        WHERE r.product.id = :productId
        AND r.deletedAt IS NULL
        ORDER BY r.createdAt DESC
    """)
    Page<ReviewResponse> findByProductId(
            @Param("productId") Long productId,
            Pageable pageable);

    // ── Entity queries ──────────────────────────────────────
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);
    Optional<Review> findByIdAndUserId(Long id, Long userId);

    // check user has purchased and received this product
    @Query("""
        SELECT COUNT(oi) > 0
        FROM OrderItem oi
        WHERE oi.order.user.id = :userId
        AND oi.product.id = :productId
        AND oi.order.status = E_commerce.com.SecureEcommerceApplication.entity.enums.OrderStatus.DELIVERED
        AND oi.order.deletedAt IS NULL
    """)
    boolean hasUserPurchasedProduct(
            @Param("userId") Long userId,
            @Param("productId") Long productId);
}