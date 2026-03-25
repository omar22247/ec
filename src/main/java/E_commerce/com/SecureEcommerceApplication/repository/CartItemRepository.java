package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // check if product already in cart
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    // ── Direct UPDATE — no SELECT before UPDATE ─────────────
    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = :qty WHERE ci.id = :id")
    void updateQuantity(@Param("id") Long id, @Param("qty") int qty);

    // ── Direct DELETE — no SELECT before DELETE ─────────────
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id = :id")
    void deleteByItemId(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);
}
