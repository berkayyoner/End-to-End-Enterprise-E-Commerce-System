import { useCallback, useEffect, useMemo, useState } from 'react'
import { fetchCurrentPersonnel, login as loginRequest, logout as logoutRequest } from '../api/personnelApi.js'
import { ApiError } from '../api/httpClient.js'
import { AuthContext } from './context.js'

export function AuthProvider({ children }) {
  const [personnel, setPersonnel] = useState(null)
  const [status, setStatus] = useState('loading')

  const refreshPersonnel = useCallback(async () => {
    try {
      const profile = await fetchCurrentPersonnel()
      setPersonnel(profile)
      setStatus('authenticated')
      return profile
    } catch (error) {
      setPersonnel(null)
      setStatus('anonymous')
      if (error instanceof ApiError && error.status !== 401) {
        throw error
      }
      return null
    }
  }, [])

  useEffect(() => {
    refreshPersonnel()
  }, [refreshPersonnel])

  const login = useCallback(async (email, password) => {
    await loginRequest(email, password)
    return refreshPersonnel()
  }, [refreshPersonnel])

  const logout = useCallback(async () => {
    try {
      await logoutRequest()
    } finally {
      setPersonnel(null)
      setStatus('anonymous')
    }
  }, [])

  /**
   * Check if the logged-in personnel has a specific permission.
   * E.g., hasPermission("P4", "EDIT") checks for "P4E" in the permissions array.
   */
  const hasPermission = useCallback((pageCode, capability) => {
    if (!personnel || !personnel.permissions) {
      return false
    }

    if (!capability) {
      // If no capability specified, just check if the page code exists (has view access)
      return personnel.permissions.some(perm => perm.startsWith(pageCode))
    }

    const capabilityLetter = capability[0].toUpperCase()
    const requiredCode = pageCode + capabilityLetter

    return personnel.permissions.some(perm => perm.includes(requiredCode))
  }, [personnel])

  const value = useMemo(
    () => ({ personnel, status, isAuthenticated: status === 'authenticated', login, logout, refreshPersonnel, hasPermission }),
    [personnel, status, login, logout, refreshPersonnel, hasPermission],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
