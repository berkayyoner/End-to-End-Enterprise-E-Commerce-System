import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import {
  listMainCategories,
  getMainCategory,
  createMainCategory,
  updateMainCategory,
  deleteMainCategory,
  getSubType,
  getSubTypesByMainCategory,
  createSubType,
  updateSubType,
  deleteSubType,
  getInnerType,
  getInnerTypesBySubType,
  createInnerType,
  updateInnerType,
  deleteInnerType,
} from '../api/categoryApi.js'
import { ApiError } from '../api/httpClient.js'
import '../styles/categories.css'

export function CategoriesPage() {
  const { t, language } = useTranslation()
  const { hasPermission } = useAuth()

  // Tab state
  const [activeTab, setActiveTab] = useState('main')

  // Main Categories
  const [mainCategories, setMainCategories] = useState([])
  const [mainLoading, setMainLoading] = useState(false)
  const [mainError, setMainError] = useState(null)
  const [mainFormOpen, setMainFormOpen] = useState(false)
  const [mainFormData, setMainFormData] = useState({ nameTr: '', nameEn: '', descTr: '', descEn: '' })
  const [mainEditingId, setMainEditingId] = useState(null)

  // Sub Types
  const [subTypes, setSubTypes] = useState([])
  const [subLoading, setSubLoading] = useState(false)
  const [subError, setSubError] = useState(null)
  const [selectedMainCategory, setSelectedMainCategory] = useState(null)
  const [subFormOpen, setSubFormOpen] = useState(false)
  const [subFormData, setSubFormData] = useState({ nameTr: '', nameEn: '', descTr: '', descEn: '' })
  const [subEditingId, setSubEditingId] = useState(null)

  // Inner Types
  const [innerTypes, setInnerTypes] = useState([])
  const [innerLoading, setInnerLoading] = useState(false)
  const [innerError, setInnerError] = useState(null)
  const [selectedSubType, setSelectedSubType] = useState(null)
  const [innerFormOpen, setInnerFormOpen] = useState(false)
  const [innerFormData, setInnerFormData] = useState({ nameTr: '', nameEn: '', descTr: '', descEn: '' })
  const [innerEditingId, setInnerEditingId] = useState(null)

  // Load main categories
  const loadMainCategories = useCallback(async () => {
    setMainLoading(true)
    setMainError(null)
    try {
      const data = await listMainCategories(language)
      setMainCategories(data)
    } catch (error) {
      setMainError(error instanceof ApiError ? error.message : t('common.error'))
    } finally {
      setMainLoading(false)
    }
  }, [language, t])

  // Load sub types for selected main category
  const loadSubTypes = useCallback(async (mainCategoryId) => {
    if (!mainCategoryId) {
      setSubTypes([])
      setSelectedSubType(null)
      setInnerTypes([])
      return
    }
    setSubLoading(true)
    setSubError(null)
    try {
      const data = await getSubTypesByMainCategory(mainCategoryId, language)
      setSubTypes(data)
      setSelectedSubType(null)
      setInnerTypes([])
    } catch (error) {
      setSubError(error instanceof ApiError ? error.message : t('common.error'))
    } finally {
      setSubLoading(false)
    }
  }, [language, t])

  // Load inner types for selected sub type
  const loadInnerTypes = useCallback(async (subTypeId) => {
    if (!subTypeId) {
      setInnerTypes([])
      return
    }
    setInnerLoading(true)
    setInnerError(null)
    try {
      const data = await getInnerTypesBySubType(subTypeId, language)
      setInnerTypes(data)
    } catch (error) {
      setInnerError(error instanceof ApiError ? error.message : t('common.error'))
    } finally {
      setInnerLoading(false)
    }
  }, [language, t])

  // Initial load
  useEffect(() => {
    loadMainCategories()
  }, [loadMainCategories])

  // Handle main category selection
  const handleSelectMainCategory = useCallback(
    (id) => {
      setSelectedMainCategory(id)
      loadSubTypes(id)
    },
    [loadSubTypes],
  )

  // Handle sub type selection
  const handleSelectSubType = useCallback(
    (id) => {
      setSelectedSubType(id)
      loadInnerTypes(id)
    },
    [loadInnerTypes],
  )

  // Main Category handlers
  const handleSaveMainCategory = useCallback(async () => {
    const translations = [
      { localeCode: 'tr', name: mainFormData.nameTr, description: mainFormData.descTr },
      { localeCode: 'en', name: mainFormData.nameEn, description: mainFormData.descEn },
    ]

    try {
      if (mainEditingId) {
        await updateMainCategory(mainEditingId, { translations })
      } else {
        await createMainCategory({ translations })
      }
      setMainFormData({ nameTr: '', nameEn: '', descTr: '', descEn: '' })
      setMainFormOpen(false)
      setMainEditingId(null)
      await loadMainCategories()
    } catch (error) {
      setMainError(error instanceof ApiError ? error.message : t('common.error'))
    }
  }, [mainFormData, mainEditingId, t, loadMainCategories])

  const handleEditMainCategory = useCallback(
    async (id) => {
      try {
        const category = await getMainCategory(id, language)
        setMainFormData({
          nameTr: category.name || '',
          nameEn: category.name || '',
          descTr: category.description || '',
          descEn: category.description || '',
        })
        setMainEditingId(id)
        setMainFormOpen(true)
      } catch (error) {
        setMainError(error instanceof ApiError ? error.message : t('common.error'))
      }
    },
    [language, t],
  )

  const handleDeleteMainCategory = useCallback(
    async (id) => {
      if (!window.confirm(t('categories.deleteConfirm'))) return
      try {
        await deleteMainCategory(id)
        if (selectedMainCategory === id) {
          setSelectedMainCategory(null)
          setSubTypes([])
          setSelectedSubType(null)
          setInnerTypes([])
        }
        await loadMainCategories()
      } catch (error) {
        setMainError(error instanceof ApiError ? error.message : t('common.error'))
      }
    },
    [selectedMainCategory, t, loadMainCategories],
  )

  // Sub Type handlers
  const handleSaveSubType = useCallback(async () => {
    const translations = [
      { localeCode: 'tr', name: subFormData.nameTr, description: subFormData.descTr },
      { localeCode: 'en', name: subFormData.nameEn, description: subFormData.descEn },
    ]

    try {
      if (subEditingId) {
        await updateSubType(subEditingId, { mainCategoryId: selectedMainCategory, translations })
      } else {
        await createSubType({ mainCategoryId: selectedMainCategory, translations })
      }
      setSubFormData({ nameTr: '', nameEn: '', descTr: '', descEn: '' })
      setSubFormOpen(false)
      setSubEditingId(null)
      await loadSubTypes(selectedMainCategory)
    } catch (error) {
      setSubError(error instanceof ApiError ? error.message : t('common.error'))
    }
  }, [subFormData, subEditingId, selectedMainCategory, t, loadSubTypes])

  const handleEditSubType = useCallback(
    async (id) => {
      try {
        const subType = await getSubType(id, language)
        setSubFormData({
          nameTr: subType.name || '',
          nameEn: subType.name || '',
          descTr: subType.description || '',
          descEn: subType.description || '',
        })
        setSubEditingId(id)
        setSubFormOpen(true)
      } catch (error) {
        setSubError(error instanceof ApiError ? error.message : t('common.error'))
      }
    },
    [language, t],
  )

  const handleDeleteSubType = useCallback(
    async (id) => {
      if (!window.confirm(t('categories.deleteConfirm'))) return
      try {
        await deleteSubType(id)
        if (selectedSubType === id) {
          setSelectedSubType(null)
          setInnerTypes([])
        }
        await loadSubTypes(selectedMainCategory)
      } catch (error) {
        setSubError(error instanceof ApiError ? error.message : t('common.error'))
      }
    },
    [selectedMainCategory, selectedSubType, t, loadSubTypes],
  )

  // Inner Type handlers
  const handleSaveInnerType = useCallback(async () => {
    const translations = [
      { localeCode: 'tr', name: innerFormData.nameTr, description: innerFormData.descTr },
      { localeCode: 'en', name: innerFormData.nameEn, description: innerFormData.descEn },
    ]

    try {
      if (innerEditingId) {
        await updateInnerType(innerEditingId, { subTypeId: selectedSubType, translations })
      } else {
        await createInnerType({ subTypeId: selectedSubType, translations })
      }
      setInnerFormData({ nameTr: '', nameEn: '', descTr: '', descEn: '' })
      setInnerFormOpen(false)
      setInnerEditingId(null)
      await loadInnerTypes(selectedSubType)
    } catch (error) {
      setInnerError(error instanceof ApiError ? error.message : t('common.error'))
    }
  }, [innerFormData, innerEditingId, selectedSubType, t, loadInnerTypes])

  const handleEditInnerType = useCallback(
    async (id) => {
      try {
        const innerType = await getInnerType(id, language)
        setInnerFormData({
          nameTr: innerType.name || '',
          nameEn: innerType.name || '',
          descTr: innerType.description || '',
          descEn: innerType.description || '',
        })
        setInnerEditingId(id)
        setInnerFormOpen(true)
      } catch (error) {
        setInnerError(error instanceof ApiError ? error.message : t('common.error'))
      }
    },
    [language, t],
  )

  const handleDeleteInnerType = useCallback(
    async (id) => {
      if (!window.confirm(t('categories.deleteConfirm'))) return
      try {
        await deleteInnerType(id)
        await loadInnerTypes(selectedSubType)
      } catch (error) {
        setInnerError(error instanceof ApiError ? error.message : t('common.error'))
      }
    },
    [selectedSubType, t, loadInnerTypes],
  )

  const canAddMainCategory = hasPermission('P4', 'ADD')
  const canEditMainCategory = hasPermission('P4', 'EDIT')
  const canDeleteMainCategory = hasPermission('P4', 'DELETE')

  return (
    <div className="categories-page">
      <h1>{t('categories.title')}</h1>

      <div className="categories-tabs">
        <button
          type="button"
          className={`tab-button ${activeTab === 'main' ? 'active' : ''}`}
          onClick={() => setActiveTab('main')}
        >
          {t('categories.mainCategories')}
        </button>
        <button
          type="button"
          className={`tab-button ${activeTab === 'sub' ? 'active' : ''}`}
          onClick={() => setActiveTab('sub')}
        >
          {t('categories.subTypes')}
        </button>
        <button
          type="button"
          className={`tab-button ${activeTab === 'inner' ? 'active' : ''}`}
          onClick={() => setActiveTab('inner')}
        >
          {t('categories.innerTypes')}
        </button>
      </div>

      {/* Main Categories Tab */}
      {activeTab === 'main' && (
        <div className="tab-content">
          {mainError && <div className="error-message">{mainError}</div>}

          {canAddMainCategory && (
            <button
              type="button"
              className="primary-button"
              onClick={() => {
                setMainFormData({ nameTr: '', nameEn: '', descTr: '', descEn: '' })
                setMainEditingId(null)
                setMainFormOpen(true)
              }}
            >
              {t('categories.createMainCategory')}
            </button>
          )}

          {mainFormOpen && (
            <div className="form-container">
              <h3>{mainEditingId ? t('categories.editMainCategory') : t('categories.createMainCategory')}</h3>
              <input
                type="text"
                placeholder={t('common.nameTr')}
                value={mainFormData.nameTr}
                onChange={(e) => setMainFormData({ ...mainFormData, nameTr: e.target.value })}
              />
              <textarea
                placeholder={t('common.descriptionTr')}
                value={mainFormData.descTr}
                onChange={(e) => setMainFormData({ ...mainFormData, descTr: e.target.value })}
              />
              <input
                type="text"
                placeholder={t('common.nameEn')}
                value={mainFormData.nameEn}
                onChange={(e) => setMainFormData({ ...mainFormData, nameEn: e.target.value })}
              />
              <textarea
                placeholder={t('common.descriptionEn')}
                value={mainFormData.descEn}
                onChange={(e) => setMainFormData({ ...mainFormData, descEn: e.target.value })}
              />
              <div className="form-buttons">
                <button type="button" className="primary-button" onClick={handleSaveMainCategory}>
                  {t('common.save')}
                </button>
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => {
                    setMainFormOpen(false)
                    setMainEditingId(null)
                  }}
                >
                  {t('common.cancel')}
                </button>
              </div>
            </div>
          )}

          {mainLoading ? (
            <div className="loading">{t('common.loading')}</div>
          ) : mainCategories.length === 0 ? (
            <div className="empty-state">{t('categories.noMainCategories')}</div>
          ) : (
            <div className="categories-list">
              {mainCategories.map((category) => (
                <div key={category.id} className="category-item">
                  <div className="category-header">
                    <h3>{category.name}</h3>
                    {category.description && <p className="description">{category.description}</p>}
                  </div>
                  <div className="category-actions">
                    {canEditMainCategory && (
                      <button type="button" className="action-button" onClick={() => handleEditMainCategory(category.id)}>
                        {t('common.edit')}
                      </button>
                    )}
                    {canDeleteMainCategory && (
                      <button type="button" className="action-button danger" onClick={() => handleDeleteMainCategory(category.id)}>
                        {t('common.delete')}
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Sub Types Tab */}
      {activeTab === 'sub' && (
        <div className="tab-content">
          <div className="category-selector">
            <label>{t('categories.selectMainCategory')}</label>
            <select value={selectedMainCategory || ''} onChange={(e) => handleSelectMainCategory(e.target.value ? Number(e.target.value) : null)}>
              <option value="">-- {t('categories.selectMainCategory')} --</option>
              {mainCategories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>

          {subError && <div className="error-message">{subError}</div>}

          {selectedMainCategory && canEditMainCategory && (
            <button
              type="button"
              className="primary-button"
              onClick={() => {
                setSubFormData({ nameTr: '', nameEn: '', descTr: '', descEn: '' })
                setSubEditingId(null)
                setSubFormOpen(true)
              }}
            >
              {t('categories.createSubType')}
            </button>
          )}

          {subFormOpen && (
            <div className="form-container">
              <h3>{subEditingId ? t('categories.editSubType') : t('categories.createSubType')}</h3>
              <input
                type="text"
                placeholder={t('common.nameTr')}
                value={subFormData.nameTr}
                onChange={(e) => setSubFormData({ ...subFormData, nameTr: e.target.value })}
              />
              <textarea
                placeholder={t('common.descriptionTr')}
                value={subFormData.descTr}
                onChange={(e) => setSubFormData({ ...subFormData, descTr: e.target.value })}
              />
              <input
                type="text"
                placeholder={t('common.nameEn')}
                value={subFormData.nameEn}
                onChange={(e) => setSubFormData({ ...subFormData, nameEn: e.target.value })}
              />
              <textarea
                placeholder={t('common.descriptionEn')}
                value={subFormData.descEn}
                onChange={(e) => setSubFormData({ ...subFormData, descEn: e.target.value })}
              />
              <div className="form-buttons">
                <button type="button" className="primary-button" onClick={handleSaveSubType}>
                  {t('common.save')}
                </button>
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => {
                    setSubFormOpen(false)
                    setSubEditingId(null)
                  }}
                >
                  {t('common.cancel')}
                </button>
              </div>
            </div>
          )}

          {!selectedMainCategory ? (
            <div className="empty-state">{t('categories.selectMainCategory')}</div>
          ) : subLoading ? (
            <div className="loading">{t('common.loading')}</div>
          ) : subTypes.length === 0 ? (
            <div className="empty-state">{t('categories.noSubTypes')}</div>
          ) : (
            <div className="categories-list">
              {subTypes.map((subType) => (
                <div key={subType.id} className="category-item">
                  <div className="category-header">
                    <h3>{subType.name}</h3>
                    {subType.description && <p className="description">{subType.description}</p>}
                  </div>
                  <div className="category-actions">
                    {canEditMainCategory && (
                      <button type="button" className="action-button" onClick={() => handleEditSubType(subType.id)}>
                        {t('common.edit')}
                      </button>
                    )}
                    {canDeleteMainCategory && (
                      <button type="button" className="action-button danger" onClick={() => handleDeleteSubType(subType.id)}>
                        {t('common.delete')}
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Inner Types Tab */}
      {activeTab === 'inner' && (
        <div className="tab-content">
          <div className="category-selector">
            <label>{t('categories.selectSubType')}</label>
            <select
              value={selectedSubType || ''}
              onChange={(e) => handleSelectSubType(e.target.value ? Number(e.target.value) : null)}
              disabled={!selectedMainCategory}
            >
              <option value="">-- {t('categories.selectSubType')} --</option>
              {subTypes.map((subType) => (
                <option key={subType.id} value={subType.id}>
                  {subType.name}
                </option>
              ))}
            </select>
          </div>

          {innerError && <div className="error-message">{innerError}</div>}

          {selectedSubType && canEditMainCategory && (
            <button
              type="button"
              className="primary-button"
              onClick={() => {
                setInnerFormData({ nameTr: '', nameEn: '', descTr: '', descEn: '' })
                setInnerEditingId(null)
                setInnerFormOpen(true)
              }}
            >
              {t('categories.createInnerType')}
            </button>
          )}

          {innerFormOpen && (
            <div className="form-container">
              <h3>{innerEditingId ? t('categories.editInnerType') : t('categories.createInnerType')}</h3>
              <input
                type="text"
                placeholder={t('common.nameTr')}
                value={innerFormData.nameTr}
                onChange={(e) => setInnerFormData({ ...innerFormData, nameTr: e.target.value })}
              />
              <textarea
                placeholder={t('common.descriptionTr')}
                value={innerFormData.descTr}
                onChange={(e) => setInnerFormData({ ...innerFormData, descTr: e.target.value })}
              />
              <input
                type="text"
                placeholder={t('common.nameEn')}
                value={innerFormData.nameEn}
                onChange={(e) => setInnerFormData({ ...innerFormData, nameEn: e.target.value })}
              />
              <textarea
                placeholder={t('common.descriptionEn')}
                value={innerFormData.descEn}
                onChange={(e) => setInnerFormData({ ...innerFormData, descEn: e.target.value })}
              />
              <div className="form-buttons">
                <button type="button" className="primary-button" onClick={handleSaveInnerType}>
                  {t('common.save')}
                </button>
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => {
                    setInnerFormOpen(false)
                    setInnerEditingId(null)
                  }}
                >
                  {t('common.cancel')}
                </button>
              </div>
            </div>
          )}

          {!selectedMainCategory ? (
            <div className="empty-state">{t('categories.selectMainCategory')}</div>
          ) : !selectedSubType ? (
            <div className="empty-state">{t('categories.selectSubType')}</div>
          ) : innerLoading ? (
            <div className="loading">{t('common.loading')}</div>
          ) : innerTypes.length === 0 ? (
            <div className="empty-state">{t('categories.noInnerTypes')}</div>
          ) : (
            <div className="categories-list">
              {innerTypes.map((innerType) => (
                <div key={innerType.id} className="category-item">
                  <div className="category-header">
                    <h3>{innerType.name}</h3>
                    {innerType.description && <p className="description">{innerType.description}</p>}
                  </div>
                  <div className="category-actions">
                    {canEditMainCategory && (
                      <button type="button" className="action-button" onClick={() => handleEditInnerType(innerType.id)}>
                        {t('common.edit')}
                      </button>
                    )}
                    {canDeleteMainCategory && (
                      <button type="button" className="action-button danger" onClick={() => handleDeleteInnerType(innerType.id)}>
                        {t('common.delete')}
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  )
}
