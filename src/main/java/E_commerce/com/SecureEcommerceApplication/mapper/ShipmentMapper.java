package E_commerce.com.SecureEcommerceApplication.mapper;

import E_commerce.com.SecureEcommerceApplication.dto.response.ShipmentResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Shipment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {

    // all fields match directly — no custom mappings needed
    ShipmentResponse toResponse(Shipment shipment);
}
