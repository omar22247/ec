import { Outlet } from 'react-router-dom'
import Navbar from './Navbar'
import Footer from './Footer'
import { Toaster } from 'react-hot-toast'

export default function Layout() {
  return (
    <div className="min-h-screen flex flex-col bg-gray-50">
      <Toaster position="top-right" toastOptions={{ duration: 3000 }} />
      <Navbar />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}

export function AdminLayout() {
  return (
    <div className="min-h-screen flex bg-gray-100">
      <Toaster position="top-right" toastOptions={{ duration: 3000 }} />
      <AdminSidebar />
      <div className="flex-1 flex flex-col min-w-0">
        <div className="flex-1 p-8 overflow-auto">
          <Outlet />
        </div>
      </div>
    </div>
  )
}

function AdminSidebar() {
  const links = [
    { to: '/admin', label: 'Dashboard', icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6' },
    { to: '/admin/products', label: 'Products', icon: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4' },
    { to: '/admin/categories', label: 'Categories', icon: 'M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z' },
    { to: '/admin/orders', label: 'Orders', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
    { to: '/admin/coupons', label: 'Coupons', icon: 'M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z' },
  ]

  return (
    <aside className="w-64 bg-gray-900 text-gray-300 flex flex-col shrink-0">
      <div className="p-6 border-b border-gray-800">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
            <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </div>
          <div>
            <p className="text-white font-semibold text-sm">ShopSecure</p>
            <p className="text-gray-400 text-xs">Admin Panel</p>
          </div>
        </div>
      </div>
      <nav className="flex-1 p-4 space-y-1">
        {links.map(({ to, label, icon }) => (
          <a
            key={to}
            href={to}
            className="flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-gray-300 hover:bg-gray-800 hover:text-white transition-colors"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={icon} />
            </svg>
            {label}
          </a>
        ))}
      </nav>
      <div className="p-4 border-t border-gray-800">
        <a href="/" className="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-gray-400 hover:text-white transition-colors">
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          Back to Store
        </a>
      </div>
    </aside>
  )
}
