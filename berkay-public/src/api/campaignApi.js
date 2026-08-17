import { apiRequest } from './httpClient.js'

export async function getActiveCampaigns() {
  return apiRequest('/campaigns', {
    method: 'GET',
  })
}

export async function getCampaignById(id) {
  return apiRequest(`/campaigns/${id}`, {
    method: 'GET',
  })
}

export async function getCampaignsByProduct(productId) {
  return apiRequest(`/campaigns/product/${productId}`, {
    method: 'GET',
  })
}
