package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.LoginRequest;
import E_commerce.com.SecureEcommerceApplication.dto.request.RegisterRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.AuthResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.UserResponse;
import E_commerce.com.SecureEcommerceApplication.entity.User;
import E_commerce.com.SecureEcommerceApplication.entity.enums.UserRole;
import E_commerce.com.SecureEcommerceApplication.exception.DuplicateResourceException;
import E_commerce.com.SecureEcommerceApplication.repository.UserRepository;
import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
import E_commerce.com.SecureEcommerceApplication.security.*;
import E_commerce.com.SecureEcommerceApplication.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil               jwtUtil;
    private final AppUserDetailsService userDetailsService;
    private final EmailService emailService;

    // ── REGISTER ────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // save user
        User user = userRepository.save(User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build());

        // generate token
        AppUserDetails userDetails = (AppUserDetails)
                userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        // ✅ Send welcome email
        try {
            emailService.sendRegisterEmail(user.getEmail(), user.getName());
        } catch (Exception e) {
            log.warn("Register email failed but user created. email={}, error={}",
                    user.getEmail(), e.getMessage());
        }
        return AuthResponse.builder()
                .accessToken(token)
                .user(toUserResponse(user))
                .build();
    }

    // ── LOGIN ───────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {

        // validates email + password — throws BadCredentialsException if wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // load user details
        AppUserDetails userDetails = (AppUserDetails)
                userDetailsService.loadUserByUsername(request.getEmail());

        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(token)
                .user(toUserResponse(userDetails.getUser()))
                .build();
    }

    // ── Helper ──────────────────────────────────────────────

    private UserResponse toUserResponse(User user) {
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