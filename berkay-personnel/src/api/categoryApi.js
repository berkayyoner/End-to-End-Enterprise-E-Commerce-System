import { apiRequest } from './httpClient.js'

// Note: Product service endpoints are at /api/products/, not /api/personnel/
const PRODUCTS_API = '/api/products'

// Main Category endpoints
export function listMainCategories(locale = 'tr') {
  return apiRequest(`${PRODUCTS_API}/categories/main?locale=${locale}`)
}

export function getMainCategory(id, locale = 'tr') {
  return apiRequest(`${PRODUCTS_API}/categories/main/${id}?locale=${locale}`)
}

export function createMainCategory(data) {
  return apiRequest(`${PRODUCTS_API}/categories/main`, {
    method: 'POST',
    json: data,
  })
}

export function updateMainCategory(id, data) {
  return apiRequest(`${PRODUCTS_API}/categories/main/${id}`, {
    method: 'PUT',
    json: data,
  })
}

export function deleteMainCategory(id) {
  return apiRequest(`${PRODUCTS_API}/categories/main/${id}`, {
    method: 'DELETE',
  })
}

// Sub Type endpoints
export function listSubTypes(locale = 'tr') {
  return apiRequest(`${PRODUCTS_API}/categories/sub?locale=${locale}`)
}

export function getSubType(id, locale = 'tr') {
  return apiRequest(`${PRODUCTS_API}/categories/sub/${id}?locale=${locale}`)
}

export function getSubTypesByMainCategory(mainCategoryId, locale = 'tr') {
  return apiRequest(`${PRODUCTS_API}/categories/sub/by-main/${mainCategoryId}?locale=${locale}`)
}

export function createSubType(data) {
  return apiRequest(`${PRODUCTS_API}/categories/sub`, {
    method: 'POST',
    json: data,
  })
}

export function updateSubType(id, data) {
  return apiRequest(`${PRODUCTS_API}/categories/sub/${id}`, {
    method: 'PUT',
    json: data,
  })
}

export function deleteSubType(id) {
  return apiRequest(`${PRODUCTS_API}/categories/sub/${id}`, {
    method: 'DELETE',
  })
}

// Inner Type endpoints
export function listInnerTypes(locale = 'tr') {
  return apiRequest(`${PRODUCTS_API}/categories/inner?locale=${locale}`)
}

export function getInnerType(id, locale = 'tr') {
  return apiRequest(`${PRODUCTS_API}/categories/inner/${id}?locale=${locale}`)
}

export function getInnerTypesBySubType(subTypeId, locale = 'tr') {
  return apiRequest(`${PRODUCTS_API}/categories/inner/by-sub/${subTypeId}?locale=${locale}`)
}

export function createInnerType(data) {
  return apiRequest(`${PRODUCTS_API}/categories/inner`, {
    method: 'POST',
    json: data,
  })
}

export function updateInnerType(id, data) {
  return apiRequest(`${PRODUCTS_API}/categories/inner/${id}`, {
    method: 'PUT',
    json: data,
  })
}

export function deleteInnerType(id) {
  return apiRequest(`${PRODUCTS_API}/categories/inner/${id}`, {
    method: 'DELETE',
  })
}
