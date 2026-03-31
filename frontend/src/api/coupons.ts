import api from './axios'
import type {
  ApiResponse,
  CouponResponse,
  CouponValidationResponse,
  CouponRequest,
} from '../types'

export const validateCoupon = async (code: string) => {
  const res = await api.post<ApiResponse<CouponValidationResponse>>(
    `/coupons/validate?code=${encodeURIComponent(code)}`
  )
  return res.data.data
}

// Admin endpoints
export const getCoupons = async () => {
  const res = await api.get<ApiResponse<CouponResponse[]>>('/admin/coupons')
  return res.data.data
}

export const getCoupon = async (id: number) => {
  const res = await api.get<ApiResponse<CouponResponse>>(`/admin/coupons/${id}`)
  return res.data.data
}

export const createCoupon = async (data: CouponRequest) => {
  const res = await api.post<ApiResponse<CouponResponse>>('/admin/coupons', data)
  return res.data.data
}

export const updateCoupon = async (id: number, data: CouponRequest) => {
  const res = await api.put<ApiResponse<CouponResponse>>(`/admin/coupons/${id}`, data)
  return res.data.data
}

export const deleteCoupon = async (id: number) => {
  const res = await api.delete<ApiResponse<null>>(`/admin/coupons/${id}`)
  return res.data
}

export const toggleCoupon = async (id: number) => {
  const res = await api.patch<ApiResponse<CouponResponse>>(`/admin/coupons/${id}/toggle`)
  return res.data.data
}
