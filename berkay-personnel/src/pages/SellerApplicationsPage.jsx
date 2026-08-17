import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { listSellerApplications, approveSellerApplication, rejectSellerApplication } from '../api/sellerApplicationsApi.js'
import { ApiError } from '../api/httpClient.js'
import '../styles/applications.css'

export function SellerApplicationsPage() {
  const { t, language } = useTranslation()
  const { hasPermission } = useAuth()

  const [applications, setApplications] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [successMessage, setSuccessMessage] = useState(null)
  const [rejectingId, setRejectingId] = useState(null)
  const [rejectReason, setRejectReason] = useState('')

  // Load applications
  const loadApplications = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await listSellerApplications(0, 50, 'PENDING')
      setApplications(data.content || [])
    } catch (err) {
      setError(err instanceof ApiError ? err.message : t('sellerApplications.error'))
    } finally {
      setLoading(false)
    }
  }, [t])

  // Initial load
  useEffect(() => {
    loadApplications()
  }, [loadApplications])

  // Handle approve
  const handleApprove = useCallback(
    async (id) => {
      if (!window.confirm(t('sellerApplications.confirmApprove'))) return

      try {
        await approveSellerApplication(id)
        setSuccessMessage(t('sellerApplications.approveSuccess'))
        setTimeout(() => setSuccessMessage(null), 3000)
        await loadApplications()
      } catch (err) {
        setError(err instanceof ApiError ? err.message : t('sellerApplications.errorApproving'))
      }
    },
    [t, loadApplications],
  )

  // Handle reject
  const handleReject = useCallback(
    async (id) => {
      if (!rejectReason.trim()) {
        setError(t('sellerApplications.reasonRequired'))
        return
      }

      try {
        await rejectSellerApplication(id, rejectReason)
        setSuccessMessage(t('sellerApplications.rejectSuccess'))
        setRejectingId(null)
        setRejectReason('')
        setTimeout(() => setSuccessMessage(null), 3000)
        await loadApplications()
      } catch (err) {
        setError(err instanceof ApiError ? err.message : t('sellerApplications.errorRejecting'))
      }
    },
    [rejectReason, t, loadApplications],
  )

  const canView = hasPermission('P2', 'VIEW')
  const canEdit = hasPermission('P2', 'EDIT')

  if (!canView) {
    return <div className="access-denied">{t('common.accessDenied')}</div>
  }

  return (
    <div className="applications-page seller-applications-page">
      <h1>{t('sellerApplications.title')}</h1>

      {successMessage && <div className="success-message">{successMessage}</div>}
      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="loading">{t('sellerApplications.loading')}</div>
      ) : applications.length === 0 ? (
        <div className="empty-state">{t('sellerApplications.noPending')}</div>
      ) : (
        <div className="applications-list">
          {applications.map((application) => (
            <div key={application.id} className="application-item">
              <div className="application-header">
                <div className="application-info">
                  <h3>{t('sellerApplications.listTitle')}</h3>
                  <div className="application-meta">
                    <span className={`status status-${application.status.toLowerCase()}`}>
                      {t(`common.${application.status.toLowerCase()}`)}
                    </span>
                    <span className="submitted-by">
                      {t('common.submittedBy')}: {application.appUserEmail}
                    </span>
                    <span className="created-at">
                      {t('common.createdAt')}: {new Date(application.createdAt).toLocaleString(language === 'tr' ? 'tr-TR' : 'en-US')}
                    </span>
                  </div>
                </div>
              </div>

              <div className="application-content">
                <div className="company-details">
                  <div className="detail-row">
                    <span className="detail-label">
                      <strong>{t('sellerApplications.companyName')}:</strong>
                    </span>
                    <span className="detail-value">{application.companyName}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">
                      <strong>{t('sellerApplications.taxId')}:</strong>
                    </span>
                    <span className="detail-value">{application.taxId}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">
                      <strong>{t('sellerApplications.companyPhone')}:</strong>
                    </span>
                    <span className="detail-value">{application.companyPhone}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">
                      <strong>{t('sellerApplications.companyAddress')}:</strong>
                    </span>
                    <span className="detail-value">{application.companyAddress}</span>
                  </div>
                </div>

                {application.status !== 'PENDING' && (
                  <div className="review-info">
                    <p>
                      <strong>{t('common.reviewedBy')}:</strong> {application.reviewedByEmail}
                    </p>
                    <p>
                      <strong>{t('common.reviewedAt')}:</strong> {new Date(application.reviewedAt).toLocaleString(language === 'tr' ? 'tr-TR' : 'en-US')}
                    </p>
                    {application.rejectionReason && (
                      <p>
                        <strong>{t('common.rejectionReason')}:</strong> {application.rejectionReason}
                      </p>
                    )}
                  </div>
                )}
              </div>

              {application.status === 'PENDING' && canEdit && (
                <div className="application-actions">
                  <button type="button" className="action-button approve" onClick={() => handleApprove(application.id)}>
                    {t('common.approve')}
                  </button>
                  <button
                    type="button"
                    className="action-button reject"
                    onClick={() => (rejectingId === application.id ? setRejectingId(null) : setRejectingId(application.id))}
                  >
                    {rejectingId === application.id ? t('common.cancel') : t('common.reject')}
                  </button>
                </div>
              )}

              {rejectingId === application.id && (
                <div className="reject-form">
                  <textarea
                    placeholder={t('common.rejectionReason')}
                    value={rejectReason}
                    onChange={(e) => setRejectReason(e.target.value)}
                  />
                  <div className="form-buttons">
                    <button type="button" className="primary-button" onClick={() => handleReject(application.id)}>
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
