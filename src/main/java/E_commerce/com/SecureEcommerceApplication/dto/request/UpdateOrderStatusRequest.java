package E_commerce.com.SecureEcommerceApplication.dto.request;

import  E_commerce.com.SecureEcommerceApplication.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
