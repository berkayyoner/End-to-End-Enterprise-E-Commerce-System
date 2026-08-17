import { apiRequest } from './httpClient.js'

export async function followSeller(sellerId) {
  return apiRequest(`/sellers/${sellerId}/follow`, {
    method: 'POST',
  })
}

export async function unfollowSeller(sellerId) {
  return apiRequest(`/sellers/${sellerId}/follow`, {
    method: 'DELETE',
  })
}

export async function isFollowingSeller(sellerId) {
  return apiRequest(`/sellers/${sellerId}/is-following`, {
    method: 'GET',
  })
}
