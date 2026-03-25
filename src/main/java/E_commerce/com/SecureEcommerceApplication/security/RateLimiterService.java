package E_commerce.com.SecureEcommerceApplication.security;

import io.github.bucket4j.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // ─────────────────────────────────────────
    // Presets
    // ─────────────────────────────────────────

    // Auth: login / register → 5 attempts per minute
    public Bucket authBucket(String key) {
        return buckets.computeIfAbsent("auth:" + key, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.builder()
                                .capacity(5)
                                .refillIntervally(5, Duration.ofMinutes(1))
                                .build())
                        .build());
    }

    // Password reset → 3 attempts per hour
    public Bucket resetPasswordBucket(String key) {
        return buckets.computeIfAbsent("reset:" + key, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.builder()
                                .capacity(3)
                                .refillIntervally(3, Duration.ofHours(1))
                                .build())
                        .build());
    }

    // Orders → 10 per minute
    public Bucket orderBucket(String key) {
        return buckets.computeIfAbsent("order:" + key, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.builder()
                                .capacity(10)
                                .refillIntervally(10, Duration.ofMinutes(1))
                                .build())
                        .build());
    }

    // General API → 60 per minute
    public Bucket generalBucket(String key) {
        return buckets.computeIfAbsent("general:" + key, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.builder()
                                .capacity(60)
                                .refillGreedy(60, Duration.ofMinutes(1))
                                .build())
                        .build());
    }

    // ─────────────────────────────────────────
    // Try consume — returns false if rate limited
    // ─────────────────────────────────────────
    public boolean isAllowed(Bucket bucket) {
        return bucket.tryConsume(1);
    }

    // ─────────────────────────────────────────
    // Cleanup — call from scheduler
    // ─────────────────────────────────────────
    public void clearBuckets() {
        buckets.clear();
    }
}