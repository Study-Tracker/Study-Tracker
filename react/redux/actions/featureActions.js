import * as types from "../constants";

export function setFeatures(features) {
  return {
    type: types.SET_FEATURES,
    payload: features
  }
}