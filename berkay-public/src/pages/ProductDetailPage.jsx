import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { getProductDetail } from '../api/productDetailApi'
import { addToBasket } from '../api/orderApi'
import '../styles/productDetail.css'

export function ProductDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { t, language } = useTranslation()
  const { isAuthenticated, user } = useAuth()

  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [currentPhotoIndex, setCurrentPhotoIndex] = useState(0)
  const [expandedDescription, setExpandedDescription] = useState(false)
  const [followingState, setFollowingState] = useState({})
  const [addingToBasket, setAddingToBasket] = useState(false)

  // Fetch product details
  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await getProductDetail(id, language)
        setProduct(data)
      } catch (err) {
        setError(t('productDetail.loadingError'))
        console.error('Failed to load product:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchProduct()
  }, [id, t, language])

  const handlePrevPhoto = () => {
    if (product?.photos && product.photos.length > 0) {
      setCurrentPhotoIndex((prev) => (prev === 0 ? product.photos.length - 1 : prev - 1))
    }
  }

  const handleNextPhoto = () => {
    if (product?.photos && product.photos.length > 0) {
      setCurrentPhotoIndex((prev) => (prev === product.photos.length - 1 ? 0 : prev + 1))
    }
  }

  const handleThumbnailClick = (index) => {
    setCurrentPhotoIndex(index)
  }

  const handleBuyNow = async () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    if (!user?.idVerified) {
      navigate('/id-verification')
      return
    }

    // Add to basket with quantity 1, then proceed to checkout
    try {
      setAddingToBasket(true)
      await addToBasket(product.id, 1)
      navigate('/checkout')
    } catch (err) {
      console.error('Error adding to basket:', err)
    } finally {
      setAddingToBasket(false)
    }
  }

  const handleAddToBasket = async () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    if (!user?.idVerified) {
      navigate('/id-verification')
      return
    }

    try {
      setAddingToBasket(true)
      await addToBasket(product.id, 1)
      navigate('/basket')
    } catch (err) {
      console.error('Error adding to basket:', err)
    } finally {
      setAddingToBasket(false)
    }
  }

  const handleFollowSeller = () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }
    // Toggle follow state (placeholder - no real backend call)
    setFollowingState((prev) => ({
      ...prev,
      [product.sellerId]: !prev[product.sellerId],
    }))
  }

  const handleProductClick = (productId) => {
    navigate(`/products/${productId}`)
  }

  const handleGoToMarket = () => {
    if (product?.sellerId) {
      navigate(`/sellers/${product.sellerId}`)
    }
  }

  const getProductName = (product) => {
    if (!product) return ''
    // Backend already provides translated name based on Accept-Language header
    return product.name || ''
  }

  const getSimpleProductName = (product) => {
    if (!product) return ''
    // SimpleProductDTO has already-translated name from backend
    return product.name || ''
  }

  const getProductDescription = (product, descType) => {
    if (!product) return ''
    // Backend already provides translated descriptions based on Accept-Language header
    if (descType === 'short') {
      return product.shortDescription || ''
    }
    if (descType === 'long') {
      return product.longDescription || ''
    }
    return ''
  }

  if (loading) {
    return (
      <section id="center" className="product-detail-container">
        <div className="loading-spinner">{t('common.loading')}</div>
      </section>
    )
  }

  if (error || !product) {
    return (
      <section id="center" className="product-detail-container">
        <div className="error-message">
          <p>{error || t('productDetail.notFound')}</p>
        </div>
      </section>
    )
  }

  const currentPhoto = product.photos && product.photos.length > 0 ? product.photos[currentPhotoIndex] : null
  const hasPhotos = product.photos && product.photos.length > 0
  const shortDesc = getProductDescription(product, 'short')
  const longDesc = getProductDescription(product, 'long')
  const displayDesc = expandedDescription ? longDesc : shortDesc
  const hasMoreDesc = longDesc && longDesc.length > shortDesc.length
  const isFollowing = followingState[product.sellerId] || false

  return (
    <section id="center" className="product-detail-container">
      <div className="product-detail-header">
        <h1>{getProductName(product)}</h1>
      </div>

      <div className="product-detail-content">
        {/* Photo Slider */}
        <div className="photo-slider-container">
          <div className="photo-slider-main">
            {currentPhoto ? (
              <img src={`data:image/jpeg;base64,${currentPhoto.imageDataBase64}`} alt={getProductName(product)} />
            ) : (
              <div className="photo-slider-placeholder">📦</div>
            )}
            {hasPhotos && (
              <div className="slider-nav">
                <button className="slider-button" onClick={handlePrevPhoto} aria-label="Previous photo">
                  ❮
                </button>
                <button className="slider-button" onClick={handleNextPhoto} aria-label="Next photo">
                  ❯
                </button>
              </div>
            )}
          </div>

          {hasPhotos && (
            <div className="slider-thumbnails">
              {product.photos.map((photo, index) => (
                <button
                  key={index}
                  className={`thumbnail ${index === currentPhotoIndex ? 'active' : ''}`}
                  onClick={() => handleThumbnailClick(index)}
                  aria-label={`Photo ${index + 1}`}
                >
                  <img src={`data:image/jpeg;base64,${photo.imageDataBase64}`} alt={`${getProductName(product)} ${index + 1}`} />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Right Column - Info */}
        <div className="product-info-column">
          {/* Header Section */}
          <div className="product-header-section">
            <h2>{getProductName(product)}</h2>

            <div className="price-stock-container">
              <div className="price-display">₺{product.price.toFixed(2)}</div>
              <div className={`stock-display ${product.stock > 0 ? 'in-stock' : 'out-of-stock'}`}>
                {product.stock > 0 ? (
                  <>
                    <span>✓</span>
                    <span>
                      {t('productDetail.stockLabel')}: {product.stock}
                    </span>
                  </>
                ) : (
                  <>
                    <span>✗</span>
                    <span>{t('productDetail.outOfStock')}</span>
                  </>
                )}
              </div>
            </div>

            {/* Rating Summary */}
            {product.ratingCount > 0 ? (
              <div className="rating-summary">
                <div className="rating-stars">
                  {[...Array(5)].map((_, i) => (
                    <span key={i}>{i < Math.round(product.averageRating) ? '★' : '☆'}</span>
                  ))}
                </div>
                <div className="rating-text">
                  {product.averageRating.toFixed(1)} ({t('productDetail.ratingCount').replace('{count}', product.ratingCount)})
                </div>
              </div>
            ) : (
              <div className="rating-summary">
                <div className="rating-text">{t('productDetail.noRatings')}</div>
              </div>
            )}
          </div>

          {/* Key Features */}
          {product.keyFeatures && product.keyFeatures.length > 0 && (
            <div className="key-features-section">
              <h3>{t('productDetail.keyFeatures')}</h3>
              <div className="features-grid">
                {product.keyFeatures.map((feature, index) => (
                  <div key={index} className="feature-box">
                    <span className="feature-icon">✓</span>
                    <span className="feature-text">{feature}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Description */}
          {displayDesc && (
            <div className="description-section">
              <h3>{t('productDetail.description')}</h3>
              <p className={`description-text ${expandedDescription ? 'expanded' : ''}`}>{displayDesc}</p>
              {hasMoreDesc && (
                <button
                  className="toggle-description-btn"
                  onClick={() => setExpandedDescription(!expandedDescription)}
                >
                  {expandedDescription ? t('productDetail.showLess') : t('productDetail.showMore')}
                </button>
              )}
            </div>
          )}

          {/* Delivery Info */}
          <div className="delivery-section">
            <span className="delivery-icon">🚚</span>
            <div>
              <div className="delivery-text">{t('productDetail.estimatedDelivery')}</div>
              <div className="delivery-date">{product.estimatedDeliveryDays}</div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="action-buttons">
            <button className="btn-buy-now" onClick={handleBuyNow} disabled={addingToBasket || product.stock === 0}>
              {addingToBasket ? t('common.loading') : t('productDetail.buyNow')}
            </button>
            <button className="btn-add-basket" onClick={handleAddToBasket} disabled={addingToBasket || product.stock === 0}>
              {addingToBasket ? t('common.loading') : t('productDetail.addToBasket')}
            </button>
            <button className={`btn-follow-seller ${isFollowing ? 'following' : ''}`} onClick={handleFollowSeller}>
              {isFollowing ? t('productDetail.followingSeller') : t('productDetail.followSeller')}
            </button>
            <button className="btn-go-to-market" onClick={handleGoToMarket}>
              🏪 {t('productDetail.goToMarket') || 'Go to Market'}
            </button>
          </div>
        </div>
      </div>

      {/* Product Sections Below */}
      <div className="product-sections">
        {/* Q&A Section */}
        <div className="product-section">
          <h2>{t('productDetail.qnaSection')}</h2>
          <div className="empty-state">
            <p>{t('productDetail.noQna')}</p>
          </div>
        </div>

        {/* Campaigns Section */}
        <div className="product-section">
          <h2>{t('productDetail.campaignsSection')}</h2>
          <div className="empty-state">
            <p>{t('productDetail.noCampaigns')}</p>
          </div>
        </div>

        {/* Similar Products */}
        {product.similarProducts && product.similarProducts.length > 0 && (
          <div className="product-section">
            <h2>{t('productDetail.similarProducts')}</h2>
            <div className="horizontal-slider">
              {product.similarProducts.map((item) => (
                <div key={item.id} className="product-slide" onClick={() => handleProductClick(item.id)}>
                  <div className="slide-image">
                    {item.photoUrl ? (
                      <img src={item.photoUrl} alt={getSimpleProductName(item)} />
                    ) : (
                      '📦'
                    )}
                  </div>
                  <div className="slide-info">
                    <div className="slide-name">{getSimpleProductName(item)}</div>
                    <div className="slide-price">₺{item.price.toFixed(2)}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Recommended Products */}
        {product.recommendedProducts && product.recommendedProducts.length > 0 && (
          <div className="product-section">
            <h2>{t('productDetail.recommendedProducts')}</h2>
            <div className="horizontal-slider">
              {product.recommendedProducts.map((item) => (
                <div key={item.id} className="product-slide" onClick={() => handleProductClick(item.id)}>
                  <div className="slide-image">
                    {item.photoUrl ? (
                      <img src={item.photoUrl} alt={getSimpleProductName(item)} />
                    ) : (
                      '📦'
                    )}
                  </div>
                  <div className="slide-info">
                    <div className="slide-name">{getSimpleProductName(item)}</div>
                    <div className="slide-price">₺{item.price.toFixed(2)}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Bought Together */}
        {product.boughtTogetherProducts && product.boughtTogetherProducts.length > 0 && (
          <div className="product-section">
            <h2>{t('productDetail.boughtTogether')}</h2>
            <div className="horizontal-slider">
              {product.boughtTogetherProducts.map((item) => (
                <div key={item.id} className="product-slide" onClick={() => handleProductClick(item.id)}>
                  <div className="slide-image">
                    {item.photoUrl ? (
                      <img src={item.photoUrl} alt={getSimpleProductName(item)} />
                    ) : (
                      '📦'
                    )}
                  </div>
                  <div className="slide-info">
                    <div className="slide-name">{getSimpleProductName(item)}</div>
                    <div className="slide-price">₺{item.price.toFixed(2)}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Might Interest You */}
        {product.mightAlsoInterestYou && product.mightAlsoInterestYou.length > 0 && (
          <div className="product-section">
            <h2>{t('productDetail.mightInterest')}</h2>
            <div className="text-list">
              {product.mightAlsoInterestYou.map((item) => (
                <a
                  key={item.id}
                  href="#"
                  className="text-list-item"
                  onClick={(e) => {
                    e.preventDefault()
                    handleProductClick(item.id)
                  }}
                >
                  <span className="text-list-icon">→</span>
                  <span>{item.name}</span>
                </a>
              ))}
            </div>
          </div>
        )}

        {/* Popular Brands/Stores */}
        {product.popularBrandsOrStores && product.popularBrandsOrStores.length > 0 && (
          <div className="product-section">
            <h2>{t('productDetail.popularBrands')}</h2>
            <div className="text-list">
              {product.popularBrandsOrStores.map((brand, index) => (
                <a
                  key={index}
                  href="#"
                  className="text-list-item"
                  onClick={(e) => {
                    e.preventDefault()
                    // Navigate to seller profile or products - placeholder for now
                  }}
                >
                  <span className="text-list-icon">🏪</span>
                  <span>{brand.storeName || brand.id}</span>
                </a>
              ))}
            </div>
          </div>
        )}

        {/* Popular Pages */}
        {product.popularPages && product.popularPages.length > 0 && (
          <div className="product-section">
            <h2>{t('productDetail.popularPages')}</h2>
            <div className="text-list">
              {product.popularPages.map((page) => (
                <a
                  key={page.id}
                  href="#"
                  className="text-list-item"
                  onClick={(e) => {
                    e.preventDefault()
                    handleProductClick(page.id)
                  }}
                >
                  <span className="text-list-icon">⭐</span>
                  <span>{page.name}</span>
                </a>
              ))}
            </div>
          </div>
        )}
      </div>
    </section>
  )
}
