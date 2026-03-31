import api from './axios'
import type { ApiResponse, WishlistItemResponse } from '../types'

export const getWishlist = async () => {
  const res = await api.get<ApiResponse<WishlistItemResponse[]>>('/wishlist')
  return res.data.data
}

export const addToWishlist = async (productId: number) => {
  const res = await api.post<ApiResponse<WishlistItemResponse>>(`/wishlist/${productId}`)
  return res.data.data
}

export const removeFromWishlist = async (productId: number) => {
  const res = await api.delete<ApiResponse<null>>(`/wishlist/${productId}`)
  return res.data
}

export const clearWishlist = async () => {
  const res = await api.delete<ApiResponse<null>>('/wishlist')
  return res.data
}
