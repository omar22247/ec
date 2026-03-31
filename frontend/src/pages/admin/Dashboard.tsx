import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { getAllOrders } from '../../api/orders'
import { getProducts } from '../../api/products'
import { getCategories } from '../../api/categories'
import { getCoupons } from '../../api/coupons'
import { formatCurrency, formatDate } from '../../utils/format'
import Badge from '../../components/ui/Badge'
import { PageSpinner } from '../../components/ui/Spinner'
import type { OrderStatus } from '../../types'

const STATUS_COLOR: Record<OrderStatus, Parameters<typeof Badge>[0]['color']> = {
  PENDING: 'yellow', PAID: 'blue', SHIPPED: 'indigo', DELIVERED: 'green', CANCELLED: 'red',
}

export default function AdminDashboard() {
  const { data: orders } = useQuery({ queryKey: ['admin-orders', 0], queryFn: () => getAllOrders(0, 5) })
  const { data: products } = useQuery({ queryKey: ['products', 0, 12, 'id'], queryFn: () => getProducts(0, 1) })
  const { data: categories } = useQuery({ queryKey: ['categories'], queryFn: getCategories })
  const { data: coupons } = useQuery({ queryKey: ['admin-coupons'], queryFn: getCoupons })

  const stats = [
    { label: 'Total Products', value: products?.totalElements ?? '—', icon: '📦', href: '/admin/products', color: 'bg-blue-50 text-blue-700' },
    { label: 'Categories', value: categories?.length ?? '—', icon: '🏷️', href: '/admin/categories', color: 'bg-purple-50 text-purple-700' },
    { label: 'Total Orders', value: orders?.totalElements ?? '—', icon: '🧾', href: '/admin/orders', color: 'bg-green-50 text-green-700' },
    { label: 'Active Coupons', value: coupons?.filter((c) => c.active && c.valid).length ?? '—', icon: '🎟️', href: '/admin/coupons', color: 'bg-orange-50 text-orange-700' },
  ]

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-8">Dashboard</h1>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {stats.map(({ label, value, icon, href, color }) => (
          <Link
            key={label}
            to={href}
            className="bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md transition-shadow"
          >
            <div className={`w-10 h-10 rounded-lg flex items-center justify-center text-xl mb-3 ${color}`}>
              {icon}
            </div>
            <p className="text-2xl font-bold text-gray-900">{value}</p>
            <p className="text-sm text-gray-500 mt-0.5">{label}</p>
          </Link>
        ))}
      </div>

      {/* Recent Orders */}
      <div className="bg-white rounded-xl border border-gray-200">
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h2 className="font-semibold text-gray-900">Recent Orders</h2>
          <Link to="/admin/orders" className="text-sm text-primary-600 hover:underline">
            View all →
          </Link>
        </div>

        {!orders ? (
          <div className="p-8"><PageSpinner /></div>
        ) : orders.content.length === 0 ? (
          <div className="p-8 text-center text-gray-500 text-sm">No orders yet</div>
        ) : (
          <div className="divide-y divide-gray-100">
            {orders.content.map((order) => (
              <Link
                key={order.id}
                to={`/admin/orders`}
                className="flex items-center justify-between px-6 py-3 hover:bg-gray-50 transition-colors"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-medium text-sm text-gray-900">Order #{order.id}</span>
                    <Badge color={STATUS_COLOR[order.status]}>{order.status}</Badge>
                  </div>
                  <p className="text-xs text-gray-400 mt-0.5">{formatDate(order.createdAt)} · {order.totalItems} items</p>
                </div>
                <span className="font-semibold text-gray-900 text-sm">{formatCurrency(order.totalPrice)}</span>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
