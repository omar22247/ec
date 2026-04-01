package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.response.RefreshTokenResponse;
import E_commerce.com.SecureEcommerceApplication.entity.RefreshToken;
import E_commerce.com.SecureEcommerceApplication.entity.User;
import E_commerce.com.SecureEcommerceApplication.exception.InvalidTokenException;
import E_commerce.com.SecureEcommerceApplication.repository.RefreshTokenRepository;
import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
import E_commerce.com.SecureEcommerceApplication.security.JwtUtil;
import E_commerce.com.SecureEcommerceApplication.util.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    // ── Dependencies ─────────────────────────────────────────────────────────

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil                jwtUtil;
    private final UserDetailsService userDetailsService;

    // ── Config ───────────────────────────────────────────────────────────────

    /** Refresh-token TTL in seconds (default: 7 days). */
    @Value("${app.jwt.refresh-token-expiration-seconds:604800}")
    private long refreshTokenExpirationSeconds;

    /** Max concurrent active sessions per user (0 = unlimited). */
    @Value("${app.jwt.max-active-sessions:5}")
    private int maxActiveSessions;

    /** Access-token TTL in seconds (mirrors JwtUtil but exposed for response DTO). */
    @Value("${app.jwt.access-token-expiration-seconds:900}")
    private long accessTokenExpirationSeconds;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // ── Public API ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public String createRefreshToken(User user, String createdByIp) {
        enforceSessionLimit(user);

        String rawToken  = generateRawToken();
        String tokenHash = TokenHashUtil.hash(rawToken);

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(refreshTokenExpirationSeconds))
                .createdAt(Instant.now())
                .createdByIp(createdByIp)
                .revoked(false)
                .build();

        refreshTokenRepository.save(entity);

        return rawToken; // returned to client ONCE — never stored in plain text
    }

    @Override
    @Transactional
    public RefreshTokenResponse rotateRefreshToken(String rawToken, String clientIp) {
        RefreshToken existing = validateAndGet(rawToken);

        // Revoke the consumed token (rotation)
        existing.revoke("ROTATED");
        refreshTokenRepository.save(existing);

        // Issue a new refresh token
        String newRawRefresh = createRefreshToken(existing.getUser(), clientIp);
        AppUserDetails userDetails = (AppUserDetails)
                userDetailsService.loadUserByUsername(existing.getUser().getEmail());
        // Issue a new access token
        String newAccessToken = jwtUtil.generateToken(userDetails);

        log.info("Refresh token rotated for user [{}]", existing.getUser().getEmail());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRawRefresh)
                .tokenType("Bearer")
                .accessTokenExpiresIn(accessTokenExpirationSeconds)
                .refreshTokenExpiresIn(refreshTokenExpirationSeconds)
                .build();
    }

    @Override
    @Transactional
    public void revokeToken(String rawToken, String reason) {
        String tokenHash = TokenHashUtil.hash(rawToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        if (token.isRevoked()) {
            log.warn("Attempted to revoke an already-revoked token (hash: {})", tokenHash);
            return;
        }

        token.revoke(reason);
        refreshTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void revokeAllTokensForUser(User user, String reason) {
        int count = refreshTokenRepository.revokeAllByUser(user, Instant.now(), reason);
        log.info("Revoked {} refresh token(s) for user [{}], reason: {}", count, user.getEmail(), reason);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateAndGet(String rawToken) {
        String tokenHash = TokenHashUtil.hash(rawToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (token.isRevoked()) {
            // Possible token reuse attack — revoke all sessions for this user
            log.warn("Revoked refresh token reuse detected for user [{}]. Revoking all sessions.",
                    token.getUser().getEmail());
            revokeAllTokensForUser(token.getUser(), "REUSE_DETECTED");
            throw new InvalidTokenException("Refresh token has been revoked");
        }

        if (token.isExpired()) {
            throw new InvalidTokenException("Refresh token has expired");
        }

        return token;
    }

    // ── Private Helpers ──────────────────────────────────────────────────────

    /**
     * Generates a cryptographically secure 256-bit (32-byte) URL-safe Base64 token.
     */
    private String generateRawToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * If {@code maxActiveSessions > 0}, checks the active session count and throws
     * a {@link InvalidTokenException} if the limit is reached.
     */
    private void enforceSessionLimit(User user) {
        if (maxActiveSessions <= 0) return;

        long activeCount = refreshTokenRepository.countActiveTokensByUser(user, Instant.now());
        if (activeCount >= maxActiveSessions) {
            throw new InvalidTokenException(
                    "Maximum concurrent sessions (" + maxActiveSessions + ") reached. " +
                            "Please log out from another device first.");
        }
    }
}