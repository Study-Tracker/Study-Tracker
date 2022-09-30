/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import {Notyf} from "notyf";

export default React.createContext(
    new Notyf({
      duration: 5000,
      position: {
        x: "right",
        y: "top",
      },
      dismissible: true,
      ripple: true,
      types: [
        {
          type: "default",
          backgroundColor: "#3B7DDD",
          icon: {
            className: "notyf__icon--success",
            tagName: "i",
          },
        },
        {
          type: "success",
          backgroundColor: "#28a745",
          icon: {
            className: "notyf__icon--success",
            tagName: "i",
          },
        },
        {
          type: "warning",
          backgroundColor: "#ffc107",
          icon: {
            className: "notyf__icon--error",
            tagName: "i",
          },
        },
        {
          type: "danger",
          backgroundColor: "#dc3545",
          icon: {
            className: "notyf__icon--error",
            tagName: "i",
          },
        },
      ],
    })
);
