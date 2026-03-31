import { ReactNode } from 'react'

type Color = 'gray' | 'green' | 'red' | 'yellow' | 'blue' | 'indigo' | 'purple' | 'orange'

interface BadgeProps {
  color?: Color
  children: ReactNode
  className?: string
}

const colors: Record<Color, string> = {
  gray:   'bg-gray-100 text-gray-700',
  green:  'bg-green-100 text-green-800',
  red:    'bg-red-100 text-red-800',
  yellow: 'bg-yellow-100 text-yellow-800',
  blue:   'bg-blue-100 text-blue-800',
  indigo: 'bg-indigo-100 text-indigo-800',
  purple: 'bg-purple-100 text-purple-800',
  orange: 'bg-orange-100 text-orange-800',
}

export default function Badge({ color = 'gray', children, className = '' }: BadgeProps) {
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${colors[color]} ${className}`}>
      {children}
    </span>
  )
}
