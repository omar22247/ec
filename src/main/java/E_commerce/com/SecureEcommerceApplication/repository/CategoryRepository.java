package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.dto.response.CategoryResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // ── READ — entity with EntityGraph ─────────────────────

    // ONE query — loads root categories + their subCategories via LEFT JOIN
    // @EntityGraph is perfect here because:
    // 1. categories are small (50-100 rows max)
    // 2. tree structure maps naturally to entity relations
    // 3. subCategories is a direct @OneToMany relation
    @EntityGraph(attributePaths = {"subCategories"})
    List<Category> findAllByParentIsNull();

    // single category with its sub-categories
    @EntityGraph(attributePaths = {"subCategories"})
    Optional<Category> findById(Long id);

    // sub-categories only — no EntityGraph needed (parent always exists)
    @Query("""
        SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.CategoryResponse(
            c.id,
            c.name,
            c.slug,
            c.parent.id,
            c.parent.name
        )
        FROM Category c
        WHERE c.parent.id = :parentId
        AND c.deletedAt IS NULL
        ORDER BY c.name ASC
    """)
    List<CategoryResponse> findSubCategoriesById(@Param("parentId") Long parentId);

    // ── WRITE — entity operations ───────────────────────────
    boolean existsBySlug(String slug);
    boolean existsByName(String name);
}