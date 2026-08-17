import { apiRequest } from './httpClient.js'

const PRODUCTS_API = '/api/products'

// Personnel product endpoints (admin can edit/delete any seller's product)
export function listProducts(locale = 'tr') {
  return apiRequest(`${PRODUCTS_API}/personnel/products?locale=${locale}`)
}

export function updateProduct(id, data) {
  return apiRequest(`${PRODUCTS_API}/personnel/products/${id}`, {
    method: 'PUT',
    json: data,
  })
}

export function deleteProduct(id) {
  return apiRequest(`${PRODUCTS_API}/personnel/products/${id}`, {
    method: 'DELETE',
  })
}
