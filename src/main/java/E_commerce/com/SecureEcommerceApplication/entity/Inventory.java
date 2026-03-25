package E_commerce.com.SecureEcommerceApplication.entity;

import E_commerce.com.SecureEcommerceApplication.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private int quantity = 0;

    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private int lowStockThreshold = 5;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── Read helpers ────────────────────────────────────────

    public boolean isInStock() {
        return quantity > 0;
    }

    public boolean isLowStock() {
        return quantity <= lowStockThreshold && quantity > 0;
    }

    // ── Validation ──────────────────────────────────────────
    // Validates that requested quantity is available
    // Called in memory after product + inventory are loaded
    // No extra DB query needed
    public void validateStock(int requestedQty) {
        if (quantity <= 0) {
            throw new OutOfStockException(product.getName());
        }
        if (requestedQty > quantity) {
            throw new OutOfStockException(product.getName(), requestedQty, quantity);
        }
    }

    // ── Write helpers ───────────────────────────────────────

    public void decreaseQuantity(int amount) {
        if (quantity < amount) {
            throw new OutOfStockException(product.getName(), amount, quantity);
        }
        this.quantity -= amount;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }
}