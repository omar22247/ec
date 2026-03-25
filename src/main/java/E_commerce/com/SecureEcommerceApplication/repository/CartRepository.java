package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.dto.response.CartItemResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // ── Simple cart — no items ──────────────────────────────
    // used by: getCart, clearCart
    Optional<Cart> findByUserId(Long userId);

    // ── Full cart — items + products + inventories ──────────
    // ONE JOIN FETCH query loads everything
    // used by: addItem, updateItem, removeItem
    // all checks happen in memory after this — no extra queries
    @Query("""
        SELECT DISTINCT c
        FROM Cart c
        LEFT JOIN FETCH c.items ci
        LEFT JOIN FETCH ci.product p
        LEFT JOIN FETCH p.inventory
        WHERE c.user.id = :userId
        AND c.deletedAt IS NULL
        AND (ci IS NULL OR ci.deletedAt IS NULL)
    """)
    Optional<Cart> findWithItemsByUserId(@Param("userId") Long userId);

    // ── DTO projection — response only ─────────────────────
    // called as the LAST step in every operation
    // no arithmetic or boolean in constructor — calculated in CartItemResponse
    @Query("""
        SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.CartItemResponse(
            ci.id,
            p.id,
            p.name,
            p.imageUrl,
            p.basePrice,
            ci.quantity,
            i.quantity
        )
        FROM CartItem ci
        JOIN ci.product p
        JOIN p.inventory i
        WHERE ci.cart.id = :cartId
        AND ci.deletedAt IS NULL
        AND p.deletedAt IS NULL
        ORDER BY ci.id ASC
    """)
    List<CartItemResponse> findCartItems(@Param("cartId") Long cartId);
}