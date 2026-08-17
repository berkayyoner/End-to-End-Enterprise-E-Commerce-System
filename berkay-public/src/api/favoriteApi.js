import { apiRequest } from './httpClient.js'

export async function addToFavorites(productId) {
  return apiRequest(`/products/${productId}/favorite`, {
    method: 'POST',
  })
}

export async function removeFromFavorites(productId) {
  return apiRequest(`/products/${productId}/favorite`, {
    method: 'DELETE',
  })
}

export async function getMyFavorites(locale = 'tr') {
  return apiRequest(`/products/favorites/me?locale=${locale}`, {
    method: 'GET',
  })
}
