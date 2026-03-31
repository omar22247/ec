import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { useSearchParams } from 'react-router-dom'
import { getProducts, searchProducts, getProductsByCategory, filterProducts } from '../api/products'
import { getCategories } from '../api/categories'
import ProductCard from '../components/products/ProductCard'
import Pagination from '../components/ui/Pagination'
import { PageSpinner } from '../components/ui/Spinner'
import Button from '../components/ui/Button'

export default function Products() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [page, setPage] = useState(0)
  const [keyword, setKeyword] = useState(searchParams.get('q') ?? '')
  const [inputValue, setInputValue] = useState(searchParams.get('q') ?? '')
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')
  const [filterMode, setFilterMode] = useState<'all' | 'search' | 'filter'>('all')
  const categoryId = searchParams.get('category') ? Number(searchParams.get('category')) : null
  const sort = searchParams.get('sort') ?? 'id'

  const { data: categories } = useQuery({ queryKey: ['categories'], queryFn: getCategories })

  const queryFn = () => {
    if (filterMode === 'search' && keyword) return searchProducts(keyword, page)
    if (filterMode === 'filter' && minPrice && maxPrice)
      return filterProducts(Number(minPrice), Number(maxPrice), page)
    if (categoryId) return getProductsByCategory(categoryId, page)
    return getProducts(page, 12, sort)
  }

  const { data, isLoading } = useQuery({
    queryKey: ['products', page, keyword, filterMode, minPrice, maxPrice, categoryId, sort],
    queryFn,
  })

  const { data: wishlistIds } = useQuery({ queryKey: ['wishlist-ids'], queryFn: async () => [] })

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setKeyword(inputValue)
    setFilterMode(inputValue ? 'search' : 'all')
    setPage(0)
  }

  const handlePriceFilter = (e: React.FormEvent) => {
    e.preventDefault()
    setFilterMode('filter')
    setPage(0)
  }

  const handleClear = () => {
    setKeyword('')
    setInputValue('')
    setMinPrice('')
    setMaxPrice('')
    setFilterMode('all')
    setPage(0)
    setSearchParams({})
  }

  const handleCategoryClick = (id: number | null) => {
    setFilterMode('all')
    setPage(0)
    if (id) setSearchParams({ category: String(id) })
    else setSearchParams({})
  }

  const sortOptions = [
    { value: 'id', label: 'Newest' },
    { value: 'basePrice,asc', label: 'Price: Low to High' },
    { value: 'basePrice,desc', label: 'Price: High to Low' },
    { value: 'name', label: 'Name A-Z' },
  ]

  const rootCategories = categories?.filter((c) => c.parentId === null) ?? []

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex flex-col lg:flex-row gap-8">
        {/* Sidebar */}
        <aside className="w-full lg:w-64 shrink-0 space-y-6">
          {/* Search */}
          <div className="bg-white rounded-xl border border-gray-200 p-4">
            <h3 className="font-semibold text-gray-900 mb-3">Search</h3>
            <form onSubmit={handleSearch} className="flex gap-2">
              <input
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder="Search products..."
                className="flex-1 text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
              <Button type="submit" size="sm">Go</Button>
            </form>
          </div>

          {/* Categories */}
          <div className="bg-white rounded-xl border border-gray-200 p-4">
            <h3 className="font-semibold text-gray-900 mb-3">Categories</h3>
            <ul className="space-y-1">
              <li>
                <button
                  onClick={() => handleCategoryClick(null)}
                  className={`w-full text-left px-3 py-2 rounded-lg text-sm transition-colors ${
                    !categoryId ? 'bg-primary-50 text-primary-700 font-medium' : 'text-gray-700 hover:bg-gray-50'
                  }`}
                >
                  All Products
                </button>
              </li>
              {rootCategories.map((cat) => (
                <li key={cat.id}>
                  <button
                    onClick={() => handleCategoryClick(cat.id)}
                    className={`w-full text-left px-3 py-2 rounded-lg text-sm transition-colors ${
                      categoryId === cat.id ? 'bg-primary-50 text-primary-700 font-medium' : 'text-gray-700 hover:bg-gray-50'
                    }`}
                  >
                    {cat.name}
                  </button>
                </li>
              ))}
            </ul>
          </div>

          {/* Price Filter */}
          <div className="bg-white rounded-xl border border-gray-200 p-4">
            <h3 className="font-semibold text-gray-900 mb-3">Price Range</h3>
            <form onSubmit={handlePriceFilter} className="space-y-2">
              <input
                type="number"
                placeholder="Min price"
                value={minPrice}
                onChange={(e) => setMinPrice(e.target.value)}
                className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
              <input
                type="number"
                placeholder="Max price"
                value={maxPrice}
                onChange={(e) => setMaxPrice(e.target.value)}
                className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
              <Button type="submit" variant="outline" size="sm" className="w-full">
                Apply Filter
              </Button>
            </form>
          </div>

          {filterMode !== 'all' && (
            <button onClick={handleClear} className="text-sm text-red-600 hover:underline w-full text-center">
              Clear all filters
            </button>
          )}
        </aside>

        {/* Products */}
        <div className="flex-1 min-w-0">
          {/* Header */}
          <div className="flex items-center justify-between mb-6">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">
                {keyword ? `Results for "${keyword}"` : categoryId ? 'Category Products' : 'All Products'}
              </h1>
              {data && (
                <p className="text-sm text-gray-500 mt-1">
                  {data.totalElements} product{data.totalElements !== 1 ? 's' : ''} found
                </p>
              )}
            </div>
            <select
              value={sort}
              onChange={(e) => setSearchParams({ ...Object.fromEntries(searchParams), sort: e.target.value })}
              className="text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              {sortOptions.map((o) => (
                <option key={o.value} value={o.value}>{o.label}</option>
              ))}
            </select>
          </div>

          {isLoading ? (
            <PageSpinner />
          ) : data?.content.length === 0 ? (
            <div className="text-center py-16 text-gray-500">
              <p className="text-4xl mb-4">🔍</p>
              <p className="font-medium">No products found</p>
              <button onClick={handleClear} className="mt-3 text-primary-600 hover:underline text-sm">
                Clear filters
              </button>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6">
                {data?.content.map((product) => (
                  <ProductCard
                    key={product.id}
                    product={product}
                    inWishlist={(wishlistIds as number[]).includes(product.id)}
                  />
                ))}
              </div>
              {data && (
                <div className="mt-8">
                  <Pagination
                    page={data.page}
                    totalPages={data.totalPages}
                    onPageChange={(p) => { setPage(p); window.scrollTo(0, 0) }}
                  />
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  )
}
