import { apiRequest } from './httpClient.js'

export function fetchCurrentUser() {
  return apiRequest('/me')
}

export function register({ email, password, firstName, lastName, phoneNumber }) {
  return apiRequest('/register', {
    method: 'POST',
    skipCsrf: true,
    json: { email, password, firstName, lastName, phoneNumber: phoneNumber || null },
  })
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

export function fetchMyIdVerification() {
  return apiRequest('/id-verifications/me')
}

export function submitIdVerification({ idNumber, frontPhoto, backPhoto }) {
  const formData = new FormData()
  formData.append('idNumber', idNumber)
  formData.append('frontPhoto', frontPhoto)
  formData.append('backPhoto', backPhoto)
  return apiRequest('/id-verifications', { method: 'POST', formData })
}

export function fetchMySellerApplication() {
  return apiRequest('/seller-applications/me')
}

export function submitSellerApplication({ companyName, taxId, companyPhone, companyAddress }) {
  return apiRequest('/seller-applications', {
    method: 'POST',
    json: { companyName, taxId, companyPhone, companyAddress },
  })
}
