import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import LanguageDropdown from './LanguageDropdown';
import { useLanguage } from '../../hooks/useLanguage';

function NavBar({ selectedButton }) {
  const [highlightedButton, setHighlightedButton] = useState(null);
  const [isMyAccountOpen, setIsMyAccountOpen] = useState(false);
  const { lang, changeLang } = useLanguage();

  console.log("DEBUG LANG:", lang);

  useEffect(() => {
    setHighlightedButton(selectedButton);
  }, [selectedButton]);

  const handleMyAccountClick = (event) => {
    event.preventDefault();
    setIsMyAccountOpen(!isMyAccountOpen);
  };

  return (
    <div className="navbar">
      <div className="navbar-logo">
        <Link to="/">
          <img src="/logo.png" alt="Berkay Logo" />
        </Link>
      </div>
      <div className="navbar-search">
        <input type="text" placeholder="Search products..." />
        <button>Search</button>
      </div>
      <div className="navbar-buttons">
        <Link
          to="/my-account"
          className={highlightedButton === 'my-account' ? 'highlighted' : ''}
          onClick={handleMyAccountClick}
        >
          My Account
        </Link>
        <Link to="/my-favorites" className={highlightedButton === 'my-favorites' ? 'highlighted' : ''}>
          My Favorites
        </Link>
        <Link to="/my-basket" className={highlightedButton === 'my-basket' ? 'highlighted' : ''}>
          My Basket
        </Link>
        <button className="navbar-mode-toggle">Light/Dark Mode</button>
        <LanguageDropdown lang={lang} onChange={changeLang} />
      </div>
      {isMyAccountOpen && (
        <div className="navbar-my-account-dropdown">
          <Link to="/my-account/orders">All My Orders</Link>
          <Link to="/my-account/reviews">My Reviews</Link>
          <Link to="/my-account/discount-coupons">My Discount Coupons</Link>
          <Link to="/my-account/seller-messages">Seller Messages</Link>
          <Link to="/my-account/user-info">My User Information</Link>
          <Link to="/logout">Log Out</Link>
        </div>
      )}
    </div>
  );
}

export default NavBar;
