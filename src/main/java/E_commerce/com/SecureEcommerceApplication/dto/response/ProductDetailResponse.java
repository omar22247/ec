package E_commerce.com.SecureEcommerceApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Used in: GET /api/v1/products/{id}  (detail page)
// Full data — everything about one product
// Reviews are fetched separately via GET /api/v1/products/{id}/reviews
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {

    private Long       id;
    private String     name;
    private String     description;  // ✅ full description (not in list)
    private BigDecimal basePrice;
    private String     imageUrl;
    private boolean    active;

    // category
    private Long   categoryId;
    private String categoryName;

    // inventory — full details here
    private int     stock;
    private boolean inStock;
    private boolean lowStock;        // ✅ low stock warning (not in list)

    // reviews — summary only
    // full list comes from GET /api/v1/products/{id}/reviews
    private Double averageRating;
    private long   reviewCount;
}