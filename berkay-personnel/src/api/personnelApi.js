import { apiRequest } from './httpClient.js'

export function fetchCurrentPersonnel() {
  return apiRequest('/me')
}

export function login(email, password) {
  return apiRequest('/login', {
    method: 'POST',
    form: { username: email, password },
  })
}

export function logout() {
  return apiRequest('/logout', { method: 'POST' })
}
