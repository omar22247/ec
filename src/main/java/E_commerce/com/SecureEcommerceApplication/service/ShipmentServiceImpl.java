package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateShipmentRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.ShipmentResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Shipment;
import E_commerce.com.SecureEcommerceApplication.entity.User;
import E_commerce.com.SecureEcommerceApplication.entity.enums.ShipmentStatus;
import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.repository.ShipmentRepository;
import E_commerce.com.SecureEcommerceApplication.repository.UserRepository;
import E_commerce.com.SecureEcommerceApplication.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // ════════════════════════════════════════════════════════
    //  USER — get shipment for their order
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(Long userId, Long orderId) {

        Shipment shipment = shipmentRepository
                .findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment", "orderId", orderId));

        return toResponse(shipment);
    }

    // ════════════════════════════════════════════════════════
    //  ADMIN — update shipment info
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ShipmentResponse updateShipment(Long orderId, UpdateShipmentRequest request) {

        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment", "orderId", orderId));

        // validate status transition
        validateStatusTransition(shipment.getStatus(), request.getStatus());

        shipment.setStatus(request.getStatus());

        // update tracking info if provided
        if (request.getCarrier() != null) {
            shipment.setCarrier(request.getCarrier());
        }
        if (request.getTrackingNumber() != null) {
            shipment.setTrackingNumber(request.getTrackingNumber());
        }
        if (request.getEstimatedDelivery() != null) {
            shipment.setEstimatedDelivery(request.getEstimatedDelivery());
        }

        // auto-set shippedAt when status moves to SHIPPED
        if (request.getStatus() == ShipmentStatus.SHIPPED
                && shipment.getShippedAt() == null) {
            shipment.setShippedAt(LocalDateTime.now());
        }


        ShipmentResponse response = toResponse(shipment);

        // ✅ Send email after save
//        User user = userRepository.findByEmail(Se).orElseThrow();
        try {
            String userEmail = shipment.getOrder().getUser().getEmail();
            String userName  = shipment.getOrder().getUser().getName();
            log.warn(userName+ " "+userEmail);
            emailService.sendShipmentUpdateEmail(userEmail, userName, response, orderId);
        } catch (Exception e) {
            // ✅ لو الإيميل فشل متوقفش الـ transaction
            log.warn("Shipment email failed but order updated. orderId={}, error={}", orderId, e.getMessage());
        }

        return response;
    }

    // ════════════════════════════════════════════════════════
    //  Private helpers
    // ════════════════════════════════════════════════════════

    private void validateStatusTransition(ShipmentStatus current, ShipmentStatus next) {
        boolean valid = switch (current) {
            case PREPARING -> next == ShipmentStatus.SHIPPED;
            case SHIPPED   -> next == ShipmentStatus.DELIVERED;
            case DELIVERED -> false;  // terminal state
            case RETURNED -> false;
        };

        if (!valid) {
            throw new BusinessException(
                    "Invalid shipment status transition: " + current + " → " + next);
        }
    }

    private ShipmentResponse toResponse(Shipment s) {
        return ShipmentResponse.builder()
                .id(s.getId())
                .status(s.getStatus())
                .carrier(s.getCarrier())
                .trackingNumber(s.getTrackingNumber())
                .shippedAt(s.getShippedAt())
                .estimatedDelivery(s.getEstimatedDelivery())
                .build();
    }
}