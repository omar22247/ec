//
//        package E_commerce.com.SecureEcommerceApplication.service;
//
//import E_commerce.com.SecureEcommerceApplication.dto.request.AddressRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.response.AddressResponse;
//import E_commerce.com.SecureEcommerceApplication.entity.Address;
//import E_commerce.com.SecureEcommerceApplication.entity.User;
//import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
//import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
//import E_commerce.com.SecureEcommerceApplication.exception.UnauthorizedException;
//import E_commerce.com.SecureEcommerceApplication.repository.AddressRepository;
//import E_commerce.com.SecureEcommerceApplication.service.AddressService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AddressServiceImpl implements AddressService {
//
//    // max addresses per user
//    private static final int MAX_ADDRESSES = 5;
//
//    private final AddressRepository addressRepository;
//
//    // ════════════════════════════════════════════════════════
//    //  READ
//    // ════════════════════════════════════════════════════════
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<AddressResponse> getAddresses(Long userId) {
//        return addressRepository.findAllByUserId(userId)
//                .stream()
//                .map(this::toResponse)
//                .toList();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public AddressResponse getAddressById(Long userId, Long addressId) {
//        return toResponse(findAddressOrThrow(userId, addressId));
//    }
//
//    // ════════════════════════════════════════════════════════
//    //  WRITE
//    // ════════════════════════════════════════════════════════
//
//    @Override
//    @Transactional
//    public AddressResponse createAddress(Long userId, AddressRequest request) {
//
//        // limit max addresses per user
//        if (addressRepository.countByUserId(userId) >= MAX_ADDRESSES) {
//            throw new BusinessException(
//                    "Maximum " + MAX_ADDRESSES + " addresses allowed per account");
//        }
//
//        // if this is the first address or marked as default
//        boolean shouldBeDefault = request.isDefault() ||
//                addressRepository.countByUserId(userId) == 0;
//
//        // clear existing default if needed
//        if (shouldBeDefault) {
//            addressRepository.clearDefaultAddress(userId);
//        }
//
//        Address address = Address.builder()
//                .user(User.builder().id(userId).build())
//                .fullName(request.getFullName())
//                .phone(request.getPhone())
//                .street(request.getStreet())
//                .city(request.getCity())
//                .country(request.getCountry())
//                .zipCode(request.getZipCode())
//                .isDefault(shouldBeDefault)
//                .build();
//
//        address = addressRepository.save(address);
//        log.info("Address created: id={}, userId={}", address.getId(), userId);
//
//        return toResponse(address);
//    }
//
//    @Override
//    @Transactional
//    public AddressResponse updateAddress(Long userId, Long addressId,
//                                         AddressRequest request) {
//
//        Address address = findAddressOrThrow(userId, addressId);
//
//        // if setting as default — clear old default first
//        if (request.isDefault() && !address.isDefault()) {
//            addressRepository.clearDefaultAddress(userId);
//        }
//
//        address.setFullName(request.getFullName());
//        address.setPhone(request.getPhone());
//        address.setStreet(request.getStreet());
//        address.setCity(request.getCity());
//        address.setCountry(request.getCountry());
//        address.setZipCode(request.getZipCode());
//        address.setDefault(request.isDefault());
//
//        address = addressRepository.save(address);
//        log.info("Address updated: id={}", addressId);
//
//        return toResponse(address);
//    }
//
//    @Override
//    @Transactional
//    public void deleteAddress(Long userId, Long addressId) {
//
//        Address address = findAddressOrThrow(userId, addressId);
//
//        // cannot delete the default address — set another as default first
//        if (address.isDefault()) {
//            throw new BusinessException(
//                    "Cannot delete default address. " +
//                            "Please set another address as default first.");
//        }
//
//        address.setDeletedAt(LocalDateTime.now());
//        addressRepository.save(address);
//        log.info("Address soft-deleted: id={}", addressId);
//    }
//
//    @Override
//    @Transactional
//    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
//
//        Address address = findAddressOrThrow(userId, addressId);
//
//        if (address.isDefault()) {
//            throw new BusinessException("Address is already set as default");
//        }
//
//        // clear current default
//        addressRepository.clearDefaultAddress(userId);
//
//        // set new default
//        address.setDefault(true);
//        address = addressRepository.save(address);
//        log.info("Default address set: id={}, userId={}", addressId, userId);
//
//        return toResponse(address);
//    }
//
//    // ════════════════════════════════════════════════════════
//    //  Private helpers
//    // ════════════════════════════════════════════════════════
//
//    // finds address and verifies it belongs to this user
//    private Address findAddressOrThrow(Long userId, Long addressId) {
//        return addressRepository.findByIdAndUserId(addressId, userId)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Address", "id", addressId));
//    }
//
//    private AddressResponse toResponse(Address address) {
//        return AddressResponse.builder()
//                .id(address.getId())
//                .fullName(address.getFullName())
//                .phone(address.getPhone())
//                .street(address.getStreet())
//                .city(address.getCity())
//                .country(address.getCountry())
//                .zipCode(address.getZipCode())
//                .isDefault(address.isDefault())
//                .build();
//    }
//}package E_commerce.com.SecureEcommerceApplication.service;
//
//import E_commerce.com.SecureEcommerceApplication.dto.request.AddressRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.response.AddressResponse;
//
//import java.util.List;
//
//public interface AddressService {
//
//    // GET /api/v1/users/me/addresses
//    List<AddressResponse> getAddresses(Long userId);
//
//    // GET /api/v1/users/me/addresses/{id}
//    AddressResponse getAddressById(Long userId, Long addressId);
//
//    // POST /api/v1/users/me/addresses
//    AddressResponse createAddress(Long userId, AddressRequest request);
//
//    // PUT /api/v1/users/me/addresses/{id}
//    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);
//
//    // DELETE /api/v1/users/me/addresses/{id}
//    void deleteAddress(Long userId, Long addressId);
//
//    // PATCH /api/v1/users/me/addresses/{id}/default
//    AddressResponse setDefaultAddress(Long userId, Long addressId);
//}package E_commerce.com.SecureEcommerceApplication.service;
//
//import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
//import E_commerce.com.SecureEcommerceApplication.entity.User;
//import E_commerce.com.SecureEcommerceApplication.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AppUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String email)
//            throws UsernameNotFoundException {
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException(
//                        "User not found with email: " + email));
//
//        // returns AppUserDetails — not Spring's built-in User
//        // this is what @AuthenticationPrincipal injects
//        return new AppUserDetails(user);
//    }
//}package E_commerce.com.SecureEcommerceApplication.service;
//
//import E_commerce.com.SecureEcommerceApplication.dto.request.LoginRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.request.RegisterRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.response.AuthResponse;
//import E_commerce.com.SecureEcommerceApplication.dto.response.UserResponse;
//import E_commerce.com.SecureEcommerceApplication.entity.User;
//import E_commerce.com.SecureEcommerceApplication.entity.enums.UserRole;
//import E_commerce.com.SecureEcommerceApplication.exception.DuplicateResourceException;
//import E_commerce.com.SecureEcommerceApplication.repository.UserRepository;
//import E_commerce.com.SecureEcommerceApplication.security.AppUserDetails;
//import E_commerce.com.SecureEcommerceApplication.security.*;
//        import E_commerce.com.SecureEcommerceApplication.security.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class AuthService {
//
//    private final UserRepository        userRepository;
//    private final PasswordEncoder       passwordEncoder;
//    private final AuthenticationManager authenticationManager;
//    private final JwtUtil               jwtUtil;
//    private final AppUserDetailsService userDetailsService;
//    private final EmailService emailService;
//    private final RefreshTokenService refreshTokenService;
//
//    // ── REGISTER ────────────────────────────────────────────
//
//    @Transactional
//    public AuthResponse register(RegisterRequest request, String clientIp) {
//
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new DuplicateResourceException("User", "email", request.getEmail());
//        }
//
//        // save user
//        User user = userRepository.save(User.builder()
//                .name(request.getName())
//                .email(request.getEmail())
//                .passwordHash(passwordEncoder.encode(request.getPassword()))
//                .role(UserRole.USER)
//                .build());
//
//        // generate token
//        AppUserDetails userDetails = (AppUserDetails)
//                userDetailsService.loadUserByUsername(user.getEmail());
//        String token = jwtUtil.generateToken(userDetails);
//        // ✅ Send welcome email
//        try {
//            emailService.sendRegisterEmail(user.getEmail(), user.getName());
//        } catch (Exception e) {
//            log.warn("Register email failed but user created. email={}, error={}",
//                    user.getEmail(), e.getMessage());
//        }
//        String rawRefreshToken = refreshTokenService.createRefreshToken(userDetails.getUser(),clientIp);
//        return AuthResponse.builder()
//                .accessToken(token)
//                .refreshToken(rawRefreshToken)
//                .user(toUserResponse(user))
//                .build();
//    }
//
//    // ── LOGIN ───────────────────────────────────────────────
//
//    public AuthResponse login(LoginRequest request, String clientIp) {
//
//        // validates email + password — throws BadCredentialsException if wrong
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//
//        // load user details
//        AppUserDetails userDetails = (AppUserDetails)
//                userDetailsService.loadUserByUsername(request.getEmail());
//
//        String token = jwtUtil.generateToken(userDetails);
//        String rawRefreshToken = refreshTokenService.createRefreshToken(userDetails.getUser(),clientIp);
//        return AuthResponse.builder()
//                .accessToken(token)
//                .refreshToken(rawRefreshToken)
//                .user(toUserResponse(userDetails.getUser()))
//                .build();
//    }
//
//    // ── Helper ──────────────────────────────────────────────
//
//    private UserResponse toUserResponse(User user) {
//        return UserResponse.builder()
//                .id(user.getId())
//                .name(user.getName())
//                .email(user.getEmail())
//                .role(user.getRole())
//                .emailVerified(user.isEmailVerified())
//                .createdAt(user.getCreatedAt())
//                .build();
//    }
//}package E_commerce.com.SecureEcommerceApplication.service;
//
//import E_commerce.com.SecureEcommerceApplication.dto.request.CartItemRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateCartItemRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.response.CartItemResponse;
//import E_commerce.com.SecureEcommerceApplication.dto.response.CartResponse;
//import E_commerce.com.SecureEcommerceApplication.entity.Cart;
//import E_commerce.com.SecureEcommerceApplication.entity.CartItem;
//import E_commerce.com.SecureEcommerceApplication.entity.Product;
//import E_commerce.com.SecureEcommerceApplication.entity.User;
//import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
//import E_commerce.com.SecureEcommerceApplication.exception.UnauthorizedException;
//import E_commerce.com.SecureEcommerceApplication.repository.CartItemRepository;
//import E_commerce.com.SecureEcommerceApplication.repository.CartRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CartServiceImpl implements CartService {
//
//    private final CartRepository     cartRepository;
//    private final CartItemRepository cartItemRepository;
//    private final ProductService productService;
//
//
//    // ════════════════════════════════════════════════════════
//    //  GET CART
//    //  Q1 → findByUserId
//    //  Q2 → findCartItems (DTO projection)
//    // ════════════════════════════════════════════════════════
//
//    @Override
////    @Transactional(readOnly = true)
//    public CartResponse getCart(Long userId) {
//        Cart cart = getOrCreateCart(userId);
//        return buildCartResponse(cart.getId());
//    }
//
//
//
//    @Override
//    @Transactional
//    public CartResponse addItem(Long userId, CartItemRequest request) {
//
//        // Q1 — cart + ALL items + products + inventories in ONE JOIN
//        Cart cart = getOrCreateCartWithItems(userId);
//
//        // check in memory — no query
//        Optional<CartItem> existing = cart.getItems().stream()
//                .filter(i -> i.getProduct().getId().equals(request.getProductId()))
//                .findFirst();
//
//        if (existing.isPresent()) {
//            // ── Case A: product already in cart ─────────────
//            // inventory already loaded from Q1 — no extra query
//            CartItem item = existing.get();
//
//            // validateStock in memory
//            item.getProduct().getInventory()
//                    .validateStock(item.getQuantity() + request.getQuantity());
//
//            // Q2 — direct UPDATE
//            cartItemRepository.updateQuantity(
//                    item.getId(), item.getQuantity() + request.getQuantity());
//            log.info("Qty updated: itemId={}, newQty={}",
//                    item.getId(), item.getQuantity() + request.getQuantity());
//
//        } else {
//            // ── Case B: product NOT in cart ──────────────────
//            // ONE query — product + inventory via @EntityGraph
//            Product product = productService
//                    .findWithInventoryByIdAndActiveTrue(request.getProductId());
//
//            // all checks in memory — inventory already loaded
//            product.getInventory().validateStock(request.getQuantity());
//
//            // INSERT — getReferenceById = proxy, no SELECT
//            cartItemRepository.save(CartItem.builder()
//                    .cart(cartRepository.getReferenceById(cart.getId()))
//                    .product(product)
//                    .quantity(request.getQuantity())
//                    .build());
//            log.info("Item added: cartId={}, productId={}",
//                    cart.getId(), request.getProductId());
//        }
//
//        return buildCartResponse(cart.getId());
//    }
//
//    // ════════════════════════════════════════════════════════
//    //  UPDATE ITEM
//    //  Q1 → findWithItemsByUserId   (cart + items + products + inventories)
//    //  Q2 → updateQuantity          (direct UPDATE)
//    //  Q3 → findCartItems           (response)
//    //
//    //  ownership + stock — all in memory from Q1
//    // ════════════════════════════════════════════════════════
//
//    @Override
//    @Transactional
//    public CartResponse updateItem(Long userId, Long itemId,
//                                   UpdateCartItemRequest request) {
//
//        // Q1 — everything loaded
//        Cart cart = getCartWithItemsOrThrow(userId);
//
//        // find item + verify ownership — in memory
//        CartItem item = cart.getItems().stream()
//                .filter(i -> i.getId().equals(itemId))
//                .findFirst()
//                .orElseThrow(() -> new UnauthorizedException(
//                        "Cart item does not belong to your cart"));
//
//        // stock check — inventory from Q1 — in memory
//        item.getProduct().getInventory().validateStock(request.getQuantity());
//
//        // Q2 — direct UPDATE
//        cartItemRepository.updateQuantity(itemId, request.getQuantity());
//        log.info("Item updated: itemId={}, qty={}", itemId, request.getQuantity());
//
//        return buildCartResponse(cart.getId()); // Q3
//    }
//
//    // ════════════════════════════════════════════════════════
//    //  REMOVE ITEM
//    //  Q1 → findWithItemsByUserId   (cart + items)
//    //  Q2 → deleteByItemId          (direct DELETE)
//    //  Q3 → findCartItems           (response)
//    //
//    //  ownership — in memory from Q1
//    // ════════════════════════════════════════════════════════
//
//    @Override
//    @Transactional
//    public CartResponse removeItem(Long userId, Long itemId) {
//
//        Cart cart = getCartWithItemsOrThrow(userId); // Q1
//
//        boolean belongs = cart.getItems().stream()
//                .anyMatch(i -> i.getId().equals(itemId));
//
//        if (!belongs) {
//            throw new UnauthorizedException("Cart item does not belong to your cart");
//        }
//
//        cartItemRepository.deleteByItemId(itemId);  // Q2
//        log.info("Item removed: itemId={}, cartId={}", itemId, cart.getId());
//
//        return buildCartResponse(cart.getId());      // Q3
//    }
//
//    // ════════════════════════════════════════════════════════
//    //  CLEAR CART
//    //  Q1 → findByUserId
//    //  Q2 → deleteAllByCartId       (direct DELETE)
//    // ════════════════════════════════════════════════════════
//
//    @Override
//    @Transactional
//    public void clearCart(Long userId) {
//        Cart cart = getCartOrThrow(userId);
//        cartItemRepository.deleteAllByCartId(cart.getId());
//        log.info("Cart cleared: cartId={}", cart.getId());
//    }
//
//    // ════════════════════════════════════════════════════════
//    //  Private helpers
//    // ════════════════════════════════════════════════════════
//
//    private Cart getOrCreateCart(Long userId) {
//        return cartRepository.findByUserId(userId)
//                .orElseGet(() -> createCart(userId));
//    }
//
//    private Cart getOrCreateCartWithItems(Long userId) {
//        return cartRepository.findWithItemsByUserId(userId)
//                .orElseGet(() -> createCart(userId));
//    }
//
//    private Cart getCartOrThrow(Long userId) {
//        return cartRepository.findByUserId(userId)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Cart", "userId", userId));
//    }
//
//    private Cart getCartWithItemsOrThrow(Long userId) {
//        return cartRepository.findWithItemsByUserId(userId)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Cart", "userId", userId));
//    }
//
//    private Cart createCart(Long userId) {
//        log.info("Creating cart for userId={}", userId);
//        return cartRepository.save(
//                Cart.builder()
//                        .user(User.builder().id(userId).build())
//                        .build()
//        );
//    }
//
//    private CartResponse buildCartResponse(Long cartId) {
//        List<CartItemResponse> items = cartRepository.findCartItems(cartId);
//
//        BigDecimal totalPrice = items.stream()
//                .map(CartItemResponse::getSubtotal)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        int totalItems = items.stream()
//                .mapToInt(CartItemResponse::getQuantity)
//                .sum();
//
//        return CartResponse.builder()
//                .id(cartId)
//                .items(items)
//                .totalItems(totalItems)
//                .totalPrice(totalPrice)
//                .build();
//    }
//}package E_commerce.com.SecureEcommerceApplication.service;
//
//import E_commerce.com.SecureEcommerceApplication.dto.request.CartItemRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.request.UpdateCartItemRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.response.CartResponse;
//
//
//public interface CartService {
//
//    // GET /api/v1/cart
//    CartResponse getCart(Long userId);
//
//    // POST /api/v1/cart/items
//    CartResponse addItem(Long userId, CartItemRequest request);
//
//    // PUT /api/v1/cart/items/{itemId}
//    CartResponse updateItem(Long userId, Long itemId, UpdateCartItemRequest request);
//
//    // DELETE /api/v1/cart/items/{itemId}
//    CartResponse removeItem(Long userId, Long itemId);
//
//    // DELETE /api/v1/cart
//    void clearCart(Long userId);
//}
//package E_commerce.com.SecureEcommerceApplication.service;
//
//import E_commerce.com.SecureEcommerceApplication.dto.request.CategoryRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.response.CategoryResponse;
//import E_commerce.com.SecureEcommerceApplication.entity.Category;
//import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
//import E_commerce.com.SecureEcommerceApplication.exception.DuplicateResourceException;
//import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
//import E_commerce.com.SecureEcommerceApplication.mapper.CategoryMapper;
//import E_commerce.com.SecureEcommerceApplication.repository.CategoryRepository;
//import E_commerce.com.SecureEcommerceApplication.repository.ProductRepository;
//import E_commerce.com.SecureEcommerceApplication.service.CategoryService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CategoryServiceImpl implements CategoryService {
//
//    private final CategoryRepository categoryRepository;
//    private final ProductRepository  productRepository;
//    private final CategoryMapper     categoryMapper;
//
//    // ════════════════════════════════════════════════════════
//    //  READ
//    // ════════════════════════════════════════════════════════
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<CategoryResponse> getAllCategories() {
//
//        // ONE query — @EntityGraph loads root categories + subCategories via LEFT JOIN
//        return categoryRepository.findAllByParentIsNull()
//                .stream()
//                .map(this::toResponse)
//                .toList();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public CategoryResponse getCategoryById(Long id) {
//
//        // ONE query — @EntityGraph loads category + subCategories
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
//
//        return toResponse(category);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<CategoryResponse> getSubCategories(Long parentId) {
//
//        if (!categoryRepository.existsById(parentId)) {
//            throw new ResourceNotFoundException("Category", "id", parentId);
//        }
//
//        return categoryRepository.findSubCategoriesById(parentId);
//    }
//
//    // ════════════════════════════════════════════════════════
//    //  WRITE
//    // ════════════════════════════════════════════════════════
//
//    @Override
//    @Transactional
//    public CategoryResponse createCategory(CategoryRequest request) {
//
//        if (categoryRepository.existsBySlug(request.getSlug())) {
//            throw new DuplicateResourceException("Category", "slug", request.getSlug());
//        }
//        if (categoryRepository.existsByName(request.getName())) {
//            throw new DuplicateResourceException("Category", "name", request.getName());
//        }
//
//        Category category = categoryMapper.toEntity(request);
//
//        if (request.getParentId() != null) {
//            Category parent = findEntityById(request.getParentId());
//            category.setParent(parent);
//        }
//
//        category = categoryRepository.save(category);
//        log.info("Category created: id={}, name={}", category.getId(), category.getName());
//
//        return toResponse(categoryRepository.findById(category.getId())
//                .orElseThrow());
//    }
//
//    @Override
//    @Transactional
//    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
//
//        Category category = findEntityById(id);
//
//        if (!category.getSlug().equals(request.getSlug())
//                && categoryRepository.existsBySlug(request.getSlug())) {
//            throw new DuplicateResourceException("Category", "slug", request.getSlug());
//        }
//        if (!category.getName().equals(request.getName())
//                && categoryRepository.existsByName(request.getName())) {
//            throw new DuplicateResourceException("Category", "name", request.getName());
//        }
//        if (request.getParentId() != null && request.getParentId().equals(id)) {
//            throw new BusinessException("A category cannot be its own parent");
//        }
//
//        categoryMapper.updateEntity(request, category);
//
//        if (request.getParentId() != null) {
//            category.setParent(findEntityById(request.getParentId()));
//        } else {
//            category.setParent(null);
//        }
//
//        categoryRepository.save(category);
//        log.info("Category updated: id={}", id);
//
//        return toResponse(categoryRepository.findById(id).orElseThrow());
//    }
//
//    @Override
//    @Transactional
//    public void deleteCategory(Long id) {
//
//        Category category = findEntityById(id);
//
//        if (productRepository.existsByCategoryId(id)) {
//            throw new BusinessException(
//                    "Cannot delete category '" + category.getName() +
//                            "' — it still has products. Move or delete them first."
//            );
//        }
//
//        if (!category.getSubCategories().isEmpty()) {
//            throw new BusinessException(
//                    "Cannot delete category '" + category.getName() +
//                            "' — it still has sub-categories. Delete them first."
//            );
//        }
//
//        category.setDeletedAt(LocalDateTime.now());
//        categoryRepository.save(category);
//        log.info("Category soft-deleted: id={}", id);
//    }
//
//    // ════════════════════════════════════════════════════════
//    //  Private helpers
//    // ════════════════════════════════════════════════════════
//
//    private Category findEntityById(Long id) {
//        return categoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
//    }
//
//    // entity → DTO
//    // subCategories mapped one level deep — avoids infinite recursion
//    private CategoryResponse toResponse(Category category) {
//        return CategoryResponse.builder()
//                .id(category.getId())
//                .name(category.getName())
//                .slug(category.getSlug())
//                .parentId(category.getParent() != null
//                        ? category.getParent().getId() : null)
//                .parentName(category.getParent() != null
//                        ? category.getParent().getName() : null)
//                // subCategories loaded by @EntityGraph — map one level deep
//                .subCategories(category.getSubCategories().stream()
//                        .map(sub -> CategoryResponse.builder()
//                                .id(sub.getId())
//                                .name(sub.getName())
//                                .slug(sub.getSlug())
//                                .parentId(category.getId())
//                                .parentName(category.getName())
//                                .subCategories(List.of())  // one level only
//                                .build())
//                        .toList())
//                .build();
//    }
//}package E_commerce.com.SecureEcommerceApplication.service;
//
//
//import E_commerce.com.SecureEcommerceApplication.dto.request.CategoryRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.response.CategoryResponse;
//
//import java.util.List;
//
//public interface CategoryService {
//
//    // ── Public ──────────────────────────────────────────────
//
//    // GET /api/v1/categories
//    // returns only root categories with their sub-categories
//    List<CategoryResponse> getAllCategories();
//
//    // GET /api/v1/categories/{id}
//    CategoryResponse getCategoryById(Long id);
//
//    // GET /api/v1/categories/{id}/subcategories
//    List<CategoryResponse> getSubCategories(Long parentId);
//
//    // ── Admin ───────────────────────────────────────────────
//
//    // POST /api/v1/categories
//    CategoryResponse createCategory(CategoryRequest request);
//
//    // PUT /api/v1/categories/{id}
//    CategoryResponse updateCategory(Long id, CategoryRequest request);
//
//    // DELETE /api/v1/categories/{id}
//    void deleteCategory(Long id);
//}
//
//package E_commerce.com.SecureEcommerceApplication.service;
//
//import E_commerce.com.SecureEcommerceApplication.dto.request.CouponRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.response.CouponResponse;
//import E_commerce.com.SecureEcommerceApplication.dto.response.CouponValidationResponse;
//import E_commerce.com.SecureEcommerceApplication.entity.Coupon;
//import E_commerce.com.SecureEcommerceApplication.exception.BusinessException;
//import E_commerce.com.SecureEcommerceApplication.exception.DuplicateResourceException;
//import E_commerce.com.SecureEcommerceApplication.exception.ResourceNotFoundException;
//import E_commerce.com.SecureEcommerceApplication.mapper.CouponMapper;
//import E_commerce.com.SecureEcommerceApplication.repository.CouponRepository;
//import E_commerce.com.SecureEcommerceApplication.service.CouponService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CouponServiceImpl implements CouponService {
//
//    private final CouponRepository couponRepository;
//    private final CouponMapper     couponMapper;
//
//    // ════════════════════════════════════════════════════════
//    //  PUBLIC
//    // ════════════════════════════════════════════════════════
//
//    @Override
//    @Transactional(readOnly = true)
//    public CouponValidationResponse validateCoupon(String code) {
//
//        Coupon coupon = couponRepository.findByCode(code)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Coupon", "code", code));
//
//        if (!coupon.isValid()) {
//            throw new BusinessException(
//                    !coupon.isActive()   ? "Coupon is inactive" :
//                            !coupon.isValid()   ? "Coupon has expired" :
//                            "Coupon has reached its usage limit"
//            );
//        }
//
//        // return only what the user needs — no admin details
//        return CouponValidationResponse.builder()
//                .code(coupon.getCode())
//                .discountType(coupon.getDiscountType())
//                .discountValue(coupon.getDiscountValue())
//                .minOrderAmount(coupon.getMinOrderAmount())
//                .build();
//    }
//
//    // ════════════════════════════════════════════════════════
//    //  ADMIN
//    // ════════════════════════════════════════════════════════
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<CouponResponse> getAllCoupons() {
//        return couponRepository.findAll()
//                .stream()
//                .map(this::toResponse)
//                .toList();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public CouponResponse getCouponById(Long id) {
//        return toResponse(findEntityById(id));
//    }
//
//    @Override
//    @Transactional
//    public CouponResponse createCoupon(CouponRequest request) {
//
//        if (couponRepository.existsByCode(request.getCode())) {
//            throw new DuplicateResourceException("Coupon", "code", request.getCode());
//        }
//
//        Coupon coupon = couponMapper.toEntity(request);
//        coupon = couponRepository.save(coupon);
//        log.info("Coupon created: id={}, code={}", coupon.getId(), coupon.getCode());
//
//        return toResponse(coupon);
//    }
//
//    @Override
//    @Transactional
//    public CouponResponse updateCoupon(Long id, CouponRequest request) {
//
//        Coupon coupon = findEntityById(id);
//
//        // check code uniqueness — ignore own code
//        if (!coupon.getCode().equals(request.getCode())
//                && couponRepository.existsByCode(request.getCode())) {
//            throw new DuplicateResourceException("Coupon", "code", request.getCode());
//        }
//
//        couponMapper.updateEntity(request, coupon);
//        coupon = couponRepository.save(coupon);
//        log.info("Coupon updated: id={}", id);
//
//        return toResponse(coupon);
//    }
//
//    @Override
//    @Transactional
//    public void deleteCoupon(Long id) {
//        Coupon coupon = findEntityById(id);
//        coupon.setDeletedAt(LocalDateTime.now());
//        coupon.setActive(false);
//        couponRepository.save(coupon);
//        log.info("Coupon soft-deleted: id={}", id);
//    }
//
//    @Override
//    @Transactional
//    public CouponResponse toggleCoupon(Long id) {
//        Coupon coupon = findEntityById(id);
//        coupon.setActive(!coupon.isActive());
//        coupon = couponRepository.save(coupon);
//        log.info("Coupon toggled: id={}, active={}", id, coupon.isActive());
//        return toResponse(coupon);
//    }
//
//    // ════════════════════════════════════════════════════════
//    //  Private helpers
//    // ════════════════════════════════════════════════════════
//
//    private Coupon findEntityById(Long id) {
//        return couponRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
//    }
//
//    // entity → DTO
//    // valid is calculated from coupon.isValid() — not stored in DB
//    private CouponResponse toResponse(Coupon coupon) {
//        return CouponResponse.builder()
//                .id(coupon.getId())
//                .code(coupon.getCode())
//                .discountType(coupon.getDiscountType())
//                .discountValue(coupon.getDiscountValue())
//                .minOrderAmount(coupon.getMinOrderAmount())
//                .maxUses(coupon.getMaxUses())
//                .usedCount(coupon.getUsedCount())
//                .expiresAt(coupon.getExpiresAt())
//                .active(coupon.isActive())
//                .valid(coupon.isValid())         // calculated in Coupon entity
//                .build();
//    }
//}package E_commerce.com.SecureEcommerceApplication.service;
//
//import E_commerce.com.SecureEcommerceApplication.dto.request.CouponRequest;
//import E_commerce.com.SecureEcommerceApplication.dto.response.CouponResponse;
//import E_commerce.com.SecureEcommerceApplication.dto.response.CouponValidationResponse;
//
//import java.util.List;
//
//public interface CouponService {
//
//    // ── Public ──────────────────────────────────────────────
//
//    // POST /api/v1/coupons/validate — check if coupon is valid before applying
//    CouponValidationResponse validateCoupon(String code);
//
//    // ── Admin ───────────────────────────────────────────────
//
//    // GET /api/v1/admin/coupons
//    List<CouponResponse> getAllCoupons();
//
//    // GET /api/v1/admin/coupons/{id}
//    CouponResponse getCouponById(Long id);
//
//    // POST /api/v1/admin/coupons
//    CouponResponse createCoupon(CouponRequest request);
//
//    // PUT /api/v1/admin/coupons/{id}
//    CouponResponse updateCoupon(Long id, CouponRequest request);
//
//    // DELETE /api/v1/admin/coupons/{id}
//    void deleteCoupon(Long id);
//
//    // PATCH /api/v1/admin/coupons/{id}/toggle
//    CouponResponse toggleCoupon(Long id);
//}//package E_commerce.com.SecureEcommerceApplication.service;
////
////import E_commerce.com.SecureEcommerceApplication.entity.User;
////import E_commerce.com.SecureEcommerceApplication.repository.UserRepository;
////import lombok.RequiredArgsConstructor;
////import org.springframework.security.core.userdetails.UserDetails;
////import org.springframework.security.core.userdetails.UserDetailsService;
////import org.springframework.stereotype.Service;
////
////@Service
////@RequiredArgsConstructor
////public class CustomUserDetailsService implements UserDetailsService {
////
////    private final UserRepository repo;
////
////    @Override
////    public UserDetails loadUserByUsername(String email) {
////
////        User user = repo.findByEmail(email)
////                .orElseThrow(() -> new RuntimeException("User not found"));
////
////        return org.springframework.security.core.userdetails.User
////                .withUsername(user.getEmail())
////                .password(user.getPasswordHash())
////                .roles(user.getRole().toString())
////                .build();
////    }
////}
//package E_commerce.com.SecureEcommerceApplication.service;
//
//import E_commerce.com.SecureEcommerceApplication.dto.response.AddressResponse;
//import E_commerce.com.SecureEcommerceApplication.dto.response.OrderItemResponse;
//import E_commerce.com.SecureEcommerceApplication.dto.response.OrderResponse;
//import E_commerce.com.SecureEcommerceApplication.dto.response.ShipmentResponse;
//import E_commerce.com.SecureEcommerceApplication.entity.enums.ShipmentStatus;
//import com.resend.*;
//        import com.resend.core.exception.ResendException;
//import com.resend.services.emails.model.CreateEmailOptions;
//import com.resend.services.emails.model.CreateEmailResponse;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Service;
//import org.springframework.web.util.HtmlUtils;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//@Service
//@Slf4j
//public class EmailService {
//
//    @Value("${resend.api-key}")
//    private String apiKey;
//
//    @Value("${resend.from}")
//    private String fromEmail;
//
//    @Value("${app.reset-password.url}")
//    private String resetPasswordUrl;
//
//    @Value("${app.url}")
//    private String appUrl;
//
//    private Resend resend;
//
//    // ✅ Cache both templates at startup
//    private String resetTemplate;
//    private String orderTemplate;
//    private String shipmentTemplate;
//    private String registerTemplate;
//    private static final DateTimeFormatter DATE_FMT =
//            DateTimeFormatter.ofPattern("MMM dd, yyyy");
//
//
//    @PostConstruct
//    public void init() {
//        this.resend           = new Resend(apiKey);
//        this.resetTemplate    = loadTemplate("templates/reset-password-email.html");
//        this.orderTemplate    = loadTemplate("templates/order-confirmation-email.html");
//        this.shipmentTemplate = loadTemplate("templates/shipment-update-email.html");
//        this.registerTemplate = loadTemplate("templates/register-email.html"); // ✅ جديد
//        log.info("Email templates loaded successfully");
//    }
//
//    // ─────────────────────────────────────────
//    // Reset Password
//    // ─────────────────────────────────────────
//    public void sendResetEmail(String toEmail, String userName, String rawToken) {
//        try {
//            String resetLink = resetPasswordUrl + "?token=" + rawToken;
//            String html = buildResetEmailTemplate(userName, resetLink);
//
//            CreateEmailOptions params = CreateEmailOptions.builder()
//                    .from(fromEmail)
//                    .to(List.of(toEmail))
//                    .subject("Reset your password")
//                    .html(html)
//                    .build();
//
//            CreateEmailResponse response = resend.emails().send(params);
//            log.info("Reset email sent. id={} to={}", response.getId(), toEmail);
//
//        } catch (ResendException e) {
//            log.error("Failed to send reset email to {}: {}", toEmail, e.getMessage());
//            throw new RuntimeException("Failed to send reset email");
//        }
//    }
//
//    // ─────────────────────────────────────────
//    // Order Confirmation
//    // ─────────────────────────────────────────
//    public void sendOrderConfirmationEmail(String toEmail, String userName, OrderResponse order) {
//        try {
//            String html = buildOrderConfirmationTemplate(userName, order);
//
//            CreateEmailOptions params = CreateEmailOptions.builder()
//                    .from(fromEmail)
//                    .to(toEmail)
//                    .subject("Your order #" + order.getId() + " is confirmed!")
//                    .html(html)
//                    .build();
//
//            CreateEmailResponse response = resend.emails().send(params);
//            log.info("Order confirmation email sent. id={} to={}", response.getId(), toEmail);
//
//        } catch (ResendException e) {
//            log.error("Failed to send order confirmation to {}: {}", toEmail, e.getMessage());
//            throw new RuntimeException("Failed to send order confirmation email");
//        }
//    }
//
//    // ─────────────────────────────────────────
//    // Builders
//    // ─────────────────────────────────────────
//    private String buildResetEmailTemplate(String userName, String resetLink) {
//        return resetTemplate
//                .replace("{{userName}}",  escapeHtml(userName))
//                .replace("{{resetLink}}", escapeHtml(resetLink));
//    }
//    public void sendShipmentUpdateEmail(String toEmail, String userName, ShipmentResponse shipment, Long orderId) {
//        try {
//            String html = buildShipmentUpdateTemplate(userName, shipment, orderId);
//
//            CreateEmailOptions params = CreateEmailOptions.builder()
//                    .from(fromEmail)
//                    .to(List.of(toEmail))
//                    .subject(resolveShipmentSubject(shipment.getStatus()))
//                    .html(html)
//                    .build();
//
//            CreateEmailResponse response = resend.emails().send(params);
//            log.info("Shipment update email sent. id={} to={}", response.getId(), toEmail);
//
//        } catch (ResendException e) {
//            log.error("Failed to send shipment update email: message={}", e.getMessage());
//            throw new RuntimeException("Failed to send shipment update email: " + e.getMessage(), e);
//        }
//    }
//    public void sendRegisterEmail(String toEmail, String userName) {
//        try {
//            String html = buildRegisterTemplate(userName);
//
//            CreateEmailOptions params = CreateEmailOptions.builder()
//                    .from(fromEmail)
//                    .to(List.of(toEmail))
//                    .subject("Welcome to YourApp!")
//                    .html(html)
//                    .build();
//
//            CreateEmailResponse response = resend.emails().send(params);
//            log.info("Register email sent. id={} to={}", response.getId(), toEmail);
//
//        } catch (ResendException e) {
//            log.error("Failed to send register email: {}", e.getMessage());
//            throw new RuntimeException("Failed to send register email: " + e.getMessage(), e);
//        }
//    }
//
//    private String buildRegisterTemplate(String userName) {
//        return registerTemplate
//                .replace("{{userName}}",  escapeHtml((userName)))
//                .replace("{{loginLink}}", appUrl + "/login");
//    }
//    private String resolveShipmentSubject(ShipmentStatus status) {
//        return switch (status) {
//            case SHIPPED    -> "Your order is on the way!";
//            case DELIVERED  -> "Your order has been delivered!";
//            case PREPARING  -> "Your order has been preparing!";
//            case RETURNED  -> "Your order has been returned!";
//            default         -> "Your shipment status has been updated";
//        };
//    }
//
//    private String buildShipmentUpdateTemplate(String userName, ShipmentResponse shipment, Long orderId) {
//        return shipmentTemplate
//                .replace("{{userName}}",          escapeHtml((userName)))
//                .replace("{{orderId}}",            orderId.toString())
//                .replace("{{status}}",             shipment.getStatus().name())
//                .replace("{{carrier}}",            (shipment.getCarrier()))
//                .replace("{{trackingNumber}}",     (shipment.getTrackingNumber()))
//                .replace("{{estimatedDelivery}}", shipment.getEstimatedDelivery() != null
//                        ? shipment.getEstimatedDelivery().format(DATE_FMT) : "TBD")
//                .replace("{{shippedAt}}",         shipment.getShippedAt() != null
//                        ? shipment.getShippedAt().format(DATE_FMT) : "")
//                .replace("{{orderLink}}",         appUrl + "/orders/" + orderId);
//    }
//    private String buildOrderConfirmationTemplate(String userName, OrderResponse order) {
//        AddressResponse addr = order.getAddress();
//        String addressLine = addr.getFullName() + "\n" +
//                addr.getPhone()    + "\n" +
//                addr.getStreet()   + "\n" +
//                addr.getCity() + ", " + addr.getZipCode() + "\n" +
//                addr.getCountry();
//
//        boolean hasCoupon = order.getCouponCode() != null
//                && !order.getCouponCode().isBlank();
//
//        return orderTemplate
//                .replace("{{userName}}",       escapeHtml(userName))
//                .replace("{{orderId}}",         order.getId().toString())
//                .replace("{{status}}",          order.getStatus().name())
//                .replace("{{originalPrice}}",   "$" + order.getOriginalPrice())
//                .replace("{{discountAmount}}",  "$" + order.getDiscountAmount())
//                .replace("{{totalPrice}}",      "$" + order.getTotalPrice())
//                .replace("{{createdAt}}",       order.getCreatedAt().format(DATE_FMT))
//                .replace("{{addressLine}}",     escapeHtml(addressLine))
//                .replace("{{couponCode}}",      hasCoupon ? escapeHtml(order.getCouponCode()) : "")
//                .replace("{{#hasCoupon}}",      hasCoupon ? "" : "<!--")
//                .replace("{{/hasCoupon}}",      hasCoupon ? "" : "-->")
//                .replace("{{orderLink}}",       appUrl + "/orders/" + order.getId());
//    }
//
//    // ─────────────────────────────────────────
//    // Helpers
//    // ─────────────────────────────────────────
//    private String loadTemplate(String path) {
//        try {
//            ClassPathResource resource = new ClassPathResource(path);
//            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            throw new RuntimeException("Could not load template: " + path, e);
//        }
//    }
//
//    private String escapeHtml(String input) {
//        if (input == null) {
//            return "";
//        }
//        return HtmlUtils.htmlEscape(input);
//    }
//
//}