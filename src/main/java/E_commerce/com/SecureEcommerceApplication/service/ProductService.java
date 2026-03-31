package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.ProductRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.PageResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ProductDetailResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ProductResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Product;

import java.math.BigDecimal;

public interface ProductService {

    // ── Guest & User ────────────────────────────────────────
    Product findWithInventoryByIdAndActiveTrue(Long id);
    // returns lightweight ProductResponse (list card)
    PageResponse<ProductResponse> getAllProducts(int page, int size, String sort);

    // returns full ProductDetailResponse (detail page)
    ProductDetailResponse getProductById(Long id);

    PageResponse<ProductResponse> getProductsByCategory(Long categoryId, int page, int size, String sort);

    PageResponse<ProductResponse> searchProducts(String keyword, int page, int size);

    PageResponse<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size);

    // ── Admin ───────────────────────────────────────────────
    PageResponse<ProductResponse> getAllAdminProducts(int page, int size, String sort);

    ProductDetailResponse createProduct(ProductRequest request);

    ProductDetailResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    ProductDetailResponse toggleProductStatus(Long id);

}