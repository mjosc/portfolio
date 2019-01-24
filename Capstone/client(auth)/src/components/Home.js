import React, { Component } from 'react';
import classNames from 'classnames';
import { uniqueId } from '../modules/utils';

import AnimatedButton from './AnimatedButton';

import '../styles/Home.css';

/**
 * Represents the application's landing page. This is the first component
 * containing any UI/UX directly accessible to the consumer.
 */
class Home extends Component {
  constructor(props) {
    super(props);
    this.id = uniqueId(); // Helpful for React 'key' attribute assignment.
  }

  renderButtons = () => {
    const description = [{
      classnames: [classNames('Home__btn--left')],
      value: 'LOGIN',
      redirect: { shouldRedirect: true, to: '/login' }
    }, {
      classnames: [
        classNames('Home__btn--right'), 
        classNames('Home__btn-animation--right')
      ],
      value: 'SIGN UP',
      redirect: { shouldRedirect: true, to: '/signup' }
    }];
    return description.map(desc => (
      <AnimatedButton 
        {...desc }
        key={this.id + desc.value}
      />
    ));
  }

  render = () => {
    return (
      <div className='Home'>
        <div className='Home__content-container--text'>
          <h1 className='Home__header--title'>PROJECT REDWOOD</h1>
          <h2 className='Home__header--tagline'>Your data. Experienced.</h2>
          <div className='Home__content-container--btn'>
            {this.renderButtons()}
          </div>
        </div>
      </div>
    )
  }
}

export default Home;
