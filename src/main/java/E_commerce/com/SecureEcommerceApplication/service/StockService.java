package E_commerce.com.SecureEcommerceApplication.service;

public interface StockService {

    // validates product exists, is active, and has enough stock
    // throws the appropriate exception if any check fails
    // used by CartService and OrderService
    void validateStock(Long productId, int quantityRequested);
}
