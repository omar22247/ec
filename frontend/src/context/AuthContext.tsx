import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import type { UserResponse } from '../types'
import api from '../api/axios'

interface AuthContextType {
  user: UserResponse | null
  token: string | null
  login: (accessToken: string, refreshToken: string, user: UserResponse) => void
  logout: () => void
  isAuthenticated: boolean
  isAdmin: boolean
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('accessToken'))
  const [user, setUser] = useState<UserResponse | null>(() => {
    const saved = localStorage.getItem('user')
    return saved ? JSON.parse(saved) : null
  })

  const login = (accessToken: string, refreshToken: string, newUser: UserResponse) => {
    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('refreshToken', refreshToken)
    localStorage.setItem('user', JSON.stringify(newUser))
    setToken(accessToken)
    setUser(newUser)
  }

  const logout = () => {
    const rt = localStorage.getItem('refreshToken')
    if (rt) {
      api.post('/auth/logout', { refreshToken: rt }).catch(() => {})
    }
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    setToken(null)
    setUser(null)
  }

  useEffect(() => {
    const handleStorage = () => {
      const t = localStorage.getItem('accessToken')
      const u = localStorage.getItem('user')
      setToken(t)
      setUser(u ? JSON.parse(u) : null)
    }
    window.addEventListener('storage', handleStorage)
    return () => window.removeEventListener('storage', handleStorage)
  }, [])

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        login,
        logout,
        isAuthenticated: !!token,
        isAdmin: user?.role === 'ADMIN',
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
