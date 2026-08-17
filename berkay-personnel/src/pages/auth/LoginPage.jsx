import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { FormField } from '../../components/FormField.jsx'
import { useTranslation } from '../../i18n'
import { useAuth } from '../../auth/useAuth.js'
import './auth.css'

export function LoginPage() {
  const { t } = useTranslation()
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [formError, setFormError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setFormError(null)
    setSubmitting(true)
    try {
      await login(email, password)
      navigate(location.state?.from?.pathname ?? '/', { replace: true })
    } catch (error) {
      setFormError(error.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>{t('auth.login.title')}</h1>
        <p className="auth-subtitle">{t('auth.login.subtitle')}</p>

        {formError ? <p className="auth-error-banner">{formError}</p> : null}

        <form className="auth-form" onSubmit={handleSubmit}>
          <FormField label={t('auth.login.email')}>
            <input type="email" required autoFocus value={email} onChange={(event) => setEmail(event.target.value)} />
          </FormField>

          <FormField label={t('auth.login.password')}>
            <input
              type="password"
              required
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </FormField>

          <button type="submit" className="auth-submit" disabled={submitting}>
            {submitting ? t('auth.login.submitting') : t('auth.login.submit')}
          </button>
        </form>
      </div>
    </div>
  )
}
