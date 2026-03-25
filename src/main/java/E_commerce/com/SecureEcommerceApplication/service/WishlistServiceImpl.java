package E_commerce.com.SecureEcommerceApplication.service;

import E_commerce.com.SecureEcommerceApplication.dto.response.WishlistItemResponse;
import E_commerce.com.SecureEcommerceApplication.entity.User;
import E_commerce.com.SecureEcommerceApplication.entity.Wishlist;
import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
import E_commerce.com.SecureEcommerceApplication.repository.ProductRepository;
import E_commerce.com.SecureEcommerceApplication.repository.WishlistRepository;
import E_commerce.com.SecureEcommerceApplication.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository  productRepository;

    // ════════════════════════════════════════════════════════
    //  GET — DTO projection (ONE query)
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    // ════════════════════════════════════════════════════════
    //  ADD
    //  Q1 → existsByIdAndActiveTrue  (product check)
    //  Q2 → findByUserIdAndProductId (duplicate check)
    //  Q3 → save
    //  Q4 → findByUserId             (return added item)
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public WishlistItemResponse addToWishlist(Long userId, Long productId) {

        // check product exists and is active
        if (!productRepository.findByIdAndActiveTrue(productId).isPresent()) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        // check not already in wishlist
        if (wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new BusinessException("Product is already in your wishlist");
        }

        // getReferenceById = proxy — no SELECT, just sets FK
        wishlistRepository.save(Wishlist.builder()
                .user(User.builder().id(userId).build())
                .product(productRepository.getReferenceById(productId))
                .build());

        log.info("Added to wishlist: userId={}, productId={}", userId, productId);

        // return the newly added item from DTO projection
        return wishlistRepository.findByUserId(userId)
                .stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow();
    }

    // ════════════════════════════════════════════════════════
    //  REMOVE — by productId (more natural for user)
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {

        Wishlist wishlist = wishlistRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wishlist item", "productId", productId));

        wishlist.setDeletedAt(LocalDateTime.now());
        wishlistRepository.save(wishlist);
        log.info("Removed from wishlist: userId={}, productId={}", userId, productId);
    }

    // ════════════════════════════════════════════════════════
    //  CLEAR — direct UPDATE (ONE query)
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void clearWishlist(Long userId) {
        wishlistRepository.deleteByUserId(userId);
        log.info("Wishlist cleared: userId={}", userId);
    }
}