import { useEffect, useRef, useState } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { searchProducts } from '../api/searchApi'
import { getMainCategories, getSubTypesByMainCategory, getInnerTypesBySubType } from '../api/categoryApi'
import '../styles/search.css'

const SORT_OPTIONS = [
  'Suggested Ranking',
  'The Most Expensive',
  'The Cheapest',
  'Newest',
  'The Most Selling',
  'The Most Favorited',
  'The Most Rated',
]

export function SearchPage() {
  const { t, language } = useTranslation()
  const [searchParams, setSearchParams] = useSearchParams()
  const navigate = useNavigate()

  // State from URL params
  const initialMainCategoryId = searchParams.get('mainCategoryId')
  const initialSubTypeId = searchParams.get('subTypeId')
  const initialInnerTypeId = searchParams.get('innerTypeId')

  // Filter state
  const [mainCategoryId, setMainCategoryId] = useState(initialMainCategoryId ? parseInt(initialMainCategoryId) : null)
  const [subTypeId, setSubTypeId] = useState(initialSubTypeId ? parseInt(initialSubTypeId) : null)
  const [innerTypeId, setInnerTypeId] = useState(initialInnerTypeId ? parseInt(initialInnerTypeId) : null)
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')
  const [sort, setSort] = useState('Suggested Ranking')

  // Category hierarchy state
  const [mainCategories, setMainCategories] = useState([])
  const [subTypes, setSubTypes] = useState([])
  const [innerTypes, setInnerTypes] = useState([])
  const [loadingCategories, setLoadingCategories] = useState(true)

  // Search results state
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [currentPage, setCurrentPage] = useState(0)
  const [hasNextPage, setHasNextPage] = useState(false)

  // Infinite scroll state
  const observerTarget = useRef(null)

  // Load main categories on mount
  useEffect(() => {
    const loadMainCategories = async () => {
      try {
        setLoadingCategories(true)
        const response = await getMainCategories()
        setMainCategories(response || [])
      } catch (err) {
        console.error('Failed to load main categories:', err)
      } finally {
        setLoadingCategories(false)
      }
    }
    loadMainCategories()
  }, [])

  // Load sub types when main category changes
  useEffect(() => {
    if (mainCategoryId) {
      const loadSubTypes = async () => {
        try {
          const response = await getSubTypesByMainCategory(mainCategoryId)
          setSubTypes(response || [])
          setSubTypeId(null)
          setInnerTypeId(null)
          setInnerTypes([])
        } catch (err) {
          console.error('Failed to load sub types:', err)
        }
      }
      loadSubTypes()
    } else {
      setSubTypes([])
      setInnerTypes([])
      setSubTypeId(null)
      setInnerTypeId(null)
    }
  }, [mainCategoryId])

  // Load inner types when sub type changes
  useEffect(() => {
    if (subTypeId) {
      const loadInnerTypes = async () => {
        try {
          const response = await getInnerTypesBySubType(subTypeId)
          setInnerTypes(response || [])
          setInnerTypeId(null)
        } catch (err) {
          console.error('Failed to load inner types:', err)
        }
      }
      loadInnerTypes()
    } else {
      setInnerTypes([])
      setInnerTypeId(null)
    }
  }, [subTypeId])

  // Fetch products when filters, sort, or page changes
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true)
        setError(null)
        const result = await searchProducts({
          mainCategoryId,
          subTypeId,
          innerTypeId,
          minPrice: minPrice || null,
          maxPrice: maxPrice || null,
          sort,
          page: currentPage,
        })

        if (currentPage === 0) {
          setProducts(result.items || [])
        } else {
          setProducts((prev) => [...prev, ...(result.items || [])])
        }

        setHasNextPage(result.hasNextPage || false)
      } catch (err) {
        setError(err.message)
        console.error('Failed to search products:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchProducts()
  }, [mainCategoryId, subTypeId, innerTypeId, minPrice, maxPrice, sort, currentPage])

  // Infinite scroll observer
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasNextPage && !loading) {
          setCurrentPage((prev) => prev + 1)
        }
      },
      { threshold: 0.1 }
    )

    const target = observerTarget.current
    if (target) {
      observer.observe(target)
    }

    return () => {
      if (target) {
        observer.unobserve(target)
      }
    }
  }, [hasNextPage, loading])

  // Update URL params when filters change
  useEffect(() => {
    const params = new URLSearchParams()
    if (mainCategoryId) params.set('mainCategoryId', mainCategoryId)
    if (subTypeId) params.set('subTypeId', subTypeId)
    if (innerTypeId) params.set('innerTypeId', innerTypeId)
    setSearchParams(params, { replace: true })
  }, [mainCategoryId, subTypeId, innerTypeId, setSearchParams])

  const handleResetFilters = () => {
    setMainCategoryId(null)
    setSubTypeId(null)
    setInnerTypeId(null)
    setMinPrice('')
    setMaxPrice('')
    setSort('Suggested Ranking')
    setCurrentPage(0)
  }

  const handleApplyFilters = () => {
    setCurrentPage(0)
  }

  const getSortKey = (sortLabel) => {
    const keyMap = {
      'Suggested Ranking': 'sort.suggestedRanking',
      'The Most Expensive': 'sort.mostExpensive',
      'The Cheapest': 'sort.cheapest',
      'Newest': 'sort.newest',
      'The Most Selling': 'sort.mostSelling',
      'The Most Favorited': 'sort.mostFavorited',
      'The Most Rated': 'sort.mostRated',
    }
    return keyMap[sortLabel] || 'sort.suggestedRanking'
  }

  const getCategoryName = (category) => {
    if (!category) return ''
    if (language === 'tr' && category.nameTr) return category.nameTr
    if (language === 'en' && category.nameEn) return category.nameEn
    return category.nameTr || category.nameEn || ''
  }

  const getProductName = (product) => {
    if (language === 'tr' && product.nameTr) return product.nameTr
    if (language === 'en' && product.nameEn) return product.nameEn
    return product.nameTr || product.nameEn || ''
  }

  const triggerIndex = Math.ceil(products.length * 0.8)

  return (
    <section id="center" className="search-page-container">
      <div className="search-header">
        <h1>{t('search.title')}</h1>
      </div>

      <div className="search-content">
        {/* Left Filter Panel */}
        <aside className="search-filter-panel">
          <div className="filter-section">
            <h3>{t('search.filter.title')}</h3>

            {/* Categories Filter */}
            <div className="filter-group">
              <h4>{t('search.filter.categories')}</h4>

              {loadingCategories ? (
                <p className="loading-text">{t('common.loading')}</p>
              ) : (
                <>
                  {/* Main Categories */}
                  <div className="filter-category-level">
                    <label className="filter-label">
                      <strong>{t('search.filter.mainCategory')}</strong>
                    </label>
                    {mainCategories.map((category) => (
                      <label key={category.id} className="filter-checkbox-label">
                        <input
                          type="checkbox"
                          checked={mainCategoryId === category.id}
                          onChange={(e) => {
                            if (e.target.checked) {
                              setMainCategoryId(category.id)
                            } else {
                              setMainCategoryId(null)
                            }
                          }}
                        />
                        <span>{getCategoryName(category)}</span>
                      </label>
                    ))}
                  </div>

                  {/* Sub Types */}
                  {mainCategoryId && subTypes.length > 0 && (
                    <div className="filter-category-level">
                      <label className="filter-label">
                        <strong>{t('search.filter.subType')}</strong>
                      </label>
                      {subTypes.map((subType) => (
                        <label key={subType.id} className="filter-checkbox-label">
                          <input
                            type="checkbox"
                            checked={subTypeId === subType.id}
                            onChange={(e) => {
                              if (e.target.checked) {
                                setSubTypeId(subType.id)
                              } else {
                                setSubTypeId(null)
                              }
                            }}
                          />
                          <span>{getCategoryName(subType)}</span>
                        </label>
                      ))}
                    </div>
                  )}

                  {/* Inner Types */}
                  {subTypeId && innerTypes.length > 0 && (
                    <div className="filter-category-level">
                      <label className="filter-label">
                        <strong>{t('search.filter.innerType')}</strong>
                      </label>
                      {innerTypes.map((innerType) => (
                        <label key={innerType.id} className="filter-checkbox-label">
                          <input
                            type="checkbox"
                            checked={innerTypeId === innerType.id}
                            onChange={(e) => {
                              if (e.target.checked) {
                                setInnerTypeId(innerType.id)
                              } else {
                                setInnerTypeId(null)
                              }
                            }}
                          />
                          <span>{getCategoryName(innerType)}</span>
                        </label>
                      ))}
                    </div>
                  )}
                </>
              )}
            </div>

            {/* Price Range Filter */}
            <div className="filter-group">
              <h4>{t('search.filter.priceRange')}</h4>
              <div className="price-input-group">
                <label className="price-input-label">
                  <span>{t('search.filter.minPrice')}</span>
                  <input
                    type="number"
                    value={minPrice}
                    onChange={(e) => setMinPrice(e.target.value)}
                    placeholder="0"
                    min="0"
                    step="0.01"
                  />
                </label>
                <label className="price-input-label">
                  <span>{t('search.filter.maxPrice')}</span>
                  <input
                    type="number"
                    value={maxPrice}
                    onChange={(e) => setMaxPrice(e.target.value)}
                    placeholder="9999999"
                    min="0"
                    step="0.01"
                  />
                </label>
              </div>
              <button className="filter-apply-btn" onClick={handleApplyFilters}>
                {t('search.filter.apply')}
              </button>
            </div>

            {/* Reset Button */}
            <button className="filter-reset-btn" onClick={handleResetFilters}>
              Reset
            </button>
          </div>
        </aside>

        {/* Main Content */}
        <div className="search-main">
          {/* Sort Dropdown */}
          <div className="search-sort-bar">
            <label htmlFor="sort-select" className="sort-label">
              {t('search.sort.label')}:
            </label>
            <select
              id="sort-select"
              value={sort}
              onChange={(e) => {
                setSort(e.target.value)
                setCurrentPage(0)
              }}
              className="sort-select"
            >
              {SORT_OPTIONS.map((option) => (
                <option key={option} value={option}>
                  {t(getSortKey(option))}
                </option>
              ))}
            </select>
          </div>

          {/* Products Grid */}
          <div className="search-results-container">
            {error && (
              <div className="error-message">
                <p>{t('search.error')}</p>
              </div>
            )}

            {!error && products.length === 0 && !loading && (
              <div className="no-results">
                <p>{t('search.noResults')}</p>
                <p className="hint">{t('search.tryDifferentFilters')}</p>
              </div>
            )}

            {products.length > 0 && (
              <div className="products-grid">
                {products.map((product, index) => (
                  <div
                    key={`${product.id}-${index}`}
                    className="product-card"
                    ref={index === triggerIndex ? observerTarget : null}
                    role="link"
                    tabIndex={0}
                    onClick={() => navigate(`/products/${product.id}`)}
                    onKeyPress={(e) => {
                      if (e.key === 'Enter') navigate(`/products/${product.id}`)
                    }}
                    style={{ cursor: 'pointer' }}
                  >
                    <div className="product-image-placeholder">
                      <div className="placeholder-icon">📦</div>
                    </div>
                    <div className="product-info">
                      <h3 className="product-name">{getProductName(product)}</h3>
                      <div className="product-meta">
                        <span className="product-price">₺{product.price.toFixed(2)}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Infinite scroll trigger */}
            <div ref={observerTarget} className="scroll-trigger" style={{ height: '20px' }} />

            {loading && products.length > 0 && (
              <div className="loading-more">
                <p>{t('common.loading')}</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </section>
  )
}
