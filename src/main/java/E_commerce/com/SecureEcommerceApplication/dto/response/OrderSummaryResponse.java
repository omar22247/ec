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
    private Long          totalItems;   // Long matches JPQL SUM() return type
    private BigDecimal    totalPrice;
    private String        couponCode;   // null if no coupon applied
    private LocalDateTime createdAt;
}
