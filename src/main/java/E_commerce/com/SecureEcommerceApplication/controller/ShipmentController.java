package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateShipmentRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ShipmentResponse;
import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
import E_commerce.com.SecureEcommerceApplication.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "APIs for tracking and managing order shipments")
@SecurityRequirement(name = "bearerAuth")
public class ShipmentController {

    private final ShipmentService shipmentService;

    // ── GET shipment (user) ────────────────────────────────────
    @GetMapping("/api/v1/orders/{orderId}/shipment")
    @Operation(summary = "Get shipment details",
            description = "Retrieves shipment tracking info for a specific order")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Shipment details retrieved")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipment(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the order") @PathVariable Long orderId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        shipmentService.getShipment(userId(userDetails), orderId))
        );
    }

    // ── PUT update shipment (admin) ────────────────────────────
    @PutMapping("/api/v1/admin/orders/{orderId}/shipment")
    @Operation(summary = "Update shipment",
            description = "Updates the shipment details for an order. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Shipment updated successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipment(
            @Parameter(description = "ID of the order") @PathVariable Long orderId,
            @Valid @RequestBody UpdateShipmentRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Shipment updated successfully",
                        shipmentService.updateShipment(orderId, request))
        );
    }

    private Long userId(AppUserDetails u) {
        return u.getUser().getId();
    }
}