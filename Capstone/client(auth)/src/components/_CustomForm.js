import React, { Component } from 'react';
import classNames from 'classnames';
import { defineAll, uniqueId, updateState } from '../modules/utils';

import '../styles/CustomForm.css';

/**
 * A reusable form with support for a custom submit button.
 * 
 * The submit button must be provided as the lone child prop and its properties
 * passed via this component's childProps prop.
 * 
 * Error state is rendered from within this component but should be controlled
 * from above.
 * 
 * This is a controlled component.
 */
class CustomForm extends Component {
  constructor(props) {
    super(props);
    this.id = uniqueId();

    const state = { inputs: {} };
    props.structure.forEach((input, index) => {
      state.inputs[this.id + index + input.type] = {
        value: input.value || '',
        hasFocus: false
      }
    });
    this.state = state;

    this.classnames = defineAll({
      arr: props.classnames,
      size: props.structure.length + 1 // + 1 for <form />
    });
  }

  /**
   * Updates the value of the specified input field.
   * 
   * This is a wrapper for utils.updateState and as such, the callback should
   * expect values according to the documentation found in utils.js.
   * 
   * @param {string} key - the input field name.
   * @param {Object} value - the value to be assigned to 'this.state.inputs[key].value'.
   * @param {Object} callback - a function to be called when the state has updated.
   */
  setValueInputState = (key, value, callback) => {
    updateState(this, `inputs.${key}.value`, value, callback);
  }

  /**
   * Updates the focused state of the specified input field.
   * 
   * This is a wrapper for utils.updateState and as such, the callback should
   * expect values according to the documentation found in utils.js.
   * 
   * @param {string} key - the input field name.
   * @param {boolean} value - the value to be assigned to 'this.state.inputs[key].hasFocus'.
   */
  setFocusInputState = (key, value) => {
    updateState(this, `inputs.${key}.hasFocus`, value);
  }

  /**
   * A helper function to remove the id and index assigned to each input field.
   * This is useful for returning the input field name and its corresponding value
   * to the parent component for error rendering, api correspondance, or otherwise.
   * 
   * @param prop - the property from which to remove the id and index.
   */
  removeUniqueId = prop => {
    return prop.substring(this.id.length + 1); // +1 for index (see constructor).
  }

  /**
   * Updates the input field having received a change event and invokes the
   * provided callback function passing to it the name of the input field whose
   * value changed, the updated value, and an object containing all updated field
   * values.
   */
  handleChange = callback => event => {
    // Asyncronous setState may lose reference to event.target.
    const { id, value } = event.target;
    this.setValueInputState(id, value,
      state => {
        const inputs = {};
        Object.keys(state.inputs).forEach(key => {
          inputs[this.removeUniqueId(key)] = state.inputs[key].value;
        });
        callback(this.removeUniqueId(id), value, inputs)
      });
  }

  handleSubmit = () => {
    const state = {};
    Object.keys(this.state.inputs).forEach(key => {
      state[this.removeUniqueId(key)] = this.state.inputs[key].value;
    });
    this.props.onSubmit(state);
  }

  // --------------- -> SignUp.js

  handleBlur = callback => event => {
    const { id, value } = event.target;
    // this.setFocusInputState(id, false);
    callback(this.removeUniqueId(id), value);
  }

  handleFocus = callback => event => {
    const { id, value } = event.target;
    // this.setFocusInputState(id, true);
    callback(this.removeUniqueId(id), value);
  }

  renderInputs = () => {
    return Object.keys(this.state.inputs).map((key, index) => {
      const input = this.props.structure[index];
      return (
        <React.Fragment key={key}>
          <input
            className={classNames('CustomForm__input', this.classnames[index], {
              'hasError': this.props.error[input.type].length > 0
            })}
            id={key}
            type={input.type} // Defaults to 'text' if invalid; Required prop.
            placeholder={input.placeholder || input.type.toUpperCase()}
            value={this.state.inputs[key].value} // Controlled component.
            onChange={this.handleChange(input.onChange || (() => null))} // If not provided, just keep state.
            onBlur={this.handleBlur(input.onBlur)}
            onFocus={this.handleFocus(input.onFocus)}
          />
          <div className={
            classNames(`CustomForm__content-container--error ${this.classnames[index]}--error`)}>
            {this.props.error[input.type]}
          </div>
        </React.Fragment>
      )
    })
  }

  renderSubmit = () => (
    React.cloneElement(
      React.Children.only(this.props.children),
      Object.assign({
        value: 'SUBMIT',
        onClick: this.handleSubmit
      }, this.props.childProps)
    )
  )

  render = () => (
    <form
      className={classNames('CustomForm', this.classnames[0])}>
      {this.renderInputs()}
      {this.renderSubmit()}
    </form>
  )
}

/* 
 * TODO
 *
 * Use default props to uniformly change the conditionals supplied in the above
 * rendering functions. For example, instead of input.onChange || (() => null)),
 * set the callback of the onChange prop to (() => null) inside the defaultProps
 * property.
 */
CustomForm.defaultProps = null;

/*
 * TODO
 * 
 * Use the prop-types library to type-check this component's props.
 */
CustomForm.propTypes = null;


export default CustomForm;
