package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    // for admin — just by orderId
    Optional<Shipment> findByOrderId(Long orderId);

    // for user — verify order belongs to them
    @Query("""
        SELECT s FROM Shipment s
        WHERE s.order.id = :orderId
        AND s.order.user.id = :userId
        AND s.order.deletedAt IS NULL
    """)
    Optional<Shipment> findByOrderIdAndUserId(
            @Param("orderId") Long orderId,
            @Param("userId")  Long userId);
}