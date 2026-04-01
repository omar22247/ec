package E_commerce.com.SecureEcommerceApplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// when someone tries to register with an existing email
// or create a coupon with an existing code
// → 409 Conflict
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    // e.g. new DuplicateResourceException("User", "email", "test@test.com")
    // → "User already exists with email: test@test.com"
    public DuplicateResourceException(String resource, String field, Object value) {
        super(String.format("%s already exists with %s: %s", resource, field, value));
    }
}