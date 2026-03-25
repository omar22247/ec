//package E_commerce.com.SecureEcommerceApplication.mapper;
//
//import E_commerce.com.SecureEcommerceApplication.dto.response.*;
//import E_commerce.com.SecureEcommerceApplication.entity.Wishlist;
//import org.mapstruct.*;
//
//@Mapper(componentModel = "spring")
//public interface WishlistMapper {
//
//    // Wishlist → WishlistResponse
//    @Mapping(target = "productId",    source = "product.id")
//    @Mapping(target = "productName",  source = "product.name")
//    @Mapping(target = "productImage", source = "product.imageUrl")
//    @Mapping(target = "productPrice", source = "product.basePrice")
//    @Mapping(target = "inStock",      ignore = true)   // checked via Inventory in service
//    WishlistItemResponse toResponse(Wishlist wishlist);
//}
