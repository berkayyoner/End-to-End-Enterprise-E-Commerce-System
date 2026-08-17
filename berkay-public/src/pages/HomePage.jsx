import { useTranslation } from '../i18n'

export function HomePage() {
  const { t } = useTranslation()

  return (
    <section id="center">
      <div>
        <h1>{t('home.title')}</h1>
        <p>{t('home.subtitle')}</p>
        <p>{t('home.comingSoon')}</p>
      </div>
    </section>
  )
}
