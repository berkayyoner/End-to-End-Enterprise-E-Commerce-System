import { API_BASE_URL } from './config.js'

/**
 * Get all currently active coupons (public, no authentication required)
 */
export async function getActiveCoupons() {
  const response = await fetch(`${API_BASE_URL}/orders/coupons`)
  if (!response.ok) {
    throw new Error(`Failed to fetch coupons: ${response.statusText}`)
  }
  return response.json()
}
