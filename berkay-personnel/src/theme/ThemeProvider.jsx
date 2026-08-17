import { useCallback, useEffect, useMemo, useState } from 'react'
import { DEFAULT_THEME, STORAGE_KEY, SUPPORTED_THEMES } from './config.js'
import { ThemeContext } from './context.js'

export function ThemeProvider({ children }) {
  const [theme, setTheme] = useState(DEFAULT_THEME)

  // Load theme from localStorage on mount
  useEffect(() => {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored && SUPPORTED_THEMES.includes(stored)) {
      setTheme(stored)
      document.documentElement.setAttribute('data-theme', stored)
    } else {
      document.documentElement.setAttribute('data-theme', DEFAULT_THEME)
    }
  }, [])

  const changeTheme = useCallback((newTheme) => {
    if (SUPPORTED_THEMES.includes(newTheme)) {
      setTheme(newTheme)
      localStorage.setItem(STORAGE_KEY, newTheme)
      document.documentElement.setAttribute('data-theme', newTheme)
    }
  }, [])

  const toggleTheme = useCallback(() => {
    changeTheme(theme === 'light' ? 'dark' : 'light')
  }, [theme, changeTheme])

  const value = useMemo(() => ({ theme, changeTheme, toggleTheme, supportedThemes: SUPPORTED_THEMES }), [theme, changeTheme, toggleTheme])

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
}
