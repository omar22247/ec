interface PaginationProps {
  page: number           // 0-based
  totalPages: number
  onPageChange: (page: number) => void
}

export default function Pagination({ page, totalPages, onPageChange }: PaginationProps) {
  if (totalPages <= 1) return null

  const pages = Array.from({ length: totalPages }, (_, i) => i)
  const visible = pages.filter(
    (p) => p === 0 || p === totalPages - 1 || Math.abs(p - page) <= 1
  )

  return (
    <div className="flex items-center justify-center gap-1">
      <button
        disabled={page === 0}
        onClick={() => onPageChange(page - 1)}
        className="px-3 py-1.5 rounded-lg text-sm font-medium border border-gray-300 disabled:opacity-40 hover:bg-gray-50"
      >
        ‹ Prev
      </button>

      {visible.map((p, i) => {
        const prev = visible[i - 1]
        const showEllipsis = prev !== undefined && p - prev > 1
        return (
          <span key={p} className="flex items-center gap-1">
            {showEllipsis && <span className="px-2 text-gray-400">…</span>}
            <button
              onClick={() => onPageChange(p)}
              className={`w-9 h-9 rounded-lg text-sm font-medium border transition-colors
                ${p === page
                  ? 'bg-primary-600 text-white border-primary-600'
                  : 'border-gray-300 hover:bg-gray-50 text-gray-700'
                }`}
            >
              {p + 1}
            </button>
          </span>
        )
      })}

      <button
        disabled={page >= totalPages - 1}
        onClick={() => onPageChange(page + 1)}
        className="px-3 py-1.5 rounded-lg text-sm font-medium border border-gray-300 disabled:opacity-40 hover:bg-gray-50"
      >
        Next ›
      </button>
    </div>
  )
}
