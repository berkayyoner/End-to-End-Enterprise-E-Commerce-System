import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { FormField } from '../../components/FormField.jsx'
import { useTranslation } from '../../i18n'
import { useAuth } from '../../auth/useAuth.js'
import { ApiError } from '../../api/httpClient.js'
import './auth.css'

const INITIAL_FORM = { firstName: '', lastName: '', email: '', password: '', phoneNumber: '' }

export function SignUpPage() {
  const { t } = useTranslation()
  const { register } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState(INITIAL_FORM)
  const [fieldErrors, setFieldErrors] = useState({})
  const [formError, setFormError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  function updateField(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setFormError(null)
    setFieldErrors({})
    setSubmitting(true)
    try {
      await register(form)
      navigate('/profile', { replace: true })
    } catch (error) {
      if (error instanceof ApiError) {
        setFieldErrors(error.fieldErrors ?? {})
        setFormError(error.fieldErrors ? null : error.message)
      } else {
        setFormError(error.message)
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>{t('auth.signUp.title')}</h1>
        <p className="auth-subtitle">{t('auth.signUp.subtitle')}</p>

        {formError ? <p className="auth-error-banner">{formError}</p> : null}

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-row" style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
            <FormField label={t('auth.signUp.firstName')} error={fieldErrors.firstName} style={{ flex: '1 0 150px', minWidth: 0 }}>
              <input
                type="text"
                required
                maxLength={100}
                value={form.firstName}
                onChange={(event) => updateField('firstName', event.target.value)}
              />
            </FormField>
            <FormField label={t('auth.signUp.lastName')} error={fieldErrors.lastName} style={{ flex: '1 0 150px', minWidth: 0 }}>
              <input
                type="text"
                required
                maxLength={100}
                value={form.lastName}
                onChange={(event) => updateField('lastName', event.target.value)}
              />
            </FormField>
          </div>

          <FormField label={t('auth.signUp.email')} error={fieldErrors.email}>
            <input
              type="email"
              required
              maxLength={254}
              value={form.email}
              onChange={(event) => updateField('email', event.target.value)}
            />
          </FormField>

          <FormField label={t('auth.signUp.password')} error={fieldErrors.password} hint={t('auth.signUp.passwordHint')}>
            <input
              type="password"
              required
              minLength={8}
              maxLength={72}
              value={form.password}
              onChange={(event) => updateField('password', event.target.value)}
            />
          </FormField>

          <FormField label={t('auth.signUp.phoneNumber')} error={fieldErrors.phoneNumber}>
            <input
              type="tel"
              maxLength={32}
              value={form.phoneNumber}
              onChange={(event) => updateField('phoneNumber', event.target.value)}
            />
          </FormField>

          <button type="submit" className="auth-submit" disabled={submitting}>
            {submitting ? t('auth.signUp.submitting') : t('auth.signUp.submit')}
          </button>
        </form>

        <p className="auth-switch">
          {t('auth.signUp.haveAccount')} <Link to="/login">{t('auth.signUp.loginLink')}</Link>
        </p>
      </div>
    </div>
  )
}
