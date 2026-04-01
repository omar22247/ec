// ─── Enums ────────────────────────────────────────────────────────────────────

export type UserRole = 'USER' | 'ADMIN'

export type OrderStatus = 'PENDING' | 'PAID' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'

export type PaymentMethod = 'CREDIT_CARD' | 'PAYPAL' | 'CASH_ON_DELIVERY'

export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED'

export type ShipmentStatus = 'PREPARING' | 'SHIPPED' | 'DELIVERED' | 'RETURNED'

export type DiscountType = 'PERCENTAGE' | 'FIXED'

// ─── API Wrapper ──────────────────────────────────────────────────────────────

export interface ApiResponse<T> {
  success: boolean
  message?: string
  data: T
  timestamp: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

// ─── Auth ─────────────────────────────────────────────────────────────────────

export interface UserResponse {
  id: number
  name: string
  email: string
  role: UserRole
  emailVerified: boolean
  createdAt: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  accessTokenExpiresIn: number
  user: UserResponse
}

export interface RefreshTokenResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  accessTokenExpiresIn: number
  refreshTokenExpiresIn: number
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  name: string
  email: string
  password: string
}

export interface ForgotPasswordRequest {
  email: string
}

export interface ResetPasswordRequest {
  newPassword: string
}

// ─── User ─────────────────────────────────────────────────────────────────────

export interface UpdateProfileRequest {
  name: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

// ─── Address ──────────────────────────────────────────────────────────────────

export interface AddressResponse {
  id: number
  fullName: string
  phone: string
  street: string
  city: string
  country: string
  zipCode: string
  isDefault: boolean
}

export interface AddressRequest {
  fullName: string
  phone: string
  street: string
  city: string
  country: string
  zipCode?: string
  isDefault?: boolean
}

// ─── Category ─────────────────────────────────────────────────────────────────

export interface CategoryResponse {
  id: number
  name: string
  slug: string
  parentId: number | null
  parentName: string | null
  subCategories: CategoryResponse[] | null
}

export interface CategoryRequest {
  name: string
  slug: string
  parentId?: number | null
}

// ─── Product ──────────────────────────────────────────────────────────────────

export interface ProductResponse {
  id: number
  name: string
  basePrice: number
  imageUrl: string | null
  active: boolean
  categoryId: number
  categoryName: string
  stock: number
  inStock: boolean
  averageRating: number | null
  reviewCount: number
}

export interface ProductDetailResponse extends ProductResponse {
  description: string | null
  lowStock: boolean
}

export interface ProductRequest {
  name: string
  description?: string
  basePrice: number
  stock: number
  categoryId: number
  imageUrl?: string
  lowStockThreshold?: number
}

// ─── Cart ─────────────────────────────────────────────────────────────────────

export interface CartItemResponse {
  id: number
  productId: number
  productName: string
  productImage: string | null
  unitPrice: number
  quantity: number
  subtotal: number
  inStock: boolean
}

export interface CartResponse {
  id: number
  items: CartItemResponse[]
  totalItems: number
  totalPrice: number
}

export interface CartItemRequest {
  productId: number
  quantity: number
}

export interface UpdateCartItemRequest {
  quantity: number
}

// ─── Wishlist ─────────────────────────────────────────────────────────────────

export interface WishlistItemResponse {
  wishlistItemId: number
  productId: number
  productName: string
  productImage: string | null
  price: number
  inStock: boolean
  addedAt: string
}

// ─── Review ───────────────────────────────────────────────────────────────────

export interface ReviewResponse {
  id: number
  userId: number
  userName: string
  productId: number
  rating: number
  comment: string
  createdAt: string
}

export interface ReviewRequest {
  rating: number
  comment: string
}

// ─── Order ────────────────────────────────────────────────────────────────────

export interface OrderItemResponse {
  id: number
  productId: number
  productName: string
  productImage: string | null
  quantity: number
  priceAtPurchase: number
  subtotal: number
}

export interface ShipmentResponse {
  id: number
  status: ShipmentStatus
  carrier: string | null
  trackingNumber: string | null
  shippedAt: string | null
  estimatedDelivery: string | null
}

export interface OrderResponse {
  id: number
  status: OrderStatus
  address: AddressResponse
  couponCode: string | null
  originalPrice: number
  discountAmount: number
  totalPrice: number
  createdAt: string
  items: OrderItemResponse[]
  shipment: ShipmentResponse | null
}

export interface OrderSummaryResponse {
  id: number
  status: OrderStatus
  totalItems: number
  totalPrice: number
  couponCode: string | null
  createdAt: string
}

export interface CreateOrderRequest {
  addressId: number
  paymentMethod: PaymentMethod
  couponCode?: string
}

export interface UpdateOrderStatusRequest {
  status: OrderStatus
}

export interface UpdateShipmentRequest {
  status: ShipmentStatus
  carrier?: string
  trackingNumber?: string
  estimatedDelivery?: string
}

// ─── Coupon ───────────────────────────────────────────────────────────────────

export interface CouponResponse {
  id: number
  code: string
  discountType: DiscountType
  discountValue: number
  minOrderAmount: number
  maxUses: number | null
  usedCount: number
  expiresAt: string | null
  active: boolean
  valid: boolean
}

export interface CouponValidationResponse {
  code: string
  discountType: DiscountType
  discountValue: number
  minOrderAmount: number
}

export interface CouponRequest {
  code: string
  discountType: DiscountType
  discountValue: number
  minOrderAmount?: number
  maxUses?: number | null
  expiresAt?: string | null
}
