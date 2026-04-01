package E_commerce.com.SecureEcommerceApplication.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartItemResponse {

    private Long       id;
    private Long       productId;
    private String     productName;
    private String     productImage;
    private BigDecimal unitPrice;
    private Integer    quantity;
    private BigDecimal subtotal;      // unitPrice * quantity
    private boolean    inStock;       // stockQuantity > 0

    @JsonIgnore
    private Integer stockQuantity;    // raw DB value — never sent to client

    // JPQL constructor — arithmetic and boolean logic handled here, not in queries
    public CartItemResponse(Long id,
                            Long productId,
                            String productName,
                            String productImage,
                            BigDecimal unitPrice,
                            Integer quantity,
                            Integer stockQuantity) {
        this.id            = id;
        this.productId     = productId;
        this.productName   = productName;
        this.productImage  = productImage;
        this.unitPrice     = unitPrice;
        this.quantity      = quantity;
        this.stockQuantity = stockQuantity;
        this.subtotal      = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.inStock       = stockQuantity != null && stockQuantity > 0;
    }
}