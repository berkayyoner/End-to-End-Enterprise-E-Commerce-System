import React from 'react';

function Footer() {
  return (
    <footer className="footer">
      <div className="footer-section">
        <h3>Who Are We?</h3>
        <p>Information about Berkay.</p>
      </div>
      <div className="footer-section">
        <h3>Contact</h3>
        <p>Contact information.</p>
      </div>
      <div className="footer-section">
        <h3>Security</h3>
        <p>Security information.</p>
      </div>
      <div className="footer-section">
        <h3>Campaigns</h3>
        <p>Campaign information.</p>
      </div>
      <div className="footer-section">
        <h3>Sell on Berkay</h3>
        <p>Information for sellers.</p>
      </div>
      <div className="footer-section">
        <h3>Live Support</h3>
        <p>Live support information.</p>
      </div>
      <div className="footer-section">
        <h3>How May I Return</h3>
        <p>Return policy information.</p>
      </div>
      <div className="footer-bottom">
        <div className="footer-payment-networks">
          <img src="/path/to/mastercard.png" alt="MasterCard" />
          <img src="/path/to/visa.png" alt="Visa" />
          <img src="/path/to/troy.png" alt="Troy" />
        </div>
        <div className="footer-social-media">
          <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">
            <img src="/path/to/facebook.png" alt="Facebook" />
          </a>
          <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">
            <img src="/path/to/twitter.png" alt="Twitter" />
          </a>
          <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">
            <img src="/path/to/instagram.png" alt="Instagram" />
          </a>
        </div>
        <div className="footer-copyright">
          ©2026 All Right Reserved
        </div>
        <div className="footer-links">
          <a href="/cookie-options">Cookie Options</a>
          <a href="/terms-of-use">Terms of Use</a>
          <a href="/protection-of-personal-data">Protection of Personal Data</a>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
