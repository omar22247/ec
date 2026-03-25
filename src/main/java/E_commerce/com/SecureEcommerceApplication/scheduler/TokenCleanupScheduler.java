package E_commerce.com.SecureEcommerceApplication.scheduler;

import E_commerce.com.SecureEcommerceApplication.repository.PasswordResetTokenRepository;
import E_commerce.com.SecureEcommerceApplication.security.RateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final PasswordResetTokenRepository tokenRepository;
    private final RateLimiterService rateLimiterService; // ✅ أضفه

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
}