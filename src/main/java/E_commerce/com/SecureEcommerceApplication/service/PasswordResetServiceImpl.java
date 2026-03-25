package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.entity.PasswordResetToken;
import E_commerce.com.SecureEcommerceApplication.entity.User;
import E_commerce.com.SecureEcommerceApplication.repository.PasswordResetTokenRepository;
import E_commerce.com.SecureEcommerceApplication.repository.UserRepository;
import E_commerce.com.SecureEcommerceApplication.util.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void initiateReset(String email) {
        // ✅ Never reveal if email exists
        userRepository.findByEmail(email).ifPresent(user -> {
            tokenRepository.deleteByUser(user);

            String rawToken = TokenHashUtil.generateRawToken();
            String hashedToken = TokenHashUtil.hash(rawToken);

            tokenRepository.save(new PasswordResetToken(hashedToken, user));

            emailService.sendResetEmail(user.getEmail(), user.getName(), rawToken);

            log.info("Password reset initiated for user id: {}", user.getId());
        });
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String hashedToken = TokenHashUtil.hash(rawToken);

        PasswordResetToken resetToken = tokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash((passwordEncoder.encode(newPassword)));
        userRepository.save(user);

        tokenRepository.delete(resetToken); // one-time use
        log.info("Password reset successful for user id: {}", user.getId());
    }
}