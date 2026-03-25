package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.request.CouponRequest;
import E_commerce.com.SecureEcommerceApplication.dto.response.CouponResponse;
import E_commerce.com.SecureEcommerceApplication.dto.response.CouponValidationResponse;
import E_commerce.com.SecureEcommerceApplication.entity.Coupon;
import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
import E_commerce.com.SecureEcommerceApplication.exception.DuplicateResourceException;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.mapper.CouponMapper;
import E_commerce.com.SecureEcommerceApplication.repository.CouponRepository;
import E_commerce.com.SecureEcommerceApplication.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper     couponMapper;

    // ════════════════════════════════════════════════════════
    //  PUBLIC
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public CouponValidationResponse validateCoupon(String code) {

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coupon", "code", code));

        if (!coupon.isValid()) {
            throw new BusinessException(
                    !coupon.isActive()   ? "Coupon is inactive" :
                            !coupon.isValid()   ? "Coupon has expired" :
                                    "Coupon has reached its usage limit"
            );
        }

        // return only what the user needs — no admin details
        return CouponValidationResponse.builder()
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .build();
    }

    // ════════════════════════════════════════════════════════
    //  ADMIN
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {

        if (couponRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Coupon", "code", request.getCode());
        }

        Coupon coupon = couponMapper.toEntity(request);
        coupon = couponRepository.save(coupon);
        log.info("Coupon created: id={}, code={}", coupon.getId(), coupon.getCode());

        return toResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {

        Coupon coupon = findEntityById(id);

        // check code uniqueness — ignore own code
        if (!coupon.getCode().equals(request.getCode())
                && couponRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Coupon", "code", request.getCode());
        }

        couponMapper.updateEntity(request, coupon);
        coupon = couponRepository.save(coupon);
        log.info("Coupon updated: id={}", id);

        return toResponse(coupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = findEntityById(id);
        coupon.setDeletedAt(LocalDateTime.now());
        coupon.setActive(false);
        couponRepository.save(coupon);
        log.info("Coupon soft-deleted: id={}", id);
    }

    @Override
    @Transactional
    public CouponResponse toggleCoupon(Long id) {
        Coupon coupon = findEntityById(id);
        coupon.setActive(!coupon.isActive());
        coupon = couponRepository.save(coupon);
        log.info("Coupon toggled: id={}, active={}", id, coupon.isActive());
        return toResponse(coupon);
    }

    // ════════════════════════════════════════════════════════
    //  Private helpers
    // ════════════════════════════════════════════════════════

    private Coupon findEntityById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
    }

    // entity → DTO
    // valid is calculated from coupon.isValid() — not stored in DB
    private CouponResponse toResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxUses(coupon.getMaxUses())
                .usedCount(coupon.getUsedCount())
                .expiresAt(coupon.getExpiresAt())
                .active(coupon.isActive())
                .valid(coupon.isValid())         // calculated in Coupon entity
                .build();
    }
}