import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import '../styles/emptyPage.css'

export function SellerMessagesPage() {
  const navigate = useNavigate()
  const { t } = useTranslation()

  const handleBack = () => {
    navigate('/profile')
  }

  return (
    <section id="center" className="empty-page-container">
      <h1>{t('sellerMessages.title')}</h1>
      <div className="empty-state">
        <p>{t('sellerMessages.comingSoon')}</p>
        <button onClick={handleBack} className="btn-back">
          {t('myAccount.title')}
        </button>
      </div>
    </section>
  )
}
