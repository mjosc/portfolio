import React, { Component } from "react";
import { defineAll } from '../modules/utils';
import classNames from 'classnames';

class SvgLogo extends Component {
  constructor(props) {
    super(props);
    this.classnames = defineAll({
      arr: props.classnames,
      size: 2 // svg, g
    })
  }
  render() {
    return (
      <svg
        className={classNames('SvgLogo', this.classnames[0])}
        width="6em"
        height="6em"
        viewBox="0 0 142 153">
        <g
          className={classNames(this.classnames[1])}
          stroke="#195468"
          strokeWidth={4}
          fill="none"
          fillRule="evenodd"
          strokeLinecap="round"
        >
          <path d="M29.773 11.32C11.527 29.447 3.461 55.4 2.171 75.724 1.9 80.013 1.752 89.05 3.327 99.65" />
          <path d="M113.728 87.881c1.983 22.315-12.646 62.669-51.375 62.669-44.432 0-53.326-45.926-51.47-75.001 1.621-25.405 15.104-60.721 48.88-72.606" />
          <path d="M47.696 139.477C19.917 128.23 20.761 85.715 21.39 75.416 23.07 47.846 39.65 7.382 83.757 6.05c12.002-.362 21.95 3.3 29.946 8.49" />
          <path d="M56.54 79.234s-2.236 21.077 6.54 20.694c11.257-.49-6.362-47.964 20.04-50.4 15.568-1.436 25.958 20.795 22.129 51.429-3.306 26.445-24.011 40.54-41.321 41.249a47.456 47.456 0 0 1-6.09-.248" />
          <path d="M39.315 92.894c-1.294-10.421-.446-21.691 1.12-30.486 3.967-22.278 21.617-37.823 44.91-37.823 21.302 0 49.48 27.278 43.561 77.795-1.179 10.062-3.742 19.943-7.73 28.876" />
          <path d="M85.636 68.223s8.239 54.627-20.142 54.627c-13.705 0-20.942-8.9-24.315-20.611M134.188 56.137c5.351 15.095 7.09 34.555 2.434 58.622M80.65 16.002a83.688 83.688 0 0 1 5.18-.38c15.56 0 35.234 11.055 45.852 34.312M34.346 52.085c6.467-19.26 19.767-30.948 39.09-34.968" />
          <path d="M85.084 124.03c-5.347 6.04-12.354 9.362-20.501 9.396-19.34.077-37.577-12.369-34.458-58.678.285-4.228.782-8.23 1.486-12.008M96.227 81.74c.754 14.723-1.718 26.952-6.615 35.813" />
          <path d="M90.65 33.937c15.51 3.279 29.89 21.389 31.26 48.648 1.314 26.122-9.79 59.632-32.298 67.965M63.068 112.05c-12.313 0-17.46-16.683-15.67-38.222C49.075 53.635 60.927 35.62 80 33.456" />
          <path d="M66.944 111.722s16.207-3.374 11.225-40.827c0 0-1.708-11.636 6.57-12.837 8.276-1.201 10.412 16.057 10.412 16.057" />
          <path d="M102.667 51.276C92.155 36.111 76.08 43.35 76.08 43.35c-17.702 6.722-18.416 28.896-18.416 28.896M105.572 55.325s7.236 9.302 8.127 27.53" />
        </g>
      </svg>
    )
  }
}

export default SvgLogo;