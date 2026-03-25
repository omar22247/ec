package E_commerce.com.SecureEcommerceApplication.dto.request;

import  E_commerce.com.SecureEcommerceApplication.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull(message = "Address is required")

    private Long addressId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    // اختياري — كوبون خصم
    private String couponCode;
}
