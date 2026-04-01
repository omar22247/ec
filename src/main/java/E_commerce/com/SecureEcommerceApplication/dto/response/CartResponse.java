package E_commerce.com.SecureEcommerceApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long                   id;
    private List<CartItemResponse> items;
    private Integer                totalItems;   // sum of all quantities
    private BigDecimal             totalPrice;   // sum of all subtotals
}