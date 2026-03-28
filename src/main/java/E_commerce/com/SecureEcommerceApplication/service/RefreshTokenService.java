package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.response.RefreshTokenResponse;
import E_commerce.com.SecureEcommerceApplication.entity.RefreshToken;
import E_commerce.com.SecureEcommerceApplication.entity.User;

public interface RefreshTokenService {


    String createRefreshToken(User user, String createdByIp);


    RefreshTokenResponse rotateRefreshToken(String rawToken, String clientIp);

    void revokeToken(String rawToken, String reason);


    void revokeAllTokensForUser(User user, String reason);

    RefreshToken validateAndGet(String rawToken);
}