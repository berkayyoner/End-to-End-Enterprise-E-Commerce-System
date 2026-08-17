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
                {hasPermission('P7') && (
                  <li>
                    <Link to="/personnel" className={`menu-link ${location.pathname === '/personnel' ? 'active' : ''}`}>
                      {t('personnel.title')}
                    </Link>
                  </li>
                )}
                {hasPermission('P3') && (
                  <li>
                    <Link to="/users" className={`menu-link ${location.pathname === '/users' ? 'active' : ''}`}>
                      {t('users.title')}
                    </Link>
                  </li>
                )}
                {hasPermission('P1', 'VIEW') && (
                  <li>
                    <Link to="/id-applications" className={`menu-link ${location.pathname === '/id-applications' ? 'active' : ''}`}>
                      {t('idApplications.title')}
                    </Link>
                  </li>
                )}
                {hasPermission('P2', 'VIEW') && (
                  <li>
                    <Link to="/seller-applications" className={`menu-link ${location.pathname === '/seller-applications' ? 'active' : ''}`}>
                      {t('sellerApplications.title')}
                    </Link>
                  </li>
                )}
                {hasPermission('P8') && (
                  <li>
                    <Link to="/user-logs" className={`menu-link ${location.pathname === '/user-logs' ? 'active' : ''}`}>
                      {t('userLogs.title')}
                    </Link>
                  </li>
                )}
                {hasPermission('P9') && (
                  <li>
                    <Link to="/personnel-logs" className={`menu-link ${location.pathname === '/personnel-logs' ? 'active' : ''}`}>
                      {t('personnelLogs.title')}
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
