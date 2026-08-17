import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { getBasket, checkout, getSavedCard } from '../api/orderApi'
import '../styles/checkout.css'

export function CheckoutPage() {
  const navigate = useNavigate()
  const { t } = useTranslation()
  const { isAuthenticated, user } = useAuth()

  const [basket, setBasket] = useState(null)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)
  const [saveCard, setSaveCard] = useState(false)

  const [formData, setFormData] = useState({
    cardNumber: '',
    expiryMonth: '',
    expiryYear: '',
    cvv: '',
    cardHolderName: '',
    couponCode: '',
  })

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    if (!user?.idVerified) {
      navigate('/id-verification')
      return
    }

    const fetchData = async () => {
      try {
        setLoading(true)
        setError(null)

        // Fetch basket
        const basketData = await getBasket()
        setBasket(basketData)

        // Try to load saved card
        const savedCard = await getSavedCard()
        if (savedCard) {
          setFormData((prev) => ({
            ...prev,
            cardNumber: `****${savedCard.cardNumberLast4 ? savedCard.cardNumberLast4.slice(-4) : ''}`,
            expiryMonth: savedCard.expiryMonth?.toString() || '',
            expiryYear: savedCard.expiryYear?.toString() || '',
            cardHolderName: savedCard.cardHolderName || '',
          }))
        }
      } catch (err) {
        setError(t('common.error') || 'Failed to load checkout data')
        console.error('Failed to load checkout data:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [isAuthenticated, user, navigate, t])

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!formData.cardNumber || !formData.expiryMonth || !formData.expiryYear || !formData.cvv || !formData.cardHolderName) {
      setError(t('common.fillAllFields') || 'Please fill all required fields')
      return
    }

    try {
      setSubmitting(true)
      setError(null)

      const response = await checkout(
        formData.cardNumber,
        parseInt(formData.expiryMonth),
        parseInt(formData.expiryYear),
        formData.cvv,
        formData.cardHolderName,
        saveCard,
        formData.couponCode || null,
      )

      navigate(`/order-details/${response.id}`)
    } catch (err) {
      console.error('Checkout error:', err)
      setError(err.message || t('checkout.error'))
    } finally {
      setSubmitting(false)
    }
  }

  const handleBackToBasket = () => {
    navigate('/basket')
  }

  if (loading) {
    return (
      <section id="center" className="checkout-container">
        <div className="loading-spinner">{t('common.loading')}</div>
      </section>
    )
  }

  const isEmpty = !basket || !basket.items || basket.items.length === 0
  if (isEmpty) {
    return (
      <section id="center" className="checkout-container">
        <div className="error-message">
          <p>{t('basket.empty')}</p>
          <button onClick={handleBackToBasket}>{t('basket.continueShopping')}</button>
        </div>
      </section>
    )
  }

  const total = basket.items.reduce((sum, item) => sum + item.quantity * item.price, 0)

  return (
    <section id="center" className="checkout-container">
      <h1>{t('checkout.title')}</h1>

      {error && <div className="error-message">{error}</div>}

      <div className="checkout-content">
        <form onSubmit={handleSubmit} className="payment-form">
          <fieldset>
            <legend>{t('checkout.paymentInfo')}</legend>

            <div className="form-group">
              <label htmlFor="cardNumber">{t('checkout.cardNumber')}</label>
              <input
                id="cardNumber"
                type="text"
                name="cardNumber"
                value={formData.cardNumber}
                onChange={handleInputChange}
                placeholder="1234 5678 9012 3456"
                maxLength="19"
                required
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="expiryMonth">{t('checkout.expiryMonth')}</label>
                <select
                  id="expiryMonth"
                  name="expiryMonth"
                  value={formData.expiryMonth}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">Select Month</option>
                  {[...Array(12)].map((_, i) => (
                    <option key={i + 1} value={i + 1}>
                      {String(i + 1).padStart(2, '0')}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="expiryYear">{t('checkout.expiryYear')}</label>
                <select
                  id="expiryYear"
                  name="expiryYear"
                  value={formData.expiryYear}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">Select Year</option>
                  {[...Array(20)].map((_, i) => {
                    const year = new Date().getFullYear() + i
                    return (
                      <option key={year} value={year}>
                        {year}
                      </option>
                    )
                  })}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="cvv">{t('checkout.cvv')}</label>
                <input
                  id="cvv"
                  type="text"
                  name="cvv"
                  value={formData.cvv}
                  onChange={handleInputChange}
                  placeholder="123"
                  maxLength="4"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="cardHolderName">{t('checkout.cardHolderName')}</label>
              <input
                id="cardHolderName"
                type="text"
                name="cardHolderName"
                value={formData.cardHolderName}
                onChange={handleInputChange}
                placeholder="John Doe"
                required
              />
            </div>

            <div className="form-group checkbox">
              <label>
                <input
                  type="checkbox"
                  checked={saveCard}
                  onChange={(e) => setSaveCard(e.target.checked)}
                />
                <span>{t('checkout.saveCard')}</span>
              </label>
            </div>
          </fieldset>

          <fieldset>
            <legend>{t('checkout.orderSummary')}</legend>

            <div className="summary-section">
              {basket.items.length > 0 && (
                <div className="items-list">
                  {basket.items.map((item) => (
                    <div key={item.id} className="summary-item">
                      <span>{`Product #${item.productId}`}</span>
                      <span>{`${item.quantity}x`}</span>
                      <span>₺{(item.quantity * item.price).toFixed(2)}</span>
                    </div>
                  ))}
                </div>
              )}

              <div className="coupon-input">
                <label htmlFor="couponCode">{t('checkout.coupon')}</label>
                <input
                  id="couponCode"
                  type="text"
                  name="couponCode"
                  value={formData.couponCode}
                  onChange={handleInputChange}
                  placeholder="DISCOUNT20"
                />
              </div>

              <div className="summary-row">
                <span>{t('checkout.subtotal')}</span>
                <span>₺{total.toFixed(2)}</span>
              </div>

              <div className="summary-total">
                <span>{t('checkout.total')}</span>
                <span>₺{total.toFixed(2)}</span>
              </div>

              <button
                type="submit"
                className="btn-place-order"
                disabled={submitting}
              >
                {submitting ? t('checkout.processing') : t('checkout.placeOrder')}
              </button>

              <button
                type="button"
                className="btn-back-to-basket"
                onClick={handleBackToBasket}
                disabled={submitting}
              >
                {t('checkout.backToBasket')}
              </button>
            </div>
          </fieldset>
        </form>
      </div>
    </section>
  )
}
