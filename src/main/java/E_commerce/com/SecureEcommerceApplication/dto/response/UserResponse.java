package E_commerce.com.SecureEcommerceApplication.dto.response;

import E_commerce.com.SecureEcommerceApplication.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long          id;
    private String        name;
    private String        email;
    private UserRole      role;
    private boolean       emailVerified;
    private LocalDateTime createdAt;

    // passwordHash is NEVER included here
}