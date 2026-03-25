package E_commerce.com.SecureEcommerceApplication.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "oauth_accounts",
       uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** e.g. "google", "github" */
    @Column(nullable = false, length = 30)
    private String provider;

    /** Unique user ID from the OAuth provider */
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
