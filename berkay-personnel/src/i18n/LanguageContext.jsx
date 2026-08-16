import { useCallback, useEffect, useMemo, useState } from 'react'
import { DEFAULT_LANGUAGE, STORAGE_KEY, SUPPORTED_LANGUAGES } from './config.js'
import { LanguageContext } from './context.js'
import { translate } from './translate.js'

function detectInitialLanguage() {
  if (typeof window === 'undefined') {
    return DEFAULT_LANGUAGE
  }

  const saved = window.localStorage.getItem(STORAGE_KEY)
  if (saved && SUPPORTED_LANGUAGES.includes(saved)) {
    return saved
  }

  const browserLanguage = window.navigator.language?.slice(0, 2)
  if (browserLanguage && SUPPORTED_LANGUAGES.includes(browserLanguage)) {
    return browserLanguage
  }

  return DEFAULT_LANGUAGE
}

export function I18nProvider({ children }) {
  const [language, setLanguage] = useState(detectInitialLanguage)

  useEffect(() => {
    window.localStorage.setItem(STORAGE_KEY, language)
    document.documentElement.lang = language
  }, [language])

  const changeLanguage = useCallback((nextLanguage) => {
    if (SUPPORTED_LANGUAGES.includes(nextLanguage)) {
      setLanguage(nextLanguage)
    }
  }, [])

  const value = useMemo(
    () => ({
      language,
      changeLanguage,
      supportedLanguages: SUPPORTED_LANGUAGES,
      t: (key, params) => translate(language, key, params),
    }),
    [language, changeLanguage],
  )

  return <LanguageContext.Provider value={value}>{children}</LanguageContext.Provider>
}
