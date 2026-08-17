import { apiRequest } from './httpClient.js'

// Note: Seller application endpoints are under /api/auth/personnel/
const SELLER_APPLICATIONS_API = '/api/auth/personnel/seller-applications'

export function listSellerApplications(page = 0, size = 20, status = 'PENDING') {
  return apiRequest(`${SELLER_APPLICATIONS_API}?page=${page}&size=${size}&status=${status}`)
}

export function approveSellerApplication(id) {
  return apiRequest(`${SELLER_APPLICATIONS_API}/${id}/approve`, {
    method: 'POST',
  })
}

export function rejectSellerApplication(id, reason) {
  return apiRequest(`${SELLER_APPLICATIONS_API}/${id}/reject`, {
    method: 'POST',
    json: { reason },
  })
}
