package E_commerce.com.SecureEcommerceApplication.dto.response;

import E_commerce.com.SecureEcommerceApplication.entity.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Returned to the USER when validating a coupon before checkout
// only what the user needs — no internal admin details
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResponse {

    private String       code;
    private DiscountType discountType;    // PERCENTAGE or FIXED
    private BigDecimal   discountValue;   // e.g. 20 or 50.00
    private BigDecimal   minOrderAmount;  // minimum cart total to apply
}