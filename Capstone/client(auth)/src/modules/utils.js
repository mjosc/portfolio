import { concat, get as resolve } from 'lodash';

/**
 * Updates the state of an instance of React.Component. This includes nested
 * state. Simply pass the context (e.g. 'this'), the path within the state object
 * (e.g. 'input.username.value'), the value to be assigned at the given path, and
 * an optional callback function to be invoked after the state has changed.
 * 
 * This is a wrapper for React.Component.setState. It does not yet support read or
 * write operations on the props argument passed through the updater function
 * described in the React documentation.
 * 
 * @param {Object} context - the component object whose state will be updated.
 * @param {string} path - a full-stop separated list of object properties describing
 * the assignment location of the new value.
 * @param {Object} value - the updated value to be assigned at the specified path.
 * @param {Object} [callback = null] - a function to be called when the state has
 * been updated. The updated context.state is provided as the single argument.                 
 */
const updateState = (context, path, value, callback = () => null) => {
  const keys = path.split('.');
  let index = keys.length - 1;
  context.setState(state => {
    let result = value;
    while (index > 0) {
      path = path.substring(0, path.lastIndexOf(keys[index]) - 1);
      result = { ...resolve(state, path), [keys[index--]]: result };
    }
    result = { ...state, [keys[index]]: result };
    return result;
  }, () => callback(context.state));
}

/**
 * Returns an array of length size where all values are either initialized or
 * nullified. If size is less than the length of the passed array, the array will
 * be truncated to size. If any item in the array is undefined, it will be set
 * to null.
 * 
 * @param {Object[]} arr - The array to be returned.
 * @param {number} size - The desired length of the returned array.
 * @returns {Object[]}
 */
const defineAll = ({ arr, size }) => {
  let result;
  if (!arr) {
    result = Array(size).fill(null);
  } else {
    result = [];
    for (let i = 0; i < arr.length; i++) {
      if (i >= size) {
        break; // Ignore all items beyond size (compare to Array.prototype.slice).
      }
      let item = arr[i];
      if (!item) {
        item = null; // Guarantee no undefined items.
      }
      result.push(item);
    }
    // Fill the remaining vacancies, if any.
    result = concat(result, Array(size - result.length).fill(null));
  }
  return result;
}

/**
 * Creates a unique string identifier to be used for dynamically assigning id
 * attributes to similar components throughout the application. This is 
 * particularly useful for generic and reusable components.
 * 
 * Ex. 'id-yejs4idl20skslfq'
 * 
 * This is not a perfect solution and there is a degree of probability a
 * collision will occur. However, for the current size of the application,
 * this should be sufficient.
 * 
 * The implementation was obtained from 
 * frontcoded.com/javascript-create-unique-ids.html
 * 
 * @returns {string}
 */
const uniqueId = () => {
  return 'id-' + Math.random().toString(36).substr(2, 16);
};

/**
 * Assigns each string value in the source array to the destination object.
 * Property assignments of the destination object are overwritten by the same
 * properties of the source.
 * 
 * Pass a customizer to provide destination assignments. Otherwise, all
 * assignments will be nullified.
 * 
 * @param {Object} obj - The destination object.
 * @param {string[]} src - The source array containing property names.
 * @param {Object} customizer - The callback to be applied.
 */
const assignArray = (obj, arr, customizer = () => null) => {
  arr.forEach(prop => {
    obj[prop] = customizer(obj, arr, prop);
  })
  return obj;
}

export {
  updateState,
  defineAll,
  uniqueId,
  assignArray,
}
