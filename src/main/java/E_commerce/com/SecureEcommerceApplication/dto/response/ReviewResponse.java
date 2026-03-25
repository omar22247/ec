package E_commerce.com.SecureEcommerceApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long          id;
    private Long          userId;
    private String        userName;
    private Long          productId;
    private int           rating;
    private String        comment;
    private LocalDateTime createdAt;
}