import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getProduct } from '../api/products'
import { getReviews, createReview, deleteReview } from '../api/reviews'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'
import StarRating from '../components/products/StarRating'
import Badge from '../components/ui/Badge'
import Button from '../components/ui/Button'
import Pagination from '../components/ui/Pagination'
import { PageSpinner } from '../components/ui/Spinner'
import { formatCurrency, formatDate, timeAgo, getErrorMessage } from '../utils/format'
import toast from 'react-hot-toast'

const PLACEHOLDER = 'https://placehold.co/600x450/e0e7ff/4f46e5?text=No+Image'

export default function ProductDetail() {
  const { id } = useParams<{ id: string }>()
  const productId = Number(id)
  const { addItem } = useCart()
  const { isAuthenticated, user } = useAuth()
  const queryClient = useQueryClient()
  const [qty, setQty] = useState(1)
  const [reviewPage, setReviewPage] = useState(0)
  const [rating, setRating] = useState(0)
  const [comment, setComment] = useState('')

  const { data: product, isLoading } = useQuery({
    queryKey: ['product', productId],
    queryFn: () => getProduct(productId),
  })

  const { data: reviews } = useQuery({
    queryKey: ['reviews', productId, reviewPage],
    queryFn: () => getReviews(productId, reviewPage),
  })

  const reviewMutation = useMutation({
    mutationFn: () => createReview(productId, { rating, comment }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviews', productId] })
      queryClient.invalidateQueries({ queryKey: ['product', productId] })
      setRating(0)
      setComment('')
      toast.success('Review submitted!')
    },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const deleteMutation = useMutation({
    mutationFn: (reviewId: number) => deleteReview(productId, reviewId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviews', productId] })
      toast.success('Review deleted')
    },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const handleAddToCart = async () => {
    if (!isAuthenticated) { toast.error('Please sign in first'); return }
    await addItem(productId, qty)
  }

  if (isLoading) return <PageSpinner />
  if (!product) return <div className="text-center py-20 text-gray-500">Product not found.</div>

  const stockBadge = !product.inStock
    ? <Badge color="red">Out of Stock</Badge>
    : product.lowStock
    ? <Badge color="orange">Low Stock ({product.stock} left)</Badge>
    : <Badge color="green">In Stock</Badge>

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Breadcrumb */}
      <nav className="flex items-center gap-2 text-sm text-gray-500 mb-6">
        <Link to="/" className="hover:text-primary-600">Home</Link>
        <span>/</span>
        <Link to="/products" className="hover:text-primary-600">Products</Link>
        <span>/</span>
        <Link to={`/products?category=${product.categoryId}`} className="hover:text-primary-600">{product.categoryName}</Link>
        <span>/</span>
        <span className="text-gray-900 font-medium truncate">{product.name}</span>
      </nav>

      {/* Product */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 mb-12">
        {/* Image */}
        <div className="rounded-2xl overflow-hidden border border-gray-200 bg-gray-50 aspect-[4/3]">
          <img
            src={product.imageUrl ?? PLACEHOLDER}
            alt={product.name}
            className="w-full h-full object-cover"
            onError={(e) => { (e.target as HTMLImageElement).src = PLACEHOLDER }}
          />
        </div>

        {/* Info */}
        <div>
          <p className="text-sm text-primary-600 font-medium mb-2">{product.categoryName}</p>
          <h1 className="text-3xl font-bold text-gray-900 mb-4">{product.name}</h1>

          {/* Rating */}
          {product.reviewCount > 0 && (
            <div className="flex items-center gap-2 mb-4">
              <StarRating rating={product.averageRating ?? 0} size="md" />
              <span className="text-sm text-gray-500">
                {product.averageRating?.toFixed(1)} ({product.reviewCount} reviews)
              </span>
            </div>
          )}

          <div className="text-4xl font-bold text-gray-900 mb-4">
            {formatCurrency(product.basePrice)}
          </div>

          <div className="mb-6">{stockBadge}</div>

          {product.description && (
            <p className="text-gray-600 text-sm leading-relaxed mb-6">{product.description}</p>
          )}

          {/* Quantity & Add to Cart */}
          {product.inStock && (
            <div className="flex items-center gap-4 mb-4">
              <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden">
                <button
                  onClick={() => setQty(Math.max(1, qty - 1))}
                  className="px-3 py-2 text-gray-600 hover:bg-gray-50 transition-colors"
                >
                  −
                </button>
                <span className="px-4 py-2 text-sm font-medium text-gray-900 border-x border-gray-300">
                  {qty}
                </span>
                <button
                  onClick={() => setQty(Math.min(product.stock, qty + 1))}
                  className="px-3 py-2 text-gray-600 hover:bg-gray-50 transition-colors"
                >
                  +
                </button>
              </div>
              <Button onClick={handleAddToCart} size="lg" className="flex-1">
                Add to Cart
              </Button>
            </div>
          )}

          <div className="mt-6 p-4 bg-gray-50 rounded-xl text-sm text-gray-600 space-y-1">
            <p>✓ Secure checkout</p>
            <p>✓ 30-day returns</p>
            <p>✓ Fast delivery</p>
          </div>
        </div>
      </div>

      {/* Reviews */}
      <div className="bg-white rounded-xl border border-gray-200 p-6">
        <h2 className="text-xl font-bold text-gray-900 mb-6">
          Customer Reviews
          {reviews && reviews.totalElements > 0 && (
            <span className="ml-2 text-base font-normal text-gray-500">
              ({reviews.totalElements})
            </span>
          )}
        </h2>

        {/* Write Review */}
        {isAuthenticated ? (
          <div className="mb-8 p-4 bg-gray-50 rounded-xl">
            <h3 className="font-semibold text-gray-900 mb-3">Write a Review</h3>
            <div className="mb-3">
              <p className="text-sm text-gray-600 mb-1">Your rating</p>
              <StarRating rating={rating} size="md" interactive onRate={setRating} />
            </div>
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Share your experience with this product..."
              rows={3}
              className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
            />
            <Button
              className="mt-3"
              disabled={rating === 0 || !comment.trim()}
              loading={reviewMutation.isPending}
              onClick={() => reviewMutation.mutate()}
            >
              Submit Review
            </Button>
          </div>
        ) : (
          <div className="mb-6 p-4 bg-primary-50 rounded-xl text-sm text-primary-700">
            <Link to="/login" className="font-medium hover:underline">Sign in</Link> to leave a review
          </div>
        )}

        {/* Reviews list */}
        {reviews?.content.length === 0 ? (
          <p className="text-gray-500 text-sm text-center py-8">No reviews yet. Be the first!</p>
        ) : (
          <div className="space-y-4">
            {reviews?.content.map((review) => (
              <div key={review.id} className="flex gap-4 pb-4 border-b border-gray-100 last:border-0">
                <div className="w-10 h-10 bg-primary-100 text-primary-700 rounded-full flex items-center justify-center text-sm font-semibold shrink-0">
                  {review.userName.charAt(0).toUpperCase()}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <p className="font-medium text-gray-900 text-sm">{review.userName}</p>
                      <StarRating rating={review.rating} />
                    </div>
                    <div className="flex items-center gap-2 shrink-0">
                      <span className="text-xs text-gray-400">{timeAgo(review.createdAt)}</span>
                      {user?.id === review.userId && (
                        <button
                          onClick={() => deleteMutation.mutate(review.id)}
                          className="text-xs text-red-400 hover:text-red-600"
                        >
                          Delete
                        </button>
                      )}
                    </div>
                  </div>
                  <p className="mt-2 text-sm text-gray-600">{review.comment}</p>
                </div>
              </div>
            ))}
          </div>
        )}

        {reviews && reviews.totalPages > 1 && (
          <div className="mt-6">
            <Pagination
              page={reviews.page}
              totalPages={reviews.totalPages}
              onPageChange={setReviewPage}
            />
          </div>
        )}
      </div>
    </div>
  )
}
