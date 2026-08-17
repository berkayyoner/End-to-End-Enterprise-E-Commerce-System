import { apiRequest } from './httpClient'

export async function listUsers(page = 0, size = 20) {
  return apiRequest(`/api/auth/personnel/users?page=${page}&size=${size}`, {
    method: 'GET',
  })
}

export async function editUser(userId, firstName, lastName, phoneNumber) {
  return apiRequest(`/api/auth/personnel/users/${userId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      firstName,
      lastName,
      phoneNumber,
    }),
  })
}

export async function deleteUser(userId) {
  return apiRequest(`/api/auth/personnel/users/${userId}`, {
    method: 'DELETE',
  })
}

export async function banUser(userId, reason) {
  return apiRequest(`/api/auth/personnel/users/${userId}/ban`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ reason }),
  })
}

export async function listBannedUsers(page = 0, size = 20) {
  return apiRequest(`/api/auth/personnel/users/banned?page=${page}&size=${size}`, {
    method: 'GET',
  })
}
