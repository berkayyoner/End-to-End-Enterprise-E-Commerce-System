import { Link, Outlet } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth.js'
import { CategoryNav } from './CategoryNav.jsx'
import { MyStoreMenu } from './MyStoreMenu.jsx'
import { MyAccountMenu } from './MyAccountMenu.jsx'
import './Layout.css'

export function Layout() {
  const { t, language, changeLanguage, supportedLanguages } = useTranslation()
  const { isAuthenticated, status, user } = useAuth()

  return (
    <>
      <header className="site-header">
        <Link to="/" className="site-logo">
          {t('common.appName')}
        </Link>

        <nav className="site-nav">
          <Link to="/">{t('common.home')}</Link>

          {status === 'loading' ? null : isAuthenticated ? (
            <>
              {user?.seller && <MyStoreMenu />}
              <Link to="/basket" className="basket-link" aria-label={t('basket.title')}>
                🛒 {t('basket.title')}
              </Link>
              <MyAccountMenu />
            </>
          ) : (
            <>
              <Link to="/login">{t('nav.login')}</Link>
              <Link to="/sign-up">{t('nav.signUp')}</Link>
            </>
          )}

          <select
            aria-label={t('common.language')}
            value={language}
            onChange={(event) => changeLanguage(event.target.value)}
          >
            {supportedLanguages.map((code) => (
              <option key={code} value={code}>
                {code.toUpperCase()}
              </option>
            ))}
          </select>
        </nav>
      </header>

      <div className="category-nav-bar">
        <CategoryNav />
      </div>

      <main className="site-main">
        <Outlet />
      </main>
    </>
  )
}
