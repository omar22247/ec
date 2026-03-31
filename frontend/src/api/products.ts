import api from './axios'
import type {
  ApiResponse,
  PageResponse,
  ProductResponse,
  ProductDetailResponse,
  ProductRequest,
} from '../types'

export const getProducts = async (page = 0, size = 12, sort = 'id') => {
  const res = await api.get<ApiResponse<PageResponse<ProductResponse>>>('/products', {
    params: { page, size, sort },
  })
  return res.data.data
}

export const getProduct = async (id: number) => {
  const res = await api.get<ApiResponse<ProductDetailResponse>>(`/products/${id}`)
  return res.data.data
}

export const getProductsByCategory = async (categoryId: number, page = 0, size = 12) => {
  const res = await api.get<ApiResponse<PageResponse<ProductResponse>>>(
    `/products/category/${categoryId}`,
    { params: { page, size } }
  )
  return res.data.data
}

export const searchProducts = async (keyword: string, page = 0, size = 12) => {
  const res = await api.get<ApiResponse<PageResponse<ProductResponse>>>('/products/search', {
    params: { keyword, page, size },
  })
  return res.data.data
}

export const filterProducts = async (
  minPrice: number,
  maxPrice: number,
  page = 0,
  size = 12
) => {
  const res = await api.get<ApiResponse<PageResponse<ProductResponse>>>('/products/filter', {
    params: { minPrice, maxPrice, page, size },
  })
  return res.data.data
}

export const getAdminProducts = async (page = 0, size = 15, sort = 'id') => {
  const res = await api.get<ApiResponse<PageResponse<ProductResponse>>>('/products/admin/all', {
    params: { page, size, sort },
  })
  return res.data.data
}

export const createProduct = async (data: ProductRequest) => {
  const res = await api.post<ApiResponse<ProductDetailResponse>>('/products', data)
  return res.data.data
}

export const updateProduct = async (id: number, data: ProductRequest) => {
  const res = await api.put<ApiResponse<ProductDetailResponse>>(`/products/${id}`, data)
  return res.data.data
}

export const deleteProduct = async (id: number) => {
  const res = await api.delete<ApiResponse<null>>(`/products/${id}`)
  return res.data
}

export const toggleProduct = async (id: number) => {
  const res = await api.patch<ApiResponse<ProductDetailResponse>>(`/products/${id}/toggle`)
  return res.data.data
}
