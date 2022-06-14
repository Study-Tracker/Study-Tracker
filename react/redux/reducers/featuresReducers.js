import * as types from '../constants'

const initialState = null;

export default function reducer(state = initialState, actions) {
  switch (actions.type) {
    case types.SET_FEATURES:
      return {
        ...state,
        ...actions.payload
      };

    default:
      return state;
  }

}