/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as types from "../constants";

const initialState = {
  isOpen: true,
  isSticky: false
};

export default function reducer(state = initialState, actions) {
  switch (actions.type) {
    case types.SIDEBAR_VISIBILITY_TOGGLE:
      return {
        ...state,
        isOpen: !state.isOpen
      };
    case types.SIDEBAR_VISIBILITY_SHOW:
      return {
        ...state,
        isOpen: true
      };
    case types.SIDEBAR_VISIBILITY_HIDE:
      return {
        ...state,
        isOpen: false
      };

    case types.SIDEBAR_STICKY_TOGGLE:
      return {
        ...state,
        isSticky: !state.isSticky
      };
    case types.SIDEBAR_STICKY_ENABLE:
      return {
        ...state,
        isSticky: true
      };
    case types.LAYOUT_BOXED_ENABLE:
    case types.LAYOUT_BOXED_TOGGLE:
    case types.SIDEBAR_STICKY_DISABLE:
      return {
        ...state,
        isSticky: false
      };

    default:
      return state;
  }
}
