import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { getMyFavorites, removeFromFavorites } from '../api/favoriteApi'
import '../styles/myFavorites.css'

export function MyFavoritesPage() {
  const navigate = useNavigate()
  const { t, language } = useTranslation()
  const { isAuthenticated } = useAuth()
  const [favorites, setFavorites] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    const fetchFavorites = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await getMyFavorites(language)
        setFavorites(data)
      } catch (err) {
        setError(t('myFavorites.loadingError') || 'Failed to load favorites')
        console.error('Failed to load favorites:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchFavorites()
  }, [isAuthenticated, language, navigate, t])

  const handleProductClick = (productId) => {
    navigate(`/products/${productId}`)
  }

  const handleRemoveFavorite = async (productId, event) => {
    event.stopPropagation()
    try {
      await removeFromFavorites(productId)
      setFavorites((prev) => prev.filter((p) => p.id !== productId))
    } catch (err) {
      console.error('Error removing favorite:', err)
    }
  }

  const getProductName = (product) => {
    return product?.name || ''
  }

  if (loading) {
    return (
      <section className="my-favorites-container">
        <div className="loading-spinner">{t('common.loading')}</div>
      </section>
    )
  }

  if (error) {
    return (
      <section className="my-favorites-container">
        <div className="error-message">
          <p>{error}</p>
        </div>
      </section>
    )
  }

  return (
    <section className="my-favorites-container">
      <div className="favorites-header">
        <h1>{t('myFavorites.title') || 'My Favorites'}</h1>
        <p className="favorites-count">
          {favorites.length} {t('myFavorites.itemsCount') || 'items'}
        </p>
      </div>

      {favorites.length === 0 ? (
        <div className="empty-state">
          <p>{t('myFavorites.empty') || 'You have no favorite products yet'}</p>
          <button
            className="btn-continue-shopping"
            onClick={() => navigate('/search')}
          >
            {t('myFavorites.continueShopping') || 'Continue Shopping'}
          </button>
        </div>
      ) : (
        <div className="favorites-grid">
          {favorites.map((product) => (
            <div
              key={product.id}
              className="product-card"
              onClick={() => handleProductClick(product.id)}
            >
              <div className="product-image">
                {product.photoUrl ? (
                  <img src={product.photoUrl} alt={getProductName(product)} />
                ) : (
                  <div className="image-placeholder">📦</div>
                )}
                <button
                  className="btn-remove-favorite"
                  onClick={(e) => handleRemoveFavorite(product.id, e)}
                  title={t('myFavorites.remove') || 'Remove from favorites'}
                >
                  ♥
                </button>
              </div>
              <div className="product-info">
                <h3>{getProductName(product)}</h3>
                <div className="product-price">
                  ₺{product.price.toFixed(2)}
                </div>
                <div className="product-rating">
                  {product.averageRating > 0
                    ? `${product.averageRating.toFixed(1)} ⭐`
                    : t('productDetail.noRatings')}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  )
}
