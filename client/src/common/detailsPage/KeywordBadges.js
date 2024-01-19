/*
 * Copyright 2019-2023 the original author or authors.
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
import PropTypes from "prop-types";
import {Badge} from "react-bootstrap";

const KeywordBadges = ({keywords}) => {

  return (
      <div>
        {
          keywords.map((k, i) => {
            return (
                <span className={"me-2"} key={"keyword-" + k.id}>
                  <Badge bg={"info"} className={"pt-1 pb-1 ps-2 pe-2"} pill>
                    {
                        k.category && <span className={"fw-light"}>{k.category}: </span>
                    }
                    <span className={"fw-bolder"}>{k.keyword}</span>
                  </Badge>
                </span>
            )
          })
        }
      </div>
  )
}

KeywordBadges.propTypes = {
  keywords: PropTypes.array.isRequired
}

export default KeywordBadges;