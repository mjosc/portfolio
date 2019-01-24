import React, { Component } from 'react';
import { Redirect } from 'react-router-dom';
import classNames from 'classnames';
import { defineAll } from '../modules/utils';

import '../styles/AnimatedButton.css'

/**
 * An animated button where, for most cases the default animation may be
 * overridden using the appropriate classnames. This component may be used to
 * simply render a new view (via react-router-dom) or, due to its core
 * functionality as an input element of type submit, notify a form component of
 * a user submission.
 * 
 * Support for non-form onClick callbacks may be added at a later time.
 */
class AnimatedButton extends Component {

  constructor(props) {
    super(props);

    this.state = {
      isHovered: false,
      isClicked: false,
      shouldRedirect: false
    }

    this.classnames = defineAll({
      arr: props.classnames,
      size: 3 // The number of elements contained in this component.
    })
  }

  handleMouseOver = () => {
    this.setState(state => ({
      isHovered: !state.isHovered
    }));
  }

  handleClick = event => {
    event.preventDefault();
    const redirect = this.props.redirect;
    this.setState({
      isClicked: true,
      shouldRedirect: redirect ? redirect.shouldRedirect : false
    });
    
    const onClick = this.props.onClick;
    if (onClick) {
      onClick();
    }
  }

  render() {
    let result;
    if (this.state.shouldRedirect) {
      result = (<Redirect push to={this.props.redirect.to} />);
    } else {
      result = (
        <div className={classNames('AnimatedButton', this.classnames[0])}>
        <span className={classNames('hoverable', this.classnames[1], {
          'hovered': this.state.isHovered,
          'unhovered': !this.state.isHovered,
        })} />
        <input
          className={classNames('button', this.classnames[2], {
              'disabled': this.props.isDisabled || false,
              'clicked': this.state.isClicked,
              'unclicked': !this.state.isClicked,
            })}
          onMouseOver={this.handleMouseOver}
          onMouseOut={this.handleMouseOver}
          onClick={this.handleClick}
          type='submit'
          value={this.props.value || 'SUBMIT'}
        />
      </div>
      );
    }
    return result;
  }
}

export default AnimatedButton;
