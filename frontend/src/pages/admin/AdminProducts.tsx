import { useState } from 'react'
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { getAdminProducts, createProduct, updateProduct, deleteProduct, toggleProduct } from '../../api/products'
import { getCategories } from '../../api/categories'
import { formatCurrency, getErrorMessage } from '../../utils/format'
import Button from '../../components/ui/Button'
import Input from '../../components/ui/Input'
import Modal from '../../components/ui/Modal'
import Badge from '../../components/ui/Badge'
import Pagination from '../../components/ui/Pagination'
import { PageSpinner } from '../../components/ui/Spinner'
import toast from 'react-hot-toast'
import type { ProductRequest, ProductDetailResponse } from '../../types'

const PLACEHOLDER = 'https://placehold.co/48x48/e0e7ff/4f46e5?text=?'

export default function AdminProducts() {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState<ProductDetailResponse | null>(null)
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null)

  const { data, isLoading } = useQuery({
    queryKey: ['admin-products', page],
    queryFn: () => getAdminProducts(page, 15),
    placeholderData: keepPreviousData,
  })

  const { data: categories } = useQuery({ queryKey: ['categories'], queryFn: getCategories })
  const flatCategories = categories?.flatMap((c) => [c, ...(c.subCategories ?? [])]) ?? []

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<ProductRequest>()

  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: ['admin-products'] })
    queryClient.invalidateQueries({ queryKey: ['products'] })
  }

  const createMutation = useMutation({
    mutationFn: createProduct,
    onSuccess: () => { invalidate(); closeModal(); toast.success('Product created') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const updateMutation = useMutation({
    mutationFn: (data: ProductRequest) => updateProduct(editing!.id, data),
    onSuccess: () => { invalidate(); closeModal(); toast.success('Product updated') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const deleteMutation = useMutation({
    mutationFn: deleteProduct,
    onSuccess: () => { invalidate(); setDeleteConfirm(null); toast.success('Product deleted') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const toggleMutation = useMutation({
    mutationFn: toggleProduct,
    onSuccess: () => { invalidate(); toast.success('Product status updated') },
  })

  const openAdd = () => { setEditing(null); reset({}); setModalOpen(true) }
  const openEdit = (p: ProductDetailResponse) => {
    setEditing(p)
    reset({
      name: p.name,
      description: p.description ?? '',
      basePrice: p.basePrice,
      stock: p.stock,
      categoryId: p.categoryId,
      imageUrl: p.imageUrl ?? '',
    })
    setModalOpen(true)
  }
  const closeModal = () => { setModalOpen(false); setEditing(null) }
  const onSubmit = (data: ProductRequest) =>
    editing ? updateMutation.mutate(data) : createMutation.mutate(data)

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Products</h1>
        <Button onClick={openAdd}>+ Add Product</Button>
      </div>

      {isLoading ? (
        <PageSpinner />
      ) : (
        <>
          <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  {['Product', 'Category', 'Price', 'Stock', 'Status', 'Actions'].map((h) => (
                    <th key={h} className="text-left px-4 py-3 font-semibold text-gray-600 text-xs uppercase tracking-wide">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {data?.content.map((p) => (
                  <tr key={p.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        <img
                          src={p.imageUrl ?? PLACEHOLDER}
                          alt={p.name}
                          className="w-10 h-10 rounded-lg object-cover border border-gray-100 shrink-0"
                          onError={(e) => { (e.target as HTMLImageElement).src = PLACEHOLDER }}
                        />
                        <span className="font-medium text-gray-900 line-clamp-1 max-w-[180px]">{p.name}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-gray-500">{p.categoryName}</td>
                    <td className="px-4 py-3 font-medium">{formatCurrency(p.basePrice)}</td>
                    <td className="px-4 py-3">
                      <span className={p.stock === 0 ? 'text-red-600 font-medium' : p.stock <= 5 ? 'text-orange-600 font-medium' : 'text-gray-700'}>
                        {p.stock}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <Badge color={p.active ? 'green' : 'gray'}>{p.active ? 'Active' : 'Hidden'}</Badge>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => openEdit(p as ProductDetailResponse)}
                          className="text-primary-600 hover:underline text-xs font-medium"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => toggleMutation.mutate(p.id)}
                          className="text-gray-500 hover:text-gray-700 text-xs"
                        >
                          {p.active ? 'Hide' : 'Show'}
                        </button>
                        <button
                          onClick={() => setDeleteConfirm(p.id)}
                          className="text-red-500 hover:underline text-xs font-medium"
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {data && (
            <div className="mt-4">
              <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
            </div>
          )}
        </>
      )}

      {/* Add/Edit Modal */}
      <Modal open={modalOpen} onClose={closeModal} title={editing ? 'Edit Product' : 'Add Product'} size="lg">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="Product name"
            error={errors.name?.message}
            {...register('name', { required: 'Required' })}
          />
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea
              rows={3}
              className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
              {...register('description')}
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Price ($)"
              type="number"
              step="0.01"
              error={errors.basePrice?.message}
              {...register('basePrice', { required: 'Required', valueAsNumber: true, min: { value: 0.01, message: 'Must be > 0' } })}
            />
            <Input
              label="Stock quantity"
              type="number"
              error={errors.stock?.message}
              {...register('stock', { required: 'Required', valueAsNumber: true, min: { value: 0, message: 'Must be ≥ 0' } })}
            />
          </div>
          <div className="space-y-1">
            <label className="block text-sm font-medium text-gray-700">Category</label>
            <select
              className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
              {...register('categoryId', { required: 'Required', valueAsNumber: true })}
            >
              <option value="">Select category</option>
              {flatCategories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.parentId ? `  ↳ ${c.name}` : c.name}
                </option>
              ))}
            </select>
          </div>
          <Input label="Image URL" placeholder="https://..." {...register('imageUrl')} />
          <Input
            label="Low stock threshold"
            type="number"
            {...register('lowStockThreshold', { valueAsNumber: true })}
          />
          <div className="flex justify-end gap-3 pt-2">
            <Button type="button" variant="outline" onClick={closeModal}>Cancel</Button>
            <Button type="submit" loading={isSubmitting}>
              {editing ? 'Save Changes' : 'Create Product'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirm Modal */}
      <Modal open={deleteConfirm !== null} onClose={() => setDeleteConfirm(null)} title="Delete Product" size="sm">
        <p className="text-sm text-gray-600 mb-4">Are you sure you want to delete this product? This action cannot be undone.</p>
        <div className="flex justify-end gap-3">
          <Button variant="outline" onClick={() => setDeleteConfirm(null)}>Cancel</Button>
          <Button
            variant="danger"
            loading={deleteMutation.isPending}
            onClick={() => deleteConfirm && deleteMutation.mutate(deleteConfirm)}
          >
            Delete
          </Button>
        </div>
      </Modal>
    </div>
  )
}
