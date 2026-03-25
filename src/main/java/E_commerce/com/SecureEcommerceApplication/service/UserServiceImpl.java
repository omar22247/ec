package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.ChangePasswordRequest;
import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateProfileRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.UserResponse;
import E_commerce.com.SecureEcommerceApplication.entity.User;
import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.repository.UserRepository;
import E_commerce.com.SecureEcommerceApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    // ════════════════════════════════════════════════════════
    //  GET PROFILE
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        return toResponse(findUserById(userId));
    }

    // ════════════════════════════════════════════════════════
    //  UPDATE PROFILE
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {

        User user = findUserById(userId);
        user.setName(request.getName());
        userRepository.save(user);

        log.info("Profile updated: userId={}", userId);
        return toResponse(user);
    }

    // ════════════════════════════════════════════════════════
    //  CHANGE PASSWORD
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {

        User user = findUserById(userId);

        // OAuth2 users have no password
        if (user.getPasswordHash() == null) {
            throw new BusinessException(
                    "Cannot change password for OAuth2 accounts. " +
                            "You signed in with Google.");
        }

        // verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException("Current password is incorrect");
        }

        // new password must be different
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessException(
                    "New password must be different from current password");
        }

        // confirm password must match new password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed: userId={}", userId);
    }

    // ════════════════════════════════════════════════════════
    //  DELETE ACCOUNT
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void deleteAccount(Long userId) {

        User user = findUserById(userId);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Account soft-deleted: userId={}", userId);
    }

    // ════════════════════════════════════════════════════════
    //  Private helpers
    // ════════════════════════════════════════════════════════

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "id", userId));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}