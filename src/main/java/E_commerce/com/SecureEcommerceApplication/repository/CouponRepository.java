package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);


    @Modifying
    @Query("UPDATE Coupon c SET c.usedCount = c.usedCount + 1 " +
           "WHERE c.id = :id AND (c.maxUses IS NULL OR c.usedCount < c.maxUses)")
    int incrementUsedCount(@Param("id") Long id);
}
 