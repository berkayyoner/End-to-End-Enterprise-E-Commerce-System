import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { FormField } from '../../components/FormField.jsx'
import { useTranslation } from '../../i18n'
import { useAuth } from '../../auth/useAuth.js'
import { fetchMySellerApplication, submitSellerApplication } from '../../api/authApi.js'
import './auth.css'

const INITIAL_FORM = { companyName: '', taxId: '', companyPhone: '', companyAddress: '' }

function StatusBadge({ status }) {
  const { t } = useTranslation()
  return <span className={`status-badge ${status.toLowerCase()}`}>{t(`status.${status.toLowerCase()}`)}</span>
}

function SellerApplicationSection({ user, refreshUser }) {
  const { t } = useTranslation()
  const [application, setApplication] = useState(undefined)
  const [form, setForm] = useState(INITIAL_FORM)
  const [fieldErrors, setFieldErrors] = useState({})
  const [formError, setFormError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (!user.seller) {
      fetchMySellerApplication().then(setApplication).catch(() => setApplication(null))
    }
  }, [user.seller])

  async function handleSubmit(event) {
    event.preventDefault()
    setFormError(null)
    setFieldErrors({})
    setSubmitting(true)
    try {
      const created = await submitSellerApplication(form)
      setApplication(created)
      await refreshUser()
    } catch (error) {
      setFieldErrors(error.fieldErrors ?? {})
      setFormError(error.fieldErrors ? null : error.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="profile-section">
      <h2>{t('profile.seller.title')}</h2>

      {user.seller ? (
        <p>{t('profile.seller.alreadySellerNotice')}</p>
      ) : !user.idVerified ? (
        <p>{t('profile.seller.mustVerifyNotice')}</p>
      ) : application === undefined ? (
        <p>{t('common.loading')}</p>
      ) : application?.status === 'PENDING' ? (
        <p>
          <StatusBadge status="PENDING" /> {t('profile.seller.pendingNotice')}
        </p>
      ) : (
        <>
          {application?.status === 'REJECTED' ? (
            <p className="auth-error-banner">
              {t('profile.seller.rejectedNotice', { reason: application.rejectionReason })}
            </p>
          ) : (
            <p>{t('profile.seller.applyPrompt')}</p>
          )}

          {formError ? <p className="auth-error-banner">{formError}</p> : null}

          <form className="auth-form" onSubmit={handleSubmit}>
            <FormField label={t('profile.seller.companyName')} error={fieldErrors.companyName}>
              <input
                type="text"
                required
                maxLength={200}
                value={form.companyName}
                onChange={(event) => setForm((current) => ({ ...current, companyName: event.target.value }))}
              />
            </FormField>
            <FormField label={t('profile.seller.taxId')} error={fieldErrors.taxId}>
              <input
                type="text"
                required
                maxLength={50}
                value={form.taxId}
                onChange={(event) => setForm((current) => ({ ...current, taxId: event.target.value }))}
              />
            </FormField>
            <FormField label={t('profile.seller.companyPhone')} error={fieldErrors.companyPhone}>
              <input
                type="tel"
                required
                maxLength={32}
                value={form.companyPhone}
                onChange={(event) => setForm((current) => ({ ...current, companyPhone: event.target.value }))}
              />
            </FormField>
            <FormField label={t('profile.seller.companyAddress')} error={fieldErrors.companyAddress}>
              <textarea
                required
                maxLength={500}
                rows={3}
                value={form.companyAddress}
                onChange={(event) => setForm((current) => ({ ...current, companyAddress: event.target.value }))}
              />
            </FormField>

            <button type="submit" className="auth-submit" disabled={submitting}>
              {submitting
                ? t('profile.seller.submitting')
                : application?.status === 'REJECTED'
                  ? t('profile.seller.tryAgain')
                  : t('profile.seller.submit')}
            </button>
          </form>
        </>
      )}
    </section>
  )
}

export function ProfilePage() {
  const { t, language } = useTranslation()
  const { user, logout, refreshUser } = useAuth()

  if (!user) {
    return <p className="page-loading">{t('common.loading')}</p>
  }

  const memberSince = new Intl.DateTimeFormat(language === 'tr' ? 'tr-TR' : 'en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  }).format(new Date(user.createdAt))

  return (
    <div className="auth-page">
      <div className="auth-card" style={{ maxWidth: 560 }}>
        <h1>{t('profile.title')}</h1>
        <p className="auth-subtitle">{t('profile.subtitle')}</p>

        <section className="profile-section">
          <h2>{t('profile.accountInfo')}</h2>
          <p>
            <strong>{t('profile.email')}:</strong> {user.email}
          </p>
          {user.phoneNumber ? (
            <p>
              <strong>{t('profile.phone')}:</strong> {user.phoneNumber}
            </p>
          ) : null}
          <p>
            <strong>{t('profile.accountType')}:</strong>{' '}
            {user.seller ? t('profile.typeSeller') : t('profile.typeCustomer')}
          </p>
          <p>{t('profile.memberSince', { date: memberSince })}</p>
        </section>

        <section className="profile-section">
          <h2>{t('profile.idVerification.title')}</h2>
          {user.idVerified ? (
            <p>{t('profile.idVerification.verifiedNotice')}</p>
          ) : (
            <>
              <p>{t('profile.idVerification.notVerifiedNotice')}</p>
              <Link className="auth-submit" to="/id-verification" style={{ display: 'inline-block', textDecoration: 'none' }}>
                {t('profile.idVerification.verifyButton')}
              </Link>
            </>
          )}
        </section>

        <SellerApplicationSection user={user} refreshUser={refreshUser} />

        <button type="button" className="auth-submit" onClick={logout}>
          {t('common.logout')}
        </button>
      </div>
    </div>
  )
}
