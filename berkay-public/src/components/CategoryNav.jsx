import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import {
  getMainCategories,
  getSubTypesByMainCategory,
  getInnerTypesBySubType,
} from '../api/categoryApi.js'
import './CategoryNav.css'

export function CategoryNav() {
  const { t, language } = useTranslation()
  const navigate = useNavigate()

  const [mainCategories, setMainCategories] = useState([])
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [hoveredMain, setHoveredMain] = useState(null)
  const [subTypes, setSubTypes] = useState({})
  const [hoveredSub, setHoveredSub] = useState(null)
  const [innerTypes, setInnerTypes] = useState({})
  const [loading, setLoading] = useState(true)

  const menuRef = useRef(null)

  // Fetch main categories on component mount
  useEffect(() => {
    ;(async () => {
      try {
        const data = await getMainCategories()
        setMainCategories(Array.isArray(data) ? data : data.data || [])
      } catch (error) {
        console.error('Failed to fetch main categories:', error)
      } finally {
        setLoading(false)
      }
    })()
  }, [])

  // Fetch sub types when hovering a main category
  useEffect(() => {
    if (!hoveredMain) {
      setHoveredSub(null)
      setInnerTypes({})
      return
    }

    ;(async () => {
      try {
        const data = await getSubTypesByMainCategory(hoveredMain.id)
        setSubTypes((prev) => ({
          ...prev,
          [hoveredMain.id]: Array.isArray(data) ? data : data.data || [],
        }))
      } catch (error) {
        console.error('Failed to fetch sub types:', error)
      }
    })()
  }, [hoveredMain])

  // Fetch inner types when hovering a sub type
  useEffect(() => {
    if (!hoveredSub) {
      setInnerTypes({})
      return
    }

    ;(async () => {
      try {
        const data = await getInnerTypesBySubType(hoveredSub.id)
        setInnerTypes((prev) => ({
          ...prev,
          [hoveredSub.id]: Array.isArray(data) ? data : data.data || [],
        }))
      } catch (error) {
        console.error('Failed to fetch inner types:', error)
      }
    })()
  }, [hoveredSub])

  const handleMenuMouseEnter = () => {
    setIsMenuOpen(true)
    // RULES.md: "The first main category should open automatically when the menu hovers."
    setHoveredMain((current) => current ?? mainCategories[0] ?? null)
  }

  const handleMenuMouseLeave = () => {
    setIsMenuOpen(false)
    setHoveredMain(null)
    setHoveredSub(null)
  }

  const handleMainCategoryClick = (categoryId) => {
    navigate(`/search?mainCategoryId=${categoryId}`)
    setIsMenuOpen(false)
  }

  const handleSubTypeClick = (subTypeId) => {
    navigate(`/search?subTypeId=${subTypeId}`)
    setIsMenuOpen(false)
  }

  const handleInnerTypeClick = (innerTypeId) => {
    navigate(`/search?innerTypeId=${innerTypeId}`)
    setIsMenuOpen(false)
  }

  const handleShowMoreClick = (subTypeId) => {
    navigate(`/search?subTypeId=${subTypeId}`)
    setIsMenuOpen(false)
  }

  const getCategoryName = (category) => {
    if (!category || !category.translations) return ''
    const translation = category.translations.find((t) => t.language === language)
    return translation?.name || category.translations[0]?.name || ''
  }

  if (loading) {
    return (
      <div className="category-nav">
        <div className="categories-button" disabled>
          ☰ {t('nav.categories')}
        </div>
      </div>
    )
  }

  return (
    <div className="category-nav" ref={menuRef}>
      <div
        className="categories-button"
        onMouseEnter={handleMenuMouseEnter}
        onMouseLeave={handleMenuMouseLeave}
      >
        <span className="hamburger-icon">☰</span>
        <span>{t('nav.categories')}</span>
      </div>

      <div className="main-categories-shortcuts">
        {mainCategories.slice(0, 6).map((category) => (
          <button
            key={category.id}
            className="shortcut-button"
            onClick={() => handleMainCategoryClick(category.id)}
            title={getCategoryName(category)}
          >
            {getCategoryName(category)}
          </button>
        ))}
      </div>

      {isMenuOpen && (
        <div
          className="mega-menu"
          onMouseEnter={handleMenuMouseEnter}
          onMouseLeave={handleMenuMouseLeave}
        >
          <div className="mega-menu-column mega-menu-main">
            {mainCategories.map((category) => (
              <div
                key={category.id}
                className={`main-category-item ${hoveredMain?.id === category.id ? 'hovered' : ''}`}
                onMouseEnter={() => setHoveredMain(category)}
                onClick={() => handleMainCategoryClick(category.id)}
              >
                {getCategoryName(category)}
              </div>
            ))}
          </div>

          {hoveredMain && (
            <div className="mega-menu-column mega-menu-sub">
              {(subTypes[hoveredMain.id] || []).map((subType) => (
                <div
                  key={subType.id}
                  className={`sub-type-item ${hoveredSub?.id === subType.id ? 'hovered' : ''}`}
                  onMouseEnter={() => setHoveredSub(subType)}
                  onClick={() => handleSubTypeClick(subType.id)}
                >
                  {getCategoryName(subType)}
                </div>
              ))}
            </div>
          )}

          {hoveredSub && (
            <div className="mega-menu-column mega-menu-inner">
              <div className="inner-type-group">
                <div className="inner-type-title">{getCategoryName(hoveredSub)}</div>
                {(innerTypes[hoveredSub.id] || []).slice(0, 5).map((innerType) => (
                  <button
                    key={innerType.id}
                    className="inner-type-item"
                    onClick={() => handleInnerTypeClick(innerType.id)}
                  >
                    {getCategoryName(innerType)}
                  </button>
                ))}
                {(innerTypes[hoveredSub.id] || []).length > 5 && (
                  <button
                    className="show-more-button"
                    onClick={() => handleShowMoreClick(hoveredSub.id)}
                  >
                    {t('nav.showMore')}
                  </button>
                )}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
