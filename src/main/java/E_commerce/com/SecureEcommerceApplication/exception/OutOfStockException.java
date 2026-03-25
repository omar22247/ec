package E_commerce.com.SecureEcommerceApplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// لما المستخدم يحاول يشتري منتج خلص stock
// → 400 Bad Request
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String productName) {
        super(String.format("Product '%s' is out of stock", productName));
    }

    public OutOfStockException(String productName, int requested, int available) {
        super(String.format(
            "Not enough stock for '%s'. Requested: %d, Available: %d",
            productName, requested, available
        ));
    }
}
