const PRODUCTS_API_BASE = import.meta.env.VITE_PRODUCTS_API_BASE_URL ?? 'http://localhost:8085/api/products'

async function productApiRequest(path) {
  const response = await fetch(`${PRODUCTS_API_BASE}${path}`, {
    credentials: 'include',
  })
  if (!response.ok) {
    throw new Error(`Failed to fetch from products API: ${response.statusText}`)
  }
  return response.json()
}

export async function searchProducts({
  q = '',
  mainCategoryId = null,
  subTypeId = null,
  innerTypeId = null,
  minPrice = null,
  maxPrice = null,
  sort = 'Suggested Ranking',
  page = 0,
} = {}) {
  const params = new URLSearchParams()
  if (q) params.append('q', q)
  if (mainCategoryId) params.append('mainCategoryId', mainCategoryId)
  if (subTypeId) params.append('subTypeId', subTypeId)
  if (innerTypeId) params.append('innerTypeId', innerTypeId)
  if (minPrice !== null && minPrice !== '') params.append('minPrice', minPrice)
  if (maxPrice !== null && maxPrice !== '') params.append('maxPrice', maxPrice)
  params.append('sort', sort)
  params.append('page', page)

  return productApiRequest(`/search?${params.toString()}`)
}
