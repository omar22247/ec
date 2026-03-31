import { Link } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'
import { formatCurrency } from '../utils/format'
import Button from '../components/ui/Button'

const PLACEHOLDER = 'https://placehold.co/80x80/e0e7ff/4f46e5?text=?'

export default function Cart() {
  const { cart, loading, updateItem, removeItem, clear } = useCart()
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-24 text-center">
        <p className="text-5xl mb-4">🛒</p>
        <h2 className="text-2xl font-bold text-gray-900 mb-2">Your cart is empty</h2>
        <p className="text-gray-500 mb-6">Sign in to view or add items to your cart.</p>
        <Link to="/login">
          <Button size="lg">Sign In</Button>
        </Link>
      </div>
    )
  }

  if (loading) return <div className="flex justify-center py-24"><div className="animate-spin h-8 w-8 border-4 border-primary-600 border-t-transparent rounded-full" /></div>

  if (!cart || cart.items.length === 0) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-24 text-center">
        <p className="text-5xl mb-4">🛒</p>
        <h2 className="text-2xl font-bold text-gray-900 mb-2">Your cart is empty</h2>
        <p className="text-gray-500 mb-6">Start shopping and add some items!</p>
        <Link to="/products">
          <Button size="lg">Browse Products</Button>
        </Link>
      </div>
    )
  }

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Shopping Cart</h1>
        <button
          onClick={clear}
          className="text-sm text-red-500 hover:text-red-700 transition-colors"
        >
          Clear cart
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Items */}
        <div className="lg:col-span-2 space-y-4">
          {cart.items.map((item) => (
            <div key={item.id} className="bg-white rounded-xl border border-gray-200 p-4 flex gap-4">
              <img
                src={item.productImage ?? PLACEHOLDER}
                alt={item.productName}
                className="w-20 h-20 object-cover rounded-lg border border-gray-100 shrink-0"
                onError={(e) => { (e.target as HTMLImageElement).src = PLACEHOLDER }}
              />
              <div className="flex-1 min-w-0">
                <Link
                  to={`/products/${item.productId}`}
                  className="font-medium text-gray-900 hover:text-primary-600 transition-colors line-clamp-2"
                >
                  {item.productName}
                </Link>
                <p className="text-primary-600 font-bold mt-1">{formatCurrency(item.unitPrice)}</p>
                {!item.inStock && (
                  <p className="text-xs text-red-500 mt-1">⚠ Out of stock</p>
                )}
                <div className="flex items-center justify-between mt-3">
                  {/* Qty control */}
                  <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden">
                    <button
                      onClick={() => item.quantity > 1 ? updateItem(item.id, item.quantity - 1) : removeItem(item.id)}
                      className="px-3 py-1 text-gray-600 hover:bg-gray-50 transition-colors text-sm"
                    >
                      −
                    </button>
                    <span className="px-3 py-1 text-sm font-medium border-x border-gray-300">
                      {item.quantity}
                    </span>
                    <button
                      onClick={() => updateItem(item.id, item.quantity + 1)}
                      className="px-3 py-1 text-gray-600 hover:bg-gray-50 transition-colors text-sm"
                    >
                      +
                    </button>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="font-semibold text-gray-900">{formatCurrency(item.subtotal)}</span>
                    <button
                      onClick={() => removeItem(item.id)}
                      className="text-gray-400 hover:text-red-500 transition-colors"
                    >
                      <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Summary */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-xl border border-gray-200 p-6 sticky top-24">
            <h2 className="font-bold text-gray-900 text-lg mb-4">Order Summary</h2>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between text-gray-600">
                <span>Items ({cart.totalItems})</span>
                <span>{formatCurrency(cart.totalPrice)}</span>
              </div>
              <div className="flex justify-between text-gray-600">
                <span>Shipping</span>
                <span className="text-green-600">Free</span>
              </div>
            </div>
            <div className="border-t border-gray-200 mt-4 pt-4 flex justify-between font-bold text-gray-900">
              <span>Total</span>
              <span>{formatCurrency(cart.totalPrice)}</span>
            </div>
            <Link to="/checkout" className="mt-4 block">
              <Button size="lg" className="w-full">
                Proceed to Checkout
              </Button>
            </Link>
            <Link to="/products" className="mt-3 block text-center text-sm text-primary-600 hover:underline">
              Continue Shopping
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
