package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.ChangePasswordRequest;
import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateProfileRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.UserResponse;

public interface UserService {

    // GET /api/v1/users/me
    UserResponse getProfile(Long userId);

    // PUT /api/v1/users/me
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    // PUT /api/v1/users/me/password
    void changePassword(Long userId, ChangePasswordRequest request);

    // DELETE /api/v1/users/me
    void deleteAccount(Long userId);
}