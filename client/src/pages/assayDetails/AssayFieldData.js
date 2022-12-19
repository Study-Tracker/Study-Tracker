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

const createMarkup = (content) => {
  return {__html: content};
};

const AssayFieldData = ({assay}) => {

  let fields = [];
  const assayTypeFields = assay.assayType.fields.sort((a, b) => {
    if (a.id > b.id) {
      return 1;
    } else if (a.id < b.id) {
      return -1;
    } else {
      return 0;
    }
  });
  const assayFields = assay.fields;
  for (let f of assayTypeFields) {
    if (assayFields.hasOwnProperty(f.fieldName)) {

      const value = assayFields[f.fieldName];

      if (["STRING", "INTEGER", "FLOAT"].indexOf(f.type) > -1) {
        fields.push(
            <div key={"assay-field-display-" + f.fieldName}>
              <h6 className="details-label">{f.displayName}</h6>
              <p>{value || 'n/a'}</p>
            </div>
        );
      } else if (f.type === "TEXT") {
        fields.push(
            <div key={"assay-field-display-" + f.fieldName}>
              <h6 className="details-label">{f.displayName}</h6>
              <div dangerouslySetInnerHTML={createMarkup(
                  !!value ? value : 'n/a')}/>
            </div>
        )
      } else if (f.type === "BOOLEAN") {
        fields.push(
            <div key={"assay-field-display-" + f.fieldName}>
              <h6 className="details-label">{f.displayName}</h6>
              <p>{!!value ? "True" : "False"}</p>
            </div>
        );
      } else if (f.type === "DATE") {
        fields.push(
            <div key={"assay-field-display-" + f.fieldName}>
              <h6 className="details-label">{f.displayName}</h6>
              <p>{!!value ? new Date(value).toLocaleString() : 'n/a'}</p>
            </div>
        );
      }

    }
  }

  return (
      <Row>
        <Col xs={12}>
          {fields}
        </Col>
      </Row>
  )

};

AssayFieldData.propTypes = {
  assay: PropTypes.object.isRequired,
}

export default AssayFieldData;