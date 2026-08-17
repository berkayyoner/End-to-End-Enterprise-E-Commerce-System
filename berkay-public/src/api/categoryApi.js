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

export async function getMainCategories() {
  return productApiRequest('/categories/main')
}

export async function getSubTypesByMainCategory(mainCategoryId) {
  return productApiRequest(`/categories/sub/by-main/${mainCategoryId}`)
}

export async function getInnerTypesBySubType(subTypeId) {
  return productApiRequest(`/categories/inner/by-sub/${subTypeId}`)
}
