import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { listProducts, updateProduct, deleteProduct } from '../api/productApi.js'
import { ApiError } from '../api/httpClient.js'
import { getInnerTypesBySubType, getSubTypesByMainCategory, listMainCategories } from '../api/categoryApi.js'
import '../styles/products.css'

export function ProductsPage() {
  const { t, language } = useTranslation()
  const { hasPermission } = useAuth()

  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [editingId, setEditingId] = useState(null)
  const [formOpen, setFormOpen] = useState(false)

  // Category data for selector during edit
  const [mainCategories, setMainCategories] = useState([])
  const [subTypes, setSubTypes] = useState([])
  const [innerTypes, setInnerTypes] = useState([])
  const [selectedMainId, setSelectedMainId] = useState(null)
  const [selectedSubId, setSelectedSubId] = useState(null)

  // Form data
  const [formData, setFormData] = useState({
    innerTypeId: null,
    price: '',
    stock: '',
    nameTr: '',
    nameEn: '',
    shortDescTr: '',
    shortDescEn: '',
    longDescTr: '',
    longDescEn: '',
    keyFeatures: [],
    newFeature: '',
  })

  // Load products on mount
  const loadProducts = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await listProducts(language)
      setProducts(data)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : t('common.error'))
    } finally {
      setLoading(false)
    }
  }, [language, t])

  // Load categories for selector
  const loadCategories = useCallback(async () => {
    try {
      const main = await listMainCategories(language)
      setMainCategories(main)
    } catch (err) {
      console.warn('Failed to load categories:', err)
    }
  }, [language])

  // Load sub types for selected main
  const loadSubTypes = useCallback(async (mainId) => {
    if (!mainId) {
      setSubTypes([])
      setInnerTypes([])
      return
    }
    try {
      const data = await getSubTypesByMainCategory(mainId, language)
      setSubTypes(data)
      setInnerTypes([])
    } catch (err) {
      console.warn('Failed to load sub types:', err)
    }
  }, [language])

  // Load inner types for selected sub
  const loadInnerTypes = useCallback(async (subId) => {
    if (!subId) {
      setInnerTypes([])
      return
    }
    try {
      const data = await getInnerTypesBySubType(subId, language)
      setInnerTypes(data)
    } catch (err) {
      console.warn('Failed to load inner types:', err)
    }
  }, [language])

  useEffect(() => {
    loadProducts()
    loadCategories()
  }, [loadProducts, loadCategories])

  const handleEdit = useCallback(
    (product) => {
      setEditingId(product.id)
      setFormData({
        innerTypeId: product.innerTypeId,
        price: product.price?.toString() || '',
        stock: product.stock?.toString() || '',
        nameTr: product.name || '',
        nameEn: product.name || '',
        shortDescTr: product.shortDescription || '',
        shortDescEn: product.shortDescription || '',
        longDescTr: product.longDescription || '',
        longDescEn: product.longDescription || '',
        keyFeatures: product.keyFeatures || [],
        newFeature: '',
      })
      setSelectedMainId(null)
      setSelectedSubId(null)
      setSubTypes([])
      setInnerTypes([])
      setFormOpen(true)
    },
    [],
  )

  const handleSave = useCallback(async () => {
    if (!formData.innerTypeId) {
      setError(t('products.selectInnerType'))
      return
    }

    try {
      const payload = {
        innerTypeId: formData.innerTypeId,
        price: parseFloat(formData.price),
        stock: parseInt(formData.stock, 10),
        translations: [
          {
            localeCode: 'tr',
            name: formData.nameTr,
            shortDescription: formData.shortDescTr,
            longDescription: formData.longDescTr,
          },
          {
            localeCode: 'en',
            name: formData.nameEn,
            shortDescription: formData.shortDescEn,
            longDescription: formData.longDescEn,
          },
        ],
        keyFeatures: formData.keyFeatures.filter((f) => f && f.trim()),
        photos: null, // Personnel cannot re-upload photos
      }

      await updateProduct(editingId, payload)
      setFormData({
        innerTypeId: null,
        price: '',
        stock: '',
        nameTr: '',
        nameEn: '',
        shortDescTr: '',
        shortDescEn: '',
        longDescTr: '',
        longDescEn: '',
        keyFeatures: [],
        newFeature: '',
      })
      setFormOpen(false)
      setEditingId(null)
      await loadProducts()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : t('common.error'))
    }
  }, [formData, editingId, t, loadProducts])

  const handleDelete = useCallback(
    async (id) => {
      if (!window.confirm(t('products.deleteConfirm'))) return
      try {
        await deleteProduct(id)
        await loadProducts()
      } catch (err) {
        setError(err instanceof ApiError ? err.message : t('common.error'))
      }
    },
    [t, loadProducts],
  )

  const canEdit = hasPermission('P5', 'EDIT')
  const canDelete = hasPermission('P5', 'DELETE')
  const canView = hasPermission('P5', 'VIEW')

  if (!canView) {
    return <div className="access-denied">{t('common.accessDenied')}</div>
  }

  return (
    <div className="products-page">
      <h1>{t('products.title')}</h1>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="loading">{t('common.loading')}</div>
      ) : products.length === 0 ? (
        <div className="empty-state">{t('products.noProducts')}</div>
      ) : (
        <div className="products-grid">
          {products.map((product) => (
            <div key={product.id} className="product-card">
              <div className="product-info">
                <h3>{product.name}</h3>
                <p>
                  <span className="label">{t('products.price')}:</span> ${product.price}
                </p>
                <p>
                  <span className="label">{t('products.stock')}:</span> {product.stock}
                </p>
                <p>
                  <span className="label">{t('products.sellerId')}:</span> {product.sellerId}
                </p>
              </div>
              <div className="product-actions">
                {canEdit && (
                  <button type="button" className="edit-button" onClick={() => handleEdit(product)}>
                    {t('common.edit')}
                  </button>
                )}
                {canDelete && (
                  <button type="button" className="delete-button" onClick={() => handleDelete(product.id)}>
                    {t('common.delete')}
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {formOpen && editingId && (
        <div className="modal-overlay" onClick={() => setFormOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="form-container">
              <h3>{t('products.editProduct')}</h3>

              <div className="form-group">
                <label>{t('products.selectCategory')}</label>
                <select value={selectedMainId || ''} onChange={(e) => {
                  const id = e.target.value ? parseInt(e.target.value, 10) : null
                  setSelectedMainId(id)
                  loadSubTypes(id)
                }} >
                  <option value="">{t('products.selectMainCategory')}</option>
                  {mainCategories.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name}
                    </option>
                  ))}
                </select>
              </div>

              {selectedMainId && (
                <div className="form-group">
                  <label>{t('products.selectSubType')}</label>
                  <select value={selectedSubId || ''} onChange={(e) => {
                    const id = e.target.value ? parseInt(e.target.value, 10) : null
                    setSelectedSubId(id)
                    loadInnerTypes(id)
                  }} >
                    <option value="">{t('products.selectSubType')}</option>
                    {subTypes.map((sub) => (
                      <option key={sub.id} value={sub.id}>
                        {sub.name}
                      </option>
                    ))}
                  </select>
                </div>
              )}

              {selectedSubId && (
                <div className="form-group">
                  <label>{t('products.selectInnerType')}</label>
                  <select value={formData.innerTypeId || ''} onChange={(e) => {
                    setFormData({ ...formData, innerTypeId: parseInt(e.target.value, 10) })
                  }} >
                    <option value="">{t('products.selectInnerType')}</option>
                    {innerTypes.map((inner) => (
                      <option key={inner.id} value={inner.id}>
                        {inner.name}
                      </option>
                    ))}
                  </select>
                </div>
              )}

              <div className="form-row">
                <div className="form-group">
                  <label>{t('products.price')}</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    value={formData.price}
                    onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>{t('products.stock')}</label>
                  <input
                    type="number"
                    min="0"
                    value={formData.stock}
                    onChange={(e) => setFormData({ ...formData, stock: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-group">
                <label>{t('common.nameTr')}</label>
                <input
                  type="text"
                  value={formData.nameTr}
                  onChange={(e) => setFormData({ ...formData, nameTr: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>{t('common.nameEn')}</label>
                <input
                  type="text"
                  value={formData.nameEn}
                  onChange={(e) => setFormData({ ...formData, nameEn: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>{t('products.shortDescTr')}</label>
                <textarea
                  value={formData.shortDescTr}
                  onChange={(e) => setFormData({ ...formData, shortDescTr: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>{t('products.shortDescEn')}</label>
                <textarea
                  value={formData.shortDescEn}
                  onChange={(e) => setFormData({ ...formData, shortDescEn: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>{t('products.longDescTr')}</label>
                <textarea
                  value={formData.longDescTr}
                  onChange={(e) => setFormData({ ...formData, longDescTr: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>{t('products.longDescEn')}</label>
                <textarea
                  value={formData.longDescEn}
                  onChange={(e) => setFormData({ ...formData, longDescEn: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>{t('products.keyFeatures')}</label>
                <div className="features-list">
                  {formData.keyFeatures.map((feature, idx) => (
                    <div key={idx} className="feature-item">
                      <span>{feature}</span>
                      <button
                        type="button"
                        className="remove-feature"
                        onClick={() => {
                          setFormData({
                            ...formData,
                            keyFeatures: formData.keyFeatures.filter((_, i) => i !== idx),
                          })
                        }}
                      >
                        ×
                      </button>
                    </div>
                  ))}
                </div>
                <div className="add-feature">
                  <input
                    type="text"
                    placeholder={t('products.addFeature')}
                    value={formData.newFeature}
                    onChange={(e) => setFormData({ ...formData, newFeature: e.target.value })}
                  />
                  <button
                    type="button"
                    className="add-feature-btn"
                    onClick={() => {
                      if (formData.newFeature.trim()) {
                        setFormData({
                          ...formData,
                          keyFeatures: [...formData.keyFeatures, formData.newFeature.trim()],
                          newFeature: '',
                        })
                      }
                    }}
                  >
                    {t('common.add')}
                  </button>
                </div>
              </div>

              <div className="form-buttons">
                <button type="button" className="primary-button" onClick={handleSave}>
                  {t('common.save')}
                </button>
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => {
                    setFormOpen(false)
                    setEditingId(null)
                  }}
                >
                  {t('common.cancel')}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
