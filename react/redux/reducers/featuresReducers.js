import * as types from "../constants";

const initialState = null;

export default function (state = initialState, action) {
  switch (action.type) {
    case types.SET_FEATURES:
      return {
        ...state,
        ...action.payload
      };

    default:
      return state;
  }
}