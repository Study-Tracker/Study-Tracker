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

import {Col, Row} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";

const AssayAttributes = ({attributes}) => {
  let items = [];
  for (let k of Object.keys(attributes)) {
    let v = attributes[k];
    items.push(
        <div key={"assay-attributes-" + k}>
          <h6 className="details-label">{k}</h6>
          <p>{v || "n/a"}</p>
        </div>
    )
  }
  return (
      <Row>
        <Col xs={12}>
          {items}
        </Col>
      </Row>
  )
};

AssayAttributes.propTypes = {
  attributes: PropTypes.object.isRequired
}

export default AssayAttributes;