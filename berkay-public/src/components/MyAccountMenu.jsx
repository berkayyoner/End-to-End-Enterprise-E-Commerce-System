import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import './myAccountMenu.css'

export function MyAccountMenu() {
  const navigate = useNavigate()
  const { t } = useTranslation()
  const { user, logout } = useAuth()
  const [isOpen, setIsOpen] = useState(false)

  const handleMenuItemClick = (path) => {
    navigate(path)
    setIsOpen(false)
  }

  const handleLogout = async () => {
    await logout()
    navigate('/')
    setIsOpen(false)
  }

  return (
    <div className="my-account-menu">
      <button
        className="account-trigger"
        onClick={() => setIsOpen(!isOpen)}
        aria-expanded={isOpen}
        aria-label={t('myAccount.title')}
      >
        👤 {t('common.profile')}
      </button>

      {isOpen && (
        <div className="account-dropdown">
          <div className="account-header">
            <div className="user-email">{user?.email}</div>
          </div>

          <div className="account-menu-items">
            <button
              className="menu-item"
              onClick={() => handleMenuItemClick('/my-orders')}
            >
              {t('myAccount.myOrders')}
            </button>

            <button
              className="menu-item"
              onClick={() => handleMenuItemClick('/my-reviews')}
            >
              {t('myAccount.myReviews')}
            </button>

            <button
              className="menu-item"
              onClick={() => handleMenuItemClick('/my-coupons')}
            >
              {t('myAccount.myCoupons')}
            </button>

            <button
              className="menu-item"
              onClick={() => handleMenuItemClick('/seller-messages')}
            >
              {t('myAccount.sellerMessages')}
            </button>

            <button
              className="menu-item"
              onClick={() => handleMenuItemClick('/my-user-info')}
            >
              {t('myAccount.myUserInfo')}
            </button>

            <hr className="menu-divider" />

            <button
              className="menu-item logout"
              onClick={handleLogout}
            >
              {t('common.logout')}
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
