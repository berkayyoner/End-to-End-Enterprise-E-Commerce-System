const PRODUCTS_API_BASE = import.meta.env.VITE_PRODUCTS_API_BASE_URL ?? 'http://localhost:8085/api/products'

async function productApiRequest(path, headers = {}) {
  const response = await fetch(`${PRODUCTS_API_BASE}${path}`, {
    credentials: 'include',
    headers,
  })
  if (!response.ok) {
    throw new Error(`Failed to fetch from products API: ${response.statusText}`)
  }
  return response.json()
}

export async function getProductDetail(productId, locale = 'tr') {
  return productApiRequest(`/${productId}/detail?locale=${locale}`)
}
