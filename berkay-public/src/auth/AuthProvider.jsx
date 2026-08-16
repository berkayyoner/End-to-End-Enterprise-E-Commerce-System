import { useCallback, useEffect, useMemo, useState } from 'react'
import { fetchCurrentUser, login as loginRequest, logout as logoutRequest, register as registerRequest } from '../api/authApi.js'
import { ApiError } from '../api/httpClient.js'
import { AuthContext } from './context.js'

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [status, setStatus] = useState('loading')

  const refreshUser = useCallback(async () => {
    try {
      const profile = await fetchCurrentUser()
      setUser(profile)
      setStatus('authenticated')
      return profile
    } catch (error) {
      setUser(null)
      setStatus('anonymous')
      if (error instanceof ApiError && error.status !== 401) {
        throw error
      }
      return null
    }
  }, [])

  useEffect(() => {
    refreshUser()
  }, [refreshUser])

  const login = useCallback(async (email, password) => {
    await loginRequest(email, password)
    return refreshUser()
  }, [refreshUser])

  const register = useCallback(async (payload) => {
    await registerRequest(payload)
    return login(payload.email, payload.password)
  }, [login])

  const logout = useCallback(async () => {
    try {
      await logoutRequest()
    } finally {
      setUser(null)
      setStatus('anonymous')
    }
  }, [])

  const value = useMemo(
    () => ({ user, status, isAuthenticated: status === 'authenticated', login, register, logout, refreshUser }),
    [user, status, login, register, logout, refreshUser],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
