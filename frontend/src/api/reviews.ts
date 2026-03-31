import api from './axios'
import type { ApiResponse, PageResponse, ReviewResponse, ReviewRequest } from '../types'

export const getReviews = async (productId: number, page = 0, size = 10) => {
  const res = await api.get<ApiResponse<PageResponse<ReviewResponse>>>(
    `/products/${productId}/reviews`,
    { params: { page, size } }
  )
  return res.data.data
}

export const createReview = async (productId: number, data: ReviewRequest) => {
  const res = await api.post<ApiResponse<ReviewResponse>>(`/products/${productId}/reviews`, data)
  return res.data.data
}

export const deleteReview = async (productId: number, reviewId: number) => {
  const res = await api.delete<ApiResponse<null>>(`/products/${productId}/reviews/${reviewId}`)
  return res.data
}
