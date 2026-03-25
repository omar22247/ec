package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    // المنتجات اللي خلص منها الـ stock
    List<Inventory> findAllByQuantityEquals(int quantity);

    // المنتجات اللي وصلت للحد الأدنى (low stock alert)
    // بنستخدمها للـ admin dashboard
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.lowStockThreshold AND i.quantity > 0")
    List<Inventory> findLowStockItems();

    // تحديث الـ stock مباشرة بـ query بدون جلب الـ entity
    // أسرع وأأمن في حالة الـ concurrent requests
    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity - :amount WHERE i.product.id = :productId AND i.quantity >= :amount")
    int decreaseStock(@Param("productId") Long productId, @Param("amount") int amount);

    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity + :amount WHERE i.product.id = :productId")
    void increaseStock(@Param("productId") Long productId, @Param("amount") int amount);

}
