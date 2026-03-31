import { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react'
import { getCart, addToCart, updateCartItem, removeCartItem, clearCart } from '../api/cart'
import type { CartResponse } from '../types'
import { useAuth } from './AuthContext'
import toast from 'react-hot-toast'

interface CartContextType {
  cart: CartResponse | null
  loading: boolean
  addItem: (productId: number, quantity?: number) => Promise<void>
  updateItem: (itemId: number, quantity: number) => Promise<void>
  removeItem: (itemId: number) => Promise<void>
  clear: () => Promise<void>
  refresh: () => Promise<void>
  totalItems: number
}

const CartContext = createContext<CartContextType | null>(null)

export function CartProvider({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth()
  const [cart, setCart] = useState<CartResponse | null>(null)
  const [loading, setLoading] = useState(false)

  const refresh = useCallback(async () => {
    if (!isAuthenticated) { setCart(null); return }
    try {
      setLoading(true)
      const data = await getCart()
      setCart(data)
    } catch {
      // silently fail
    } finally {
      setLoading(false)
    }
  }, [isAuthenticated])

  useEffect(() => {
    refresh()
  }, [refresh])

  const addItem = async (productId: number, quantity = 1) => {
    try {
      const updated = await addToCart({ productId, quantity })
      setCart(updated)
      toast.success('Added to cart')
    } catch (e: unknown) {
      const msg = e && typeof e === 'object' && 'response' in e
        ? (e as { response?: { data?: { message?: string } } }).response?.data?.message
        : 'Failed to add item'
      toast.error(msg ?? 'Failed to add item')
    }
  }

  const updateItem = async (itemId: number, quantity: number) => {
    try {
      const updated = await updateCartItem(itemId, { quantity })
      setCart(updated)
    } catch {
      toast.error('Failed to update item')
    }
  }

  const removeItem = async (itemId: number) => {
    try {
      const updated = await removeCartItem(itemId)
      setCart(updated)
      toast.success('Item removed')
    } catch {
      toast.error('Failed to remove item')
    }
  }

  const clear = async () => {
    try {
      await clearCart()
      setCart(null)
    } catch {
      toast.error('Failed to clear cart')
    }
  }

  return (
    <CartContext.Provider
      value={{
        cart,
        loading,
        addItem,
        updateItem,
        removeItem,
        clear,
        refresh,
        totalItems: cart?.totalItems ?? 0,
      }}
    >
      {children}
    </CartContext.Provider>
  )
}

export function useCart() {
  const ctx = useContext(CartContext)
  if (!ctx) throw new Error('useCart must be used within CartProvider')
  return ctx
}
