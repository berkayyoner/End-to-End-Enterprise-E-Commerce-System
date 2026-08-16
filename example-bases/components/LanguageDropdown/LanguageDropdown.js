import { useState } from "react";
import enFlag from "../../assets/flags/en.png";
import trFlag from "../../assets/flags/tr.png";
import "./LanguageDropdown.css";

function LanguageDropdown({ lang, onChange }) {
  const [isOpen, setIsOpen] = useState(false);

  const flagMap = { en: enFlag, tr: trFlag };

  return (
    <div
      className="language-dropdown"
      onMouseEnter={() => setIsOpen(true)}
      onMouseLeave={() => setIsOpen(false)}
    >
      {/* Selected language */}
      <button>
        <img src={flagMap[lang]} alt={lang} className="flag-icon" />
      </button>

      {/* Hover menu */}
      {isOpen && (
        <div className="dropdown-menu">
          {Object.keys(flagMap)
            .filter((code) => code !== lang)
            .map((code) => (
              <div
                key={code}
                className="dropdown-item"
                onClick={() => {
                  onChange(code);
                  setTimeout(() => {
                    window.location.reload(); // Reload the page after the language change to apply translations
                  }, 100);
                }}
              >
                <img src={flagMap[code]} alt={code} className="flag-icon" />
              </div>
            ))}
        </div>
      )}
    </div>
  );
}

export default LanguageDropdown;
