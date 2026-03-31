import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { getCategories, createCategory, updateCategory, deleteCategory } from '../../api/categories'
import { getErrorMessage } from '../../utils/format'
import Button from '../../components/ui/Button'
import Input from '../../components/ui/Input'
import Modal from '../../components/ui/Modal'
import { PageSpinner } from '../../components/ui/Spinner'
import toast from 'react-hot-toast'
import type { CategoryRequest, CategoryResponse } from '../../types'

export default function AdminCategories() {
  const queryClient = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState<CategoryResponse | null>(null)
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null)

  const { data: categories, isLoading } = useQuery({
    queryKey: ['categories'],
    queryFn: getCategories,
  })

  const { register, handleSubmit, reset, setValue, formState: { errors, isSubmitting } } = useForm<CategoryRequest>()

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ['categories'] })

  const createMutation = useMutation({
    mutationFn: createCategory,
    onSuccess: () => { invalidate(); closeModal(); toast.success('Category created') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const updateMutation = useMutation({
    mutationFn: (data: CategoryRequest) => updateCategory(editing!.id, data),
    onSuccess: () => { invalidate(); closeModal(); toast.success('Category updated') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const deleteMutation = useMutation({
    mutationFn: deleteCategory,
    onSuccess: () => { invalidate(); setDeleteConfirm(null); toast.success('Category deleted') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const openAdd = (parentId?: number) => {
    setEditing(null)
    reset({ name: '', slug: '', parentId: parentId ?? null })
    setModalOpen(true)
  }

  const openEdit = (cat: CategoryResponse) => {
    setEditing(cat)
    reset({ name: cat.name, slug: cat.slug, parentId: cat.parentId })
    setModalOpen(true)
  }

  const closeModal = () => { setModalOpen(false); setEditing(null) }

  const onSubmit = (data: CategoryRequest) => {
    const payload = { ...data, parentId: data.parentId || null }
    editing ? updateMutation.mutate(payload) : createMutation.mutate(payload)
  }

  const rootCategories = categories?.filter((c) => c.parentId === null) ?? []

  if (isLoading) return <PageSpinner />

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Categories</h1>
        <Button onClick={() => openAdd()}>+ Add Category</Button>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              {['Name', 'Slug', 'Parent', 'Subcategories', 'Actions'].map((h) => (
                <th key={h} className="text-left px-4 py-3 font-semibold text-gray-600 text-xs uppercase tracking-wide">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {rootCategories.map((cat) => (
              <>
                <tr key={cat.id} className="bg-gray-50/50 hover:bg-gray-50">
                  <td className="px-4 py-3 font-semibold text-gray-900">{cat.name}</td>
                  <td className="px-4 py-3 text-gray-500 font-mono text-xs">{cat.slug}</td>
                  <td className="px-4 py-3 text-gray-400">—</td>
                  <td className="px-4 py-3 text-gray-500">{cat.subCategories?.length ?? 0}</td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-2">
                      <button onClick={() => openAdd(cat.id)} className="text-xs text-green-600 hover:underline font-medium">+ Sub</button>
                      <button onClick={() => openEdit(cat)} className="text-xs text-primary-600 hover:underline font-medium">Edit</button>
                      <button onClick={() => setDeleteConfirm(cat.id)} className="text-xs text-red-500 hover:underline font-medium">Delete</button>
                    </div>
                  </td>
                </tr>
                {cat.subCategories?.map((sub) => (
                  <tr key={sub.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 pl-10 text-gray-700">↳ {sub.name}</td>
                    <td className="px-4 py-3 text-gray-500 font-mono text-xs">{sub.slug}</td>
                    <td className="px-4 py-3 text-gray-500">{cat.name}</td>
                    <td className="px-4 py-3 text-gray-400">—</td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2">
                        <button onClick={() => openEdit(sub)} className="text-xs text-primary-600 hover:underline font-medium">Edit</button>
                        <button onClick={() => setDeleteConfirm(sub.id)} className="text-xs text-red-500 hover:underline font-medium">Delete</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </>
            ))}
          </tbody>
        </table>
      </div>

      {/* Add/Edit Modal */}
      <Modal open={modalOpen} onClose={closeModal} title={editing ? 'Edit Category' : 'Add Category'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="Name"
            error={errors.name?.message}
            {...register('name', { required: 'Required' })}
          />
          <Input
            label="Slug"
            placeholder="e.g. electronics"
            error={errors.slug?.message}
            {...register('slug', { required: 'Required' })}
          />
          <div className="space-y-1">
            <label className="block text-sm font-medium text-gray-700">Parent category (optional)</label>
            <select
              className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
              {...register('parentId', { valueAsNumber: true })}
            >
              <option value="">None (root category)</option>
              {rootCategories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <Button type="button" variant="outline" onClick={closeModal}>Cancel</Button>
            <Button type="submit" loading={isSubmitting}>
              {editing ? 'Save Changes' : 'Create'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirm */}
      <Modal open={deleteConfirm !== null} onClose={() => setDeleteConfirm(null)} title="Delete Category" size="sm">
        <p className="text-sm text-gray-600 mb-4">Delete this category? Any products in it may need to be reassigned.</p>
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
