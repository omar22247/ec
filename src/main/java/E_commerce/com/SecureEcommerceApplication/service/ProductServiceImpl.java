package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.ProductRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.PageResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ProductDetailResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ProductResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Category;
import E_commerce.com.SecureEcommerceApplication.entity.Inventory;
import E_commerce.com.SecureEcommerceApplication.entity.Product;
import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.mapper.ProductMapper;
import E_commerce.com.SecureEcommerceApplication.repository.CategoryRepository;
import E_commerce.com.SecureEcommerceApplication.repository.InventoryRepository;
import E_commerce.com.SecureEcommerceApplication.repository.ProductRepository;
import E_commerce.com.SecureEcommerceApplication.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository   productRepository;
    private final CategoryRepository  categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductMapper       productMapper;

    // ════════════════════════════════════════════════════════
    //  READ — DTO straight from DB (zero N+1)
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(int page, int size, String sort) {
        return PageResponse.of(
                productRepository.findAllActiveProducts(buildPageable(page, size, sort))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllAdminProducts(int page, int size, String sort) {
        return PageResponse.of(
                productRepository.findAllProducts(buildPageable(page, size, sort))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductById(Long id) {
        return findDetailOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProductsByCategory(
            Long categoryId, int page, int size, String sort) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        return PageResponse.of(
                productRepository.findAllByCategoryId(categoryId, buildPageable(page, size, sort))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> searchProducts(String keyword, int page, int size) {
        return PageResponse.of(
                productRepository.searchByName(keyword, buildPageable(page, size, "name"))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProductsByPriceRange(
            BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessException("Min price cannot be greater than max price");
        }
        return PageResponse.of(
                productRepository.findByPriceRange(minPrice, maxPrice, buildPageable(page, size, "basePrice"))
        );
    }

    // ════════════════════════════════════════════════════════
    //  WRITE — entity for mutations, DTO for response
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ProductDetailResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", "id", request.getCategoryId()));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product = productRepository.save(product);

        inventoryRepository.save(Inventory.builder()
                .product(product)
                .quantity(request.getStock())
                .lowStockThreshold(request.getLowStockThreshold())
                .build());

        log.info("Product created: id={}, name={}", product.getId(), product.getName());

        // after save → fetch the full DTO
        // this should always succeed since we just saved it
        return findDetailOrThrow(product.getId());
    }

    @Override
    @Transactional
    public ProductDetailResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!request.getCategoryId().equals(product.getCategory().getId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }

        productMapper.updateEntity(request, product);

        Inventory inventory = inventoryRepository.findByProductId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory", "productId", id));
        inventory.setQuantity(request.getStock());
        inventory.setLowStockThreshold(request.getLowStockThreshold());
        inventoryRepository.save(inventory);

        productRepository.save(product);
        log.info("Product updated: id={}", id);

        return findDetailOrThrow(id);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setDeletedAt(LocalDateTime.now());
        product.setActive(false);
        productRepository.save(product);
        log.info("Product soft-deleted: id={}", id);
    }

    @Override
    @Transactional
    public ProductDetailResponse toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setActive(!product.isActive());
        productRepository.save(product);
        log.info("Product toggled: id={}, active={}", id, product.isActive());

        // after toggle — if product was deactivated, findProductDetailById
        // won't find it (WHERE active = true)
        // so we use findById (admin can see inactive products)
        return productRepository.findProductDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    // ════════════════════════════════════════════════════════
    //  Private helpers
    // ════════════════════════════════════════════════════════

    // centralizes the Optional handling for findProductDetailById
    // so every caller gets a clean ResourceNotFoundException
    // instead of a raw NoSuchElementException
    private ProductDetailResponse findDetailOrThrow(Long id) {
        return productRepository.findProductDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }


    private Pageable buildPageable(int page, int size, String sort) {
        size = Math.min(size, 50);
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }
        String[]       parts = sort.split(",");
        String         field = parts[0].trim();
        Sort.Direction dir   = (parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(dir, field));
    }

    @Override
    @Transactional(readOnly = true)
    public Product findWithInventoryByIdAndActiveTrue(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

}