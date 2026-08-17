import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { getActiveCoupons } from '../api/couponApi'
import '../styles/emptyPage.css'

export function MyCouponsPage() {
  const navigate = useNavigate()
  const { t } = useTranslation()
  const [coupons, setCoupons] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchCoupons = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await getActiveCoupons()
        setCoupons(data)
      } catch (err) {
        setError(err.message || t('myCoupons.loadError'))
        console.error('Failed to load coupons:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchCoupons()
  }, [t])

  const handleBack = () => {
    navigate('/profile')
  }

  if (loading) {
    return (
      <section id="center" className="empty-page-container">
        <h1>{t('myCoupons.title')}</h1>
        <div className="loading-spinner">{t('common.loading')}</div>
      </section>
    )
  }

  if (error) {
    return (
      <section id="center" className="empty-page-container">
        <h1>{t('myCoupons.title')}</h1>
        <div className="error-message">
          <p>{error}</p>
          <button onClick={() => window.location.reload()} className="btn-primary">
            {t('common.retry') || 'Retry'}
          </button>
        </div>
      </section>
    )
  }

  if (coupons.length === 0) {
    return (
      <section id="center" className="empty-page-container">
        <h1>{t('myCoupons.title')}</h1>
        <div className="empty-state">
          <p>{t('myCoupons.empty')}</p>
          <button onClick={handleBack} className="btn-back">
            {t('myAccount.title')}
          </button>
        </div>
      </section>
    )
  }

  return (
    <section id="center" className="empty-page-container">
      <h1>{t('myCoupons.title')}</h1>
      <div className="coupons-list">
        {coupons.map((coupon) => (
          <div key={coupon.code} className="coupon-card">
            <div className="coupon-code">
              <strong>{coupon.code}</strong>
            </div>
            <div className="coupon-discount">
              {coupon.discountPercentage.toFixed(1)}% {t('myCoupons.discount')}
            </div>
          </div>
        ))}
      </div>
      <button onClick={handleBack} className="btn-back" style={{ marginTop: '1rem' }}>
        {t('myAccount.title')}
      </button>
    </section>
  )
}
