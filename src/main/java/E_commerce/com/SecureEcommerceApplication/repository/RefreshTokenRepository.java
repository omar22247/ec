package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.entity.RefreshToken;
import E_commerce.com.SecureEcommerceApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUser(User user);

    List<RefreshToken> findAllByUserAndRevokedFalse(User user);

    /** Count active (non-revoked, non-expired) tokens for a user — used to cap concurrent sessions. */
    @Query("""
            SELECT COUNT(rt) FROM RefreshToken rt
            WHERE rt.user = :user
              AND rt.revoked = false
              AND rt.expiryDate > :now
            """)
    long countActiveTokensByUser(@Param("user") User user, @Param("now") Instant now);

    /** Revoke all tokens for a user (e.g. on password change or admin action). */
    @Modifying
    @Query("""
            UPDATE RefreshToken rt
            SET rt.revoked = true,
                rt.revokedAt = :now,
                rt.revocationReason = :reason
            WHERE rt.user = :user
              AND rt.revoked = false
            """)
    int revokeAllByUser(@Param("user") User user,
                        @Param("now") Instant now,
                        @Param("reason") String reason);

    /** Hard-delete expired tokens older than the given threshold (called by scheduler). */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :threshold")
    int deleteAllExpiredBefore(@Param("threshold") Instant threshold);

    /** Hard-delete revoked tokens older than the given threshold. */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.revoked = true AND rt.revokedAt < :threshold")
    int deleteAllRevokedBefore(@Param("threshold") Instant threshold);
}