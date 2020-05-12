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
  isBoxed: false
};

export default function reducer(state = initialState, actions) {
  switch (actions.type) {
    case types.LAYOUT_BOXED_TOGGLE:
      return {
        ...state,
        isBoxed: !state.isBoxed
      };
    case types.LAYOUT_BOXED_ENABLE:
      return {
        ...state,
        isBoxed: true
      };
    case types.SIDEBAR_STICKY_ENABLE:
    case types.SIDEBAR_STICKY_TOGGLE:
    case types.LAYOUT_BOXED_DISABLE:
      return {
        ...state,
        isBoxed: false
      };

    default:
      return state;
  }
}
