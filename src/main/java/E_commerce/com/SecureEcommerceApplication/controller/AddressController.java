package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.request.AddressRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.AddressResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
import E_commerce.com.SecureEcommerceApplication.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/addresses")
@RequiredArgsConstructor
@Tag(name = "User Addresses", description = "APIs for managing authenticated user's addresses")
@SecurityRequirement(name = "bearerAuth") // Assumes you are using JWT/Bearer token auth
public class AddressController {

    private final AddressService addressService;

    // GET /api/v1/users/me/addresses
    @GetMapping
    @Operation(summary = "Get all addresses", description = "Retrieves a list of all addresses for the currently authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success(addressService.getAddresses(userDetails.getUser().getId()))
        );
    }

    // GET /api/v1/users/me/addresses/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID", description = "Retrieves a specific address belonging to the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved address")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the address to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(addressService.getAddressById(userDetails.getUser().getId(), id))
        );
    }

    // POST /api/v1/users/me/addresses
    @PostMapping
    @Operation(summary = "Create a new address", description = "Adds a new address for the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Address created successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address created successfully",
                        addressService.createAddress(userDetails.getUser().getId(), request)));
    }

    // PUT /api/v1/users/me/addresses/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update an address", description = "Updates an existing address by its ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address updated successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the address to update") @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Address updated successfully",
                        addressService.updateAddress(userDetails.getUser().getId(), id, request))
        );
    }

    // DELETE /api/v1/users/me/addresses/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address", description = "Deletes a specific address by its ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address deleted successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the address to delete") @PathVariable Long id) {
        addressService.deleteAddress(userDetails.getUser().getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }

    // PATCH /api/v1/users/me/addresses/{id}/default
    @PatchMapping("/{id}/default")
    @Operation(summary = "Set default address", description = "Marks a specific address as the default for the user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Default address updated successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Parameter(description = "ID of the address to set as default") @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Default address updated",
                        addressService.setDefaultAddress(userDetails.getUser().getId(), id))
        );
    }

    private Long userId(AppUserDetails u) {
        return u.getUser().getId();
    }
}