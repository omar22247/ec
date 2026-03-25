package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.CartItemRequest;
import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateCartItemRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.CartResponse;


public interface CartService {

    // GET /api/v1/cart
    CartResponse getCart(Long userId);

    // POST /api/v1/cart/items
    CartResponse addItem(Long userId, CartItemRequest request);

    // PUT /api/v1/cart/items/{itemId}
    CartResponse updateItem(Long userId, Long itemId, UpdateCartItemRequest request);

    // DELETE /api/v1/cart/items/{itemId}
    CartResponse removeItem(Long userId, Long itemId);

    // DELETE /api/v1/cart
    void clearCart(Long userId);
}
