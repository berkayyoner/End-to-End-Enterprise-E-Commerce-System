import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { FormField } from '../../components/FormField.jsx'
import { useTranslation } from '../../i18n'
import { useAuth } from '../../auth/useAuth.js'
import { fetchMyIdVerification, submitIdVerification } from '../../api/authApi.js'
import './auth.css'

const INITIAL_FORM = { idNumber: '', frontPhoto: null, backPhoto: null }

export function IdVerificationPage() {
  const { t } = useTranslation()
  const { user, refreshUser } = useAuth()

  const [latestApplication, setLatestApplication] = useState(undefined)
  const [form, setForm] = useState(INITIAL_FORM)
  const [formError, setFormError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (!user?.idVerified) {
      fetchMyIdVerification().then(setLatestApplication).catch(() => setLatestApplication(null))
    }
  }, [user])

  async function handleSubmit(event) {
    event.preventDefault()
    if (!form.frontPhoto || !form.backPhoto) {
      setFormError(t('idVerification.missingPhotos'))
      return
    }
    setFormError(null)
    setSubmitting(true)
    try {
      const application = await submitIdVerification(form)
      setLatestApplication(application)
      await refreshUser()
    } catch (error) {
      setFormError(error.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (user?.idVerified) {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <h1>{t('idVerification.title')}</h1>
          <p className="auth-subtitle">{t('idVerification.alreadyVerifiedNotice')}</p>
          <Link to="/profile">{t('idVerification.backToProfile')}</Link>
        </div>
      </div>
    )
  }

  if (latestApplication === undefined) {
    return <p className="page-loading">{t('common.loading')}</p>
  }

  if (latestApplication?.status === 'PENDING') {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <h1>{t('idVerification.title')}</h1>
          <p className="auth-subtitle">{t('idVerification.pendingNotice')}</p>
          <Link to="/profile">{t('idVerification.backToProfile')}</Link>
        </div>
      </div>
    )
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>{t('idVerification.title')}</h1>
        <p className="auth-subtitle">{t('idVerification.subtitle')}</p>

        {latestApplication?.status === 'REJECTED' ? (
          <p className="auth-error-banner">
            {t('profile.idVerification.rejectedNotice', { reason: latestApplication.rejectionReason })}
          </p>
        ) : null}

        {formError ? <p className="auth-error-banner">{formError}</p> : null}

        <form className="auth-form" onSubmit={handleSubmit}>
          <FormField label={t('idVerification.idNumber')}>
            <input
              type="text"
              required
              maxLength={50}
              value={form.idNumber}
              onChange={(event) => setForm((current) => ({ ...current, idNumber: event.target.value }))}
            />
          </FormField>

          <FormField label={t('idVerification.frontPhoto')}>
            <input
              type="file"
              accept="image/*"
              required
              onChange={(event) => setForm((current) => ({ ...current, frontPhoto: event.target.files[0] ?? null }))}
            />
          </FormField>

          <FormField label={t('idVerification.backPhoto')}>
            <input
              type="file"
              accept="image/*"
              required
              onChange={(event) => setForm((current) => ({ ...current, backPhoto: event.target.files[0] ?? null }))}
            />
          </FormField>

          <button type="submit" className="auth-submit" disabled={submitting}>
            {submitting ? t('idVerification.submitting') : t('idVerification.submit')}
          </button>
        </form>

        <p className="auth-switch">
          <Link to="/profile">{t('idVerification.backToProfile')}</Link>
        </p>
      </div>
    </div>
  )
}
