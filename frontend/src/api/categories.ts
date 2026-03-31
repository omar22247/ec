import api from './axios'
import type { ApiResponse, CategoryResponse, CategoryRequest } from '../types'

export const getCategories = async () => {
  const res = await api.get<ApiResponse<CategoryResponse[]>>('/categories')
  return res.data.data
}

export const getCategory = async (id: number) => {
  const res = await api.get<ApiResponse<CategoryResponse>>(`/categories/${id}`)
  return res.data.data
}

export const getSubcategories = async (id: number) => {
  const res = await api.get<ApiResponse<CategoryResponse[]>>(`/categories/${id}/subcategories`)
  return res.data.data
}

export const createCategory = async (data: CategoryRequest) => {
  const res = await api.post<ApiResponse<CategoryResponse>>('/categories', data)
  return res.data.data
}

export const updateCategory = async (id: number, data: CategoryRequest) => {
  const res = await api.put<ApiResponse<CategoryResponse>>(`/categories/${id}`, data)
  return res.data.data
}

export const deleteCategory = async (id: number) => {
  const res = await api.delete<ApiResponse<null>>(`/categories/${id}`)
  return res.data
}
