import { apiRequest } from './httpClient.js'
import { API_BASE_URL } from './config.js'

const ORDER_API_BASE = '/orders'
const BASKET_API_BASE = '/basket'
const PAYMENT_API_BASE = '/payment'

export async function getBasket() {
  return apiRequest(`${BASKET_API_BASE}`)
}

export async function addToBasket(productId, quantity) {
  return apiRequest(`${BASKET_API_BASE}/items`, {
    method: 'POST',
    json: {
      productId,
      quantity,
    },
  })
}

export async function updateBasketItemQuantity(itemId, quantity) {
  return apiRequest(`${BASKET_API_BASE}/items/${itemId}`, {
    method: 'PUT',
    json: {
      quantity,
    },
  })
}

export async function removeFromBasket(itemId) {
  return apiRequest(`${BASKET_API_BASE}/items/${itemId}`, {
    method: 'DELETE',
  })
}

export async function getSavedCard() {
  try {
    const response = await fetch(`${API_BASE_URL}${PAYMENT_API_BASE}/saved-card`, {
      method: 'GET',
      credentials: 'include',
    })

    if (response.status === 204) {
      return null
    }

    if (!response.ok) {
      if (response.status === 401) {
        return null
      }
      throw new Error('Failed to fetch saved card')
    }

    return response.json()
  } catch (error) {
    console.error('Error fetching saved card:', error)
    return null
  }
}

export async function checkout(cardNumber, expiryMonth, expiryYear, cvv, cardHolderName, saveCard, couponCode) {
  return apiRequest(`${ORDER_API_BASE}/checkout`, {
    method: 'POST',
    json: {
      payment: {
        cardNumber,
        expiryMonth,
        expiryYear,
        cvv,
        cardHolderName,
        saveCard: saveCard ?? false,
      },
      couponCode: couponCode ?? null,
    },
  })
}

export async function getOrder(orderId) {
  return apiRequest(`${ORDER_API_BASE}/${orderId}`)
}

export async function getMyOrders(page = 0, size = 10) {
  return apiRequest(`${ORDER_API_BASE}?page=${page}&size=${size}`)
}
