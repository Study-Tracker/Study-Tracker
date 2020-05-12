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

export function toggleSidebar() {
  return {
    type: types.SIDEBAR_VISIBILITY_TOGGLE
  };
}

export function showSidebar() {
  return {
    type: types.SIDEBAR_VISIBILITY_SHOW
  };
}

export function hideSidebar() {
  return {
    type: types.SIDEBAR_VISIBILITY_HIDE
  };
}

export function toggleStickySidebar() {
  return {
    type: types.SIDEBAR_STICKY_TOGGLE
  };
}

export function enableStickySidebar() {
  return {
    type: types.SIDEBAR_STICKY_ENABLE
  };
}

export function disableStickySidebar() {
  return {
    type: types.SIDEBAR_STICKY_DISABLE
  };
}
