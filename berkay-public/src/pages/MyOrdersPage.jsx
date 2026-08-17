import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { getMyOrders } from '../api/orderApi'
import '../styles/myOrders.css'

export function MyOrdersPage() {
  const navigate = useNavigate()
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()

  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    const fetchOrders = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await getMyOrders(0, 20)
        setOrders(data.content || [])
      } catch (err) {
        setError(t('common.error') || 'Failed to load orders')
        console.error('Failed to load orders:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchOrders()
  }, [isAuthenticated, navigate, t])

  const handleViewDetails = (orderId) => {
    navigate(`/order-details/${orderId}`)
  }

  const handleBackToAccount = () => {
    navigate('/profile')
  }

  const formatDate = (isoString) => {
    return new Date(isoString).toLocaleDateString()
  }

  if (loading) {
    return (
      <section id="center" className="my-orders-container">
        <div className="loading-spinner">{t('common.loading')}</div>
      </section>
    )
  }

  if (orders.length === 0) {
    return (
      <section id="center" className="my-orders-container">
        <h1>{t('myOrders.title')}</h1>
        <div className="empty-state">
          <p>{t('myOrders.empty')}</p>
          <button onClick={() => navigate('/search')} className="btn-start-shopping">
            {t('myOrders.startShopping')}
          </button>
        </div>
      </section>
    )
  }

  return (
    <section id="center" className="my-orders-container">
      <h1>{t('myOrders.title')}</h1>

      {error && <div className="error-message">{error}</div>}

      <div className="orders-list">
        <table className="orders-table">
          <thead>
            <tr>
              <th>{t('myOrders.orderNumber')}</th>
              <th>{t('myOrders.date')}</th>
              <th>{t('myOrders.total')}</th>
              <th>{t('myOrders.status')}</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id} className="order-row">
                <td className="order-number">#{order.id}</td>
                <td className="order-date">{formatDate(order.createdAt)}</td>
                <td className="order-total">₺{order.totalAmount?.toFixed(2) || '0.00'}</td>
                <td className="order-status">
                  <span className="status-badge">
                    {t(`myOrders.orderStatus.${order.status.toLowerCase()}`) || order.status}
                  </span>
                </td>
                <td className="order-action">
                  <button
                    onClick={() => handleViewDetails(order.id)}
                    className="btn-view-details"
                  >
                    {t('myOrders.viewDetails')}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="page-actions">
        <button onClick={handleBackToAccount} className="btn-back">
          {t('myAccount.title')}
        </button>
      </div>
    </section>
  )
}
