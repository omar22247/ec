package E_commerce.com.SecureEcommerceApplication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String email;
}
