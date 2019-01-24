import React, { Component } from 'react';
import { get, post } from 'axios';
import { Redirect, Link } from 'react-router-dom';
import { debounce as _debounce } from 'lodash';
import { validate as validateEmail } from 'email-validator';
import PasswordValidator from 'password-validator';
import { updateState } from '../modules/utils';

import AnimatedButton from './AnimatedButton';
import Logo from '../icons/Logo';
import CustomForm from './_CustomForm';

import '../styles/SignUp.css';

class SignUp extends Component {
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
          email: '',
          password: ''
        }
      }
    };
    
    this.form = Object.keys(this.state.form.error).map(input => ({
      type: input,
      onFocus: this.handleFocus,
      onBlur: this.handleBlur,
      onChange: this.handleChange,
    }));

    this.serverUrl = 'http://localhost:4567';
    this.passwordValidationSchema = new PasswordValidator().is().min(8);
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
   * Ensures the submitted password matches the specifications provided in the
   * constructor.
   */
  validatePassword = password => {
    return this.passwordValidationSchema.validate(password);
  }

  /**
   * Sets the error state to 'Required' when a user leaves an input field empty.
   * 
   * @param {string} key - The input field name passed from CustomForm which has
   * received the onBlur event.
   * @param {string} value - The value of the field passed from CustomForm having
   * received the onBlur event.
   */
  handleBlur = (key, value) => {
    if (value.length < 1) {
      this.setErrorState(key, 'Required');
    }
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
    let formIsSubmittable = true;
    Object.keys(state).forEach(key => {
      // TODO
      // The following block of conditionals should be re-written to simplify and
      // increase readability. Before doing so, consider alternative methods for
      // verifying whether the form is submittable.
      if (key.localeCompare('password') && state[key].length < 0) {
        formIsSubmittable = false;
      } else if (state[key].length === 0) {
        formIsSubmittable = false;
      }
      if (this.state.form.error[key].length > 0) {
        formIsSubmittable = false;
      }
    })
    this.setSelfState('isSubmittable', formIsSubmittable);
  }

  /**
   * Invoked on every change event no matter the input type. In other words, this
   * function is passed to each input field in the CustomForm component and ensures
   * error messages are cleared and submit is disabled until the appropriate error
   * checking is complete.
   * 
   * Currently, a momentary flicker will appear on the submit button when a user
   * deletes a character from a completely filled-in form. The optimal fix will
   * may require a substantial rewrite.
   * 
   * @param {string} key - The name of the input field which receieved the change
   * event.
   * @param {string} value - The value of the input field which received the change
   * event.
   * @param {Object} state - The key:value pairs of all input field names and their
   * respective values.
   */
  handleChange = (key, value, state) => {
    this.setErrorState(key, '');
    this.setSelfState('isSubmittable', false);
    this.handleChangeAtTarget[key](key, value, state);
  }

  /**
   * An object containing debounce functions which correspond to the input fields.
   * These functions are to be passed to the CustomForm.onChange prop (perhaps
   * from within another function; e.g. handleChange).
   */
  handleChangeAtTarget = {

    /**
     * Interfaces with the server to determine whether or not the current username
     * is available. Also sets specific constraints on the length of the username.
     * 
     * @param {string} key - The name of the input field (will be 'username' in this
     * case).
     * @param {string} username - The value of the username input field.
     * @param {state} state - The key-value pairs of all input fields the moment
     * the change event was observed.
     */
    username: this.debounce((key, username, state) => {
      if (username.length > 4) {
        get(`${this.serverUrl}/users/${username}`)
          .then(res => this.setErrorState(key, ''))
          .catch(err => {
            if (err.response.status === 409) {
              this.setErrorState(key, 'Sorry, this username is already taken.');
            } else {
              alert('Oops. Something is not right. Please come back later.')
            }
          })
      } else if (username.length > 1) {
        this.setErrorState(key, 'Almost there... Your username needs to be at least 5 characters.')
      }
      // Currently required to be called within the debounce function to avoid
      // enabling the submit button before an error message can be resolved.
      this.setSubmittableState(state);
    }),

    /**
     * Sets constraints on the email input field.
     * 
     * @param {string} key - The name of the input field (will be 'email' in this
     * case).
     * @param {string} username - The value of the username input field.
     * @param {state} state - The key-value pairs of all input fields the moment
     * the change event was observed.
     */
    email: this.debounce((key, email, state) => {
      if (!validateEmail(email)) {
        this.setErrorState(key, 'Yikes! This is not a valid email');
      }
      // Currently required to be called within the debounce function to avoid
      // enabling the submit button before an error message can be resolved.
      this.setSubmittableState(state);
    }),
    
    /**
     * Sets constraints on the password input field.
     * 
     * @param {string} key - The name of the input field (will be 'password' in this
     * case).
     * @param {string} username - The value of the username input field.
     * @param {state} state - The key-value pairs of all input fields the moment
     * the change event was observed.
     */
    password: this.debounce((key, password, state) => {
      if (!this.validatePassword(password)) {
        this.setErrorState(key, 'Please enter a valid password.')
      }
      // Currently required to be called within the debounce function to avoid
      // enabling the submit button before an error message can be resolved.
      this.setSubmittableState(state);
    })
  }

  /**
   * Posts the CustomForm's state to the server. That is, each input type
   * followed by its current value. A 200 response from the server will trigger
   * a redirect to the uploads component.
   * 
   * @param {Object} state - The key-value pairs of all input fields when the
   * onClick event was received.
   */
  handleSubmit = state => {
    post(`${this.serverUrl}/signup`, state)
      .then(res => {
        console.log(res);
        this.setSelfState('shouldRedirect', true)
      })
      .catch(err => {
        console.log(err.response);
        alert('Oops. Something is not right. Please try again later.')
      })
  }
  
  renderCustomForm = () => {
    return (
      <CustomForm
        classnames={['SignUp__CustomForm']}
        structure={this.form}
        error={this.state.form.error}
        onSubmit={this.handleSubmit}
        childProps={{
          classnames: ['SignUp__submit', 'SignUp__animation'],
          isDisabled: !this.state.self.isSubmittable
        }}>
        <AnimatedButton />
      </CustomForm>
    )
  }

  renderRedirectContent = () => {
    return (
      <div className='SignUp__content-container--redirect'>
        <p className='SignUp__redirect-text'>Already have an account?
          <Link
            className='SignUp__redirect-link'
            to={'/login'}>
            LOGIN
            </Link>
        </p>
      </div>
    )
  }

  render = () => {
    let result;
    if (this.state.self.shouldRedirect) {
      result = (<Redirect push to={'/upload'} />);
    } else {
      result = (
        <div className='SignUp'>
          <div className='SignUp__content-container--custom-form' >
            <Logo classnames={['SignUp__logo']} />
            {this.renderCustomForm()}
            {this.renderRedirectContent()}
          </div>
        </div>)
    }
    return result;
  }
}

export default SignUp;
