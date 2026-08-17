import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import '../styles/comingSoon.css'

export function ComingSoonPage() {
  const navigate = useNavigate()
  const { t } = useTranslation()

  return (
    <section id="center" className="coming-soon-container">
      <div className="coming-soon-content">
        <div className="coming-soon-icon">🚀</div>
        <h1>{t('productDetail.comingSoon')}</h1>
        <p>This feature will be available in Phase 5.</p>
        <button className="back-button" onClick={() => navigate(-1)}>
          Go Back
        </button>
      </div>
    </section>
  )
}
