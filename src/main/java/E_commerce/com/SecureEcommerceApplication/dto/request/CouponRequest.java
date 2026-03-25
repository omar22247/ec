package E_commerce.com.SecureEcommerceApplication.dto.request;

import  E_commerce.com.SecureEcommerceApplication.entity.enums.DiscountType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponRequest {

    @NotBlank(message = "Coupon code is required")
    @Size(min = 3, max = 50)
    private String code;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.0")
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    // null = unlimited
    @Min(value = 1)
    private Integer maxUses;

    // null = no expiry
    private LocalDateTime expiresAt;
}
