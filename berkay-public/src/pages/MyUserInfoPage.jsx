import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import '../styles/myUserInfo.css'

export function MyUserInfoPage() {
  const navigate = useNavigate()
  const { t } = useTranslation()
  const { user } = useAuth()

  const handleBack = () => {
    navigate('/profile')
  }

  const handleVerifyIdentity = () => {
    navigate('/id-verification')
  }

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A'
    return new Date(dateString).toLocaleDateString()
  }

  return (
    <section id="center" className="my-user-info-container">
      <h1>{t('myUserInfo.title')}</h1>

      <div className="info-section">
        <div className="info-row">
          <span className="label">{t('myUserInfo.email')}</span>
          <span className="value">{user?.email || 'N/A'}</span>
        </div>

        <div className="info-row">
          <span className="label">{t('myUserInfo.phone')}</span>
          <span className="value">{user?.phone || 'Not provided'}</span>
        </div>

        <div className="info-row">
          <span className="label">{t('myUserInfo.memberSince')}</span>
          <span className="value">{user?.createdAt ? formatDate(user.createdAt) : 'N/A'}</span>
        </div>

        <div className="info-row">
          <span className="label">{t('myUserInfo.accountType')}</span>
          <span className="value">
            {user?.seller ? t('profile.typeSeller') : t('profile.typeCustomer')}
          </span>
        </div>

        <div className="info-row">
          <span className="label">{t('myUserInfo.idVerification')}</span>
          <span className={`value verification-status ${user?.idVerified ? 'verified' : 'not-verified'}`}>
            {user?.idVerified ? t('myUserInfo.verified') : t('myUserInfo.notVerified')}
          </span>
        </div>

        {!user?.idVerified && (
          <div className="action-row">
            <button onClick={handleVerifyIdentity} className="btn-verify">
              {t('myUserInfo.verifyNow')}
            </button>
          </div>
        )}
      </div>

      <div className="page-actions">
        <button onClick={handleBack} className="btn-back">
          {t('myAccount.title')}
        </button>
      </div>
    </section>
  )
}
