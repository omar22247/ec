package E_commerce.com.SecureEcommerceApplication.service;


import E_commerce.com.SecureEcommerceApplication.dto.request.CategoryRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    // ── Public ──────────────────────────────────────────────

    // GET /api/v1/categories
    // returns only root categories with their sub-categories
    List<CategoryResponse> getAllCategories();

    // GET /api/v1/categories/{id}
    CategoryResponse getCategoryById(Long id);

    // GET /api/v1/categories/{id}/subcategories
    List<CategoryResponse> getSubCategories(Long parentId);

    // ── Admin ───────────────────────────────────────────────

    // POST /api/v1/categories
    CategoryResponse createCategory(CategoryRequest request);

    // PUT /api/v1/categories/{id}
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    // DELETE /api/v1/categories/{id}
    void deleteCategory(Long id);
}

