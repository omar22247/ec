package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.AddressRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {

    // GET /api/v1/users/me/addresses
    List<AddressResponse> getAddresses(Long userId);

    // GET /api/v1/users/me/addresses/{id}
    AddressResponse getAddressById(Long userId, Long addressId);

    // POST /api/v1/users/me/addresses
    AddressResponse createAddress(Long userId, AddressRequest request);

    // PUT /api/v1/users/me/addresses/{id}
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

    // DELETE /api/v1/users/me/addresses/{id}
    void deleteAddress(Long userId, Long addressId);

    // PATCH /api/v1/users/me/addresses/{id}/default
    AddressResponse setDefaultAddress(Long userId, Long addressId);
}