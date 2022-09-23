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

export const ModelDetailsAttributeList = ({attributes}) => {

  let list = [];
  for (let key of Object.keys(attributes)) {
    list.push(
        <React.Fragment key={'attribute-' + key}>
          <h6 className="details-label">{key}</h6>
          <p>{attributes[key]}</p>
        </React.Fragment>
    )
  }

  return list;

}