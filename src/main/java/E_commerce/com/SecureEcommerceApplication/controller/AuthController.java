package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.request.*;
import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.AuthResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.RefreshTokenResponse;
import E_commerce.com.SecureEcommerceApplication.service.AuthService;
import E_commerce.com.SecureEcommerceApplication.service.PasswordResetServiceImpl;
import E_commerce.com.SecureEcommerceApplication.service.RefreshTokenService;
import E_commerce.com.SecureEcommerceApplication.util.Ip;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "APIs for authentication and token management")
public class AuthController {

    private final AuthService              authService;
    private final PasswordResetServiceImpl passwordResetService;
    private final RefreshTokenService      refreshTokenService;
    private final Ip                       ip;

    // ── Register ───────────────────────────────────────────────
    @PostMapping("/register")
    @Operation(summary = "Register", description = "Creates a new user account")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Account created successfully")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            HttpServletRequest httpRequest,
            @Valid @RequestBody RegisterRequest registerRequest) {

        AuthResponse data = authService.register(
                registerRequest, ip.resolveClientIp(httpRequest));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", data));
    }

    // ── Login ──────────────────────────────────────────────────
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns access + refresh tokens")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            HttpServletRequest httpRequest,
            @Valid @RequestBody LoginRequest loginRequest) {

        AuthResponse data = authService.login(
                loginRequest, ip.resolveClientIp(httpRequest));

        return ResponseEntity.ok(
                ApiResponse.success("Login successful", data)
        );
    }

    // ── Forgot Password ────────────────────────────────────────
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Sends a password reset link to the provided email")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reset email sent if account exists")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.initiateReset(request.getEmail());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "If an account with this email exists, a password reset link has been sent.")
        );
    }

    // ── Reset Password ─────────────────────────────────────────
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets the user's password using a valid token")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset successfully")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(token, request.getNewPassword());

        return ResponseEntity.ok(
                ApiResponse.success("Password reset successfully")
        );
    }

    // ── Refresh Token ──────────────────────────────────────────
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Issues a new access token using a valid refresh token")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {

        RefreshTokenResponse data = refreshTokenService.rotateRefreshToken(
                request.getRefreshToken(),
                ip.resolveClientIp(httpRequest)
        );

        return ResponseEntity.ok(
                ApiResponse.success("Token refreshed", data)
        );
    }

    // ── Logout ─────────────────────────────────────────────────
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revokes the current refresh token")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logged out successfully")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        refreshTokenService.revokeToken(request.getRefreshToken(), "LOGOUT");

        return ResponseEntity.ok(
                ApiResponse.success("Logged out successfully")
        );
    }

    // ── Logout All ─────────────────────────────────────────────
    @PostMapping("/logout-all")
    @Operation(summary = "Logout all devices", description = "Revokes all active refresh tokens for the user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logged out from all devices")
    public ResponseEntity<ApiResponse<Void>> logoutAll(
            @Valid @RequestBody RefreshTokenRequest request) {

        var token = refreshTokenService.validateAndGet(request.getRefreshToken());
        refreshTokenService.revokeAllTokensForUser(token.getUser(), "LOGOUT_ALL");

        return ResponseEntity.ok(
                ApiResponse.success("Logged out from all devices")
        );
    }
}