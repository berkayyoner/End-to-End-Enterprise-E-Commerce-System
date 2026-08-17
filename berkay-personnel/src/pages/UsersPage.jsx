import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import {
  listUsers,
  editUser,
  deleteUser,
  banUser,
} from '../api/usersApi'
import { ApiError } from '../api/httpClient'
import '../styles/users.css'

export function UsersPage() {
  const { t } = useTranslation()
  const { hasPermission } = useAuth()

  // Users list state
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [page, setPage] = useState(0)

  // Edit form state
  const [editingId, setEditingId] = useState(null)
  const [editFormData, setEditFormData] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
  })
  const [editError, setEditError] = useState(null)
  const [editLoading, setEditLoading] = useState(false)

  // Delete confirmation state
  const [deleteConfirmId, setDeleteConfirmId] = useState(null)
  const [deleteLoading, setDeleteLoading] = useState(false)

  // Ban confirmation state
  const [banConfirmId, setBanConfirmId] = useState(null)
  const [banReason, setBanReason] = useState('')
  const [banLoading, setBanLoading] = useState(false)
  const [banError, setBanError] = useState(null)

  // Load users list
  const loadUsers = useCallback(async (pageNum = 0) => {
    setLoading(true)
    setError(null)
    try {
      const data = await listUsers(pageNum, 20)
      setUsers(Array.isArray(data.content) ? data.content : [])
      setPage(pageNum)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : t('common.error'))
    } finally {
      setLoading(false)
    }
  }, [t])

  // Initial load
  useEffect(() => {
    loadUsers(0)
  }, [loadUsers])

  // Handle edit form submission
  const handleEditSubmit = async (userId) => {
    if (!editFormData.firstName || !editFormData.lastName) {
      setEditError(t('users.editForm.requiredFields'))
      return
    }

    setEditError(null)
    setEditLoading(true)

    try {
      await editUser(
        userId,
        editFormData.firstName,
        editFormData.lastName,
        editFormData.phoneNumber
      )

      setEditingId(null)
      setEditFormData({ firstName: '', lastName: '', phoneNumber: '' })
      await loadUsers(page)
    } catch (err) {
      setEditError(err instanceof ApiError ? err.message : t('common.error'))
    } finally {
      setEditLoading(false)
    }
  }

  // Handle delete
  const handleDelete = async (userId) => {
    setDeleteLoading(true)

    try {
      await deleteUser(userId)
      setDeleteConfirmId(null)
      await loadUsers(page)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : t('common.error'))
    } finally {
      setDeleteLoading(false)
    }
  }

  // Handle ban
  const handleBan = async (userId) => {
    if (!banReason.trim()) {
      setBanError(t('users.banReason') + ' is required')
      return
    }

    setBanError(null)
    setBanLoading(true)

    try {
      await banUser(userId, banReason)
      setBanConfirmId(null)
      setBanReason('')
      await loadUsers(page)
    } catch (err) {
      setBanError(err instanceof ApiError ? err.message : t('common.error'))
    } finally {
      setBanLoading(false)
    }
  }

  // Check permissions
  const canView = hasPermission('P3', 'VIEW')
  const canEdit = hasPermission('P3', 'EDIT')
  const canDelete = hasPermission('P3', 'DELETE')

  if (!canView) {
    return <div className="users-page"><p>{t('common.accessDenied')}</p></div>
  }

  return (
    <div className="users-page">
      <div className="page-header">
        <h1>{t('users.title')}</h1>
      </div>

      {/* Users List */}
      <div className="users-list-section">
        <h2>{t('users.listTitle')}</h2>

        {loading && <p>{t('common.loading')}</p>}
        {error && <div className="error-message">{error}</div>}

        {!loading && users.length === 0 && (
          <p className="empty-message">{t('users.emptyList')}</p>
        )}

        {!loading && users.length > 0 && (
          <div className="users-table-container">
            <table className="users-table">
              <thead>
                <tr>
                  <th>{t('users.table.email')}</th>
                  <th>{t('users.table.name')}</th>
                  <th>{t('users.table.phone')}</th>
                  <th>{t('users.table.idVerified')}</th>
                  <th>{t('users.table.seller')}</th>
                  <th>{t('users.table.banned')}</th>
                  <th>{t('users.table.createdAt')}</th>
                  <th>{t('users.table.actions')}</th>
                </tr>
              </thead>
              <tbody>
                {users.map(user => (
                  <tr key={user.id}>
                    <td>
                      {editingId === user.id ? (
                        <span>{user.email}</span>
                      ) : (
                        <span>{user.email}</span>
                      )}
                    </td>
                    <td>
                      {editingId === user.id ? (
                        <div className="edit-inline-row">
                          <input
                            type="text"
                            value={editFormData.firstName}
                            onChange={(e) => setEditFormData({ ...editFormData, firstName: e.target.value })}
                            placeholder={t('users.editForm.firstName')}
                            disabled={editLoading}
                          />
                          <input
                            type="text"
                            value={editFormData.lastName}
                            onChange={(e) => setEditFormData({ ...editFormData, lastName: e.target.value })}
                            placeholder={t('users.editForm.lastName')}
                            disabled={editLoading}
                          />
                        </div>
                      ) : (
                        <span>{user.firstName} {user.lastName}</span>
                      )}
                    </td>
                    <td>
                      {editingId === user.id ? (
                        <input
                          type="text"
                          value={editFormData.phoneNumber}
                          onChange={(e) => setEditFormData({ ...editFormData, phoneNumber: e.target.value })}
                          placeholder={t('users.editForm.phoneNumber')}
                          disabled={editLoading}
                        />
                      ) : (
                        <span>{user.phoneNumber || '-'}</span>
                      )}
                    </td>
                    <td>
                      <span className={user.idVerified ? 'badge badge-success' : 'badge badge-danger'}>
                        {user.idVerified ? 'Yes' : 'No'}
                      </span>
                    </td>
                    <td>
                      <span className={user.seller ? 'badge badge-success' : 'badge badge-secondary'}>
                        {user.seller ? 'Yes' : 'No'}
                      </span>
                    </td>
                    <td>
                      <span className={user.banned ? 'badge badge-danger' : 'badge badge-secondary'}>
                        {user.banned ? 'Yes' : 'No'}
                      </span>
                    </td>
                    <td>{new Date(user.createdAt).toLocaleDateString()}</td>
                    <td className="actions-cell">
                      {editingId === user.id ? (
                        <div className="action-buttons-edit">
                          <button
                            className="btn btn-sm btn-success"
                            onClick={() => handleEditSubmit(user.id)}
                            disabled={editLoading}
                          >
                            {editLoading ? t('common.loading') : t('common.save')}
                          </button>
                          <button
                            className="btn btn-sm btn-secondary"
                            onClick={() => {
                              setEditingId(null)
                              setEditFormData({ firstName: '', lastName: '', phoneNumber: '' })
                              setEditError(null)
                            }}
                            disabled={editLoading}
                          >
                            {t('common.cancel')}
                          </button>
                        </div>
                      ) : (
                        <div className="action-buttons">
                          {canEdit && !user.banned && (
                            <button
                              className="btn btn-sm btn-secondary"
                              onClick={() => {
                                setEditingId(user.id)
                                setEditFormData({
                                  firstName: user.firstName,
                                  lastName: user.lastName,
                                  phoneNumber: user.phoneNumber || '',
                                })
                                setEditError(null)
                              }}
                            >
                              {t('users.editButton')}
                            </button>
                          )}
                          {canDelete && !user.banned && (
                            <button
                              className="btn btn-sm btn-danger"
                              onClick={() => setDeleteConfirmId(user.id)}
                            >
                              {t('users.deleteButton')}
                            </button>
                          )}
                          {canDelete && !user.banned && (
                            <button
                              className="btn btn-sm btn-warning"
                              onClick={() => setBanConfirmId(user.id)}
                            >
                              {t('users.banButton')}
                            </button>
                          )}
                        </div>
                      )}
                    </td>

                    {/* Edit error message in row */}
                    {editingId === user.id && editError && (
                      <tr>
                        <td colSpan="8" className="error-row">
                          <span className="error-message">{editError}</span>
                        </td>
                      </tr>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Delete Confirmation Modal */}
        {deleteConfirmId && (
          <div className="modal-overlay" onClick={() => setDeleteConfirmId(null)}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h3>{t('users.deleteConfirm')}</h3>
              <div className="modal-actions">
                <button
                  className="btn btn-danger"
                  onClick={() => handleDelete(deleteConfirmId)}
                  disabled={deleteLoading}
                >
                  {deleteLoading ? t('common.loading') : t('common.delete')}
                </button>
                <button
                  className="btn btn-secondary"
                  onClick={() => setDeleteConfirmId(null)}
                  disabled={deleteLoading}
                >
                  {t('common.cancel')}
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Ban Confirmation Modal */}
        {banConfirmId && (
          <div className="modal-overlay" onClick={() => setBanConfirmId(null)}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h3>{t('users.banConfirm')}</h3>
              {banError && <div className="error-message">{banError}</div>}
              <div className="form-group">
                <label>{t('users.banReason')}</label>
                <textarea
                  value={banReason}
                  onChange={(e) => setBanReason(e.target.value)}
                  placeholder={t('users.banReasonPlaceholder')}
                  disabled={banLoading}
                  rows="4"
                />
              </div>
              <div className="modal-actions">
                <button
                  className="btn btn-danger"
                  onClick={() => handleBan(banConfirmId)}
                  disabled={banLoading || !banReason.trim()}
                >
                  {banLoading ? t('common.loading') : t('users.banButton')}
                </button>
                <button
                  className="btn btn-secondary"
                  onClick={() => {
                    setBanConfirmId(null)
                    setBanReason('')
                    setBanError(null)
                  }}
                  disabled={banLoading}
                >
                  {t('common.cancel')}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
