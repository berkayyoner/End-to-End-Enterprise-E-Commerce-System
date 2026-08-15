import { useState, useEffect } from "react";
import config from "../config.json";

const SUPPORTED_LANGS = config.supportedLanguages;
const DEFAULT_LANG = "en";

export default function useLanguage() {
  const [lang, setLang] = useState(DEFAULT_LANG);

  useEffect(() => {
    // 1. LocalStorage control
    const saved = localStorage.getItem("lang");
    if (saved && SUPPORTED_LANGS.includes(saved)) {
      setLang(saved);
      return;
    }

    // 2. Browser language control
    const browserLang = navigator.language.slice(0, 2); // Example: "tr-TR" → "tr"
    if (SUPPORTED_LANGS.includes(browserLang)) {
      setLang(browserLang);
      localStorage.setItem("lang", browserLang);
      return;
    }

    // 3. Fallback to default
    setLang(DEFAULT_LANG);
    localStorage.setItem("lang", DEFAULT_LANG);
  }, []);

  // Save to localStorage on language change
  const changeLang = (newLang) => {
    if (SUPPORTED_LANGS.includes(newLang)) {
      setLang(newLang);
      localStorage.setItem("lang", newLang);
    }
  };

  return { lang, changeLang };
}
