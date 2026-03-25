package E_commerce.com.SecureEcommerceApplication.mapper;

import E_commerce.com.SecureEcommerceApplication.dto.request.ReviewRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.ReviewResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Review;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReviewMapper {

    // Review → ReviewResponse
    @Mapping(target = "userId",    source = "user.id")
    @Mapping(target = "userName",  source = "user.name")
    @Mapping(target = "productId", source = "product.id")
    ReviewResponse toResponse(Review review);

    // ReviewRequest → Review (create)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "user",      ignore = true)   // set from SecurityContext in service
    @Mapping(target = "product",   ignore = true)   // fetched from DB in service
    @Mapping(target = "createdAt", ignore = true)   // set by @PrePersist
    @Mapping(target = "deletedAt", ignore = true)
    Review toEntity(ReviewRequest request);
}
