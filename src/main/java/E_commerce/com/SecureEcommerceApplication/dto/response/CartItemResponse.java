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
    private BigDecimal subtotal;   // calculated in constructor
    private boolean    inStock;    // calculated in constructor

    @JsonIgnore
    private Integer stockQuantity; // raw value from DB — not sent to client

    // JPQL constructor — 7 raw values, NO arithmetic, NO boolean expressions
    // Rules:
    //   - all numeric types are wrapper (Integer, Long) not primitive
    //   - subtotal calculated here: unitPrice * quantity
    //   - inStock calculated here:  stockQuantity > 0
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
