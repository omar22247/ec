package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.request.ChangePasswordRequest;
import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateProfileRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.AuthResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.UserResponse;
import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
import E_commerce.com.SecureEcommerceApplication.service.UserService;
import E_commerce.com.SecureEcommerceApplication.util.Ip;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "APIs for managing the authenticated user's profile")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final Ip          ip;

    // ── GET profile ────────────────────────────────────────────
    @GetMapping("/me")
    @Operation(summary = "Get profile",
            description = "Returns the profile of the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Profile retrieved successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.getProfile(userId(userDetails)))
        );
    }

    // ── PUT update profile ─────────────────────────────────────
    @PutMapping("/me")
    @Operation(summary = "Update profile",
            description = "Updates the name or email of the authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Profile updated successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Profile updated successfully",
                        userService.updateProfile(userId(userDetails), request))
        );
    }

    // ── PUT change password ────────────────────────────────────
    @PutMapping("/me/password")
    @Operation(summary = "Change password",
            description = "Changes the password of the authenticated user and re-issues tokens")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Password changed successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AuthResponse>> changePassword(
            HttpServletRequest httpRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {

        AuthResponse data = userService.changePassword(
                userId(userDetails),
                changePasswordRequest,
                ip.resolveClientIp(httpRequest)
        );

        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully", data)
        );
    }

    // ── DELETE account ─────────────────────────────────────────
    @DeleteMapping("/me")
    @Operation(summary = "Delete account",
            description = "Permanently deletes the authenticated user's account")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Account deleted successfully")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails) {

        userService.deleteAccount(userId(userDetails));
        return ResponseEntity.ok(
                ApiResponse.success("Account deleted successfully")
        );
    }

    private Long userId(AppUserDetails u) {
        return u.getUser().getId();
    }
}