import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import { getSellerProducts, deleteProduct, updateProduct } from '../api/sellerApi'
import { getMainCategories, getSubTypesByMainCategory, getInnerTypesBySubType } from '../api/categoryApi'
import { getProductQuestions, answerQuestion } from '../api/qnaApi'
import '../styles/myProducts.css'

export function MyProductsPage() {
  const navigate = useNavigate()
  const { t, language } = useTranslation()
  const { user } = useAuth()
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [editingId, setEditingId] = useState(null)
  const [_mainCategories, setMainCategories] = useState([])
  const [_subTypes, setSubTypes] = useState([])
  const [_innerTypes, setInnerTypes] = useState([])
  const [deleteConfirm, setDeleteConfirm] = useState(null)
  const [success, setSuccess] = useState(null)
  const [editForm, setEditForm] = useState({})

  // Unanswered questions state
  const [unansweredQuestions, setUnansweredQuestions] = useState([])
  const [showAnswerForm, setShowAnswerForm] = useState(null)
  const [answerText, setAnswerText] = useState('')
  const [submittingAnswer, setSubmittingAnswer] = useState(false)
  const [answerError, setAnswerError] = useState(null)

  const loadProducts = useCallback(async () => {
    if (!user?.id) return
    try {
      setLoading(true)
      const data = await getSellerProducts(user.id, language)
      setProducts(data)

      // Load questions for all products and find unanswered ones
      const allQuestions = []
      for (const product of data) {
        try {
          const questions = await getProductQuestions(product.id)
          const unanswered = questions.filter((q) => !q.answer)
          allQuestions.push(
            ...unanswered.map((q) => ({
              ...q,
              productId: product.id,
              productName: product.name,
            }))
          )
        } catch (err) {
          console.error(`Failed to load questions for product ${product.id}:`, err)
        }
      }
      setUnansweredQuestions(allQuestions)
    } catch (err) {
      setError(err?.message || t('myProducts.loadError') || 'Failed to load products')
    } finally {
      setLoading(false)
    }
  }, [user?.id, language, t])

  const loadCategories = async () => {
    try {
      const cats = await getMainCategories()
      setMainCategories(cats)
    } catch (_err) {
      console.error('Failed to load categories:', _err)
    }
  }

  // Load products on mount
  useEffect(() => {
    loadProducts()
    loadCategories()
  }, [loadProducts])

  const handleEditClick = async (product) => {
    setEditingId(product.id)
    const subData = await getSubTypesByMainCategory(product.mainCategoryId)
    setSubTypes(subData)
    try {
      const innerData = await getInnerTypesBySubType(product.subTypeId)
      setInnerTypes(innerData)
    } catch (_err) {
      console.error('Failed to load inner types:', _err)
    }

    setEditForm({
      id: product.id,
      price: product.price,
      stock: product.stock,
      nameTr: product.nameTr || '',
      nameEn: product.nameEn || '',
      shortDescTr: product.shortDescTr || '',
      shortDescEn: product.shortDescEn || '',
      longDescTr: product.longDescTr || '',
      longDescEn: product.longDescEn || '',
      innerTypeId: product.innerTypeId,
      mainCategoryId: product.mainCategoryId,
      subTypeId: product.subTypeId,
    })
  }

  const handleEditChange = (field, value) => {
    setEditForm((prev) => ({
      ...prev,
      [field]: value,
    }))
  }

  const handleSaveEdit = async () => {
    try {
      const request = {
        innerTypeId: editForm.innerTypeId,
        price: parseFloat(editForm.price),
        stock: parseInt(editForm.stock),
        translations: [
          {
            localeCode: 'tr',
            name: editForm.nameTr,
            shortDescription: editForm.shortDescTr,
            longDescription: editForm.longDescTr,
          },
          {
            localeCode: 'en',
            name: editForm.nameEn,
            shortDescription: editForm.shortDescEn,
            longDescription: editForm.longDescEn,
          },
        ],
        keyFeatures: [],
        photos: [],
      }

      await updateProduct(editForm.id, request)
      setSuccess(t('myProducts.updateSuccess'))
      setEditingId(null)
      setTimeout(() => {
        setSuccess(null)
        loadProducts()
      }, 1500)
    } catch (err) {
      setError(err.message || 'Failed to update product')
    }
  }

  const handleDeleteClick = (productId) => {
    setDeleteConfirm(productId)
  }

  const handleConfirmDelete = async () => {
    try {
      await deleteProduct(deleteConfirm)
      setSuccess(t('myProducts.deleteSuccess'))
      setDeleteConfirm(null)
      setTimeout(() => {
        setSuccess(null)
        loadProducts()
      }, 1500)
    } catch (err) {
      setError(err.message || t('myProducts.deleteError'))
    }
  }

  const getProductName = (product) => {
    if (editingId === product.id) {
      return editForm.nameTr || editForm.nameEn
    }
    return product.name || ''
  }

  const handleSubmitAnswer = async (question) => {
    if (!answerText.trim()) {
      setAnswerError(t('myProducts.answerRequired') || 'Answer cannot be empty')
      return
    }

    try {
      setSubmittingAnswer(true)
      setAnswerError(null)

      await answerQuestion(question.productId, question.id, {
        answerText: answerText.trim(),
      })

      // Clear form and refresh questions
      setAnswerText('')
      setShowAnswerForm(null)
      setTimeout(() => {
        loadProducts()
      }, 500)
    } catch (err) {
      setAnswerError(err.message || t('myProducts.answerError') || 'Failed to submit answer')
      console.error('Failed to submit answer:', err)
    } finally {
      setSubmittingAnswer(false)
    }
  }

  return (
    <section className="my-products-container">
      <div className="my-products-header">
        <h1>{t('myProducts.title')}</h1>
        <button className="btn-add-product" onClick={() => navigate('/add-product')}>
          + {t('myStore.addNewProduct')}
        </button>
      </div>

      {error && <div className="error-banner">{error}</div>}
      {success && <div className="success-banner">{success}</div>}

      {loading ? (
        <div className="loading-spinner">{t('common.loading')}</div>
      ) : products.length === 0 ? (
        <div className="empty-state">
          <p>{t('myProducts.empty')}</p>
          <p>{t('myProducts.addFirst')}</p>
          <button className="btn-add-product" onClick={() => navigate('/add-product')}>
            + {t('myStore.addNewProduct')}
          </button>
        </div>
      ) : (
        <div className="products-table-wrapper">
          <table className="products-table">
            <thead>
              <tr>
                <th>{t('addProduct.name')}</th>
                <th>{t('addProduct.price')}</th>
                <th>{t('addProduct.stock')}</th>
                <th>İşlemler</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td>
                    {editingId === product.id ? (
                      <div className="edit-field">
                        <input
                          type="text"
                          value={editForm.nameTr}
                          onChange={(e) => handleEditChange('nameTr', e.target.value)}
                          placeholder="Türkçe Adı"
                        />
                        <input
                          type="text"
                          value={editForm.nameEn}
                          onChange={(e) => handleEditChange('nameEn', e.target.value)}
                          placeholder="English Name"
                        />
                      </div>
                    ) : (
                      <div>
                        <div className="product-name">{getProductName(product)}</div>
                      </div>
                    )}
                  </td>
                  <td>
                    {editingId === product.id ? (
                      <input
                        type="number"
                        step="0.01"
                        value={editForm.price}
                        onChange={(e) => handleEditChange('price', e.target.value)}
                      />
                    ) : (
                      `₺${product.price.toFixed(2)}`
                    )}
                  </td>
                  <td>
                    {editingId === product.id ? (
                      <input
                        type="number"
                        value={editForm.stock}
                        onChange={(e) => handleEditChange('stock', e.target.value)}
                      />
                    ) : (
                      product.stock
                    )}
                  </td>
                  <td className="actions-cell">
                    {editingId === product.id ? (
                      <>
                        <button className="btn-save" onClick={handleSaveEdit}>
                          ✓ Kaydet
                        </button>
                        <button className="btn-cancel" onClick={() => setEditingId(null)}>
                          ✕ İptal
                        </button>
                      </>
                    ) : (
                      <>
                        <button
                          className="btn-edit"
                          onClick={() => handleEditClick(product)}
                        >
                          ✎ {t('myProducts.edit')}
                        </button>
                        <button
                          className="btn-delete"
                          onClick={() => handleDeleteClick(product.id)}
                        >
                          🗑 {t('myProducts.delete')}
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Unanswered Questions Section */}
      {unansweredQuestions.length > 0 && (
        <div className="unanswered-questions-section">
          <h2>{t('myProducts.unansweredQuestions') || 'Unanswered Questions'} ({unansweredQuestions.length})</h2>
          {answerError && <div className="error-banner">{answerError}</div>}
          <div className="questions-container">
            {unansweredQuestions.map((question) => (
              <div key={question.id} className="question-item-seller">
                <div className="question-product">
                  <strong>{question.productName}</strong>
                </div>
                <div className="question-text">{question.questionText}</div>
                <div className="question-date">
                  {new Date(question.createdAt).toLocaleDateString()}
                </div>

                {showAnswerForm === question.id ? (
                  <div className="answer-form">
                    <textarea
                      value={answerText}
                      onChange={(e) => setAnswerText(e.target.value)}
                      placeholder={t('myProducts.answerPlaceholder') || 'Type your answer...'}
                      rows="3"
                    />
                    <div className="answer-form-buttons">
                      <button
                        className="btn-submit-answer"
                        onClick={() => handleSubmitAnswer(question)}
                        disabled={submittingAnswer}
                      >
                        {submittingAnswer ? t('common.loading') : t('myProducts.submitAnswer') || 'Submit Answer'}
                      </button>
                      <button
                        className="btn-cancel-answer"
                        onClick={() => {
                          setShowAnswerForm(null)
                          setAnswerText('')
                          setAnswerError(null)
                        }}
                      >
                        {t('myProducts.cancel') || 'Cancel'}
                      </button>
                    </div>
                  </div>
                ) : (
                  <button
                    className="btn-answer"
                    onClick={() => {
                      setShowAnswerForm(question.id)
                      setAnswerText('')
                      setAnswerError(null)
                    }}
                  >
                    {t('myProducts.answerQuestion') || 'Answer'}
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {deleteConfirm !== null && (
        <div className="delete-confirmation-modal">
          <div className="modal-content">
            <p>{t('myProducts.deleteConfirm')}</p>
            <div className="modal-actions">
              <button className="btn-primary" onClick={handleConfirmDelete}>
                Sil
              </button>
              <button className="btn-secondary" onClick={() => setDeleteConfirm(null)}>
                İptal
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  )
}
