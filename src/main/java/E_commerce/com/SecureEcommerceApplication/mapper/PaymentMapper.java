package E_commerce.com.SecureEcommerceApplication.mapper;

import E_commerce.com.SecureEcommerceApplication.dto.response.PaymentResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    // all fields match directly — no custom mappings needed
    PaymentResponse toResponse(Payment payment);
}
