import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { getProductDetail } from '../api/productDetailApi'
import { addToBasket } from '../api/orderApi'
import { followSeller, unfollowSeller, isFollowingSeller } from '../api/followApi'
import { submitReview, getProductReviews } from '../api/reviewApi'
import { askQuestion, getProductQuestions } from '../api/qnaApi'
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

  // Review form state
  const [reviews, setReviews] = useState([])
  const [reviewForm, setReviewForm] = useState({ rating: 5, comment: '' })
  const [submittingReview, setSubmittingReview] = useState(false)
  const [reviewError, setReviewError] = useState(null)
  const [reviewSuccess, setReviewSuccess] = useState(false)

  // QNA state
  const [questions, setQuestions] = useState([])
  const [questionForm, setQuestionForm] = useState({ questionText: '' })
  const [submittingQuestion, setSubmittingQuestion] = useState(false)
  const [questionError, setQuestionError] = useState(null)

  // Fetch product details, follow state, reviews, and questions
  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await getProductDetail(id, language)
        setProduct(data)

        // Fetch reviews
        try {
          const reviewsData = await getProductReviews(id)
          setReviews(reviewsData)
        } catch (err) {
          console.error('Failed to load reviews:', err)
        }

        // Fetch questions
        try {
          const questionsData = await getProductQuestions(id)
          setQuestions(questionsData)
        } catch (err) {
          console.error('Failed to load questions:', err)
        }

        // Fetch follow state if authenticated
        if (isAuthenticated && data.sellerId) {
          try {
            const followResponse = await isFollowingSeller(data.sellerId)
            setFollowingState((prev) => ({
              ...prev,
              [data.sellerId]: followResponse.isFollowing,
            }))
          } catch (err) {
            console.error('Failed to load follow state:', err)
          }
        }
      } catch (err) {
        setError(t('productDetail.loadingError'))
        console.error('Failed to load product:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchProduct()
  }, [id, t, language, isAuthenticated])

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

  const handleFollowSeller = async () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    const isCurrentlyFollowing = followingState[product.sellerId] || false
    try {
      if (isCurrentlyFollowing) {
        await unfollowSeller(product.sellerId)
      } else {
        await followSeller(product.sellerId)
      }
      // Toggle follow state
      setFollowingState((prev) => ({
        ...prev,
        [product.sellerId]: !prev[product.sellerId],
      }))
    } catch (err) {
      console.error('Error toggling follow state:', err)
    }
  }

  const handleProductClick = (productId) => {
    navigate(`/products/${productId}`)
  }

  const handleGoToMarket = () => {
    if (product?.sellerId) {
      navigate(`/sellers/${product.sellerId}`)
    }
  }

  const handleSubmitReview = async () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    if (!user?.idVerified) {
      navigate('/id-verification')
      return
    }

    try {
      setSubmittingReview(true)
      setReviewError(null)
      setReviewSuccess(false)

      await submitReview(product.id, {
        rating: parseInt(reviewForm.rating),
        comment: reviewForm.comment || null,
      })

      setReviewSuccess(true)
      setReviewForm({ rating: 5, comment: '' })

      // Refresh reviews after successful submission
      setTimeout(async () => {
        try {
          const reviewsData = await getProductReviews(id)
          setReviews(reviewsData)
          setReviewSuccess(false)
        } catch (err) {
          console.error('Failed to refresh reviews:', err)
        }
      }, 1500)
    } catch (err) {
      const message = err.message || t('productDetail.reviewError')
      if (message.includes('already submitted') || message.includes('not purchased')) {
        setReviewError(t('productDetail.reviewAlreadySubmitted'))
      } else {
        setReviewError(message)
      }
      console.error('Failed to submit review:', err)
    } finally {
      setSubmittingReview(false)
    }
  }

  const handleSubmitQuestion = async () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    if (!questionForm.questionText.trim()) {
      setQuestionError(t('productDetail.questionRequired'))
      return
    }

    try {
      setSubmittingQuestion(true)
      setQuestionError(null)

      await askQuestion(product.id, {
        questionText: questionForm.questionText,
      })

      setQuestionForm({ questionText: '' })

      // Refresh questions after successful submission
      try {
        const questionsData = await getProductQuestions(id)
        setQuestions(questionsData)
      } catch (err) {
        console.error('Failed to refresh questions:', err)
      }
    } catch (err) {
      setQuestionError(err.message || t('productDetail.questionError'))
      console.error('Failed to submit question:', err)
    } finally {
      setSubmittingQuestion(false)
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
        {/* Review Section */}
        <div className="product-section">
          <h2>{t('productDetail.reviewsSection') || 'Reviews'}</h2>
          {isAuthenticated && (
            <div className="review-form">
              <h3>{t('productDetail.submitReview') || 'Submit a Review'}</h3>
              {reviewError && <div className="error-banner">{reviewError}</div>}
              {reviewSuccess && <div className="success-banner">{t('productDetail.reviewSubmitted') || 'Review submitted successfully!'}</div>}
              <div className="form-group">
                <label>{t('productDetail.rating') || 'Rating'} (1-5)</label>
                <div className="rating-input">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      key={star}
                      className={`star ${star <= reviewForm.rating ? 'selected' : ''}`}
                      onClick={() => setReviewForm((prev) => ({ ...prev, rating: star }))}
                    >
                      ★
                    </button>
                  ))}
                </div>
              </div>
              <div className="form-group">
                <label>{t('productDetail.comment') || 'Comment (Optional)'}</label>
                <textarea
                  value={reviewForm.comment}
                  onChange={(e) => setReviewForm((prev) => ({ ...prev, comment: e.target.value }))}
                  placeholder={t('productDetail.commentPlaceholder') || 'Share your experience...'}
                  rows="3"
                />
              </div>
              <button
                className="btn-submit-review"
                onClick={handleSubmitReview}
                disabled={submittingReview}
              >
                {submittingReview ? t('common.loading') : t('productDetail.submitReview') || 'Submit Review'}
              </button>
            </div>
          )}
          {reviews.length > 0 ? (
            <div className="reviews-list">
              <h3>{t('productDetail.allReviews') || 'All Reviews'}</h3>
              {reviews.map((review) => (
                <div key={review.id} className="review-item">
                  <div className="review-header">
                    <div className="review-rating">
                      {[...Array(5)].map((_, i) => (
                        <span key={i}>{i < review.rating ? '★' : '☆'}</span>
                      ))}
                    </div>
                    <div className="review-date">
                      {new Date(review.createdAt).toLocaleDateString()}
                    </div>
                  </div>
                  {review.comment && <p className="review-comment">{review.comment}</p>}
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-state">
              <p>{t('productDetail.noReviews') || 'No reviews yet'}</p>
            </div>
          )}
        </div>

        {/* Q&A Section */}
        <div className="product-section">
          <h2>{t('productDetail.qnaSection')}</h2>
          {isAuthenticated && (
            <div className="question-form">
              <h3>{t('productDetail.askQuestion') || 'Ask a Question'}</h3>
              {questionError && <div className="error-banner">{questionError}</div>}
              <div className="form-group">
                <textarea
                  value={questionForm.questionText}
                  onChange={(e) => setQuestionForm((prev) => ({ ...prev, questionText: e.target.value }))}
                  placeholder={t('productDetail.questionPlaceholder') || 'Ask your question...'}
                  rows="3"
                />
              </div>
              <button
                className="btn-submit-question"
                onClick={handleSubmitQuestion}
                disabled={submittingQuestion}
              >
                {submittingQuestion ? t('common.loading') : t('productDetail.askQuestion') || 'Ask Question'}
              </button>
            </div>
          )}
          {questions.length > 0 ? (
            <div className="questions-list">
              <h3>{t('productDetail.allQuestions') || 'Questions & Answers'}</h3>
              {questions.map((question) => (
                <div key={question.id} className="question-item">
                  <div className="question-header">
                    <div className="question-text">{question.questionText}</div>
                    <div className="question-date">
                      {new Date(question.createdAt).toLocaleDateString()}
                    </div>
                  </div>
                  {question.answer ? (
                    <div className="answer-content">
                      <strong>{t('productDetail.answer') || 'Answer'}:</strong>
                      <p>{question.answer.answerText}</p>
                    </div>
                  ) : (
                    <div className="answer-pending">
                      {t('productDetail.answerPending') || 'Awaiting seller response...'}
                    </div>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-state">
              <p>{t('productDetail.noQna')}</p>
            </div>
          )}
        </div>

        {/* Campaigns Section */}
        <div className="product-section">
          <h2>{t('productDetail.campaignsSection')}</h2>
          {product.campaigns && product.campaigns.length > 0 ? (
            <div className="campaigns-list">
              {product.campaigns.map((campaign) => (
                <div key={campaign.id} className="campaign-item">
                  <div className="campaign-name">
                    <a href="/campaigns" style={{ color: 'var(--accent)', textDecoration: 'none' }}>
                      {campaign.name}
                    </a>
                  </div>
                  {campaign.description && (
                    <p className="campaign-description">{campaign.description}</p>
                  )}
                  <div className="campaign-dates-small">
                    {new Date(campaign.startDate).toLocaleDateString()} - {new Date(campaign.endDate).toLocaleDateString()}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-state">
              <p>{t('productDetail.noCampaigns')}</p>
            </div>
          )}
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
