import api from './axios'
import type {
  ApiResponse,
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
} from '../types'

export const login = async (data: LoginRequest) => {
  const res = await api.post<ApiResponse<AuthResponse>>('/auth/login', data)
  return res.data.data
}

export const register = async (data: RegisterRequest) => {
  const res = await api.post<ApiResponse<AuthResponse>>('/auth/register', data)
  return res.data.data
}

export const forgotPassword = async (data: ForgotPasswordRequest) => {
  const res = await api.post<ApiResponse<null>>('/auth/forgot-password', data)
  return res.data
}

export const resetPassword = async (token: string, data: ResetPasswordRequest) => {
  const res = await api.post<ApiResponse<string>>(`/auth/reset-password?token=${token}`, data)
  return res.data
}
