import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { getMyOrders } from '../api/orders'
import { formatCurrency, formatDate, statusColor } from '../utils/format'
import Pagination from '../components/ui/Pagination'
import { PageSpinner } from '../components/ui/Spinner'
import Badge from '../components/ui/Badge'
import type { OrderStatus } from '../types'

const STATUS_COLOR_MAP: Record<OrderStatus, Parameters<typeof Badge>[0]['color']> = {
  PENDING:   'yellow',
  PAID:      'blue',
  SHIPPED:   'indigo',
  DELIVERED: 'green',
  CANCELLED: 'red',
}

export default function Orders() {
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['orders', page],
    queryFn: () => getMyOrders(page),
  })

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">My Orders</h1>

      {isLoading ? (
        <PageSpinner />
      ) : !data || data.content.length === 0 ? (
        <div className="bg-white rounded-xl border border-gray-200 p-16 text-center">
          <p className="text-4xl mb-4">📦</p>
          <p className="text-lg font-semibold text-gray-900 mb-2">No orders yet</p>
          <p className="text-gray-500 mb-4 text-sm">Your orders will appear here once you make a purchase.</p>
          <Link to="/products" className="text-primary-600 hover:underline font-medium text-sm">
            Start Shopping →
          </Link>
        </div>
      ) : (
        <>
          <div className="space-y-3">
            {data.content.map((order) => (
              <Link
                key={order.id}
                to={`/orders/${order.id}`}
                className="block bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md transition-shadow"
              >
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <span className="font-semibold text-gray-900">Order #{order.id}</span>
                      <Badge color={STATUS_COLOR_MAP[order.status]}>
                        {order.status}
                      </Badge>
                    </div>
                    <p className="text-sm text-gray-500">
                      {order.totalItems} item{order.totalItems !== 1 ? 's' : ''} · Placed {formatDate(order.createdAt)}
                    </p>
                    {order.couponCode && (
                      <p className="text-xs text-green-600 mt-1">Coupon: {order.couponCode}</p>
                    )}
                  </div>
                  <div className="text-right shrink-0">
                    <p className="font-bold text-gray-900">{formatCurrency(order.totalPrice)}</p>
                    <p className="text-xs text-primary-600 mt-1">View details →</p>
                  </div>
                </div>
              </Link>
            ))}
          </div>

          <div className="mt-6">
            <Pagination
              page={data.page}
              totalPages={data.totalPages}
              onPageChange={setPage}
            />
          </div>
        </>
      )}
    </div>
  )
}
