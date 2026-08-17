import { apiRequest } from './httpClient.js'

// Note: ID verification endpoints are under /api/auth/personnel/
const ID_APPLICATIONS_API = '/api/auth/personnel/id-verifications'

export function listIdApplications(page = 0, size = 20, status = 'PENDING') {
  return apiRequest(`${ID_APPLICATIONS_API}?page=${page}&size=${size}&status=${status}`)
}

export function approveIdApplication(id) {
  return apiRequest(`${ID_APPLICATIONS_API}/${id}/approve`, {
    method: 'POST',
  })
}

export function rejectIdApplication(id, reason) {
  return apiRequest(`${ID_APPLICATIONS_API}/${id}/reject`, {
    method: 'POST',
    json: { reason },
  })
}
