import api from './axios'
import type { ApiResponse, AddressResponse, AddressRequest } from '../types'

export const getAddresses = async () => {
  const res = await api.get<ApiResponse<AddressResponse[]>>('/users/me/addresses')
  return res.data.data
}

export const getAddress = async (id: number) => {
  const res = await api.get<ApiResponse<AddressResponse>>(`/users/me/addresses/${id}`)
  return res.data.data
}

export const createAddress = async (data: AddressRequest) => {
  const res = await api.post<ApiResponse<AddressResponse>>('/users/me/addresses', data)
  return res.data.data
}

export const updateAddress = async (id: number, data: AddressRequest) => {
  const res = await api.put<ApiResponse<AddressResponse>>(`/users/me/addresses/${id}`, data)
  return res.data.data
}

export const deleteAddress = async (id: number) => {
  const res = await api.delete<ApiResponse<null>>(`/users/me/addresses/${id}`)
  return res.data
}

export const setDefaultAddress = async (id: number) => {
  const res = await api.patch<ApiResponse<AddressResponse>>(`/users/me/addresses/${id}/default`)
  return res.data.data
}
