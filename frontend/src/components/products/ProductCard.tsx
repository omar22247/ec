import { Link } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { useCart } from '../../context/CartContext'
import StarRating from './StarRating'
import Badge from '../ui/Badge'
import { formatCurrency } from '../../utils/format'
import type { ProductResponse } from '../../types'
import { addToWishlist, removeFromWishlist } from '../../api/wishlist'
import { useState } from 'react'
import toast from 'react-hot-toast'

interface ProductCardProps {
  product: ProductResponse
  inWishlist?: boolean
  onWishlistToggle?: () => void
}

const PLACEHOLDER = 'https://placehold.co/400x300/e0e7ff/4f46e5?text=No+Image'

export default function ProductCard({ product, inWishlist = false, onWishlistToggle }: ProductCardProps) {
  const { isAuthenticated } = useAuth()
  const { addItem } = useCart()
  const [adding, setAdding] = useState(false)
  const [wishlistLoading, setWishlistLoading] = useState(false)

  const handleAddToCart = async (e: React.MouseEvent) => {
    e.preventDefault()
    if (!isAuthenticated) { toast.error('Please sign in to add items to cart'); return }
    setAdding(true)
    await addItem(product.id)
    setAdding(false)
  }

  const handleWishlist = async (e: React.MouseEvent) => {
    e.preventDefault()
    if (!isAuthenticated) { toast.error('Please sign in first'); return }
    setWishlistLoading(true)
    try {
      if (inWishlist) {
        await removeFromWishlist(product.id)
        toast.success('Removed from wishlist')
      } else {
        await addToWishlist(product.id)
        toast.success('Added to wishlist')
      }
      onWishlistToggle?.()
    } catch {
      toast.error('Failed to update wishlist')
    } finally {
      setWishlistLoading(false)
    }
  }

  return (
    <Link to={`/products/${product.id}`} className="group block">
      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-md transition-shadow">
        {/* Image */}
        <div className="relative aspect-[4/3] bg-gray-100 overflow-hidden">
          <img
            src={product.imageUrl ?? PLACEHOLDER}
            alt={product.name}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            onError={(e) => { (e.target as HTMLImageElement).src = PLACEHOLDER }}
          />
          {/* Stock badge */}
          <div className="absolute top-2 left-2">
            {!product.inStock && <Badge color="red">Out of Stock</Badge>}
            {product.inStock && product.stock <= 5 && (
              <Badge color="orange">Low Stock</Badge>
            )}
          </div>
          {/* Wishlist */}
          <button
            onClick={handleWishlist}
            disabled={wishlistLoading}
            className="absolute top-2 right-2 w-8 h-8 bg-white rounded-full shadow flex items-center justify-center text-gray-400 hover:text-red-500 transition-colors"
          >
            <svg
              className="w-4 h-4"
              viewBox="0 0 24 24"
              fill={inWishlist ? 'currentColor' : 'none'}
              stroke="currentColor"
              strokeWidth={2}
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
              />
            </svg>
          </button>
        </div>

        {/* Info */}
        <div className="p-4">
          <p className="text-xs text-gray-400 mb-1">{product.categoryName}</p>
          <h3 className="font-medium text-gray-900 line-clamp-2 mb-2 group-hover:text-primary-600 transition-colors">
            {product.name}
          </h3>

          {/* Rating */}
          {product.reviewCount > 0 && (
            <div className="flex items-center gap-1.5 mb-2">
              <StarRating rating={product.averageRating ?? 0} />
              <span className="text-xs text-gray-500">({product.reviewCount})</span>
            </div>
          )}

          <div className="flex items-center justify-between mt-3">
            <span className="text-lg font-bold text-gray-900">
              {formatCurrency(product.basePrice)}
            </span>
            <button
              onClick={handleAddToCart}
              disabled={!product.inStock || adding}
              className="px-3 py-1.5 bg-primary-600 text-white text-xs font-medium rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {adding ? '...' : 'Add to Cart'}
            </button>
          </div>
        </div>
      </div>
    </Link>
  )
}
