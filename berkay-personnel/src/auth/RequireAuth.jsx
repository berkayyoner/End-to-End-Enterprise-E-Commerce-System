import { Navigate, useLocation } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from './useAuth.js'

export function RequireAuth({ children }) {
  const { status } = useAuth()
  const { t } = useTranslation()
  const location = useLocation()

  if (status === 'loading') {
    return <p className="page-loading">{t('common.loading')}</p>
  }

  if (status === 'anonymous') {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  return children
}
