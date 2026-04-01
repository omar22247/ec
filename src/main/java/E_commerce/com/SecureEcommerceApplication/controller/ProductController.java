package E_commerce.com.SecureEcommerceApplication.controller;

import E_commerce.com.SecureEcommerceApplication.dto.request.ProductRequest;
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

    // ════════════════════════════════════════════════════════════
    //  PUBLIC
    // ════════════════════════════════════════════════════════════

    @GetMapping
    @Operation(summary = "Get all products",
            description = "Retrieves a paginated list of all products, optionally sorted")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Products retrieved successfully")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @Parameter(description = "Page number (0-based)")  @RequestParam(defaultValue = "0")   int    page,
            @Parameter(description = "Items per page")         @RequestParam(defaultValue = "10")  int    size,
            @Parameter(description = "Sort field (e.g. basePrice,asc)") @RequestParam(defaultValue = "id") String sort) {

        return ResponseEntity.ok(
                ApiResponse.success(productService.getAllProducts(page, size, sort))
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID",
            description = "Retrieves detailed information for a specific product")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Product details retrieved")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
            description = "Product not found")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(
            @Parameter(description = "ID of the product") @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(productService.getProductById(id))
        );
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category",
            description = "Retrieves a paginated list of products within a specific category")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Products retrieved successfully")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "ID of the category") @PathVariable Long categoryId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")  int    page,
            @Parameter(description = "Items per page")        @RequestParam(defaultValue = "10") int    size,
            @Parameter(description = "Sort field")            @RequestParam(defaultValue = "id") String sort) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.getProductsByCategory(categoryId, page, size, sort))
        );
    }

    @GetMapping("/search")
    @Operation(summary = "Search products",
            description = "Searches for products based on a keyword")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Search results retrieved")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(
            @Parameter(description = "Keyword to search for") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Items per page")        @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(productService.searchProducts(keyword, page, size))
        );
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter products by price",
            description = "Retrieves products within a specific price range")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Filtered products retrieved")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam BigDecimal maxPrice,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Items per page")        @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.getProductsByPriceRange(minPrice, maxPrice, page, size))
        );
    }

    // ════════════════════════════════════════════════════════════
    //  ADMIN
    // ════════════════════════════════════════════════════════════

    @GetMapping("/admin/all")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all products (admin)", description = "Retrieves all products including inactive ones. Requires ADMIN privileges.")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllAdminProducts(
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "15") int    size,
            @RequestParam(defaultValue = "id") String sort) {

        PageResponse<ProductResponse> data = productService.getAllAdminProducts(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create product",
            description = "Creates a new product. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
            description = "Product created successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully",
                        productService.createProduct(request)));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update product",
            description = "Updates an existing product. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Product updated successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(
            @Parameter(description = "ID of the product to update") @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully",
                        productService.updateProduct(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete product",
            description = "Permanently deletes a product. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Product deleted successfully")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "ID of the product to delete") @PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.ok(
                ApiResponse.success("Product deleted successfully")
        );
    }

    @PatchMapping("/{id}/toggle")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle product status",
            description = "Activates or deactivates a product. Requires ADMIN privileges.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Product status updated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> toggleProductStatus(
            @Parameter(description = "ID of the product to toggle") @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success("Product status updated",
                        productService.toggleProductStatus(id))
        );
    }
}