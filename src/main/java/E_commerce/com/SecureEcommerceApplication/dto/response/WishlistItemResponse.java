package E_commerce.com.SecureEcommerceApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {

    private Long          wishlistItemId;
    private Long          productId;
    private String        productName;
    private String        productImage;
    private BigDecimal    price;
    private boolean       inStock;
    private LocalDateTime addedAt;

    // JPQL constructor — stockQuantity converted to boolean here, not in query
    public WishlistItemResponse(Long wishlistItemId, Long productId,
                                String productName, String productImage,
                                BigDecimal price, Integer stockQuantity,
                                LocalDateTime addedAt) {
        this.wishlistItemId = wishlistItemId;
        this.productId      = productId;
        this.productName    = productName;
        this.productImage   = productImage;
        this.price          = price;
        this.inStock        = stockQuantity != null && stockQuantity > 0;
        this.addedAt        = addedAt;
    }
}