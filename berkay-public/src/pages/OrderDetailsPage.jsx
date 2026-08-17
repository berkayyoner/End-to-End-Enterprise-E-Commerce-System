import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { getOrder } from '../api/orderApi'
import '../styles/orderDetails.css'

export function OrderDetailsPage() {
  const { orderId } = useParams()
  const navigate = useNavigate()
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()

  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    const fetchOrder = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await getOrder(orderId)
        setOrder(data)
      } catch (err) {
        setError(t('common.error') || 'Failed to load order')
        console.error('Failed to load order:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchOrder()
  }, [orderId, isAuthenticated, navigate, t])

  const handleBackToOrders = () => {
    navigate('/my-orders')
  }

  if (loading) {
    return (
      <section id="center" className="order-details-container">
        <div className="loading-spinner">{t('common.loading')}</div>
      </section>
    )
  }

  if (error || !order) {
    return (
      <section id="center" className="order-details-container">
        <div className="error-message">
          <p>{error || t('common.error')}</p>
          <button onClick={handleBackToOrders}>{t('orderDetails.backToOrders')}</button>
        </div>
      </section>
    )
  }

  const formatDate = (isoString) => {
    return new Date(isoString).toLocaleDateString()
  }

  return (
    <section id="center" className="order-details-container">
      <h1>{t('orderDetails.title')}</h1>

      <div className="order-info">
        <div className="info-row">
          <span className="label">{t('orderDetails.orderId')}</span>
          <span className="value">#{order.id}</span>
        </div>

        <div className="info-row">
          <span className="label">{t('orderDetails.date')}</span>
          <span className="value">{formatDate(order.createdAt)}</span>
        </div>

        <div className="info-row">
          <span className="label">{t('orderDetails.status')}</span>
          <span className="value status-badge">{t(`myOrders.orderStatus.${order.status.toLowerCase()}`) || order.status}</span>
        </div>
      </div>

      <div className="order-items">
        <h2>{t('orderDetails.items')}</h2>
        <table className="items-table">
          <thead>
            <tr>
              <th>{t('productDetail.price')}</th>
              <th>{t('orderDetails.quantity')}</th>
              <th>{t('orderDetails.unitPrice')}</th>
              <th>{t('basket.total')}</th>
            </tr>
          </thead>
          <tbody>
            {order.items && order.items.map((item, index) => (
              <tr key={index}>
                <td>
                  <a href={`/products/${item.productId}`}>{`Product #${item.productId}`}</a>
                </td>
                <td>{item.quantity}</td>
                <td>₺{item.unitPrice?.toFixed(2) || '0.00'}</td>
                <td>₺{(item.quantity * item.unitPrice)?.toFixed(2) || '0.00'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="order-total">
        <div className="total-row">
          <span className="label">{t('checkout.total')}</span>
          <span className="value">₺{order.totalAmount?.toFixed(2) || '0.00'}</span>
        </div>
      </div>

      <div className="order-actions">
        <button onClick={handleBackToOrders} className="btn-back">
          {t('orderDetails.backToOrders')}
        </button>
      </div>
    </section>
  )
}
