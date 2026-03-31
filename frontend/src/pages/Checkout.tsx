import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useQuery, useMutation } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { useCart } from '../context/CartContext'
import { getAddresses } from '../api/addresses'
import { validateCoupon } from '../api/coupons'
import { createOrder } from '../api/orders'
import { formatCurrency, getErrorMessage } from '../utils/format'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import toast from 'react-hot-toast'
import type { CreateOrderRequest, PaymentMethod, CouponValidationResponse } from '../types'

const PAYMENT_METHODS: { value: PaymentMethod; label: string; icon: string }[] = [
  { value: 'CREDIT_CARD', label: 'Credit Card', icon: '💳' },
  { value: 'PAYPAL', label: 'PayPal', icon: '🅿️' },
  { value: 'CASH_ON_DELIVERY', label: 'Cash on Delivery', icon: '💵' },
]

interface FormData {
  couponCode: string
}

export default function Checkout() {
  const navigate = useNavigate()
  const { cart, refresh } = useCart()
  const [selectedAddress, setSelectedAddress] = useState<number | null>(null)
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('CREDIT_CARD')
  const [coupon, setCoupon] = useState<CouponValidationResponse | null>(null)
  const [couponError, setCouponError] = useState('')

  const { register, handleSubmit: handleCouponSubmit, watch } = useForm<FormData>()
  const couponCode = watch('couponCode', '')

  const { data: addresses } = useQuery({
    queryKey: ['addresses'],
    queryFn: getAddresses,
  })

  // Auto-select default (or first) address once loaded
  useEffect(() => {
    if (addresses && selectedAddress === null) {
      const def = addresses.find((a) => a.isDefault) ?? addresses[0]
      if (def) setSelectedAddress(def.id)
    }
  }, [addresses])

  const validateCouponMutation = useMutation({
    mutationFn: () => validateCoupon(couponCode),
    onSuccess: (data) => {
      setCoupon(data)
      setCouponError('')
      toast.success(`Coupon applied: ${data.discountType === 'PERCENTAGE' ? data.discountValue + '%' : formatCurrency(data.discountValue)} off`)
    },
    onError: () => {
      setCoupon(null)
      setCouponError('Invalid or expired coupon code')
    },
  })

  const orderMutation = useMutation({
    mutationFn: (data: CreateOrderRequest) => createOrder(data),
    onSuccess: async (order) => {
      await refresh()
      toast.success('Order placed successfully!')
      navigate(`/orders/${order.id}`)
    },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  if (!cart || cart.items.length === 0) {
    return (
      <div className="max-w-xl mx-auto px-4 py-24 text-center">
        <p className="text-5xl mb-4">🛒</p>
        <p className="text-xl font-bold text-gray-900">Your cart is empty</p>
        <a href="/products" className="mt-4 block text-primary-600 hover:underline">Browse products</a>
      </div>
    )
  }

  const discount = coupon
    ? coupon.discountType === 'PERCENTAGE'
      ? (cart.totalPrice * coupon.discountValue) / 100
      : Math.min(coupon.discountValue, cart.totalPrice)
    : 0
  const total = Math.max(0, cart.totalPrice - discount)

  const handlePlaceOrder = () => {
    if (!selectedAddress) { toast.error('Please select a shipping address'); return }
    orderMutation.mutate({
      addressId: selectedAddress,
      paymentMethod,
      couponCode: coupon?.code,
    })
  }

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-8">Checkout</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left: Address + Payment */}
        <div className="lg:col-span-2 space-y-6">
          {/* Shipping Address */}
          <div className="bg-white rounded-xl border border-gray-200 p-6">
            <h2 className="font-bold text-gray-900 mb-4">Shipping Address</h2>
            {!addresses || addresses.length === 0 ? (
              <div className="text-center py-4">
                <p className="text-gray-500 text-sm mb-3">No addresses saved.</p>
                <a href="/addresses" className="text-primary-600 hover:underline text-sm font-medium">
                  Add an address →
                </a>
              </div>
            ) : (
              <div className="space-y-3">
                {addresses.map((addr) => (
                  <label
                    key={addr.id}
                    className={`flex items-start gap-3 p-3 rounded-lg border cursor-pointer transition-colors ${
                      selectedAddress === addr.id
                        ? 'border-primary-500 bg-primary-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <input
                      type="radio"
                      name="address"
                      value={addr.id}
                      checked={selectedAddress === addr.id}
                      onChange={() => setSelectedAddress(addr.id)}
                      className="mt-1 accent-primary-600"
                    />
                    <div className="text-sm">
                      <p className="font-medium text-gray-900">{addr.fullName}</p>
                      <p className="text-gray-500">{addr.street}, {addr.city}, {addr.country} {addr.zipCode}</p>
                      <p className="text-gray-500">{addr.phone}</p>
                      {addr.isDefault && (
                        <span className="text-xs text-primary-600 font-medium">Default</span>
                      )}
                    </div>
                  </label>
                ))}
                <a href="/addresses" className="text-sm text-primary-600 hover:underline block mt-2">
                  + Add new address
                </a>
              </div>
            )}
          </div>

          {/* Payment Method */}
          <div className="bg-white rounded-xl border border-gray-200 p-6">
            <h2 className="font-bold text-gray-900 mb-4">Payment Method</h2>
            <div className="space-y-3">
              {PAYMENT_METHODS.map((pm) => (
                <label
                  key={pm.value}
                  className={`flex items-center gap-3 p-3 rounded-lg border cursor-pointer transition-colors ${
                    paymentMethod === pm.value
                      ? 'border-primary-500 bg-primary-50'
                      : 'border-gray-200 hover:border-gray-300'
                  }`}
                >
                  <input
                    type="radio"
                    name="payment"
                    value={pm.value}
                    checked={paymentMethod === pm.value}
                    onChange={() => setPaymentMethod(pm.value)}
                    className="accent-primary-600"
                  />
                  <span className="text-xl">{pm.icon}</span>
                  <span className="text-sm font-medium text-gray-900">{pm.label}</span>
                </label>
              ))}
            </div>
          </div>

          {/* Coupon */}
          <div className="bg-white rounded-xl border border-gray-200 p-6">
            <h2 className="font-bold text-gray-900 mb-4">Coupon Code</h2>
            <form
              onSubmit={handleCouponSubmit(() => validateCouponMutation.mutate())}
              className="flex gap-2"
            >
              <Input
                placeholder="Enter coupon code"
                {...register('couponCode')}
                error={couponError}
                className="flex-1"
              />
              <Button
                type="submit"
                variant="outline"
                loading={validateCouponMutation.isPending}
                disabled={!couponCode.trim()}
              >
                Apply
              </Button>
            </form>
            {coupon && (
              <p className="mt-2 text-sm text-green-700 bg-green-50 rounded-lg px-3 py-2">
                ✓ Coupon <strong>{coupon.code}</strong> applied —{' '}
                {coupon.discountType === 'PERCENTAGE'
                  ? `${coupon.discountValue}% off`
                  : `${formatCurrency(coupon.discountValue)} off`}
              </p>
            )}
          </div>
        </div>

        {/* Right: Summary */}
        <div>
          <div className="bg-white rounded-xl border border-gray-200 p-6 sticky top-24">
            <h2 className="font-bold text-gray-900 text-lg mb-4">Order Summary</h2>

            <div className="space-y-2 mb-4 max-h-48 overflow-y-auto">
              {cart.items.map((item) => (
                <div key={item.id} className="flex justify-between text-sm text-gray-600">
                  <span className="truncate mr-2">{item.productName} × {item.quantity}</span>
                  <span className="shrink-0">{formatCurrency(item.subtotal)}</span>
                </div>
              ))}
            </div>

            <div className="border-t border-gray-200 pt-3 space-y-2 text-sm">
              <div className="flex justify-between text-gray-600">
                <span>Subtotal</span>
                <span>{formatCurrency(cart.totalPrice)}</span>
              </div>
              {discount > 0 && (
                <div className="flex justify-between text-green-700">
                  <span>Discount</span>
                  <span>−{formatCurrency(discount)}</span>
                </div>
              )}
              <div className="flex justify-between text-gray-600">
                <span>Shipping</span>
                <span className="text-green-600">Free</span>
              </div>
              <div className="flex justify-between font-bold text-gray-900 text-base border-t border-gray-200 pt-2">
                <span>Total</span>
                <span>{formatCurrency(total)}</span>
              </div>
            </div>

            <Button
              size="lg"
              className="w-full mt-4"
              loading={orderMutation.isPending}
              onClick={handlePlaceOrder}
            >
              Place Order
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
