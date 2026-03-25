package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.CategoryRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.CategoryResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Category;
import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
import E_commerce.com.SecureEcommerceApplication.exception.DuplicateResourceException;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.mapper.CategoryMapper;
import E_commerce.com.SecureEcommerceApplication.repository.CategoryRepository;
import E_commerce.com.SecureEcommerceApplication.repository.ProductRepository;
import E_commerce.com.SecureEcommerceApplication.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository  productRepository;
    private final CategoryMapper     categoryMapper;

    // ════════════════════════════════════════════════════════
    //  READ
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        // ONE query — @EntityGraph loads root categories + subCategories via LEFT JOIN
        return categoryRepository.findAllByParentIsNull()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        // ONE query — @EntityGraph loads category + subCategories
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubCategories(Long parentId) {

        if (!categoryRepository.existsById(parentId)) {
            throw new ResourceNotFoundException("Category", "id", parentId);
        }

        return categoryRepository.findSubCategoriesById(parentId);
    }

    // ════════════════════════════════════════════════════════
    //  WRITE
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Category", "slug", request.getSlug());
        }
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category", "name", request.getName());
        }

        Category category = categoryMapper.toEntity(request);

        if (request.getParentId() != null) {
            Category parent = findEntityById(request.getParentId());
            category.setParent(parent);
        }

        category = categoryRepository.save(category);
        log.info("Category created: id={}, name={}", category.getId(), category.getName());

        return toResponse(categoryRepository.findById(category.getId())
                .orElseThrow());
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {

        Category category = findEntityById(id);

        if (!category.getSlug().equals(request.getSlug())
                && categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Category", "slug", request.getSlug());
        }
        if (!category.getName().equals(request.getName())
                && categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category", "name", request.getName());
        }
        if (request.getParentId() != null && request.getParentId().equals(id)) {
            throw new BusinessException("A category cannot be its own parent");
        }

        categoryMapper.updateEntity(request, category);

        if (request.getParentId() != null) {
            category.setParent(findEntityById(request.getParentId()));
        } else {
            category.setParent(null);
        }

        categoryRepository.save(category);
        log.info("Category updated: id={}", id);

        return toResponse(categoryRepository.findById(id).orElseThrow());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {

        Category category = findEntityById(id);

        if (productRepository.existsByCategoryId(id)) {
            throw new BusinessException(
                    "Cannot delete category '" + category.getName() +
                            "' — it still has products. Move or delete them first."
            );
        }

        if (!category.getSubCategories().isEmpty()) {
            throw new BusinessException(
                    "Cannot delete category '" + category.getName() +
                            "' — it still has sub-categories. Delete them first."
            );
        }

        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
        log.info("Category soft-deleted: id={}", id);
    }

    // ════════════════════════════════════════════════════════
    //  Private helpers
    // ════════════════════════════════════════════════════════

    private Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    // entity → DTO
    // subCategories mapped one level deep — avoids infinite recursion
    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parentId(category.getParent() != null
                        ? category.getParent().getId() : null)
                .parentName(category.getParent() != null
                        ? category.getParent().getName() : null)
                // subCategories loaded by @EntityGraph — map one level deep
                .subCategories(category.getSubCategories().stream()
                        .map(sub -> CategoryResponse.builder()
                                .id(sub.getId())
                                .name(sub.getName())
                                .slug(sub.getSlug())
                                .parentId(category.getId())
                                .parentName(category.getName())
                                .subCategories(List.of())  // one level only
                                .build())
                        .toList())
                .build();
    }
}