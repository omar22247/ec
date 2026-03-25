package E_commerce.com.SecureEcommerceApplication.dto.response;

import E_commerce.com.SecureEcommerceApplication.entity.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {

    private Long           id;
    private ShipmentStatus status;
    private String         carrier;
    private String         trackingNumber;
    private LocalDateTime  shippedAt;
    private LocalDateTime  estimatedDelivery;
}