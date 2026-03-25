package E_commerce.com.SecureEcommerceApplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// للـ business logic errors — 400 Bad Request
// مثلاً: "Cannot delete category that has products"
// مثلاً: "Coupon has expired"
// مثلاً: "Not enough stock"
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
