import api from './axios'
import type { ApiResponse, CartResponse, CartItemRequest, UpdateCartItemRequest } from '../types'

export const getCart = async () => {
  const res = await api.get<ApiResponse<CartResponse>>('/cart')
  return res.data.data
}

export const addToCart = async (data: CartItemRequest) => {
  const res = await api.post<ApiResponse<CartResponse>>('/cart/items', data)
  return res.data.data
}

export const updateCartItem = async (itemId: number, data: UpdateCartItemRequest) => {
  const res = await api.put<ApiResponse<CartResponse>>(`/cart/items/${itemId}`, data)
  return res.data.data
}

export const removeCartItem = async (itemId: number) => {
  const res = await api.delete<ApiResponse<CartResponse>>(`/cart/items/${itemId}`)
  return res.data.data
}

export const clearCart = async () => {
  const res = await api.delete<ApiResponse<null>>('/cart')
  return res.data
}
