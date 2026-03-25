package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.dto.response.WishlistItemResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // ── DTO Projection ──────────────────────────────────────
    // w.addedAt not w.createdAt — field name in Wishlist entity
    @Query("""
        SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.WishlistItemResponse(
            w.id,
            p.id,
            p.name,
            p.imageUrl,
            p.basePrice,
            i.quantity,
            w.addedAt
        )
        FROM Wishlist w
        JOIN w.product p
        JOIN p.inventory i
        WHERE w.user.id = :userId
        ORDER BY w.addedAt DESC
    """)
    List<WishlistItemResponse> findByUserId(@Param("userId") Long userId);

    // ── Entity queries ──────────────────────────────────────
    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);
    Optional<Wishlist> findByIdAndUserId(Long id, Long userId);

    // soft delete all items — direct UPDATE
    void deleteByUserId(Long userId);
}