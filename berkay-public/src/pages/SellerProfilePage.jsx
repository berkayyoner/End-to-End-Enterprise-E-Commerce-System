import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { getPublicSellerProfile, getSellerProducts } from '../api/sellerApi'
import '../styles/sellerProfile.css'

export function SellerProfilePage() {
  const { id } = useParams()
  const { t, language } = useTranslation()
  const { isAuthenticated } = useAuth()
  const [profile, setProfile] = useState(null)
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [followState, setFollowState] = useState({})

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        const profileData = await getPublicSellerProfile(id)
        setProfile(profileData)

        const productsData = await getSellerProducts(id, language)
        setProducts(productsData)
      } catch (err) {
        setError(t('sellerProfile.loadError') || 'Failed to load seller profile')
        console.error('Failed to load seller profile:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [id, language, t])

  const handleFollowSeller = () => {
    if (!isAuthenticated) {
      // In a real app, would redirect to login
      return
    }
    // Toggle follow state (placeholder - no real backend call per task 4.2)
    setFollowState((prev) => ({
      ...prev,
      [id]: !prev[id],
    }))
  }

  const getProductName = (product) => {
    if (!product) return ''
    return product.name || ''
  }

  if (loading) {
    return (
      <section className="seller-profile-container">
        <div className="loading-spinner">{t('common.loading')}</div>
      </section>
    )
  }

  if (error || !profile) {
    return (
      <section className="seller-profile-container">
        <div className="error-message">
          <p>{error || t('sellerProfile.notFound')}</p>
        </div>
      </section>
    )
  }

  const isFollowing = followState[id] || false

  return (
    <section className="seller-profile-container">
      {/* Seller Header */}
      <div className="seller-profile-header">
        <div className="seller-info">
          <div className="seller-avatar">
            {profile.storeName ? profile.storeName.charAt(0).toUpperCase() : 'S'}
          </div>
          <div className="seller-details">
            <h1>{profile.storeName || `${t('sellerProfile.storeName')}`}</h1>
            <div className="seller-stats">
              <span className="stat">
                <span className="stat-value">{profile.followerCount || 0}</span>
                <span className="stat-label">{t('sellerProfile.followers')}</span>
              </span>
            </div>
          </div>
        </div>

        <div className="seller-actions">
          <button
            className={`btn-follow ${isFollowing ? 'following' : ''}`}
            onClick={handleFollowSeller}
          >
            {isFollowing ? t('sellerProfile.followingSeller') : t('sellerProfile.followSeller')}
          </button>
        </div>
      </div>

      {/* Products Section */}
      <div className="products-section">
        <h2>{t('sellerProfile.products')}</h2>

        {products.length === 0 ? (
          <div className="empty-state">
            <p>{t('sellerProfile.noProducts')}</p>
          </div>
        ) : (
          <div className="products-grid">
            {products.map((product) => (
              <div key={product.id} className="product-card">
                <div className="product-image">
                  {product.photoUrl ? (
                    <img src={product.photoUrl} alt={getProductName(product)} />
                  ) : (
                    <div className="image-placeholder">📦</div>
                  )}
                </div>
                <div className="product-info">
                  <h3>{getProductName(product)}</h3>
                  <div className="product-price">₺{product.price.toFixed(2)}</div>
                  <div className="product-stock">
                    {product.stock > 0 ? (
                      <span className="in-stock">✓ {t('productDetail.stockLabel')}</span>
                    ) : (
                      <span className="out-of-stock">✗ {t('productDetail.outOfStock')}</span>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Q&A Section */}
      <div className="qna-section">
        <h2>{t('sellerProfile.qnaSection')}</h2>
        <div className="empty-state">
          <p>{t('sellerProfile.noQna')}</p>
        </div>
      </div>
    </section>
  )
}
