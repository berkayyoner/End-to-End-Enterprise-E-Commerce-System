import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { listPersonnelLogs } from '../api/logsApi'
import { ApiError } from '../api/httpClient'
import '../styles/logs.css'

export function PersonnelLogsPage() {
  const { t } = useTranslation()
  const { hasPermission } = useAuth()

  // Logs state
  const [logs, setLogs] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [page, setPage] = useState(0)
  const [pageSize] = useState(20)

  // Load logs
  const loadLogs = useCallback(async (pageNum = 0) => {
    setLoading(true)
    setError(null)
    try {
      const data = await listPersonnelLogs(pageNum, pageSize)
      setLogs(Array.isArray(data.content) ? data.content : [])
      setPage(pageNum)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : t('common.error'))
    } finally {
      setLoading(false)
    }
  }, [pageSize, t])

  // Initial load
  useEffect(() => {
    if (hasPermission('P9')) {
      loadLogs(0)
    }
  }, [loadLogs, hasPermission])

  const handlePreviousPage = () => {
    if (page > 0) {
      loadLogs(page - 1)
    }
  }

  const handleNextPage = () => {
    loadLogs(page + 1)
  }

  const formatDate = (instantString) => {
    if (!instantString) return '-'
    return new Date(instantString).toLocaleString()
  }

  if (!hasPermission('P9')) {
    return <div className="logs-container error">{t('common.permissionDenied')}</div>
  }

  return (
    <div className="logs-container">
      <h2>{t('personnelLogs.title')}</h2>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="loading-message">{t('common.loading')}</div>
      ) : logs.length === 0 ? (
        <div className="empty-message">{t('personnelLogs.emptyList')}</div>
      ) : (
        <>
          <table className="logs-table">
            <thead>
              <tr>
                <th>{t('logs.table.id')}</th>
                <th>{t('logs.table.actorId')}</th>
                <th>{t('logs.table.action')}</th>
                <th>{t('logs.table.sourceService')}</th>
                <th>{t('logs.table.details')}</th>
                <th>{t('logs.table.ipAddress')}</th>
                <th>{t('logs.table.timestamp')}</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id}>
                  <td>{log.id}</td>
                  <td>{log.actorId}</td>
                  <td>
                    <span className="badge action-badge">{log.action}</span>
                  </td>
                  <td>{log.sourceService}</td>
                  <td className="details-cell">{log.details || '-'}</td>
                  <td>{log.ipAddress || '-'}</td>
                  <td>{formatDate(log.occurredAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="pagination-controls">
            <button
              type="button"
              className="button button-secondary"
              onClick={handlePreviousPage}
              disabled={page === 0}
            >
              {t('common.previous')}
            </button>
            <span className="page-info">
              {t('common.page')} {page + 1}
            </span>
            <button
              type="button"
              className="button button-secondary"
              onClick={handleNextPage}
              disabled={logs.length < pageSize}
            >
              {t('common.next')}
            </button>
          </div>
        </>
      )}
    </div>
  )
}
