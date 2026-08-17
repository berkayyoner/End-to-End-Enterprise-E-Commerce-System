import { apiRequest } from './httpClient.js'

// Note: Change request endpoints are under /api/products/personnel/
const CHANGE_REQUESTS_API = '/api/products/personnel/category-change-requests'

export function listCategoryChangeRequests(page = 0, size = 20) {
  return apiRequest(`${CHANGE_REQUESTS_API}?page=${page}&size=${size}`)
}

export function approveCategoryChangeRequest(id) {
  return apiRequest(`${CHANGE_REQUESTS_API}/${id}/approve`, {
    method: 'POST',
  })
}

export function rejectCategoryChangeRequest(id, reason) {
  return apiRequest(`${CHANGE_REQUESTS_API}/${id}/reject`, {
    method: 'POST',
    json: { reason },
  })
}
