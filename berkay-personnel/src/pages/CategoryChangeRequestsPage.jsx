import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { listCategoryChangeRequests, approveCategoryChangeRequest, rejectCategoryChangeRequest } from '../api/changeRequestApi.js'
import { ApiError } from '../api/httpClient.js'
import '../styles/changeRequests.css'

const CATEGORY_LEVEL_LABELS = {
  MAIN: 'Main Category',
  SUB: 'Sub Type',
  INNER: 'Inner Type',
}

export function CategoryChangeRequestsPage() {
  const { t, language } = useTranslation()
  const { hasPermission } = useAuth()

  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [successMessage, setSuccessMessage] = useState(null)
  const [rejectingId, setRejectingId] = useState(null)
  const [rejectReason, setRejectReason] = useState('')

  // Load requests
  const loadRequests = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await listCategoryChangeRequests(0, 50)
      setRequests(data.content || [])
    } catch (err) {
      setError(err instanceof ApiError ? err.message : t('changeRequests.error'))
    } finally {
      setLoading(false)
    }
  }, [t])

  // Initial load
  useEffect(() => {
    loadRequests()
  }, [loadRequests])

  // Handle approve
  const handleApprove = useCallback(
    async (id) => {
      if (!window.confirm(t('changeRequests.confirmApprove'))) return

      try {
        await approveCategoryChangeRequest(id)
        setSuccessMessage(t('changeRequests.approveSuccess'))
        setTimeout(() => setSuccessMessage(null), 3000)
        await loadRequests()
      } catch (err) {
        setError(err instanceof ApiError ? err.message : t('changeRequests.errorApproving'))
      }
    },
    [t, loadRequests],
  )

  // Handle reject
  const handleReject = useCallback(
    async (id) => {
      if (!rejectReason.trim()) {
        setError(t('changeRequests.reasonRequired'))
        return
      }

      try {
        await rejectCategoryChangeRequest(id, rejectReason)
        setSuccessMessage(t('changeRequests.rejectSuccess'))
        setRejectingId(null)
        setRejectReason('')
        setTimeout(() => setSuccessMessage(null), 3000)
        await loadRequests()
      } catch (err) {
        setError(err instanceof ApiError ? err.message : t('changeRequests.errorRejecting'))
      }
    },
    [rejectReason, t, loadRequests],
  )

  const canView = hasPermission('P0', 'VIEW')
  const canEdit = hasPermission('P0', 'EDIT')

  if (!canView) {
    return <div className="access-denied">{t('common.accessDenied')}</div>
  }

  return (
    <div className="change-requests-page">
      <h1>{t('changeRequests.title')}</h1>

      {successMessage && <div className="success-message">{successMessage}</div>}
      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="loading">{t('common.loading')}</div>
      ) : requests.length === 0 ? (
        <div className="empty-state">{t('changeRequests.noPending')}</div>
      ) : (
        <div className="requests-list">
          {requests.map((request) => (
            <div key={request.id} className="request-item">
              <div className="request-header">
                <div className="request-info">
                  <h3>{CATEGORY_LEVEL_LABELS[request.categoryLevel]}</h3>
                  <div className="request-meta">
                    <span className={`status status-${request.status.toLowerCase()}`}>{t(`changeRequests.${request.status.toLowerCase()}`)}</span>
                    <span className="submitted-by">
                      {t('common.submittedBy')}: {request.submittedByEmail}
                    </span>
                    <span className="created-at">
                      {t('common.createdAt')}: {new Date(request.createdAt).toLocaleString(language === 'tr' ? 'tr-TR' : 'en-US')}
                    </span>
                  </div>
                </div>
              </div>

              <div className="request-content">
                <div className="translation-pair">
                  <div className="translation">
                    <h4>Turkish</h4>
                    <p className="translation-name">
                      <strong>{t('common.name')}:</strong> {request.nameTranslationTr}
                    </p>
                    {request.descriptionTranslationTr && (
                      <p className="translation-desc">
                        <strong>{t('common.description')}:</strong> {request.descriptionTranslationTr}
                      </p>
                    )}
                  </div>
                  <div className="translation">
                    <h4>English</h4>
                    <p className="translation-name">
                      <strong>{t('common.name')}:</strong> {request.nameTranslationEn}
                    </p>
                    {request.descriptionTranslationEn && (
                      <p className="translation-desc">
                        <strong>{t('common.description')}:</strong> {request.descriptionTranslationEn}
                      </p>
                    )}
                  </div>
                </div>

                {request.status !== 'PENDING' && (
                  <div className="review-info">
                    <p>
                      <strong>{t('common.reviewedBy')}:</strong> {request.reviewedByEmail}
                    </p>
                    <p>
                      <strong>{t('common.reviewedAt')}:</strong> {new Date(request.reviewedAt).toLocaleString(language === 'tr' ? 'tr-TR' : 'en-US')}
                    </p>
                    {request.rejectionReason && (
                      <p>
                        <strong>{t('common.rejectionReason')}:</strong> {request.rejectionReason}
                      </p>
                    )}
                  </div>
                )}
              </div>

              {request.status === 'PENDING' && canEdit && (
                <div className="request-actions">
                  <button type="button" className="action-button approve" onClick={() => handleApprove(request.id)}>
                    {t('common.approve')}
                  </button>
                  <button
                    type="button"
                    className="action-button reject"
                    onClick={() => (rejectingId === request.id ? setRejectingId(null) : setRejectingId(request.id))}
                  >
                    {rejectingId === request.id ? t('common.cancel') : t('common.reject')}
                  </button>
                </div>
              )}

              {rejectingId === request.id && (
                <div className="reject-form">
                  <textarea
                    placeholder={t('common.rejectionReason')}
                    value={rejectReason}
                    onChange={(e) => setRejectReason(e.target.value)}
                  />
                  <div className="form-buttons">
                    <button type="button" className="primary-button" onClick={() => handleReject(request.id)}>
                      {t('common.reject')}
                    </button>
                    <button type="button" className="secondary-button" onClick={() => setRejectingId(null)}>
                      {t('common.cancel')}
                    </button>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
