package E_commerce.com.SecureEcommerceApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lightweight — used for admin stock management endpoints
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockResponse {

    private Long    id;
    private String  name;
    private boolean active;
    private Integer stock;
}