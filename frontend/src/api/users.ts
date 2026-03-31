import api from './axios'
import type { ApiResponse, UserResponse, UpdateProfileRequest, ChangePasswordRequest } from '../types'

export const getMe = async () => {
  const res = await api.get<ApiResponse<UserResponse>>('/users/me')
  return res.data.data
}

export const updateProfile = async (data: UpdateProfileRequest) => {
  const res = await api.put<ApiResponse<UserResponse>>('/users/me', data)
  return res.data.data
}

export const changePassword = async (data: ChangePasswordRequest) => {
  const res = await api.put<ApiResponse<null>>('/users/me/password', data)
  return res.data
}

export const deleteAccount = async () => {
  const res = await api.delete<ApiResponse<null>>('/users/me')
  return res.data
}
