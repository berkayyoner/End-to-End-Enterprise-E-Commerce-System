import { apiRequest } from './httpClient.js'

/**
 * Ask a question about a product (authenticated)
 */
export async function askQuestion(productId, request) {
  return apiRequest(`/products/${productId}/questions`, {
    method: 'POST',
    json: request,
  })
}

/**
 * Answer a question about a product (authenticated, seller-owned product only)
 */
export async function answerQuestion(productId, questionId, request) {
  return apiRequest(`/products/${productId}/questions/${questionId}/answer`, {
    method: 'POST',
    json: request,
  })
}

/**
 * Get all questions for a product (public)
 */
export async function getProductQuestions(productId) {
  return apiRequest(`/products/${productId}/questions`, {
    method: 'GET',
  })
}
