import { Link, Outlet, useLocation } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth.js'
import { useTheme } from '../theme'
import './Layout.css'

export function Layout() {
  const { t, language, changeLanguage, supportedLanguages } = useTranslation()
  const { isAuthenticated, status, logout, hasPermission } = useAuth()
  const { theme, toggleTheme } = useTheme()
  const location = useLocation()

  return (
    <>
      <header className="admin-header">
        <h1 className="admin-title">{t('common.appName')}</h1>

        <nav className="admin-nav">
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

          <button type="button" className="theme-toggle-button" onClick={toggleTheme} title={t('common.toggleTheme')}>
            {theme === 'light' ? '🌙' : '☀️'}
          </button>

          {status === 'loading' ? null : isAuthenticated ? (
            <button type="button" className="logout-button" onClick={logout}>
              {t('common.logout')}
            </button>
          ) : null}
        </nav>
      </header>

      {status !== 'loading' && isAuthenticated ? (
        <div className="admin-container">
          <aside className="admin-sidebar">
            <nav className="admin-menu">
              <ul>
                <li>
                  <Link to="/" className={`menu-link ${location.pathname === '/' ? 'active' : ''}`}>
                    Home
                  </Link>
                </li>
                {hasPermission('P4') && (
                  <li>
                    <Link to="/categories" className={`menu-link ${location.pathname === '/categories' ? 'active' : ''}`}>
                      {t('categories.title')}
                    </Link>
                  </li>
                )}
                {hasPermission('P0', 'VIEW') && (
                  <li>
                    <Link to="/category-change-requests" className={`menu-link ${location.pathname === '/category-change-requests' ? 'active' : ''}`}>
                      {t('changeRequests.title')}
                    </Link>
                  </li>
                )}
                {hasPermission('P5') && (
                  <li>
                    <Link to="/products" className={`menu-link ${location.pathname === '/products' ? 'active' : ''}`}>
                      {t('products.title')}
                    </Link>
                  </li>
                )}
              </ul>
            </nav>
          </aside>

          <main className="admin-main">
            <Outlet />
          </main>
        </div>
      ) : (
        <main className="admin-main">
          <Outlet />
        </main>
      )}
    </>
  )
}
