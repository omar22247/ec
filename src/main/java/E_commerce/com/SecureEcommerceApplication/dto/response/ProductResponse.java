package E_commerce.com.SecureEcommerceApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Used in list endpoints — lightweight, no description, no low-stock warning
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

    // category — name only
    private Long       categoryId;
    private String     categoryName;

    // inventory — availability only
    private int        stock;
    private boolean    inStock;

    // reviews — summary only
    private Double     averageRating;
    private long       reviewCount;
}