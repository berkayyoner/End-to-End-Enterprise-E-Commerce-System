import { apiRequest } from './httpClient.js'

const LOGS_API = '/api/logs'

export function listUserLogs(page = 0, size = 20) {
  return apiRequest(`${LOGS_API}?actorType=USER&page=${page}&size=${size}`)
}

export function listPersonnelLogs(page = 0, size = 20) {
  return apiRequest(`${LOGS_API}?actorType=PERSONNEL&page=${page}&size=${size}`)
}
