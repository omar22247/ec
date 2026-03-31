import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { getMe, updateProfile, changePassword } from '../api/users'
import { useAuth } from '../context/AuthContext'
import { formatDate, getErrorMessage } from '../utils/format'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import { PageSpinner } from '../components/ui/Spinner'
import toast from 'react-hot-toast'
import type { UpdateProfileRequest, ChangePasswordRequest } from '../types'

export default function Profile() {
  const { login, user: authUser } = useAuth()
  const queryClient = useQueryClient()
  const [tab, setTab] = useState<'profile' | 'password'>('profile')

  const { data: user, isLoading } = useQuery({ queryKey: ['me'], queryFn: getMe })

  const profileForm = useForm<UpdateProfileRequest>({
    defaultValues: { name: authUser?.name ?? '' },
  })

  const passwordForm = useForm<ChangePasswordRequest>()

  const profileMutation = useMutation({
    mutationFn: (data: UpdateProfileRequest) => updateProfile(data),
    onSuccess: (updated) => {
      queryClient.setQueryData(['me'], updated)
      login(localStorage.getItem('accessToken')!, updated)
      toast.success('Profile updated successfully')
    },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  const passwordMutation = useMutation({
    mutationFn: (data: ChangePasswordRequest) => changePassword(data),
    onSuccess: () => {
      passwordForm.reset()
      toast.success('Password changed successfully')
    },
    onError: (e) => toast.error(getErrorMessage(e)),
  })

  if (isLoading) return <PageSpinner />
  if (!user) return null

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">My Profile</h1>

      {/* Avatar card */}
      <div className="bg-white rounded-xl border border-gray-200 p-6 mb-6 flex items-center gap-5">
        <div className="w-16 h-16 bg-primary-100 text-primary-700 rounded-full flex items-center justify-center text-2xl font-bold shrink-0">
          {user.name.charAt(0).toUpperCase()}
        </div>
        <div>
          <p className="text-lg font-semibold text-gray-900">{user.name}</p>
          <p className="text-sm text-gray-500">{user.email}</p>
          <div className="flex items-center gap-2 mt-1">
            <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
              user.role === 'ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-gray-100 text-gray-700'
            }`}>
              {user.role}
            </span>
            <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
              user.emailVerified ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'
            }`}>
              {user.emailVerified ? 'Verified' : 'Unverified'}
            </span>
          </div>
          <p className="text-xs text-gray-400 mt-1">Member since {formatDate(user.createdAt)}</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-gray-200 mb-6">
        {(['profile', 'password'] as const).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-4 py-2 text-sm font-medium capitalize border-b-2 -mb-px transition-colors ${
              tab === t
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            {t === 'profile' ? 'Profile Info' : 'Change Password'}
          </button>
        ))}
      </div>

      {tab === 'profile' ? (
        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <form onSubmit={profileForm.handleSubmit((d) => profileMutation.mutate(d))} className="space-y-4">
            <Input
              label="Full name"
              defaultValue={user.name}
              error={profileForm.formState.errors.name?.message}
              {...profileForm.register('name', {
                required: 'Name is required',
                minLength: { value: 2, message: 'At least 2 characters' },
              })}
            />
            <Input label="Email address" value={user.email} disabled hint="Email cannot be changed" />
            <Button type="submit" loading={profileMutation.isPending}>
              Save Changes
            </Button>
          </form>
        </div>
      ) : (
        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <form
            onSubmit={passwordForm.handleSubmit((d) => passwordMutation.mutate(d))}
            className="space-y-4"
          >
            <Input
              label="Current password"
              type="password"
              placeholder="••••••••"
              error={passwordForm.formState.errors.currentPassword?.message}
              {...passwordForm.register('currentPassword', { required: 'Current password is required' })}
            />
            <Input
              label="New password"
              type="password"
              placeholder="••••••••"
              hint="At least 8 characters"
              error={passwordForm.formState.errors.newPassword?.message}
              {...passwordForm.register('newPassword', {
                required: 'New password is required',
                minLength: { value: 8, message: 'At least 8 characters' },
              })}
            />
            <Input
              label="Confirm new password"
              type="password"
              placeholder="••••••••"
              error={passwordForm.formState.errors.confirmPassword?.message}
              {...passwordForm.register('confirmPassword', {
                required: 'Please confirm your password',
                validate: (v) =>
                  v === passwordForm.watch('newPassword') || 'Passwords do not match',
              })}
            />
            <Button type="submit" loading={passwordMutation.isPending}>
              Change Password
            </Button>
          </form>
        </div>
      )}
    </div>
  )
}
