import api from './axios'
import type {
  ApiResponse,
  PageResponse,
  OrderResponse,
  OrderSummaryResponse,
  CreateOrderRequest,
  UpdateOrderStatusRequest,
  UpdateShipmentRequest,
  ShipmentResponse,
} from '../types'

export const createOrder = async (data: CreateOrderRequest) => {
  const res = await api.post<ApiResponse<OrderResponse>>('/orders', data)
  return res.data.data
}

export const getMyOrders = async (page = 0, size = 10) => {
  const res = await api.get<ApiResponse<PageResponse<OrderSummaryResponse>>>('/orders', {
    params: { page, size },
  })
  return res.data.data
}

export const getOrder = async (id: number) => {
  const res = await api.get<ApiResponse<OrderResponse>>(`/orders/${id}`)
  return res.data.data
}

export const getShipment = async (orderId: number) => {
  const res = await api.get<ApiResponse<ShipmentResponse>>(`/orders/${orderId}/shipment`)
  return res.data.data
}

// Admin endpoints
export const getAllOrders = async (page = 0, size = 20) => {
  const res = await api.get<ApiResponse<PageResponse<OrderSummaryResponse>>>('/admin/orders', {
    params: { page, size },
  })
  return res.data.data
}

export const updateOrderStatus = async (id: number, data: UpdateOrderStatusRequest) => {
  const res = await api.patch<ApiResponse<OrderResponse>>(`/admin/orders/${id}/status`, data)
  return res.data.data
}

export const updateShipment = async (orderId: number, data: UpdateShipmentRequest) => {
  const res = await api.put<ApiResponse<ShipmentResponse>>(
    `/admin/orders/${orderId}/shipment`,
    data
  )
  return res.data.data
}
