import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { getMainCategories, getSubTypesByMainCategory, getInnerTypesBySubType } from '../api/categoryApi'
import { createProduct } from '../api/sellerApi'
import '../styles/addProduct.css'

export function AddProductPage() {
  const navigate = useNavigate()
  const { t, language } = useTranslation()
  const [mainCategories, setMainCategories] = useState([])
  const [subTypes, setSubTypes] = useState([])
  const [innerTypes, setInnerTypes] = useState([])
  const [selectedMainCategory, setSelectedMainCategory] = useState('')
  const [selectedSubType, setSelectedSubType] = useState('')
  const [selectedInnerType, setSelectedInnerType] = useState('')
  const [photos, setPhotos] = useState([])
  const [features, setFeatures] = useState([''])
  const [formData, setFormData] = useState({
    nameTr: '',
    nameEn: '',
    shortDescTr: '',
    shortDescEn: '',
    longDescTr: '',
    longDescEn: '',
    price: '',
    stock: '',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(false)

  // Load categories on mount
  useEffect(() => {
    const loadCategories = async () => {
      try {
        const cats = await getMainCategories()
        setMainCategories(cats)
      } catch (err) {
        console.error('Failed to load categories:', err)
      }
    }
    loadCategories()
  }, [])

  // Load sub types when main category changes
  useEffect(() => {
    if (selectedMainCategory) {
      const loadSubTypes = async () => {
        try {
          const subs = await getSubTypesByMainCategory(selectedMainCategory)
          setSubTypes(subs)
          setSelectedSubType('')
          setInnerTypes([])
          setSelectedInnerType('')
        } catch (err) {
          console.error('Failed to load sub types:', err)
        }
      }
      loadSubTypes()
    }
  }, [selectedMainCategory])

  // Load inner types when sub type changes
  useEffect(() => {
    if (selectedSubType) {
      const loadInnerTypes = async () => {
        try {
          const inners = await getInnerTypesBySubType(selectedSubType)
          setInnerTypes(inners)
          setSelectedInnerType('')
        } catch (err) {
          console.error('Failed to load inner types:', err)
        }
      }
      loadInnerTypes()
    }
  }, [selectedSubType])

  const handleFormChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handlePhotoChange = async (e) => {
    const files = Array.from(e.target.files)
    if (photos.length + files.length > 10) {
      setError(t('addProduct.maxPhotos'))
      return
    }

    const newPhotos = []
    for (const file of files) {
      const reader = new FileReader()
      reader.onload = (event) => {
        const base64 = event.target.result.split(',')[1]
        newPhotos.push({
          imageDataBase64: base64,
          displayOrder: photos.length + newPhotos.length,
        })
        if (newPhotos.length === files.length) {
          setPhotos((prev) => [...prev, ...newPhotos])
        }
      }
      reader.readAsDataURL(file)
    }
  }

  const removePhoto = (index) => {
    setPhotos((prev) => prev.filter((_, i) => i !== index))
  }

  const handleFeatureChange = (index, value) => {
    const newFeatures = [...features]
    newFeatures[index] = value
    setFeatures(newFeatures)
  }

  const addFeature = () => {
    setFeatures((prev) => [...prev, ''])
  }

  const removeFeature = (index) => {
    setFeatures((prev) => prev.filter((_, i) => i !== index))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setSuccess(false)

    if (!selectedInnerType) {
      setError(t('addProduct.innerType') + ' ' + t('common.required'))
      return
    }

    if (!formData.nameTr || !formData.nameEn) {
      setError(t('addProduct.name') + ' ' + t('common.required'))
      return
    }

    if (!formData.price || !formData.stock) {
      setError(t('addProduct.price') + '/' + t('addProduct.stock') + ' ' + t('common.required'))
      return
    }

    setLoading(true)
    try {
      const request = {
        innerTypeId: parseInt(selectedInnerType),
        price: parseFloat(formData.price),
        stock: parseInt(formData.stock),
        translations: [
          {
            localeCode: 'tr',
            name: formData.nameTr,
            shortDescription: formData.shortDescTr || '',
            longDescription: formData.longDescTr || '',
          },
          {
            localeCode: 'en',
            name: formData.nameEn,
            shortDescription: formData.shortDescEn || '',
            longDescription: formData.longDescEn || '',
          },
        ],
        keyFeatures: features.filter((f) => f.trim()),
        photos: photos,
      }

      await createProduct(request)
      setSuccess(true)
      setTimeout(() => {
        navigate('/my-products')
      }, 1500)
    } catch (err) {
      setError(err.message || t('addProduct.error'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="add-product-container">
      <div className="add-product-header">
        <h1>{t('addProduct.title')}</h1>
      </div>

      {error && <div className="error-banner">{error}</div>}
      {success && <div className="success-banner">{t('addProduct.success')}</div>}

      <form className="add-product-form" onSubmit={handleSubmit}>
        {/* Category Selection */}
        <div className="form-section">
          <h2>Kategori</h2>
          <div className="form-group">
            <label>{t('addProduct.innerType')}</label>
            <div className="category-selectors">
              <select value={selectedMainCategory} onChange={(e) => setSelectedMainCategory(e.target.value)} required>
                <option value="">Ana Kategori Seçin</option>
                {mainCategories.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {language === 'tr' ? cat.translations?.tr?.name || cat.name : cat.translations?.en?.name || cat.name}
                  </option>
                ))}
              </select>

              {selectedMainCategory && (
                <select value={selectedSubType} onChange={(e) => setSelectedSubType(e.target.value)} required>
                  <option value="">Alt Tip Seçin</option>
                  {subTypes.map((sub) => (
                    <option key={sub.id} value={sub.id}>
                      {language === 'tr' ? sub.translations?.tr?.name || sub.name : sub.translations?.en?.name || sub.name}
                    </option>
                  ))}
                </select>
              )}

              {selectedSubType && (
                <select value={selectedInnerType} onChange={(e) => setSelectedInnerType(e.target.value)} required>
                  <option value="">İç Tip Seçin</option>
                  {innerTypes.map((inner) => (
                    <option key={inner.id} value={inner.id}>
                      {language === 'tr' ? inner.translations?.tr?.name || inner.name : inner.translations?.en?.name || inner.name}
                    </option>
                  ))}
                </select>
              )}
            </div>
          </div>
        </div>

        {/* Product Names */}
        <div className="form-section">
          <h2>Ürün Adı</h2>
          <div className="form-group">
            <label>{t('addProduct.name')}</label>
            <input
              type="text"
              name="nameTr"
              maxLength={255}
              value={formData.nameTr}
              onChange={handleFormChange}
              required
            />
          </div>
          <div className="form-group">
            <label>{t('addProduct.nameEn')}</label>
            <input
              type="text"
              name="nameEn"
              maxLength={255}
              value={formData.nameEn}
              onChange={handleFormChange}
              required
            />
          </div>
        </div>

        {/* Descriptions */}
        <div className="form-section">
          <h2>Açıklamalar</h2>
          <div className="form-group">
            <label>{t('addProduct.shortDesc')}</label>
            <textarea
              name="shortDescTr"
              maxLength={500}
              value={formData.shortDescTr}
              onChange={handleFormChange}
              rows={3}
            />
          </div>
          <div className="form-group">
            <label>{t('addProduct.shortDescEn')}</label>
            <textarea
              name="shortDescEn"
              maxLength={500}
              value={formData.shortDescEn}
              onChange={handleFormChange}
              rows={3}
            />
          </div>
          <div className="form-group">
            <label>{t('addProduct.longDesc')}</label>
            <textarea
              name="longDescTr"
              maxLength={2000}
              value={formData.longDescTr}
              onChange={handleFormChange}
              rows={5}
            />
          </div>
          <div className="form-group">
            <label>{t('addProduct.longDescEn')}</label>
            <textarea
              name="longDescEn"
              maxLength={2000}
              value={formData.longDescEn}
              onChange={handleFormChange}
              rows={5}
            />
          </div>
        </div>

        {/* Price & Stock */}
        <div className="form-section">
          <h2>Fiyat ve Stok</h2>
          <div className="form-row">
            <div className="form-group">
              <label>{t('addProduct.price')} (₺)</label>
              <input
                type="number"
                name="price"
                step="0.01"
                min="0.01"
                value={formData.price}
                onChange={handleFormChange}
                required
              />
            </div>
            <div className="form-group">
              <label>{t('addProduct.stock')}</label>
              <input
                type="number"
                name="stock"
                min="0"
                value={formData.stock}
                onChange={handleFormChange}
                required
              />
            </div>
          </div>
        </div>

        {/* Key Features */}
        <div className="form-section">
          <h2>{t('addProduct.keyFeatures')}</h2>
          {features.map((feature, index) => (
            <div key={index} className="feature-input-group">
              <input
                type="text"
                maxLength={100}
                value={feature}
                onChange={(e) => handleFeatureChange(index, e.target.value)}
                placeholder={`${t('addProduct.keyFeatures')} ${index + 1}`}
              />
              {features.length > 1 && (
                <button type="button" className="btn-remove" onClick={() => removeFeature(index)}>
                  ✕
                </button>
              )}
            </div>
          ))}
          <button type="button" className="btn-secondary" onClick={addFeature}>
            + {t('addProduct.addFeature')}
          </button>
        </div>

        {/* Photos */}
        <div className="form-section">
          <h2>{t('addProduct.photos')}</h2>
          <div className="form-group">
            <label>{t('addProduct.uploadPhotos')}</label>
            <input
              type="file"
              multiple
              accept="image/*"
              onChange={handlePhotoChange}
              disabled={photos.length >= 10}
            />
            <small>{photos.length}/10 {t('addProduct.maxPhotos')}</small>
          </div>

          {photos.length > 0 && (
            <div className="photo-preview-grid">
              {photos.map((photo, index) => (
                <div key={index} className="photo-preview-item">
                  <img src={`data:image/jpeg;base64,${photo.imageDataBase64}`} alt={`Photo ${index + 1}`} />
                  <button type="button" className="btn-remove-photo" onClick={() => removePhoto(index)}>
                    ✕
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Submit Button */}
        <div className="form-actions">
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? t('addProduct.submitting') : t('addProduct.submit')}
          </button>
          <button type="button" className="btn-secondary" onClick={() => navigate('/my-products')}>
            İptal
          </button>
        </div>
      </form>
    </section>
  )
}
