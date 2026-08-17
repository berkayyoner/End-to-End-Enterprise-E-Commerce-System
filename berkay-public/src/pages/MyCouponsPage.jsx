import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import '../styles/emptyPage.css'

export function MyCouponsPage() {
  const navigate = useNavigate()
  const { t } = useTranslation()

  const handleBack = () => {
    navigate('/profile')
  }

  return (
    <section id="center" className="empty-page-container">
      <h1>{t('myCoupons.title')}</h1>
      <div className="empty-state">
        <p>{t('myCoupons.empty')}</p>
        <p>{t('myCoupons.comingSoon')}</p>
        <button onClick={handleBack} className="btn-back">
          {t('myAccount.title')}
        </button>
      </div>
    </section>
  )
}
