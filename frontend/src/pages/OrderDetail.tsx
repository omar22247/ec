import { useParams, Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { getOrder } from '../api/orders'
import { formatCurrency, formatDate, formatDateTime } from '../utils/format'
import { PageSpinner } from '../components/ui/Spinner'
import Badge from '../components/ui/Badge'
import type { OrderStatus, ShipmentStatus } from '../types'

const ORDER_COLOR: Record<OrderStatus, Parameters<typeof Badge>[0]['color']> = {
  PENDING: 'yellow', PAID: 'blue', SHIPPED: 'indigo', DELIVERED: 'green', CANCELLED: 'red',
}
const SHIP_COLOR: Record<ShipmentStatus, Parameters<typeof Badge>[0]['color']> = {
  PREPARING: 'orange', SHIPPED: 'indigo', DELIVERED: 'green', RETURNED: 'gray',
}

const PLACEHOLDER = 'https://placehold.co/64x64/e0e7ff/4f46e5?text=?'

export default function OrderDetail() {
  const { id } = useParams<{ id: string }>()

  const { data: order, isLoading } = useQuery({
    queryKey: ['order', Number(id)],
    queryFn: () => getOrder(Number(id)),
  })

  if (isLoading) return <PageSpinner />
  if (!order) return <div className="text-center py-20 text-gray-500">Order not found.</div>

  const steps: { status: OrderStatus; label: string }[] = [
    { status: 'PENDING', label: 'Placed' },
    { status: 'PAID', label: 'Paid' },
    { status: 'SHIPPED', label: 'Shipped' },
    { status: 'DELIVERED', label: 'Delivered' },
  ]
  const stepIdx = steps.findIndex((s) => s.status === order.status)
  const isCancelled = order.status === 'CANCELLED'

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="flex items-center gap-3 mb-2">
        <Link to="/orders" className="text-primary-600 hover:underline text-sm">← My Orders</Link>
      </div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Order #{order.id}</h1>
          <p className="text-sm text-gray-500 mt-1">Placed on {formatDate(order.createdAt)}</p>
        </div>
        <Badge color={ORDER_COLOR[order.status]}>{order.status}</Badge>
      </div>

      {/* Progress */}
      {!isCancelled && (
        <div className="bg-white rounded-xl border border-gray-200 p-6 mb-6">
          <div className="flex items-center justify-between relative">
            <div className="absolute left-0 right-0 top-4 h-0.5 bg-gray-200" />
            <div
              className="absolute left-0 top-4 h-0.5 bg-primary-500 transition-all"
              style={{ width: `${(stepIdx / (steps.length - 1)) * 100}%` }}
            />
            {steps.map((step, i) => (
              <div key={step.status} className="relative flex flex-col items-center gap-2 z-10">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center border-2 text-xs font-bold transition-colors ${
                  i <= stepIdx
                    ? 'bg-primary-600 border-primary-600 text-white'
                    : 'bg-white border-gray-300 text-gray-400'
                }`}>
                  {i < stepIdx ? '✓' : i + 1}
                </div>
                <span className={`text-xs font-medium ${i <= stepIdx ? 'text-primary-600' : 'text-gray-400'}`}>
                  {step.label}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        {/* Shipping Address */}
        <div className="bg-white rounded-xl border border-gray-200 p-5">
          <h2 className="font-semibold text-gray-900 mb-3">Shipping Address</h2>
          <div className="text-sm text-gray-600 space-y-0.5">
            <p className="font-medium text-gray-900">{order.address.fullName}</p>
            <p>{order.address.street}</p>
            <p>{order.address.city}, {order.address.country} {order.address.zipCode}</p>
            <p>{order.address.phone}</p>
          </div>
        </div>

        {/* Shipment */}
        {order.shipment && (
          <div className="bg-white rounded-xl border border-gray-200 p-5">
            <h2 className="font-semibold text-gray-900 mb-3">Shipment Tracking</h2>
            <div className="space-y-2 text-sm">
              <div className="flex items-center justify-between">
                <span className="text-gray-600">Status</span>
                <Badge color={SHIP_COLOR[order.shipment.status]}>{order.shipment.status}</Badge>
              </div>
              {order.shipment.carrier && (
                <div className="flex justify-between text-gray-600">
                  <span>Carrier</span><span>{order.shipment.carrier}</span>
                </div>
              )}
              {order.shipment.trackingNumber && (
                <div className="flex justify-between text-gray-600">
                  <span>Tracking #</span>
                  <span className="font-mono text-xs">{order.shipment.trackingNumber}</span>
                </div>
              )}
              {order.shipment.estimatedDelivery && (
                <div className="flex justify-between text-gray-600">
                  <span>Est. Delivery</span>
                  <span>{formatDate(order.shipment.estimatedDelivery)}</span>
                </div>
              )}
            </div>
          </div>
        )}
      </div>

      {/* Items */}
      <div className="bg-white rounded-xl border border-gray-200 p-5 mb-6">
        <h2 className="font-semibold text-gray-900 mb-4">Items ({order.items.length})</h2>
        <div className="divide-y divide-gray-100">
          {order.items.map((item) => (
            <div key={item.id} className="flex gap-4 py-3">
              <img
                src={item.productImage ?? PLACEHOLDER}
                alt={item.productName}
                className="w-16 h-16 object-cover rounded-lg border border-gray-100 shrink-0"
                onError={(e) => { (e.target as HTMLImageElement).src = PLACEHOLDER }}
              />
              <div className="flex-1 min-w-0">
                <Link
                  to={`/products/${item.productId}`}
                  className="font-medium text-gray-900 hover:text-primary-600 text-sm line-clamp-2"
                >
                  {item.productName}
                </Link>
                <p className="text-xs text-gray-500 mt-1">
                  {formatCurrency(item.priceAtPurchase)} × {item.quantity}
                </p>
              </div>
              <p className="font-semibold text-gray-900 text-sm shrink-0">
                {formatCurrency(item.subtotal)}
              </p>
            </div>
          ))}
        </div>
      </div>

      {/* Totals */}
      <div className="bg-white rounded-xl border border-gray-200 p-5">
        <h2 className="font-semibold text-gray-900 mb-4">Payment Summary</h2>
        <div className="space-y-2 text-sm">
          <div className="flex justify-between text-gray-600">
            <span>Subtotal</span><span>{formatCurrency(order.originalPrice)}</span>
          </div>
          {order.discountAmount > 0 && (
            <div className="flex justify-between text-green-700">
              <span>Discount {order.couponCode ? `(${order.couponCode})` : ''}</span>
              <span>−{formatCurrency(order.discountAmount)}</span>
            </div>
          )}
          <div className="flex justify-between text-gray-600">
            <span>Shipping</span><span className="text-green-600">Free</span>
          </div>
          <div className="flex justify-between font-bold text-gray-900 border-t border-gray-200 pt-2 text-base">
            <span>Total</span><span>{formatCurrency(order.totalPrice)}</span>
          </div>
        </div>
      </div>
    </div>
  )
}
