import React from 'react';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import './App.css';

function App() {
  return (
    <div className="App">
      <Navbar />
      <header className="App-header">
        <p>
          Welcome to Berkay Public Frontend!
        </p>
      </header>
      <Footer />
    </div>
  );
}

export default App;
