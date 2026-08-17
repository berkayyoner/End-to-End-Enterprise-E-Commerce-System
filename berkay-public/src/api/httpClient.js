import { API_BASE_URL } from './config.js'

const MUTATING_METHODS = new Set(['POST', 'PUT', 'PATCH', 'DELETE'])

export class ApiError extends Error {
  constructor(status, message, fieldErrors) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors ?? null
  }
}

/**
 * auth-service's default SecurityConfig chain issues a fresh CSRF cookie/token pair, and the
 * token gets rotated by Spring Security on every successful authentication (login) - so rather
 * than caching one, every mutating call (other than /register, which the backend explicitly
 * exempts since it precedes any session) fetches a fresh token right before it's used.
 */
async function fetchCsrfToken() {
  const response = await fetch(`${API_BASE_URL}/csrf-token`, { credentials: 'include' })
  if (!response.ok) {
    throw new ApiError(response.status, 'Could not prepare a secure request. Please try again.')
  }
  return response.json()
}

async function parseErrorBody(response) {
  try {
    const body = await response.json()
    if (typeof body.errors === 'string') {
      return new ApiError(response.status, body.errors)
    }
    if (body.errors && typeof body.errors === 'object') {
      const firstMessage = Object.values(body.errors)[0]
      return new ApiError(response.status, firstMessage ?? 'Request failed', body.errors)
    }
    if (typeof body.error === 'string') {
      return new ApiError(response.status, body.error)
    }
    return new ApiError(response.status, 'Request failed')
  } catch {
    return new ApiError(response.status, 'Request failed')
  }
}

/**
 * @param {string} path - relative to API_BASE_URL, e.g. "/login"
 * @param {{method?: string, json?: object, formData?: FormData, skipCsrf?: boolean}} options
 */
export async function apiRequest(path, options = {}) {
  const method = options.method ?? 'GET'
  const headers = {}
  let body

  if (options.json !== undefined) {
    headers['Content-Type'] = 'application/json'
    body = JSON.stringify(options.json)
  } else if (options.formData !== undefined) {
    body = options.formData
  } else if (options.form !== undefined) {
    headers['Content-Type'] = 'application/x-www-form-urlencoded'
    body = new URLSearchParams(options.form).toString()
  }

  if (MUTATING_METHODS.has(method) && !options.skipCsrf) {
    const csrf = await fetchCsrfToken()
    headers[csrf.headerName] = csrf.token
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    credentials: 'include',
    headers,
    body,
  })

  if (!response.ok) {
    throw await parseErrorBody(response)
  }
  if (response.status === 204) {
    return null
  }
  const contentType = response.headers.get('Content-Type') ?? ''
  return contentType.includes('application/json') ? response.json() : null
}
