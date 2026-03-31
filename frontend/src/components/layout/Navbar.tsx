import { useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { useCart } from '../../context/CartContext'

export default function Navbar() {
  const { user, isAuthenticated, isAdmin, logout } = useAuth()
  const { totalItems } = useCart()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)
  const [userMenuOpen, setUserMenuOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const navLink = ({ isActive }: { isActive: boolean }) =>
    `text-sm font-medium transition-colors ${
      isActive ? 'text-primary-600' : 'text-gray-700 hover:text-primary-600'
    }`

  return (
    <nav className="sticky top-0 z-40 bg-white border-b border-gray-200 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
              <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
            </div>
            <span className="text-lg font-bold text-gray-900">ShopSecure</span>
          </Link>

          {/* Desktop Nav */}
          <div className="hidden md:flex items-center gap-8">
            <NavLink to="/products" className={navLink}>Products</NavLink>
            {isAdmin && (
              <NavLink to="/admin" className={navLink}>Admin</NavLink>
            )}
          </div>

          {/* Right Side */}
          <div className="flex items-center gap-3">
            {/* Cart */}
            {isAuthenticated && (
              <Link to="/cart" className="relative p-2 text-gray-600 hover:text-primary-600 transition-colors">
                <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                    d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                {totalItems > 0 && (
                  <span className="absolute -top-0.5 -right-0.5 w-5 h-5 bg-primary-600 text-white text-xs rounded-full flex items-center justify-center font-medium">
                    {totalItems > 99 ? '99+' : totalItems}
                  </span>
                )}
              </Link>
            )}

            {/* User Menu */}
            {isAuthenticated ? (
              <div className="relative">
                <button
                  onClick={() => setUserMenuOpen(!userMenuOpen)}
                  className="flex items-center gap-2 p-1.5 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  <div className="w-8 h-8 bg-primary-100 text-primary-700 rounded-full flex items-center justify-center text-sm font-semibold">
                    {user?.name.charAt(0).toUpperCase()}
                  </div>
                  <svg className="w-4 h-4 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </button>
                {userMenuOpen && (
                  <>
                    <div className="fixed inset-0 z-10" onClick={() => setUserMenuOpen(false)} />
                    <div className="absolute right-0 mt-2 w-52 bg-white rounded-xl shadow-lg border border-gray-100 py-1 z-20">
                      <div className="px-4 py-2 border-b border-gray-100">
                        <p className="text-sm font-semibold text-gray-900 truncate">{user?.name}</p>
                        <p className="text-xs text-gray-500 truncate">{user?.email}</p>
                      </div>
                      {[
                        { to: '/profile', label: 'Profile' },
                        { to: '/orders', label: 'My Orders' },
                        { to: '/wishlist', label: 'Wishlist' },
                        { to: '/addresses', label: 'Addresses' },
                      ].map(({ to, label }) => (
                        <Link
                          key={to}
                          to={to}
                          onClick={() => setUserMenuOpen(false)}
                          className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                        >
                          {label}
                        </Link>
                      ))}
                      {isAdmin && (
                        <Link
                          to="/admin"
                          onClick={() => setUserMenuOpen(false)}
                          className="block px-4 py-2 text-sm text-primary-600 hover:bg-gray-50 font-medium"
                        >
                          Admin Panel
                        </Link>
                      )}
                      <hr className="my-1" />
                      <button
                        onClick={handleLogout}
                        className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                      >
                        Sign Out
                      </button>
                    </div>
                  </>
                )}
              </div>
            ) : (
              <div className="hidden md:flex items-center gap-2">
                <Link
                  to="/login"
                  className="px-4 py-2 text-sm font-medium text-gray-700 hover:text-primary-600"
                >
                  Sign In
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 text-sm font-medium bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                >
                  Get Started
                </Link>
              </div>
            )}

            {/* Mobile menu button */}
            <button
              className="md:hidden p-2 rounded-lg text-gray-600 hover:bg-gray-100"
              onClick={() => setMenuOpen(!menuOpen)}
            >
              <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d={menuOpen ? 'M6 18L18 6M6 6l12 12' : 'M4 6h16M4 12h16M4 18h16'}
                />
              </svg>
            </button>
          </div>
        </div>

        {/* Mobile Menu */}
        {menuOpen && (
          <div className="md:hidden py-3 border-t space-y-1">
            <NavLink to="/products" className="block px-3 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg">
              Products
            </NavLink>
            {isAuthenticated ? (
              <>
                <NavLink to="/orders" className="block px-3 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg">My Orders</NavLink>
                <NavLink to="/wishlist" className="block px-3 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg">Wishlist</NavLink>
                <NavLink to="/profile" className="block px-3 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg">Profile</NavLink>
                {isAdmin && (
                  <NavLink to="/admin" className="block px-3 py-2 text-sm text-primary-600 hover:bg-gray-50 rounded-lg font-medium">Admin</NavLink>
                )}
                <button onClick={handleLogout} className="block w-full text-left px-3 py-2 text-sm text-red-600 hover:bg-red-50 rounded-lg">
                  Sign Out
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="block px-3 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg">Sign In</Link>
                <Link to="/register" className="block px-3 py-2 text-sm text-primary-600 font-medium hover:bg-gray-50 rounded-lg">Get Started</Link>
              </>
            )}
          </div>
        )}
      </div>
    </nav>
  )
}
