import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { getBasket, updateBasketItemQuantity, removeFromBasket } from '../api/orderApi'
import '../styles/basket.css'

export function BasketPage() {
  const navigate = useNavigate()
  const { t } = useTranslation()
  const { isAuthenticated, user } = useAuth()

  const [basket, setBasket] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [updating, setUpdating] = useState(false)

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    const fetchBasket = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await getBasket()
        setBasket(data)
      } catch (err) {
        setError(t('common.error') || 'Failed to load basket')
        console.error('Failed to load basket:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchBasket()
  }, [isAuthenticated, navigate, t])

  const handleUpdateQuantity = async (itemId, newQuantity) => {
    if (newQuantity < 1) return

    try {
      setUpdating(true)
      const updated = await updateBasketItemQuantity(itemId, newQuantity)
      setBasket(updated)
    } catch (err) {
      console.error('Error updating quantity:', err)
      setError('Failed to update quantity')
    } finally {
      setUpdating(false)
    }
  }

  const handleRemoveItem = async (itemId) => {
    if (!confirm(t('basket.removeConfirm'))) return

    try {
      setUpdating(true)
      const updated = await removeFromBasket(itemId)
      setBasket(updated)
    } catch (err) {
      console.error('Error removing item:', err)
      setError('Failed to remove item')
    } finally {
      setUpdating(false)
    }
  }

  const handleCheckout = () => {
    if (!user?.idVerified) {
      navigate('/id-verification')
      return
    }
    navigate('/checkout')
  }

  const handleContinueShopping = () => {
    navigate('/search')
  }

  if (loading) {
    return (
      <section id="center" className="basket-container">
        <div className="loading-spinner">{t('common.loading')}</div>
      </section>
    )
  }

  const isEmpty = !basket || !basket.items || basket.items.length === 0

  if (isEmpty) {
    return (
      <section id="center" className="basket-container">
        <div className="basket-empty">
          <h1>{t('basket.title')}</h1>
          <div className="empty-state">
            <p>{t('basket.empty')}</p>
            <p>{t('basket.addItems')}</p>
            <button className="btn-continue-shopping" onClick={handleContinueShopping}>
              {t('basket.continueShopping')}
            </button>
          </div>
        </div>
      </section>
    )
  }

  const total = basket.items.reduce((sum, item) => sum + item.quantity * item.price, 0)

  return (
    <section id="center" className="basket-container">
      <h1>{t('basket.title')}</h1>

      {error && <div className="error-message">{error}</div>}

      <div className="basket-content">
        <div className="basket-items">
          <table className="items-table">
            <thead>
              <tr>
                <th>{t('productDetail.price')}</th>
                <th>{t('basket.quantity')}</th>
                <th>{t('basket.price')}</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {basket.items.map((item) => (
                <tr key={item.id} className="basket-item">
                  <td className="item-product-id">
                    <a href={`/products/${item.productId}`}>{`Product #${item.productId}`}</a>
                  </td>
                  <td className="item-quantity">
                    <div className="quantity-controls">
                      <button
                        onClick={() => handleUpdateQuantity(item.id, item.quantity - 1)}
                        disabled={updating || item.quantity <= 1}
                      >
                        −
                      </button>
                      <span>{item.quantity}</span>
                      <button
                        onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                        disabled={updating}
                      >
                        +
                      </button>
                    </div>
                  </td>
                  <td className="item-price">₺{item.price?.toFixed(2) || '0.00'}</td>
                  <td className="item-actions">
                    <button
                      className="btn-remove"
                      onClick={() => handleRemoveItem(item.id)}
                      disabled={updating}
                    >
                      {t('basket.remove')}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="basket-summary">
          <div className="summary-section">
            <h2>{t('checkout.orderSummary')}</h2>
            <div className="summary-row">
              <span>{t('basket.subtotal')}</span>
              <span>₺{total.toFixed(2)}</span>
            </div>
            <div className="summary-total">
              <span>{t('basket.total')}</span>
              <span>₺{total.toFixed(2)}</span>
            </div>
            <button
              className="btn-checkout"
              onClick={handleCheckout}
              disabled={updating || isEmpty}
            >
              {t('basket.checkout')}
            </button>
            <button
              className="btn-continue-shopping"
              onClick={handleContinueShopping}
            >
              {t('basket.continueShopping')}
            </button>
          </div>
        </div>
      </div>
    </section>
  )
}
