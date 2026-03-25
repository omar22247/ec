package E_commerce.com.SecureEcommerceApplication.service;


import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateShipmentRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.ShipmentResponse;

public interface ShipmentService {

    // GET /api/v1/orders/{orderId}/shipment
    // user checks their shipment status
    ShipmentResponse getShipment(Long userId, Long orderId);

    // PUT /api/v1/admin/orders/{orderId}/shipment
    // admin updates carrier, tracking number, estimated delivery
    ShipmentResponse updateShipment(Long orderId, UpdateShipmentRequest request);
}