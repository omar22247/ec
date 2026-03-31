import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getAllOrders, updateOrderStatus, updateShipment } from '../../api/orders'
import { formatCurrency, formatDate, getErrorMessage } from '../../utils/format'
import Button from '../../components/ui/Button'
import Modal from '../../components/ui/Modal'
import Badge from '../../components/ui/Badge'
import Pagination from '../../components/ui/Pagination'
import { PageSpinner } from '../../components/ui/Spinner'
import toast from 'react-hot-toast'
import type { OrderStatus, OrderSummaryResponse, ShipmentStatus } from '../../types'

const ORDER_STATUSES: OrderStatus[] = ['PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED']
const SHIP_STATUSES: ShipmentStatus[] = ['PREPARING', 'SHIPPED', 'DELIVERED', 'RETURNED']

const STATUS_COLOR: Record<OrderStatus, Parameters<typeof Badge>[0]['color']> = {
  PENDING: 'yellow', PAID: 'blue', SHIPPED: 'indigo', DELIVERED: 'green', CANCELLED: 'red',
}

export default function AdminOrders() {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)
  const [selectedOrder, setSelectedOrder] = useState<OrderSummaryResponse | null>(null)
  const [statusModal, setStatusModal] = useState(false)
  const [shipmentModal, setShipmentModal] = useState(false)
  const [newStatus, setNewStatus] = useState<OrderStatus>('PENDING')
  const [shipData, setShipData] = useState({ status: 'PREPARING' as ShipmentStatus, carrier: '', trackingNumber: '', estimatedDelivery: '' })

  const { data, isLoading } = useQuery({
    queryKey: ['admin-orders', page],
    queryFn: () => getAllOrders(page, 20),
  })

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ['admin-orders'] })

  const statusMutation = useMutation({
    mutationFn: () => updateOrderStatus(selectedOrder!.id, { status: newStatus }),
    onSuccess: () => { invalidate(); setStatusModal(false); toast.success('Order status updated') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const shipmentMutation = useMutation({
    mutationFn: () => updateShipment(selectedOrder!.id, {
      status: shipData.status,
      carrier: shipData.carrier || undefined,
      trackingNumber: shipData.trackingNumber || undefined,
      estimatedDelivery: shipData.estimatedDelivery || undefined,
    }),
    onSuccess: () => { invalidate(); setShipmentModal(false); toast.success('Shipment updated') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const openStatusModal = (order: OrderSummaryResponse) => {
    setSelectedOrder(order)
    setNewStatus(order.status)
    setStatusModal(true)
  }

  const openShipmentModal = (order: OrderSummaryResponse) => {
    setSelectedOrder(order)
    setShipData({ status: 'PREPARING', carrier: '', trackingNumber: '', estimatedDelivery: '' })
    setShipmentModal(true)
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Orders</h1>

      {isLoading ? (
        <PageSpinner />
      ) : (
        <>
          <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  {['Order', 'Date', 'Items', 'Total', 'Status', 'Actions'].map((h) => (
                    <th key={h} className="text-left px-4 py-3 font-semibold text-gray-600 text-xs uppercase tracking-wide">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {data?.content.map((order) => (
                  <tr key={order.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 font-medium text-gray-900">#{order.id}</td>
                    <td className="px-4 py-3 text-gray-500">{formatDate(order.createdAt)}</td>
                    <td className="px-4 py-3 text-gray-700">{order.totalItems}</td>
                    <td className="px-4 py-3 font-semibold">{formatCurrency(order.totalPrice)}</td>
                    <td className="px-4 py-3">
                      <Badge color={STATUS_COLOR[order.status]}>{order.status}</Badge>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => openStatusModal(order)}
                          className="text-xs text-primary-600 hover:underline font-medium"
                        >
                          Status
                        </button>
                        <button
                          onClick={() => openShipmentModal(order)}
                          className="text-xs text-gray-500 hover:text-gray-700"
                        >
                          Shipment
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {data && (
            <div className="mt-4">
              <Pagination page={data.page} totalPages={data.totalPages} onPageChange={setPage} />
            </div>
          )}
        </>
      )}

      {/* Status Modal */}
      <Modal open={statusModal} onClose={() => setStatusModal(false)} title={`Update Order #${selectedOrder?.id} Status`} size="sm">
        <div className="space-y-3">
          {ORDER_STATUSES.map((s) => (
            <label key={s} className={`flex items-center gap-3 p-3 rounded-lg border cursor-pointer transition-colors ${
              newStatus === s ? 'border-primary-500 bg-primary-50' : 'border-gray-200 hover:border-gray-300'
            }`}>
              <input type="radio" value={s} checked={newStatus === s} onChange={() => setNewStatus(s)} className="accent-primary-600" />
              <Badge color={STATUS_COLOR[s]}>{s}</Badge>
            </label>
          ))}
          <div className="flex justify-end gap-3 pt-2">
            <Button variant="outline" onClick={() => setStatusModal(false)}>Cancel</Button>
            <Button loading={statusMutation.isPending} onClick={() => statusMutation.mutate()}>Update</Button>
          </div>
        </div>
      </Modal>

      {/* Shipment Modal */}
      <Modal open={shipmentModal} onClose={() => setShipmentModal(false)} title={`Shipment for Order #${selectedOrder?.id}`}>
        <div className="space-y-4">
          <div className="space-y-1">
            <label className="block text-sm font-medium text-gray-700">Shipment Status</label>
            <select
              value={shipData.status}
              onChange={(e) => setShipData({ ...shipData, status: e.target.value as ShipmentStatus })}
              className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              {SHIP_STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>
          </div>
          {(['carrier', 'trackingNumber'] as const).map((field) => (
            <div key={field} className="space-y-1">
              <label className="block text-sm font-medium text-gray-700 capitalize">
                {field === 'trackingNumber' ? 'Tracking Number' : 'Carrier'}
              </label>
              <input
                value={shipData[field]}
                onChange={(e) => setShipData({ ...shipData, [field]: e.target.value })}
                className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
                placeholder={field === 'carrier' ? 'e.g. FedEx' : 'e.g. 1Z999AA10123456784'}
              />
            </div>
          ))}
          <div className="space-y-1">
            <label className="block text-sm font-medium text-gray-700">Estimated Delivery</label>
            <input
              type="datetime-local"
              value={shipData.estimatedDelivery}
              onChange={(e) => setShipData({ ...shipData, estimatedDelivery: e.target.value })}
              className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <Button variant="outline" onClick={() => setShipmentModal(false)}>Cancel</Button>
            <Button loading={shipmentMutation.isPending} onClick={() => shipmentMutation.mutate()}>Save</Button>
          </div>
        </div>
      </Modal>
    </div>
  )
}
