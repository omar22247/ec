package E_commerce.com.SecureEcommerceApplication.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class TokenHashUtil {

    public static String generateRawToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean matches(String rawToken, String storedHash) {
        String computed = hash(rawToken);
        if (computed.length() != storedHash.length()) return false;

        int result = 0;
        for (int i = 0; i < computed.length(); i++) {
            result |= computed.charAt(i) ^ storedHash.charAt(i);
        }
        return result == 0;
    }
}