package E_commerce.com.SecureEcommerceApplication.repository;


import E_commerce.com.SecureEcommerceApplication.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}