package E_commerce.com.SecureEcommerceApplication.mapper;

import E_commerce.com.SecureEcommerceApplication.dto.response.CartItemResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.CartResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Cart;
import E_commerce.com.SecureEcommerceApplication.entity.CartItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartMapper {

    // CartItem → CartItemResponse
    @Mapping(target = "productId",    source = "product.id")
    @Mapping(target = "productName",  source = "product.name")
    @Mapping(target = "productImage", source = "product.imageUrl")
    @Mapping(target = "unitPrice",    source = "product.basePrice")
    @Mapping(target = "subtotal",     ignore = true)   // quantity * unitPrice — calculated in service
    CartItemResponse toItemResponse(CartItem cartItem);

    // Cart → CartResponse
    @Mapping(target = "items",      ignore = true)   // mapped via toItemResponse()
    @Mapping(target = "totalItems", ignore = true)   // calculated in service
    @Mapping(target = "totalPrice", ignore = true)   // calculated in service
    CartResponse toResponse(Cart cart);
}
