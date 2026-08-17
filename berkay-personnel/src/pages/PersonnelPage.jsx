import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from '../i18n'
import { useAuth } from '../auth/useAuth'
import {
  listPersonnelAccounts,
  createPersonnelAccount,
  reassignPermissionGroup,
  listPermissionGroups,
} from '../api/personnelApi'
import { ApiError } from '../api/httpClient'
import '../styles/personnel.css'

export function PersonnelPage() {
  const { t } = useTranslation()
  const { hasPermission } = useAuth()

  // Personnel list state
  const [personnel, setPersonnel] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  // Permission groups
  const [permissionGroups, setPermissionGroups] = useState([])
  const [groupsLoading, setGroupsLoading] = useState(false)

  // Create form state
  const [createFormOpen, setCreateFormOpen] = useState(false)
  const [createFormData, setCreateFormData] = useState({
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    permissionGroupId: '',
  })
  const [createError, setCreateError] = useState(null)
  const [createLoading, setCreateLoading] = useState(false)

  // Reassign form state
  const [reassigningId, setReassigningId] = useState(null)
  const [reassignGroupId, setReassignGroupId] = useState('')
  const [reassignError, setReassignError] = useState(null)
  const [reassignLoading, setReassignLoading] = useState(false)

  // Load personnel list
  const loadPersonnel = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await listPersonnelAccounts()
      setPersonnel(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err instanceof ApiError ? err.message : t('common.error'))
    } finally {
      setLoading(false)
    }
  }, [t])

  // Load permission groups
  const loadPermissionGroups = useCallback(async () => {
    setGroupsLoading(true)
    try {
      const data = await listPermissionGroups()
      setPermissionGroups(Array.isArray(data) ? data : [])
    } catch {
      // Non-critical error, silently fail
      setPermissionGroups([])
    } finally {
      setGroupsLoading(false)
    }
  }, [])

  // Initial load
  useEffect(() => {
    loadPersonnel()
    loadPermissionGroups()
  }, [loadPersonnel, loadPermissionGroups])

  // Handle create form submission
  const handleCreateSubmit = async (e) => {
    e.preventDefault()
    setCreateError(null)
    setCreateLoading(true)

    try {
      if (!createFormData.email || !createFormData.password || !createFormData.firstName ||
          !createFormData.lastName || !createFormData.permissionGroupId) {
        setCreateError(t('personnel.createForm.requiredFields'))
        setCreateLoading(false)
        return
      }

      await createPersonnelAccount(
        createFormData.email,
        createFormData.password,
        createFormData.firstName,
        createFormData.lastName,
        parseInt(createFormData.permissionGroupId)
      )

      setCreateFormData({
        email: '',
        password: '',
        firstName: '',
        lastName: '',
        permissionGroupId: '',
      })
      setCreateFormOpen(false)
      await loadPersonnel()
    } catch (err) {
      setCreateError(err instanceof ApiError ? err.message : t('common.error'))
    } finally {
      setCreateLoading(false)
    }
  }

  // Handle reassign permission group
  const handleReassignSubmit = async (personnelId) => {
    if (!reassignGroupId) {
      setReassignError(t('personnel.reassignForm.selectGroup'))
      return
    }

    setReassignError(null)
    setReassignLoading(true)

    try {
      await reassignPermissionGroup(personnelId, parseInt(reassignGroupId))
      setReassigningId(null)
      setReassignGroupId('')
      await loadPersonnel()
    } catch (err) {
      setReassignError(err instanceof ApiError ? err.message : t('common.error'))
    } finally {
      setReassignLoading(false)
    }
  }

  // Check permissions
  const canView = hasPermission('P7', 'VIEW')
  const canCreate = hasPermission('P7', 'ADD')
  const canEdit = hasPermission('P7', 'EDIT')

  if (!canView) {
    return <div className="personnel-page"><p>{t('common.accessDenied')}</p></div>
  }

  return (
    <div className="personnel-page">
      <div className="page-header">
        <h1>{t('personnel.title')}</h1>
        {canCreate && (
          <button
            className="btn btn-primary"
            onClick={() => setCreateFormOpen(!createFormOpen)}
          >
            {createFormOpen ? t('common.cancel') : t('personnel.createButton')}
          </button>
        )}
      </div>

      {/* Create Form */}
      {canCreate && createFormOpen && (
        <div className="form-section">
          <h2>{t('personnel.createForm.title')}</h2>
          {createError && <div className="error-message">{createError}</div>}

          <form onSubmit={handleCreateSubmit}>
            <div className="form-group">
              <label>{t('personnel.createForm.email')}</label>
              <input
                type="email"
                value={createFormData.email}
                onChange={(e) => setCreateFormData({ ...createFormData, email: e.target.value })}
                placeholder={t('personnel.createForm.emailPlaceholder')}
                disabled={createLoading}
              />
            </div>

            <div className="form-group">
              <label>{t('personnel.createForm.password')}</label>
              <input
                type="password"
                value={createFormData.password}
                onChange={(e) => setCreateFormData({ ...createFormData, password: e.target.value })}
                placeholder={t('personnel.createForm.passwordPlaceholder')}
                disabled={createLoading}
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>{t('personnel.createForm.firstName')}</label>
                <input
                  type="text"
                  value={createFormData.firstName}
                  onChange={(e) => setCreateFormData({ ...createFormData, firstName: e.target.value })}
                  placeholder={t('personnel.createForm.firstNamePlaceholder')}
                  disabled={createLoading}
                />
              </div>

              <div className="form-group">
                <label>{t('personnel.createForm.lastName')}</label>
                <input
                  type="text"
                  value={createFormData.lastName}
                  onChange={(e) => setCreateFormData({ ...createFormData, lastName: e.target.value })}
                  placeholder={t('personnel.createForm.lastNamePlaceholder')}
                  disabled={createLoading}
                />
              </div>
            </div>

            <div className="form-group">
              <label>{t('personnel.createForm.permissionGroup')}</label>
              <select
                value={createFormData.permissionGroupId}
                onChange={(e) => setCreateFormData({ ...createFormData, permissionGroupId: e.target.value })}
                disabled={createLoading || groupsLoading}
              >
                <option value="">{t('personnel.createForm.selectGroup')}</option>
                {permissionGroups.map(group => (
                  <option key={group.id} value={group.id}>{group.name}</option>
                ))}
              </select>
            </div>

            <button
              type="submit"
              className="btn btn-success"
              disabled={createLoading}
            >
              {createLoading ? t('common.loading') : t('personnel.createForm.submit')}
            </button>
          </form>
        </div>
      )}

      {/* Personnel List */}
      <div className="personnel-list-section">
        <h2>{t('personnel.listTitle')}</h2>

        {loading && <p>{t('common.loading')}</p>}
        {error && <div className="error-message">{error}</div>}

        {!loading && personnel.length === 0 && (
          <p className="empty-message">{t('personnel.emptyList')}</p>
        )}

        {!loading && personnel.length > 0 && (
          <div className="personnel-table">
            <table>
              <thead>
                <tr>
                  <th>{t('personnel.table.email')}</th>
                  <th>{t('personnel.table.name')}</th>
                  <th>{t('personnel.table.permissionGroup')}</th>
                  <th>{t('personnel.table.createdAt')}</th>
                  {canEdit && <th>{t('personnel.table.actions')}</th>}
                </tr>
              </thead>
              <tbody>
                {personnel.map(person => (
                  <tr key={person.id}>
                    <td>{person.email}</td>
                    <td>{person.firstName} {person.lastName}</td>
                    <td>
                      {reassigningId === person.id ? (
                        <div className="reassign-inline">
                          <select
                            value={reassignGroupId}
                            onChange={(e) => setReassignGroupId(e.target.value)}
                            disabled={reassignLoading}
                          >
                            <option value="">{t('personnel.reassignForm.selectGroup')}</option>
                            {permissionGroups.map(group => (
                              <option key={group.id} value={group.id}>{group.name}</option>
                            ))}
                          </select>
                          <button
                            className="btn btn-sm btn-success"
                            onClick={() => handleReassignSubmit(person.id)}
                            disabled={reassignLoading}
                          >
                            {t('personnel.reassignForm.confirm')}
                          </button>
                          <button
                            className="btn btn-sm btn-secondary"
                            onClick={() => {
                              setReassigningId(null)
                              setReassignGroupId('')
                              setReassignError(null)
                            }}
                            disabled={reassignLoading}
                          >
                            {t('common.cancel')}
                          </button>
                        </div>
                      ) : (
                        <div className="permission-group-display">
                          <span>{person.permissionGroupName}</span>
                          {canEdit && (
                            <button
                              className="btn btn-sm btn-secondary"
                              onClick={() => {
                                setReassigningId(person.id)
                                setReassignGroupId(person.permissionGroupId)
                                setReassignError(null)
                              }}
                            >
                              {t('personnel.reassignForm.changeButton')}
                            </button>
                          )}
                        </div>
                      )}
                    </td>
                    <td>{new Date(person.createdAt).toLocaleDateString()}</td>
                    {canEdit && (
                      <td>
                        {reassigningId === person.id && reassignError && (
                          <span className="error-message" style={{ fontSize: '0.85em' }}>{reassignError}</span>
                        )}
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
