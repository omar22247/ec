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
public class OrderItemResponse {

    private Long       id;
    private Long       productId;
    private String     productName;
    private String     productImage;
    private int        quantity;
    private BigDecimal priceAtPurchase;   // snapshot — price when order was placed
    private BigDecimal subtotal;          // priceAtPurchase * quantity
}