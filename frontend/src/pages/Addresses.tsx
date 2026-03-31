import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import {
  getAddresses, createAddress, updateAddress, deleteAddress, setDefaultAddress,
} from '../api/addresses'
import { getErrorMessage } from '../utils/format'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Modal from '../components/ui/Modal'
import { PageSpinner } from '../components/ui/Spinner'
import toast from 'react-hot-toast'
import type { AddressRequest, AddressResponse } from '../types'

export default function Addresses() {
  const queryClient = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState<AddressResponse | null>(null)

  const { data: addresses, isLoading } = useQuery({
    queryKey: ['addresses'],
    queryFn: getAddresses,
  })

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<AddressRequest>()

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ['addresses'] })

  const createMutation = useMutation({
    mutationFn: createAddress,
    onSuccess: () => { invalidate(); closeModal(); toast.success('Address added') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const updateMutation = useMutation({
    mutationFn: (data: AddressRequest) => updateAddress(editing!.id, data),
    onSuccess: () => { invalidate(); closeModal(); toast.success('Address updated') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const deleteMutation = useMutation({
    mutationFn: deleteAddress,
    onSuccess: () => { invalidate(); toast.success('Address deleted') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const defaultMutation = useMutation({
    mutationFn: setDefaultAddress,
    onSuccess: () => { invalidate(); toast.success('Default address updated') },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const openAdd = () => { setEditing(null); reset({}); setModalOpen(true) }
  const openEdit = (addr: AddressResponse) => {
    setEditing(addr)
    reset({
      fullName: addr.fullName, phone: addr.phone, street: addr.street,
      city: addr.city, country: addr.country, zipCode: addr.zipCode,
      isDefault: addr.isDefault,
    })
    setModalOpen(true)
  }
  const closeModal = () => { setModalOpen(false); setEditing(null) }

  const onSubmit = (data: AddressRequest) =>
    editing ? updateMutation.mutate(data) : createMutation.mutate(data)

  if (isLoading) return <PageSpinner />

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Addresses</h1>
        <Button onClick={openAdd}>+ Add Address</Button>
      </div>

      {!addresses || addresses.length === 0 ? (
        <div className="bg-white rounded-xl border border-gray-200 p-12 text-center">
          <p className="text-4xl mb-4">📍</p>
          <p className="font-semibold text-gray-900 mb-1">No addresses saved</p>
          <p className="text-gray-500 text-sm mb-4">Add an address for faster checkout.</p>
          <Button onClick={openAdd}>Add Your First Address</Button>
        </div>
      ) : (
        <div className="space-y-3">
          {addresses.map((addr) => (
            <div
              key={addr.id}
              className={`bg-white rounded-xl border p-5 ${
                addr.isDefault ? 'border-primary-300 ring-1 ring-primary-200' : 'border-gray-200'
              }`}
            >
              <div className="flex items-start justify-between gap-4">
                <div className="text-sm space-y-0.5">
                  <div className="flex items-center gap-2">
                    <p className="font-semibold text-gray-900">{addr.fullName}</p>
                    {addr.isDefault && (
                      <span className="text-xs bg-primary-100 text-primary-700 px-2 py-0.5 rounded-full font-medium">
                        Default
                      </span>
                    )}
                  </div>
                  <p className="text-gray-600">{addr.street}</p>
                  <p className="text-gray-600">{addr.city}, {addr.country} {addr.zipCode}</p>
                  <p className="text-gray-500">{addr.phone}</p>
                </div>
                <div className="flex items-center gap-2 shrink-0">
                  {!addr.isDefault && (
                    <button
                      onClick={() => defaultMutation.mutate(addr.id)}
                      className="text-xs text-gray-500 hover:text-primary-600 transition-colors"
                    >
                      Set default
                    </button>
                  )}
                  <button
                    onClick={() => openEdit(addr)}
                    className="text-xs text-primary-600 hover:underline"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => deleteMutation.mutate(addr.id)}
                    className="text-xs text-red-500 hover:underline"
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal
        open={modalOpen}
        onClose={closeModal}
        title={editing ? 'Edit Address' : 'Add New Address'}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Full name"
              error={errors.fullName?.message}
              {...register('fullName', { required: 'Required' })}
            />
            <Input
              label="Phone"
              error={errors.phone?.message}
              {...register('phone', { required: 'Required' })}
            />
          </div>
          <Input
            label="Street address"
            error={errors.street?.message}
            {...register('street', { required: 'Required' })}
          />
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="City"
              error={errors.city?.message}
              {...register('city', { required: 'Required' })}
            />
            <Input label="Zip / Postal code" {...register('zipCode')} />
          </div>
          <Input
            label="Country"
            error={errors.country?.message}
            {...register('country', { required: 'Required' })}
          />
          <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
            <input type="checkbox" className="accent-primary-600" {...register('isDefault')} />
            Set as default address
          </label>
          <div className="flex justify-end gap-3 pt-2">
            <Button type="button" variant="outline" onClick={closeModal}>Cancel</Button>
            <Button type="submit" loading={isSubmitting}>
              {editing ? 'Save Changes' : 'Add Address'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
