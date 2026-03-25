package E_commerce.com.SecureEcommerceApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long    id;
    private String  fullName;
    private String  phone;
    private String  street;
    private String  city;
    private String  country;
    private String  zipCode;
    private boolean isDefault;
}