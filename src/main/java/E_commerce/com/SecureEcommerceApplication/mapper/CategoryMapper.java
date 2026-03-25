package E_commerce.com.SecureEcommerceApplication.mapper;

import E_commerce.com.SecureEcommerceApplication.dto.request.CategoryRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.CategoryResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Category;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoryMapper {

    // Category → CategoryResponse
    @Mapping(target = "parentId",      source = "parent.id")
    @Mapping(target = "parentName",    source = "parent.name")
    @Mapping(target = "subCategories", ignore = true)   // mapped manually to avoid deep recursion
    CategoryResponse toResponse(Category category);

    // CategoryRequest → Category (create)
    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "parent",        ignore = true)   // fetched from DB in service
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "products",      ignore = true)
    @Mapping(target = "deletedAt",     ignore = true)
    Category toEntity(CategoryRequest request);

    // CategoryRequest → Category (update)
    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "parent",        ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "products",      ignore = true)
    @Mapping(target = "deletedAt",     ignore = true)
    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
