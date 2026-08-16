import './App.css'
import { useTranslation } from './i18n'

function App() {
  const { t, language, changeLanguage, supportedLanguages } = useTranslation()

  return (
    <section id="center">
      <div>
        <h1>{t('home.title')}</h1>
        <p>{t('home.subtitle')}</p>
        <p>{t('home.comingSoon')}</p>
      </div>
      <div className="ticks">
        {supportedLanguages.map((code) => (
          <button
            key={code}
            type="button"
            className="counter"
            disabled={code === language}
            onClick={() => changeLanguage(code)}
          >
            {code.toUpperCase()}
          </button>
        ))}
      </div>
    </section>
  )
}

export default App
