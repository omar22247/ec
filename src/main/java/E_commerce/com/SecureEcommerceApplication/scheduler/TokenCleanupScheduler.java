package E_commerce.com.SecureEcommerceApplication.scheduler;

import E_commerce.com.SecureEcommerceApplication.repository.PasswordResetTokenRepository;
import E_commerce.com.SecureEcommerceApplication.repository.RefreshTokenRepository;
import E_commerce.com.SecureEcommerceApplication.security.RateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final PasswordResetTokenRepository tokenRepository;
    private final RateLimiterService rateLimiterService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.revoked-token-retention-days:1}")
    private int revokedRetentionDays;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void deleteExpiredTokens() {
        tokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
        log.info("Expired password reset tokens cleaned up");
    }

    // ✅ امسح الـ buckets كل 6 ساعات عشان ما تتراكمش في الـ memory
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    public void clearRateLimitBuckets() {
        rateLimiterService.clearBuckets();
        log.info("Rate limit buckets cleared");
    }
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanUpTokens() {
        Instant now = Instant.now();

        // 1. Delete all expired tokens (regardless of revocation status)
        int expiredDeleted = refreshTokenRepository.deleteAllExpiredBefore(now);

        // 2. Delete revoked tokens older than the retention window
        Instant revokedThreshold = now.minus(revokedRetentionDays, ChronoUnit.DAYS);
        int revokedDeleted = refreshTokenRepository.deleteAllRevokedBefore(revokedThreshold);

        log.info("Token cleanup: deleted {} expired token(s), {} old revoked token(s)",
                expiredDeleted, revokedDeleted);
    }
}