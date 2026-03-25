package E_commerce.com.SecureEcommerceApplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

// مفيش Soft Delete — بيانات مالية مش بتتحذف
@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    // Snapshot للسعر وقت الشراء
    // مهم عشان لو السعر اتغير بعدين، الطلبات القديمة تفضل صح
    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase;

    // ── Helper ──────────────────────────────────────────────
    public BigDecimal getSubtotal() {
        return priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
    }
}
