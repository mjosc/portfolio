import React, { Component } from 'react';
import { Route } from 'react-router-dom';

import Home from './Home';
import SignUp from './_SignUp';
import Login from './Login';
import Upload from './Upload';

import PrivateRoute from './PrivateRoute';


import '../styles/App.css';

/**
 * The application context. Is a representation of and a container for all
 * static pages. In other words, dynamically renders those components which
 * would, in a traditional application, be sent from the server as indivdiual
 * pages.
 */
class App extends Component {
  // Ensure the server-side routing redirects to index.html for all non-index
  // paths rendered below. Otherwise, a page refresh will trigger a 404.
  render() {
    return (
      <div className='App'>
        <Route exact path="/" component={Home} />
        <Route path="/login" component={Login} />
        <Route path="/signup" component={SignUp} />
        <PrivateRoute path='/upload' component={Upload} />
      </div>
    );
  }
}

export default App;
