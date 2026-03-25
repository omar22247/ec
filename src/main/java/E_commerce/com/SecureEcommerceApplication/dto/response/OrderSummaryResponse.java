package E_commerce.com.SecureEcommerceApplication.dto.response;

import E_commerce.com.SecureEcommerceApplication.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Used in list endpoints — no items, no address details
// just what the user/admin needs to scan a list of orders
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {

    private Long          id;
    private OrderStatus   status;
    private int           totalItems;    // total quantity of all items
    private BigDecimal    totalPrice;
    private String        couponCode;    // null if no coupon applied
    private LocalDateTime createdAt;

    // JPQL constructor — called directly from the query
    public OrderSummaryResponse(Long id, OrderStatus status,
                                BigDecimal totalPrice,
                                String couponCode,
                                LocalDateTime createdAt) {
        this.id         = id;
        this.status     = status;
        this.totalPrice = totalPrice;
        this.couponCode = couponCode;
        this.createdAt  = createdAt;
        // totalItems calculated separately or set by service
    }
}
