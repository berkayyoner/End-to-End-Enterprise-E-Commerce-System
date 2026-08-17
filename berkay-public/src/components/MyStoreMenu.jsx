import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { getSellerProfile } from '../api/sellerApi'
import './myStoreMenu.css'

export function MyStoreMenu() {
  const { t } = useTranslation()
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const data = await getSellerProfile()
        setProfile(data)
      } catch (error) {
        console.error('Failed to load seller profile:', error)
      } finally {
        setLoading(false)
      }
    }

    fetchProfile()
  }, [])

  return (
    <div className="my-store-menu">
      <button type="button" className="my-store-button">
        {t('myStore.title')} ▼
      </button>
      <div className="my-store-dropdown">
        {loading ? (
          <div className="menu-item loading">{t('common.loading')}</div>
        ) : profile ? (
          <>
            <div className="menu-item earnings">
              <span className="earnings-label">{t('myStore.earnedMoney')}:</span>
              <span className="earnings-value">₺{profile.totalEarned?.toFixed(2) || '0.00'}</span>
            </div>
            <hr className="menu-separator" />
            <Link to="/my-products" className="menu-item link">
              {t('myStore.myProducts')}
            </Link>
            <Link to="/add-product" className="menu-item link">
              {t('myStore.addNewProduct')}
            </Link>
          </>
        ) : (
          <div className="menu-item error">{t('common.loading')}</div>
        )}
      </div>
    </div>
  )
}
