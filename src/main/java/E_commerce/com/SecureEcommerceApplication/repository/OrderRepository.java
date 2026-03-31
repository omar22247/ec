package E_commerce.com.SecureEcommerceApplication.repository;

import E_commerce.com.SecureEcommerceApplication.dto.response.OrderSummaryResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Order;
import E_commerce.com.SecureEcommerceApplication.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ════════════════════════════════════════════════════════
    //  LIST — DTO Projection (summary only)
    //  No items, no address details — just what you need to scan a list
    // ════════════════════════════════════════════════════════

    // User — my orders
    @Query(value = """
        SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.OrderSummaryResponse(
            o.id,
            o.status,
            (SELECT COALESCE(SUM(oi.quantity), 0L) FROM OrderItem oi WHERE oi.order = o),
            o.totalPrice,
            CASE WHEN o.coupon IS NOT NULL THEN o.coupon.code ELSE null END,
            o.createdAt
        )
        FROM Order o
        WHERE o.user.id = :userId
        AND o.deletedAt IS NULL
        ORDER BY o.createdAt DESC
        """,
            countQuery = """
        SELECT COUNT(o) FROM Order o
        WHERE o.user.id = :userId
        AND o.deletedAt IS NULL
        """)
    Page<OrderSummaryResponse> findSummariesByUserId(
            @Param("userId") Long userId,
            Pageable pageable);

    // Admin — all orders
    @Query(value = """
        SELECT new E_commerce.com.SecureEcommerceApplication.dto.response.OrderSummaryResponse(
            o.id,
            o.status,
            (SELECT COALESCE(SUM(oi.quantity), 0L) FROM OrderItem oi WHERE oi.order = o),
            o.totalPrice,
            CASE WHEN o.coupon IS NOT NULL THEN o.coupon.code ELSE null END,
            o.createdAt
        )
        FROM Order o
        WHERE o.deletedAt IS NULL
        ORDER BY o.createdAt DESC
        """,
            countQuery = """
        SELECT COUNT(o) FROM Order o
        WHERE o.deletedAt IS NULL
        """)
    Page<OrderSummaryResponse> findAllSummaries(Pageable pageable);

    // ════════════════════════════════════════════════════════
    //  DETAIL — LEFT JOIN FETCH (full data for single order)
    //
    //  Why LEFT JOIN FETCH and NOT @EntityGraph?
    //  @EntityGraph uses INNER JOIN on items.product
    //  If product was soft-deleted → @SQLRestriction filters it
    //  → Hibernate throws FetchNotFoundException 💥
    //  LEFT JOIN FETCH returns null for deleted products → safe ✅
    // ════════════════════════════════════════════════════════

    // User — single order (verify ownership)
    @Query("""
        SELECT DISTINCT o
        FROM Order o
        LEFT JOIN FETCH o.items i
        LEFT JOIN FETCH i.product
        LEFT JOIN FETCH o.address
        LEFT JOIN FETCH o.coupon
        LEFT JOIN FETCH o.shipment
        WHERE o.id = :id
        AND o.user.id = :userId
        AND o.deletedAt IS NULL
    """)
    Optional<Order> findDetailByIdAndUserId(
            @Param("id") Long id,
            @Param("userId") Long userId);

    // Admin — single order by id
    @Query("""
        SELECT DISTINCT o
        FROM Order o
        LEFT JOIN FETCH o.items i
        LEFT JOIN FETCH i.product
        LEFT JOIN FETCH o.address
        LEFT JOIN FETCH o.coupon
        LEFT JOIN FETCH o.shipment
        WHERE o.id = :id
        AND o.deletedAt IS NULL
    """)
    Optional<Order> findDetailById(@Param("id") Long id);

    // for status update — needs items for stock restore on cancel
    @Query("""
        SELECT DISTINCT o
        FROM Order o
        LEFT JOIN FETCH o.items i
        LEFT JOIN FETCH i.product
        LEFT JOIN FETCH o.shipment
        WHERE o.id = :id
        AND o.deletedAt IS NULL
    """)
    Optional<Order> findForStatusUpdate(@Param("id") Long id);

    long countByStatus(OrderStatus status);
}