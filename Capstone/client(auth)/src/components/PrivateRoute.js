import React, { Component } from 'react';
import { Route, Redirect } from 'react-router-dom';
import { get } from 'axios';

import '../styles/PrivateRoute.css';

const serverUrl = 'http://localhost:4567';

/**
 * A simple Route wrapper redirecting to Login if the user is not authenticated.
 */
class PrivateRoute extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      isLoading: true,
      isAuthenticated: false
    }
  }

  componentDidMount() {
    get(`${serverUrl}/users/authenticate`)
      .then(res => {
        this.setState({
          isLoading: false,
          isAuthenticated: true
        })
      })
      .catch(err => console.log(err.response));
  }

  render() {
    const { component: Component, ...rest } = this.props;
    return (
      <Route {...rest} render={props =>
        this.state.isAuthenticated ? (
          <Component {...props} />
        ) : (
            this.state.isLoading ? (
              <div className='PrivateRoute__content-container'>
                <h1 className='PrivateRoute__loading'>LOADING</h1>
              </div>
            ) : (
                <Redirect push to='/login' />
              )
          )
      } />
    )
  }
}

export default PrivateRoute;