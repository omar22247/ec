package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // all addresses for a user
    List<Address> findAllByUserId(Long userId);

    // single address — verify it belongs to user (security)
    Optional<Address> findByIdAndUserId(Long id, Long userId);

    // get current default address
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    // before setting new default → clear existing default
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultAddress(@Param("userId") Long userId);

    // count user addresses — limit max addresses per user
    long countByUserId(Long userId);
}