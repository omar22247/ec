package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.entity.*;

import E_commerce.com.SecureEcommerceApplication.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByOrderId(Long orderId);

    // آخر payment ناجحة على الـ order
    Optional<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);

    // تحقق من الـ transaction ID (منع التكرار)
    boolean existsByTransactionId(String transactionId);
}
