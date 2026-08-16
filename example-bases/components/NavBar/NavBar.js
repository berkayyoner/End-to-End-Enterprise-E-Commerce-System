import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { translate } from "../../utils/translate";
import LanguageDropdown from "../LanguageDropdown/LanguageDropdown";
import useLanguage from "../../hooks/useLanguage";
import "./NavBar.css";
import logo from "../../assets/images/logo.png";
import styled from "@emotion/styled";

function NavBar({ selectedButton }) {
  const [highlightedButton, setHighlightedButton] = useState(null);
  const { lang, changeLang } = useLanguage();

  console.log("DEBUG LANG:", lang);

  useEffect(() => {
    setHighlightedButton(selectedButton);
  }, [selectedButton]);

  return (
    <div className="navbar">
      <div className="navbar-logo">
        <img src={logo} alt="Logo" className="navbar-logo" />
      </div>

      <ul className="navbar-links">
        <li className={highlightedButton === "home" ? "navbar-highlighted" : "navbar-list-item"}>
          <Link className="navbar-link" to="/">{translate(lang, "navBar.home")}</Link>
        </li>
        <li className={highlightedButton === "projects" ? "navbar-highlighted" : "navbar-list-item"}>
          <Link className="navbar-link" to="/projects">{translate(lang, "navBar.projects")}</Link>
        </li>
        <li className={highlightedButton === "login" ? "navbar-highlighted" : "navbar-list-item"}>
          <Link className="navbar-link" to="/login">{translate(lang, "navBar.login")}</Link>
        </li>
      </ul>

      {/* Language Selection */}
      <LanguageDropdown lang={lang} onChange={changeLang} />
    </div>
  );
}

export default NavBar;
