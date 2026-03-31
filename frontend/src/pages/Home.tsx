import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { getProducts } from '../api/products'
import { getCategories } from '../api/categories'
import ProductCard from '../components/products/ProductCard'
import { PageSpinner } from '../components/ui/Spinner'

export default function Home() {
  const { data: productsPage, isLoading: loadingProducts } = useQuery({
    queryKey: ['products', 'featured'],
    queryFn: () => getProducts(0, 8, 'id'),
  })

  const { data: categories } = useQuery({
    queryKey: ['categories'],
    queryFn: getCategories,
  })

  const rootCategories = categories?.filter((c) => c.parentId === null) ?? []

  return (
    <div>
      {/* Hero */}
      <section className="bg-gradient-to-br from-primary-600 via-primary-700 to-indigo-800 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24 flex flex-col items-center text-center">
          <span className="inline-flex items-center gap-2 px-4 py-1.5 bg-white/20 rounded-full text-sm font-medium mb-6">
            <span className="w-2 h-2 bg-green-400 rounded-full animate-pulse" />
            Secure & Trusted Shopping
          </span>
          <h1 className="text-5xl font-extrabold leading-tight mb-6 max-w-2xl">
            Shop Smarter, <br /> Shop Securely
          </h1>
          <p className="text-primary-100 text-lg max-w-xl mb-8">
            Discover thousands of products with enterprise-grade security, fast shipping, and easy returns.
          </p>
          <div className="flex gap-4 flex-wrap justify-center">
            <Link
              to="/products"
              className="px-8 py-3 bg-white text-primary-700 font-semibold rounded-xl hover:bg-primary-50 transition-colors"
            >
              Shop Now
            </Link>
            <Link
              to="/register"
              className="px-8 py-3 bg-white/10 text-white font-semibold rounded-xl hover:bg-white/20 transition-colors border border-white/30"
            >
              Create Account
            </Link>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="border-b bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            {[
              { icon: '🚚', title: 'Free Shipping', desc: 'On orders over $50' },
              { icon: '🔒', title: 'Secure Payments', desc: 'SSL encrypted checkout' },
              { icon: '↩️', title: 'Easy Returns', desc: '30-day return policy' },
              { icon: '💬', title: '24/7 Support', desc: 'Always here to help' },
            ].map(({ icon, title, desc }) => (
              <div key={title} className="flex items-center gap-3">
                <span className="text-3xl">{icon}</span>
                <div>
                  <p className="font-semibold text-gray-900 text-sm">{title}</p>
                  <p className="text-gray-500 text-xs">{desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Categories */}
      {rootCategories.length > 0 && (
        <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">Shop by Category</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
            {rootCategories.slice(0, 6).map((cat) => (
              <Link
                key={cat.id}
                to={`/products?category=${cat.id}`}
                className="group bg-white border border-gray-200 rounded-xl p-4 text-center hover:border-primary-300 hover:shadow-md transition-all"
              >
                <div className="w-12 h-12 bg-primary-100 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:bg-primary-200 transition-colors">
                  <span className="text-2xl">📦</span>
                </div>
                <p className="text-sm font-medium text-gray-900 group-hover:text-primary-600 transition-colors">
                  {cat.name}
                </p>
              </Link>
            ))}
          </div>
        </section>
      )}

      {/* Featured Products */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-16">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Featured Products</h2>
          <Link to="/products" className="text-primary-600 font-medium hover:underline text-sm">
            View all →
          </Link>
        </div>

        {loadingProducts ? (
          <PageSpinner />
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {productsPage?.content.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </section>
    </div>
  )
}
