package E_commerce.com.SecureEcommerceApplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// لما حد يحاول يسجّل بإيميل موجود
// أو يعمل coupon بكود موجود
// → 409 Conflict
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    // مثلاً: new DuplicateResourceException("User", "email", "test@test.com")
    // → "User already exists with email: test@test.com"
    public DuplicateResourceException(String resource, String field, Object value) {
        super(String.format("%s already exists with %s: %s", resource, field, value));
    }
}
