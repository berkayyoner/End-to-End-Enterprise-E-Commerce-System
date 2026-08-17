import { apiRequest } from './httpClient.js'

export function getSellerProfile() {
  return apiRequest('/sellers/me')
}

export function updateStoreName(storeName) {
  return apiRequest('/sellers/me/store-name', {
    method: 'PUT',
    json: { storeName },
  })
}

export function getPublicSellerProfile(sellerId) {
  return apiRequest(`/sellers/${sellerId}/public-profile`)
}

export function getSellerProducts(sellerId, locale = 'tr') {
  return apiRequest(`/products/by-seller/${sellerId}`, {
    searchParams: { locale },
  })
}

export function createProduct(request) {
  return apiRequest('/products', {
    method: 'POST',
    json: request,
  })
}

export function updateProduct(productId, request) {
  return apiRequest(`/products/${productId}`, {
    method: 'PUT',
    json: request,
  })
}

export function deleteProduct(productId) {
  return apiRequest(`/products/${productId}`, {
    method: 'DELETE',
  })
}
