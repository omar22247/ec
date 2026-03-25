//package E_commerce.com.SecureEcommerceApplication.mapper;
//
//import E_commerce.com.SecureEcommerceApplication.dto.request.RegisterRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.response.UserResponse;
//import E_commerce.com.SecureEcommerceApplication.entity.User;
//import org.mapstruct.*;
//
//@Mapper(componentModel = "spring")
//public interface UserMapper {
//
//    // User → UserResponse
//    // passwordHash is never exposed — not in UserResponse so MapStruct ignores it automatically
//    UserResponse toResponse(User user);
//
//    // RegisterRequest → User
//    @Mapping(target = "id",            ignore = true)
//    @Mapping(target = "passwordHash",  ignore = true)   // hashed in AuthService
//    @Mapping(target = "role",          ignore = true)   // defaults to USER
//    @Mapping(target = "emailVerified", ignore = true)   // defaults to false
//    @Mapping(target = "createdAt",     ignore = true)   // set by @PrePersist
//    @Mapping(target = "deletedAt",     ignore = true)
//    @Mapping(target = "oauthAccounts", ignore = true)
//    @Mapping(target = "addresses",     ignore = true)
//    @Mapping(target = "cart",          ignore = true)
//    @Mapping(target = "orders",        ignore = true)
//    @Mapping(target = "reviews",       ignore = true)
//    @Mapping(target = "wishlist",      ignore = true)
//    User toEntity(RegisterRequest request);
//}
