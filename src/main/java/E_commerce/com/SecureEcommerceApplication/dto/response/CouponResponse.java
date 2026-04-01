package E_commerce.com.SecureEcommerceApplication.dto.response;

import E_commerce.com.SecureEcommerceApplication.entity.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {

    private Long          id;
    private String        code;
    private DiscountType  discountType;
    private BigDecimal    discountValue;
    private BigDecimal    minOrderAmount;
    private Integer       maxUses;
    private int           usedCount;
    private LocalDateTime expiresAt;
    private boolean       active;

    // calculated: active + not expired + has remaining uses
    private boolean       valid;
}