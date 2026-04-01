package E_commerce.com.SecureEcommerceApplication.mapper;

import E_commerce.com.SecureEcommerceApplication.dto.request.AddressRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.AddressResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Address;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AddressMapper {

    // Address → AddressResponse
    // "default" is the MapStruct property name derived from the boolean getter isDefault()
    @Mapping(source = "default", target = "isDefault")
    AddressResponse toResponse(Address address);

    // AddressRequest → Address (create)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "user",      ignore = true)   // set in service
    @Mapping(target = "deletedAt", ignore = true)
    Address toEntity(AddressRequest request);

    // AddressRequest → Address (update)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "user",      ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(AddressRequest request, @MappingTarget Address address);
}
