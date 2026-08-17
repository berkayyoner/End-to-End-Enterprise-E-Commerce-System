import { apiRequest } from './httpClient.js'

export function fetchCurrentPersonnel() {
  return apiRequest('/me')
}

export function login(email, password) {
  return apiRequest('/login', {
    method: 'POST',
    form: { username: email, password },
  })
}

export function logout() {
  return apiRequest('/logout', { method: 'POST' })
}

// Permission Group endpoints
export function listPermissionGroups() {
  return apiRequest('/personnel/permission-groups')
}

// Personnel management endpoints (P7)
export function listPersonnelAccounts() {
  return apiRequest('/personnel/personnel-accounts')
}

export function createPersonnelAccount(email, password, firstName, lastName, permissionGroupId) {
  return apiRequest('/personnel/personnel-accounts', {
    method: 'POST',
    json: {
      email,
      password,
      firstName,
      lastName,
      permissionGroupId,
    },
  })
}

export function reassignPermissionGroup(personnelId, permissionGroupId) {
  return apiRequest(`/personnel/personnel-accounts/${personnelId}/permission-group`, {
    method: 'PUT',
    json: {
      permissionGroupId,
    },
  })
}
