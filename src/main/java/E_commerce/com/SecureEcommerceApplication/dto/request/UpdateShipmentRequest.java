package E_commerce.com.SecureEcommerceApplication.dto.request;

import E_commerce.com.SecureEcommerceApplication.entity.enums.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateShipmentRequest {

    @NotNull(message = "Status is required")
    private ShipmentStatus status;

    private String        carrier;
    private String        trackingNumber;
    private LocalDateTime estimatedDelivery;
}