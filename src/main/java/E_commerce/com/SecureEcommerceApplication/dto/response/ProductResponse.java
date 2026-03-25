package E_commerce.com.SecureEcommerceApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long       id;
    private String     name;
    private BigDecimal basePrice;
    private String     imageUrl;
    private boolean    active;

    // category — name only, no need for full object
    private Long   categoryId;
    private String categoryName;

    // inventory — just availability, not the full details
    private int     stock;
    private boolean inStock;

    // reviews — summary only (no full review list)
    private Double averageRating;
    private long   reviewCount;
}
