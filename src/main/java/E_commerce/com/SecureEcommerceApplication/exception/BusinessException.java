package E_commerce.com.SecureEcommerceApplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// business logic violations — 400 Bad Request
// e.g. "Cannot delete category that has products"
// e.g. "Coupon has expired"
// e.g. "Not enough stock"
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}