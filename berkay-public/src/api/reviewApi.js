import { apiRequest } from './httpClient.js'

/**
 * Submit a review for a product (authenticated, verified buyer required)
 */
export async function submitReview(productId, request) {
  return apiRequest(`/products/${productId}/reviews`, {
    method: 'POST',
    json: request,
  })
}

/**
 * Get all reviews for a product (public)
 */
export async function getProductReviews(productId) {
  return apiRequest(`/products/${productId}/reviews`, {
    method: 'GET',
  })
}
