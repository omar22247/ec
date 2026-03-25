package E_commerce.com.SecureEcommerceApplication.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SQLRestriction("deleted_at IS NULL")
@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    // ── Denormalized review stats ───────────────────────────
    // Double without precision/scale — floating point type
    // precision + scale only work with BigDecimal / DECIMAL columns
    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;

    @Column(name = "review_count", nullable = false)
    @Builder.Default
    private int reviewCount = 0;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ── Relations ──────────────────────────────────────────
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory inventory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @Builder.Default
    private List<Wishlist> wishlistItems = new ArrayList<>();

    // ── Helper methods ─────────────────────────────────────

    // called when a NEW review is added
    // formula: newAvg = (oldAvg * oldCount + newRating) / newCount
    public void addReview(int rating) {
        this.reviewCount += 1;
        this.averageRating = ((averageRating * (reviewCount - 1)) + rating) / reviewCount;
        this.averageRating = round(averageRating);
    }

    // called when a review is UPDATED
    // formula: newAvg = (oldAvg * count - oldRating + newRating) / count
    public void updateReview(int oldRating, int newRating) {
        if (reviewCount == 0) return;
        this.averageRating = ((averageRating * reviewCount) - oldRating + newRating) / reviewCount;
        this.averageRating = round(averageRating);
    }

    // called when a review is DELETED
    public void removeReview(int rating) {
        if (reviewCount == 0) return;
        if (reviewCount == 1) {
            this.reviewCount   = 0;
            this.averageRating = 0.0;
            return;
        }
        this.averageRating = ((averageRating * reviewCount) - rating) / (reviewCount - 1);
        this.averageRating = round(averageRating);
        this.reviewCount  -= 1;
    }

    private Double round(Double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}