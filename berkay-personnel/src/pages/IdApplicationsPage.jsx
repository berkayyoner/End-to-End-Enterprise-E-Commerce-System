import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { listIdApplications, approveIdApplication, rejectIdApplication } from '../api/idApplicationsApi.js'
import { ApiError } from '../api/httpClient.js'
import '../styles/applications.css'

export function IdApplicationsPage() {
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
      const data = await listIdApplications(0, 50, 'PENDING')
      setApplications(data.content || [])
    } catch (err) {
      setError(err instanceof ApiError ? err.message : t('idApplications.error'))
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
      if (!window.confirm(t('idApplications.confirmApprove'))) return

      try {
        await approveIdApplication(id)
        setSuccessMessage(t('idApplications.approveSuccess'))
        setTimeout(() => setSuccessMessage(null), 3000)
        await loadApplications()
      } catch (err) {
        setError(err instanceof ApiError ? err.message : t('idApplications.errorApproving'))
      }
    },
    [t, loadApplications],
  )

  // Handle reject
  const handleReject = useCallback(
    async (id) => {
      if (!rejectReason.trim()) {
        setError(t('idApplications.reasonRequired'))
        return
      }

      try {
        await rejectIdApplication(id, rejectReason)
        setSuccessMessage(t('idApplications.rejectSuccess'))
        setRejectingId(null)
        setRejectReason('')
        setTimeout(() => setSuccessMessage(null), 3000)
        await loadApplications()
      } catch (err) {
        setError(err instanceof ApiError ? err.message : t('idApplications.errorRejecting'))
      }
    },
    [rejectReason, t, loadApplications],
  )

  const canView = hasPermission('P1', 'VIEW')
  const canEdit = hasPermission('P1', 'EDIT')

  if (!canView) {
    return <div className="access-denied">{t('common.accessDenied')}</div>
  }

  return (
    <div className="applications-page id-applications-page">
      <h1>{t('idApplications.title')}</h1>

      {successMessage && <div className="success-message">{successMessage}</div>}
      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="loading">{t('idApplications.loading')}</div>
      ) : applications.length === 0 ? (
        <div className="empty-state">{t('idApplications.noPending')}</div>
      ) : (
        <div className="applications-list">
          {applications.map((application) => (
            <div key={application.id} className="application-item">
              <div className="application-header">
                <div className="application-info">
                  <h3>{t('idApplications.listTitle')}</h3>
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
                <div className="id-details">
                  <div className="id-number">
                    <strong>{t('idApplications.idNumber')}:</strong> {application.idNumber}
                  </div>
                </div>

                <div className="photos-section">
                  <h4>{t('idApplications.photos')}</h4>
                  <div className="photos-grid">
                    {application.frontPhotoBase64 && (
                      <div className="photo-container">
                        <p className="photo-label">{t('idApplications.frontPhoto')}</p>
                        <img src={application.frontPhotoBase64} alt="Front of ID" className="id-photo" />
                      </div>
                    )}
                    {application.backPhotoBase64 && (
                      <div className="photo-container">
                        <p className="photo-label">{t('idApplications.backPhoto')}</p>
                        <img src={application.backPhotoBase64} alt="Back of ID" className="id-photo" />
                      </div>
                    )}
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
