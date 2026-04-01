package E_commerce.com.SecureEcommerceApplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Stored as a SHA-256 hash for security.
     * The raw token is only returned once to the client.
     */
    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private Instant createdAt;

    /** IP address of the client that requested this token (for audit/revocation). */
    @Column(length = 45)
    private String createdByIp;

    /** Flag to allow explicit revocation without immediate deletion. */
    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;

    /** When was it revoked (null if still valid). */
    private Instant revokedAt;

    /** Reason for revocation (e.g. "LOGOUT", "ROTATED", "ADMIN_REVOKE"). */
    @Column(length = 64)
    private String revocationReason;

    // ── Helpers ─────────────────────────────────────────────────────────────

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public void revoke(String reason) {
        this.revoked = true;
        this.revokedAt = Instant.now();
        this.revocationReason = reason;
    }
}