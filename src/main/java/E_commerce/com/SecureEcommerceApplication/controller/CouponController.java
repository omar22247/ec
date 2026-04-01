package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.request.CouponRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.CouponResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.CouponValidationResponse;
import E_commerce.com.SecureEcommerceApplication.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "APIs for managing discount coupons")
public class CouponController {

    private final CouponService couponService;

    // ════════════════════════════════════════════════════════════
    //  USER — authenticated
    // ════════════════════════════════════════════════════════════

    @PostMapping("/api/v1/coupons/validate")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Validate coupon",
            description = "Checks if a coupon code is valid before applying it to an order")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Coupon validation result")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CouponValidationResponse>> validateCoupon(
            @Parameter(description = "The coupon code to validate") @RequestParam String code) {

        return ResponseEntity.ok(
                ApiResponse.success(couponService.validateCoupon(code))
        );
    }

    // ════════════════════════════════════════════════════════════
    //  ADMIN only
    // ════════════════════════════════════════════════════════════

    @GetMapping("/api/v1/admin/coupons")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all coupons",
            description = "Retrieves all coupons in the system. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Coupons retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAllCoupons() {

        return ResponseEntity.ok(
                ApiResponse.success(couponService.getAllCoupons())
        );
    }

    @GetMapping("/api/v1/admin/coupons/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get coupon by ID",
            description = "Retrieves a specific coupon. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Coupon retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponById(
            @Parameter(description = "ID of the coupon") @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(couponService.getCouponById(id))
        );
    }

    @PostMapping("/api/v1/admin/coupons")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create coupon",
            description = "Creates a new discount coupon. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
            description = "Coupon created successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(
            @Valid @RequestBody CouponRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coupon created successfully",
                        couponService.createCoupon(request)));
    }

    @PutMapping("/api/v1/admin/coupons/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update coupon",
            description = "Updates an existing coupon. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Coupon updated successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
            @Parameter(description = "ID of the coupon to update") @PathVariable Long id,
            @Valid @RequestBody CouponRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Coupon updated successfully",
                        couponService.updateCoupon(id, request))
        );
    }

    @DeleteMapping("/api/v1/admin/coupons/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete coupon",
            description = "Permanently deletes a coupon. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Coupon deleted successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(
            @Parameter(description = "ID of the coupon to delete") @PathVariable Long id) {

        couponService.deleteCoupon(id);
        return ResponseEntity.ok(
                ApiResponse.success("Coupon deleted successfully")
        );
    }

    @PatchMapping("/api/v1/admin/coupons/{id}/toggle")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle coupon status",
            description = "Activates or deactivates a coupon. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Coupon status updated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponResponse>> toggleCoupon(
            @Parameter(description = "ID of the coupon to toggle") @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success("Coupon status updated",
                        couponService.toggleCoupon(id))
        );
    }
}