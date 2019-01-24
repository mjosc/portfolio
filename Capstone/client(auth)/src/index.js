import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router } from 'react-router-dom'

import App from './components/App';

import './index.css';

/**
 * Entry point. Wraps the application context in a BrowserRouter object to 
 * facilitate the appearance of a multi-page application.
 */
ReactDOM.render((
  <Router>
    <App />
  </Router>
), document.getElementById('root'))
