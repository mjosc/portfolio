import React, { Component } from 'react';
import { debounce as _debounce } from 'lodash';
import { updateState } from '../modules/utils';
import { Link, Redirect } from 'react-router-dom';
import { post } from 'axios';

import CustomForm from './_CustomForm';
import Animatedbutton from './AnimatedButton';
import Logo from '../icons/Logo';

import '../styles/Login.css';

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      self: {
        shouldRedirect: false,
        isSubmittable: false
      },
      form: {
        error: {
          username: '',
          password: ''
        }
      }
    }
    this.form = Object.keys(this.state.form.error).map(input => ({
      type: input,
      onFocus: this.handleFocus,
      onBlur: this.handleBlur,
      onChange: this.handleChange
    }))
    this.serverUrl = 'http://localhost:4567';
  }

  componentWillUnmount = () => {

  }

  /**
   * Updates this component's error state without having to provide the full path
   * to the error object.
   * 
   * For example, this.setErrorState('username', 'awesome_user') will assign
   * state.from.error.username to 'awesome_user'.
   * 
   * This is a wrapper for utils.updateState.
   * 
   * @param {string} key - The key within this.state to which a value will be reassigned. 
   *              This may be a nested key.
   * @param {string} value - The value to be assigned to key.
   */
  setErrorState = (key, value) => {
    updateState(this, `form.error.${key}`, value);
  }

  /**
   * Updates this component's self state without having to provide the full path
   * to the error object.
   * 
   * For example, this.setSelfState('isSubmittable', true) will assign
   * state.self.isSubmittable to true.
   * 
   * This is a wrapper for utils.updateState.
   * 
   * @param {string} key - The key within this.state to which a value will be
   * reassigned. This may be a nested key.
   * @param {string} value - The value to be assigned to the key.
   */
  setSelfState = (key, value) => {
    updateState(this, `self.${key}`, value)
  }

  /**
   * Delays the invocation of the provided function until 500ms have passed
   * without additional invocation attempts.
   * 
   * This is a wrapper for lodash.debounce.
   * 
   * @param {Object} func - The function to be invoked.
   */
  debounce = func => _debounce(func, 500);

  /**
   * Not implemented.
   */
  handleBlur = () => {
    // TODO
  }

  /**
  * Not implemented.
  */
  handleFocus = (key, value) => {
    // TODO
  }

/**
 * Determines whether the form is submittable. That is, whether the form has
 * valid data. Updates this.state.self.isSubmittable accordingly.
 * 
 * @param {Object} state - The updated state passed from CustomForm (equivalent
 * to reading CustomForm.state after invoking componentDidUpdate).
 */
  setSubmittableState = state => {
    let isSubmittable = true;
    Object.keys(state).forEach(key => {
      if (state[key].length === 0) {
        isSubmittable = false;
      }
    })
    this.setSelfState('isSubmittable', isSubmittable);
  }

  handleChange = (key, value, state) => {
    this.setSubmittableState(state);
  };

  handleSubmit = this.debounce(state => {
    post(`${this.serverUrl}/login`, state)
      .then(res => {
        console.log(res);
        this.setSelfState('shouldRedirect', true)
      })
      .catch(err => {
        if (err.response.status === 401) {
          this.setErrorState('password', 'Sorry. Username and password do not match.');
        } else {
          alert('Oops. Something is not right. Please come back later.');
        }
      })
    });
  
  renderCustomForm = () => {
    return (
      <CustomForm
        classnames={['Login__CustomForm']}
        structure={this.form}
        error={this.state.form.error}
        onSubmit={this.handleSubmit}
        childProps={{
          classnames: ['Login__submit', 'Login__animation'],
          isDisabled: !this.state.self.isSubmittable
        }}
      >
        <Animatedbutton />
      </CustomForm>
    )
  }

  renderRedirectContent = () => {
    return (
      <div className='Login__content-container--redirect'>
        <p className='Login__redirect-text'>Don't have an account?
          <Link
            className='Login__redirect-link'
            to={'/signup'}>
            SIGN UP
            </Link>
        </p>
      </div>
    )
  }

  render = () => {
    let result;
    if (this.state.self.shouldRedirect) {
      result = (<Redirect push to={'/data'} />);
    } else {
      result = (
        <div className='Login'>
        <div className='Login__content-container--custom-form' >
          <Logo classnames={['Login__logo']} />
          {this.renderCustomForm()}
          {this.renderRedirectContent()}
        </div>
      </div>
      );
    }
    return result;
  }
}

export default Login;
