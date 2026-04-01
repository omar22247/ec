package E_commerce.com.SecureEcommerceApplication.dto.response;

import E_commerce.com.SecureEcommerceApplication.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Full detail — used for single order endpoint only
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long                     id;
    private OrderStatus              status;

    // address snapshot at time of order
    private AddressResponse          address;

    // coupon applied (null if none)
    private String                   couponCode;

    // pricing breakdown
    private BigDecimal               originalPrice;
    private BigDecimal               discountAmount;
    private BigDecimal               totalPrice;

    private LocalDateTime            createdAt;

    // full item list
    private List<OrderItemResponse>  items;

    // shipment info
    private ShipmentResponse         shipment;
}