package E_commerce.com.SecureEcommerceApplication.mapper;

import E_commerce.com.SecureEcommerceApplication.dto.request.ProductRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.*;
import E_commerce.com.SecureEcommerceApplication.entity.Product;
import org.mapstruct.*;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {

    // ── Product → ProductResponse (list) ───────────────────
    // averageRating and reviewCount come directly from the entity now
    // no more ignore — they are real columns in the products table
    @Mapping(target = "categoryId",   source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "stock",        ignore = true)   // comes from Inventory
    @Mapping(target = "inStock",      ignore = true)   // comes from Inventory
    ProductResponse toResponse(Product product);

    // ── Product → ProductDetailResponse (detail page) ──────
    // same as above but also maps description and lowStock
    @Mapping(target = "categoryId",   source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "stock",        ignore = true)   // comes from Inventory
    @Mapping(target = "inStock",      ignore = true)   // comes from Inventory
    @Mapping(target = "lowStock",     ignore = true)   // comes from Inventory
    ProductDetailResponse toDetailResponse(Product product);

    // ── ProductRequest → Product (create) ──────────────────
    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "category",      ignore = true)   // fetched from DB in service
    @Mapping(target = "inventory",     ignore = true)   // created separately
    @Mapping(target = "reviews",       ignore = true)
    @Mapping(target = "wishlistItems", ignore = true)
    @Mapping(target = "active",        constant = "true")
    @Mapping(target = "deletedAt",     ignore = true)
    @Mapping(target = "averageRating", ignore = true)   // defaults to 0.0
    @Mapping(target = "reviewCount",   ignore = true)   // defaults to 0
    Product toEntity(ProductRequest request);

    // ── ProductRequest → Product (update) ──────────────────
    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "category",      ignore = true)
    @Mapping(target = "inventory",     ignore = true)
    @Mapping(target = "reviews",       ignore = true)
    @Mapping(target = "wishlistItems", ignore = true)
    @Mapping(target = "active",        ignore = true)
    @Mapping(target = "deletedAt",     ignore = true)
    @Mapping(target = "averageRating", ignore = true)   // managed by ReviewService
    @Mapping(target = "reviewCount",   ignore = true)   // managed by ReviewService
    void updateEntity(ProductRequest request, @MappingTarget Product product);
}