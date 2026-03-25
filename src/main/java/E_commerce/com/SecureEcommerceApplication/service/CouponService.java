package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.CouponRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.CouponResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.CouponValidationResponse;

import java.util.List;

public interface CouponService {

    // ── Public ──────────────────────────────────────────────

    // POST /api/v1/coupons/validate — check if coupon is valid before applying
    CouponValidationResponse validateCoupon(String code);

    // ── Admin ───────────────────────────────────────────────

    // GET /api/v1/admin/coupons
    List<CouponResponse> getAllCoupons();

    // GET /api/v1/admin/coupons/{id}
    CouponResponse getCouponById(Long id);

    // POST /api/v1/admin/coupons
    CouponResponse createCoupon(CouponRequest request);

    // PUT /api/v1/admin/coupons/{id}
    CouponResponse updateCoupon(Long id, CouponRequest request);

    // DELETE /api/v1/admin/coupons/{id}
    void deleteCoupon(Long id);

    // PATCH /api/v1/admin/coupons/{id}/toggle
    CouponResponse toggleCoupon(Long id);
}