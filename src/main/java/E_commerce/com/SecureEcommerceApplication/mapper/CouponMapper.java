package E_commerce.com.SecureEcommerceApplication.mapper;

import E_commerce.com.SecureEcommerceApplication.dto.request.CouponRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.CouponResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Coupon;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CouponMapper {

    // Coupon → CouponResponse
    @Mapping(target = "valid", ignore = true)   // calculated via coupon.isValid() in service
    CouponResponse toResponse(Coupon coupon);

    // CouponRequest → Coupon (create)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "usedCount", constant = "0")
    @Mapping(target = "active",    constant = "true")
    @Mapping(target = "deletedAt", ignore = true)
    Coupon toEntity(CouponRequest request);

    // CouponRequest → Coupon (update)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "active",    ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(CouponRequest request, @MappingTarget Coupon coupon);
}
