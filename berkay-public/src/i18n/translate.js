import tr from './locales/tr.json'
import en from './locales/en.json'
import { DEFAULT_LANGUAGE, SUPPORTED_LANGUAGES } from './config.js'

const dictionaries = { tr, en }

function getByPath(obj, path) {
  return path.split('.').reduce((acc, key) => (acc && acc[key] !== undefined ? acc[key] : undefined), obj)
}

function interpolate(str, params) {
  if (!params || typeof str !== 'string') return str
  return str.replace(/\{(\w+)\}/g, (_, key) => params[key] ?? `{${key}}`)
}

/**
 * translate(lang, "a.b.c", params?) - dot-path lookup, falls back to
 * DEFAULT_LANGUAGE when the language or key is missing, and to the key itself
 * as a last resort so the UI never renders "undefined".
 */
export function translate(lang, key, params) {
  const safeLang = SUPPORTED_LANGUAGES.includes(lang) ? lang : DEFAULT_LANGUAGE
  const dict = dictionaries[safeLang] ?? dictionaries[DEFAULT_LANGUAGE]

  let value = getByPath(dict, key)
  if (value === undefined) {
    const fallback = getByPath(dictionaries[DEFAULT_LANGUAGE], key)
    value = fallback !== undefined ? fallback : key
  }

  return interpolate(value, params)
}
