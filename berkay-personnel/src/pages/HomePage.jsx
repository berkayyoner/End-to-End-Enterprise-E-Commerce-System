import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth.js'

export function HomePage() {
  const { t } = useTranslation()
  const { personnel } = useAuth()

  return (
    <div>
      <h1>{t('home.title')}</h1>
      <p>{t('home.subtitle')}</p>
      {personnel ? (
        <div>
          <p>
            {t('common.welcome')} {personnel.firstName} {personnel.lastName}
          </p>
          <p>{t('home.comingSoon')}</p>
        </div>
      ) : (
        <p>{t('home.comingSoon')}</p>
      )}
    </div>
  )
}
