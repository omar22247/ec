package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.dto.response.ProductDetailResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.ProductResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ════════════════════════════════════════════════════════
    //  LIST — ProductResponse (lightweight)
    // ════════════════════════════════════════════════════════

    // GET /api/v1/products
    @Query(
            value = """
            SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.ProductResponse(
                p.id,
                p.name,
                p.basePrice,
                p.imageUrl,
                p.active,
                c.id,
                c.name,
                COALESCE(i.quantity, 0),
                (COALESCE(i.quantity, 0) > 0),
                p.averageRating,
                p.reviewCount
            )
            FROM Product p
            JOIN p.category c
            LEFT JOIN p.inventory i
            WHERE p.active = true
            AND p.deletedAt IS NULL
            """,
            countQuery = """
            SELECT COUNT(p)
            FROM Product p
            WHERE p.active = true
            AND p.deletedAt IS NULL
            """
    )
    Page<ProductResponse> findAllActiveProducts(Pageable pageable);

    // GET /api/v1/products/category/{id}
    @Query(
            value = """
            SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.ProductResponse(
                p.id,
                p.name,
                p.basePrice,
                p.imageUrl,
                p.active,
                c.id,
                c.name,
                COALESCE(i.quantity, 0),
                (COALESCE(i.quantity, 0) > 0),
                p.averageRating,
                p.reviewCount
            )
            FROM Product p
            JOIN p.category c
            LEFT JOIN p.inventory i
            WHERE p.active = true
            AND p.deletedAt IS NULL
            AND c.id = :categoryId
            """,
            countQuery = """
            SELECT COUNT(p)
            FROM Product p
            WHERE p.active = true
            AND p.deletedAt IS NULL
            AND p.category.id = :categoryId
            """
    )
    Page<ProductResponse> findAllByCategoryId(
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    // GET /api/v1/products/search?keyword=
    @Query(
            value = """
            SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.ProductResponse(
                p.id,
                p.name,
                p.basePrice,
                p.imageUrl,
                p.active,
                c.id,
                c.name,
                COALESCE(i.quantity, 0),
                (COALESCE(i.quantity, 0) > 0),
                p.averageRating,
                p.reviewCount
            )
            FROM Product p
            JOIN p.category c
            LEFT JOIN p.inventory i
            WHERE p.active = true
            AND p.deletedAt IS NULL
            AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """,
            countQuery = """
            SELECT COUNT(p)
            FROM Product p
            WHERE p.active = true
            AND p.deletedAt IS NULL
            AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """
    )
    Page<ProductResponse> searchByName(
            @Param("keyword") String keyword,
            Pageable pageable);

    // GET /api/v1/products/filter?minPrice=&maxPrice=
    @Query(
            value = """
            SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.ProductResponse(
                p.id,
                p.name,
                p.basePrice,
                p.imageUrl,
                p.active,
                c.id,
                c.name,
                COALESCE(i.quantity, 0),
                (COALESCE(i.quantity, 0) > 0),
                p.averageRating,
                p.reviewCount
            )
            FROM Product p
            JOIN p.category c
            LEFT JOIN p.inventory i
            WHERE p.active = true
            AND p.deletedAt IS NULL
            AND p.basePrice BETWEEN :min AND :max
            """,
            countQuery = """
            SELECT COUNT(p)
            FROM Product p
            WHERE p.active = true
            AND p.deletedAt IS NULL
            AND p.basePrice BETWEEN :min AND :max
            """
    )
    Page<ProductResponse> findByPriceRange(
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max,
            Pageable pageable);

    // Admin — all products (active + inactive, not deleted)
    @Query(
            value = """
            SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.ProductResponse(
                p.id,
                p.name,
                p.basePrice,
                p.imageUrl,
                p.active,
                c.id,
                c.name,
                COALESCE(i.quantity, 0),
                (COALESCE(i.quantity, 0) > 0),
                p.averageRating,
                p.reviewCount
            )
            FROM Product p
            JOIN p.category c
            LEFT JOIN p.inventory i
            WHERE p.deletedAt IS NULL
            """,
            countQuery = """
            SELECT COUNT(p)
            FROM Product p
            WHERE p.deletedAt IS NULL
            """
    )
    Page<ProductResponse> findAllProducts(Pageable pageable);

    // ════════════════════════════════════════════════════════
    //  DETAIL — ProductDetailResponse (full data)
    // ════════════════════════════════════════════════════════

    // GET /api/v1/products/{id}
    @Query("""
        SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.ProductDetailResponse(
            p.id,
            p.name,
            p.description,
            p.basePrice,
            p.imageUrl,
            p.active,
            c.id,
            c.name,
            COALESCE(i.quantity, 0),
            (COALESCE(i.quantity, 0) > 0),
            (COALESCE(i.quantity, 0) <= COALESCE(i.lowStockThreshold, 0) AND COALESCE(i.quantity, 0) > 0),
            p.averageRating,
            p.reviewCount
        )
        FROM Product p
        JOIN p.category c
        LEFT JOIN p.inventory i
        WHERE p.id = :id
        AND p.active = true
        AND p.deletedAt IS NULL
    """)
    Optional<ProductDetailResponse> findProductDetailById(@Param("id") Long id);

    // ════════════════════════════════════════════════════════
    //  Entity queries — WRITE operations only
    // ════════════════════════════════════════════════════════

    // used by CartService — loads product + inventory in ONE JOIN
    // inventory needed for in-memory stock validation
    // no need for separate InventoryRepository call
    @EntityGraph(attributePaths = {"inventory"})
    Optional<Product> findWithInventoryByIdAndActiveTrue(Long id);

    Optional<Product> findByIdAndActiveTrue(Long id);

    Optional<Product> findById(Long id);

    boolean existsByCategoryId(Long categoryId);
}
