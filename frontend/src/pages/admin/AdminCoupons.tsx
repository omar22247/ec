import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { getCoupons, createCoupon, updateCoupon, deleteCoupon, toggleCoupon } from '../../api/coupons'
import { formatCurrency, formatDate, getErrorMessage } from '../../utils/format'
import Button from '../../components/ui/Button'
import Input from '../../components/ui/Input'
import Modal from '../../components/ui/Modal'
import Badge from '../../components/ui/Badge'
import { PageSpinner } from '../../components/ui/Spinner'
import toast from 'react-hot-toast'
import type { CouponRequest, CouponResponse } from '../../types'

export default function AdminCoupons() {
  const queryClient = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState<CouponResponse | null>(null)
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null)

  const { data: coupons, isLoading } = useQuery({
    queryKey: ['admin-coupons'],
    queryFn: getCoupons,
  })

  const { register, handleSubmit, reset, watch, formState: { errors, isSubmitting } } = useForm<CouponRequest>()
  const discountType = watch('discountType')

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ['admin-coupons'] })

  const createMutation = useMutation({
    mutationFn: createCoupon,
    onSuccess: () => { invalidate(); closeModal(); toast.success('Coupon created') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const updateMutation = useMutation({
    mutationFn: (data: CouponRequest) => updateCoupon(editing!.id, data),
    onSuccess: () => { invalidate(); closeModal(); toast.success('Coupon updated') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const deleteMutation = useMutation({
    mutationFn: deleteCoupon,
    onSuccess: () => { invalidate(); setDeleteConfirm(null); toast.success('Coupon deleted') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const toggleMutation = useMutation({
    mutationFn: toggleCoupon,
    onSuccess: () => { invalidate() },
  })

  const openAdd = () => { setEditing(null); reset({ discountType: 'PERCENTAGE' }); setModalOpen(true) }
  const openEdit = (c: CouponResponse) => {
    setEditing(c)
    reset({
      code: c.code,
      discountType: c.discountType,
      discountValue: c.discountValue,
      minOrderAmount: c.minOrderAmount,
      maxUses: c.maxUses ?? undefined,
      expiresAt: c.expiresAt ? c.expiresAt.slice(0, 16) : undefined,
    })
    setModalOpen(true)
  }
  const closeModal = () => { setModalOpen(false); setEditing(null) }
  const onSubmit = (data: CouponRequest) => {
    const payload = { ...data, maxUses: data.maxUses || null, expiresAt: data.expiresAt || null }
    editing ? updateMutation.mutate(payload) : createMutation.mutate(payload)
  }

  if (isLoading) return <PageSpinner />

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Coupons</h1>
        <Button onClick={openAdd}>+ Add Coupon</Button>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              {['Code', 'Discount', 'Min Order', 'Uses', 'Expires', 'Status', 'Actions'].map((h) => (
                <th key={h} className="text-left px-4 py-3 font-semibold text-gray-600 text-xs uppercase tracking-wide">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {coupons?.map((c) => (
              <tr key={c.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-mono font-semibold text-gray-900">{c.code}</td>
                <td className="px-4 py-3 text-gray-700">
                  {c.discountType === 'PERCENTAGE'
                    ? `${c.discountValue}%`
                    : formatCurrency(c.discountValue)}
                </td>
                <td className="px-4 py-3 text-gray-500">{formatCurrency(c.minOrderAmount)}</td>
                <td className="px-4 py-3 text-gray-500">
                  {c.usedCount}{c.maxUses ? ` / ${c.maxUses}` : ''}
                </td>
                <td className="px-4 py-3 text-gray-500">
                  {c.expiresAt ? formatDate(c.expiresAt) : '—'}
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-1.5">
                    <Badge color={c.active ? 'green' : 'gray'}>{c.active ? 'Active' : 'Disabled'}</Badge>
                    {!c.valid && c.active && <Badge color="red">Invalid</Badge>}
                  </div>
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-2">
                    <button onClick={() => openEdit(c)} className="text-xs text-primary-600 hover:underline font-medium">Edit</button>
                    <button onClick={() => toggleMutation.mutate(c.id)} className="text-xs text-gray-500 hover:text-gray-700">
                      {c.active ? 'Disable' : 'Enable'}
                    </button>
                    <button onClick={() => setDeleteConfirm(c.id)} className="text-xs text-red-500 hover:underline font-medium">Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {coupons?.length === 0 && (
          <div className="p-8 text-center text-gray-500 text-sm">No coupons yet. Add one above.</div>
        )}
      </div>

      {/* Add/Edit Modal */}
      <Modal open={modalOpen} onClose={closeModal} title={editing ? 'Edit Coupon' : 'Add Coupon'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="Coupon code"
            placeholder="e.g. SAVE20"
            error={errors.code?.message}
            {...register('code', { required: 'Required', minLength: { value: 3, message: 'Min 3 characters' } })}
          />
          <div className="space-y-1">
            <label className="block text-sm font-medium text-gray-700">Discount type</label>
            <select
              className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
              {...register('discountType', { required: 'Required' })}
            >
              <option value="PERCENTAGE">Percentage (%)</option>
              <option value="FIXED">Fixed Amount ($)</option>
            </select>
          </div>
          <Input
            label={`Discount value (${discountType === 'PERCENTAGE' ? '%' : '$'})`}
            type="number"
            step="0.01"
            error={errors.discountValue?.message}
            {...register('discountValue', { required: 'Required', valueAsNumber: true, min: { value: 0.01, message: 'Must be > 0' } })}
          />
          <Input
            label="Minimum order amount ($)"
            type="number"
            step="0.01"
            {...register('minOrderAmount', { valueAsNumber: true })}
          />
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Max uses (leave blank for unlimited)"
              type="number"
              {...register('maxUses', { valueAsNumber: true })}
            />
            <div className="space-y-1">
              <label className="block text-sm font-medium text-gray-700">Expiry date (optional)</label>
              <input
                type="datetime-local"
                className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
                {...register('expiresAt')}
              />
            </div>
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <Button type="button" variant="outline" onClick={closeModal}>Cancel</Button>
            <Button type="submit" loading={isSubmitting}>
              {editing ? 'Save Changes' : 'Create Coupon'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirm */}
      <Modal open={deleteConfirm !== null} onClose={() => setDeleteConfirm(null)} title="Delete Coupon" size="sm">
        <p className="text-sm text-gray-600 mb-4">Delete this coupon permanently?</p>
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
