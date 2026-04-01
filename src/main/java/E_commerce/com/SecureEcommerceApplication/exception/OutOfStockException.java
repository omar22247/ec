package E_commerce.com.SecureEcommerceApplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// when a user tries to buy a product with insufficient stock
// → 400 Bad Request
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OutOfStockException extends RuntimeException {

    // product is completely out of stock
    public OutOfStockException(String productName) {
        super(String.format("Product '%s' is out of stock", productName));
    }

    // product has stock but not enough for the requested quantity
    public OutOfStockException(String productName, int requested, int available) {
        super(String.format(
                "Not enough stock for '%s'. Requested: %d, Available: %d",
                productName, requested, available
        ));
    }
}