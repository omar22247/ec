package E_commerce.com.SecureEcommerceApplication.entity;

import E_commerce.com.SecureEcommerceApplication.entity.enums.PaymentMethod;
import E_commerce.com.SecureEcommerceApplication.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// مفيش Soft Delete — بيانات مالية
@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ManyToOne عشان order ممكن يكون فيها أكتر من محاولة دفع
    // (فشلت الأولى، نجحت التانية)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    // الـ ID الراجع من payment gateway زي Stripe أو PayPal
    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // وقت الدفع الفعلي — NULL لو لسه PENDING
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
