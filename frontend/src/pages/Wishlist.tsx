import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { getWishlist, removeFromWishlist, clearWishlist } from '../api/wishlist'
import { useCart } from '../context/CartContext'
import { formatCurrency, formatDate } from '../utils/format'
import Button from '../components/ui/Button'
import Badge from '../components/ui/Badge'
import { PageSpinner } from '../components/ui/Spinner'
import toast from 'react-hot-toast'

const PLACEHOLDER = 'https://placehold.co/80x80/e0e7ff/4f46e5?text=?'

export default function Wishlist() {
  const queryClient = useQueryClient()
  const { addItem } = useCart()

  const { data: items, isLoading } = useQuery({
    queryKey: ['wishlist'],
    queryFn: getWishlist,
  })

  const removeMutation = useMutation({
    mutationFn: removeFromWishlist,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['wishlist'] })
      toast.success('Removed from wishlist')
    },
  })

  const clearMutation = useMutation({
    mutationFn: clearWishlist,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['wishlist'] })
      toast.success('Wishlist cleared')
    },
  })

  if (isLoading) return <PageSpinner />

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">
          My Wishlist
          {items && items.length > 0 && (
            <span className="ml-2 text-base font-normal text-gray-500">({items.length})</span>
          )}
        </h1>
        {items && items.length > 0 && (
          <button
            onClick={() => clearMutation.mutate()}
            className="text-sm text-red-500 hover:text-red-700"
          >
            Clear all
          </button>
        )}
      </div>

      {!items || items.length === 0 ? (
        <div className="bg-white rounded-xl border border-gray-200 p-16 text-center">
          <p className="text-4xl mb-4">❤️</p>
          <p className="text-lg font-semibold text-gray-900 mb-2">Your wishlist is empty</p>
          <p className="text-gray-500 text-sm mb-4">Save items you love to come back to them later.</p>
          <Link to="/products">
            <Button>Browse Products</Button>
          </Link>
        </div>
      ) : (
        <div className="space-y-3">
          {items.map((item) => (
            <div key={item.wishlistItemId} className="bg-white rounded-xl border border-gray-200 p-4 flex gap-4">
              <Link to={`/products/${item.productId}`} className="shrink-0">
                <img
                  src={item.productImage ?? PLACEHOLDER}
                  alt={item.productName}
                  className="w-20 h-20 object-cover rounded-lg border border-gray-100"
                  onError={(e) => { (e.target as HTMLImageElement).src = PLACEHOLDER }}
                />
              </Link>
              <div className="flex-1 min-w-0">
                <Link
                  to={`/products/${item.productId}`}
                  className="font-medium text-gray-900 hover:text-primary-600 transition-colors line-clamp-2"
                >
                  {item.productName}
                </Link>
                <p className="text-primary-600 font-bold mt-1">{formatCurrency(item.price)}</p>
                <div className="flex items-center gap-2 mt-1">
                  {item.inStock
                    ? <Badge color="green">In Stock</Badge>
                    : <Badge color="red">Out of Stock</Badge>
                  }
                  <span className="text-xs text-gray-400">Added {formatDate(item.addedAt)}</span>
                </div>
              </div>
              <div className="flex flex-col gap-2 shrink-0">
                <Button
                  size="sm"
                  disabled={!item.inStock}
                  onClick={() => addItem(item.productId)}
                >
                  Add to Cart
                </Button>
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => removeMutation.mutate(item.productId)}
                  className="text-red-500 hover:bg-red-50"
                >
                  Remove
                </Button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
