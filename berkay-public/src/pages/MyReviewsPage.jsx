import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import '../styles/emptyPage.css'

export function MyReviewsPage() {
  const navigate = useNavigate()
  const { t } = useTranslation()

  const handleBack = () => {
    navigate('/profile')
  }

  return (
    <section id="center" className="empty-page-container">
      <h1>{t('myReviews.title')}</h1>
      <div className="empty-state">
        <p>{t('myReviews.empty')}</p>
        <p>{t('myReviews.comingSoon')}</p>
        <button onClick={handleBack} className="btn-back">
          {t('myAccount.title')}
        </button>
      </div>
    </section>
  )
}
