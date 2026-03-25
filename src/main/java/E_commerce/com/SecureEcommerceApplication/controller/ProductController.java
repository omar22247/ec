package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.request.ProductRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.PageResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.*;
import E_commerce.com.SecureEcommerceApplication.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "APIs for managing the product catalog")
public class ProductController {

    private final ProductService productService;

    // ════════════════════════════════════════════════════════
    //  PUBLIC — No login required
    // ════════════════════════════════════════════════════════

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all products, optionally sorted")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")   int    page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10")  int    size,
            @Parameter(description = "Sorting criteria (e.g., basePrice,asc)") @RequestParam(defaultValue = "id")  String sort) {

        PageResponse<ProductResponse> data = productService.getAllProducts(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves detailed information for a specific product")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product details retrieved")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(
            @Parameter(description = "ID of the product") @PathVariable Long id) {

        ProductDetailResponse data = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieves a paginated list of products within a specific category")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category products retrieved")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "ID of the category") @PathVariable Long categoryId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")  int    page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int    size,
            @Parameter(description = "Sorting criteria") @RequestParam(defaultValue = "id") String sort) {

        PageResponse<ProductResponse> data =
                productService.getProductsByCategory(categoryId, page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Searches for products based on a keyword")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results retrieved")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(
            @Parameter(description = "Keyword to search for") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {

        PageResponse<ProductResponse> data =
                productService.searchProducts(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter products by price", description = "Retrieves products falling within a specific price range")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Filtered products retrieved")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam BigDecimal maxPrice,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {

        PageResponse<ProductResponse> data =
                productService.getProductsByPriceRange(minPrice, maxPrice, page, size);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // ════════════════════════════════════════════════════════
    //  ADMIN — Requires ADMIN role
    // ════════════════════════════════════════════════════════

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create product", description = "Creates a new product in the catalog. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product created successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductDetailResponse data = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)           // 201 Created
                .body(ApiResponse.success("Product created successfully", data));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update product", description = "Updates details of an existing product. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product updated successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(
            @Parameter(description = "ID of the product to update") @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        ProductDetailResponse data = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", data));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete product", description = "Permanently deletes a product. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product deleted successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "ID of the product to delete") @PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

    @PatchMapping("/{id}/toggle")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle product status", description = "Activates or deactivates a product without deleting it. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product status updated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> toggleProductStatus(
            @Parameter(description = "ID of the product to toggle") @PathVariable Long id) {

        ProductDetailResponse data = productService.toggleProductStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Product status updated", data));
    }
}