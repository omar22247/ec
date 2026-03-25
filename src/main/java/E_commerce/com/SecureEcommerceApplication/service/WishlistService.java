package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.response.WishlistItemResponse;

import java.util.List;

public interface WishlistService {

    // GET /api/v1/wishlist
    List<WishlistItemResponse> getWishlist(Long userId);

    // POST /api/v1/wishlist/{productId}
    WishlistItemResponse addToWishlist(Long userId, Long productId);

    // DELETE /api/v1/wishlist/{productId}
    void removeFromWishlist(Long userId, Long productId);

    // DELETE /api/v1/wishlist
    void clearWishlist(Long userId);
}