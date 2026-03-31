import { format, formatDistanceToNow } from 'date-fns'

export const formatCurrency = (amount: number): string =>
  new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount)

export const formatDate = (dateStr: string): string => {
  try {
    return format(new Date(dateStr), 'MMM d, yyyy')
  } catch {
    return dateStr
  }
}

export const formatDateTime = (dateStr: string): string => {
  try {
    return format(new Date(dateStr), 'MMM d, yyyy h:mm a')
  } catch {
    return dateStr
  }
}

export const timeAgo = (dateStr: string): string => {
  try {
    return formatDistanceToNow(new Date(dateStr), { addSuffix: true })
  } catch {
    return dateStr
  }
}

export const statusColor = (status: string): string => {
  const map: Record<string, string> = {
    PENDING:   'bg-yellow-100 text-yellow-800',
    PAID:      'bg-blue-100 text-blue-800',
    SHIPPED:   'bg-indigo-100 text-indigo-800',
    DELIVERED: 'bg-green-100 text-green-800',
    CANCELLED: 'bg-red-100 text-red-800',
    PREPARING: 'bg-orange-100 text-orange-800',
    RETURNED:  'bg-gray-100 text-gray-800',
    SUCCESS:   'bg-green-100 text-green-800',
    FAILED:    'bg-red-100 text-red-800',
    REFUNDED:  'bg-purple-100 text-purple-800',
  }
  return map[status] ?? 'bg-gray-100 text-gray-800'
}

export const getErrorMessage = (error: unknown): string => {
  if (error && typeof error === 'object' && 'response' in error) {
    const axiosError = error as { response?: { data?: { message?: string } } }
    return axiosError.response?.data?.message ?? 'An error occurred'
  }
  if (error instanceof Error) return error.message
  return 'An unexpected error occurred'
}
