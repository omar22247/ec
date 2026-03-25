package E_commerce.com.SecureEcommerceApplication.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;
        // test if it accepts 1.1
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;
}
