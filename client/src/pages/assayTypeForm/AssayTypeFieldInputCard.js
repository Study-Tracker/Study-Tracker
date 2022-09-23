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
import {Card, Col, Form, Row} from 'react-bootstrap'
import {XCircle} from 'react-feather'
import {FormGroup} from "../../common/forms/common";
import PropTypes from "prop-types";

const AssayTypeFieldInputCard = ({
  field,
  index,
  handleFieldUpdate,
  handleRemoveField
}) => {
  return (
      <Card className="mb-3 bg-light cursor-grab border">

        <Card.Header className="bg-light pt-0 pb-0">
          <div className="card-actions float-end">
            <a className="text-danger" title={"Remove field"}
               onClick={() => handleRemoveField(index)}>
              <XCircle className="align-middle mt-3" size={12}/>
            </a>
          </div>
        </Card.Header>

        <Card.Body className="pb-3 pr-3 pl-3 pt-0">
          <Row>

            <Col md={6} lg={3}>
              <FormGroup>
                <Form.Label>Name *</Form.Label>
                <Form.Control
                    type="text"
                    value={field.displayName}
                    onChange={(e) => {
                      let val = e.target.value;
                      handleFieldUpdate({
                        "displayName": val,
                        "fieldName": val.replace(/[\W]+/g, "_")
                      }, index);
                    }}
                />
              </FormGroup>
            </Col>

            <Col md={6} lg={3}>
              <FormGroup>
                <Form.Label>Type</Form.Label>
                <Form.Select
                    value={field.type}
                    onChange={(e) => {
                      handleFieldUpdate({"type": e.target.value}, index);
                    }}
                >
                  <option value="STRING">Text String</option>
                  <option value="TEXT">Text Block</option>
                  <option value="INTEGER">Integer</option>
                  <option value="FLOAT">Float</option>
                  <option value="BOOLEAN">Boolean</option>
                  <option value="DATE">Date</option>
                </Form.Select>
              </FormGroup>
            </Col>

            <Col md={12} lg={4}>
              <FormGroup>
                <Form.Label>Description</Form.Label>
                <Form.Control
                    as="textarea"
                    rows={4}
                    value={field.description}
                    onChange={(e) => handleFieldUpdate(
                        {"description": e.target.value}, index)}
                />
              </FormGroup>
            </Col>

            <Col md={6} lg={1}>
              <Form.Check
                  type="checkbox"
                  onChange={(e) => {
                    handleFieldUpdate({"required": e.target.checked}, index)
                  }}
                  checked={field.required}
                  label={"Required"}
              />
            </Col>

          </Row>
        </Card.Body>
      </Card>
  )
};

AssayTypeFieldInputCard.propTypes = {
  field: PropTypes.object.isRequired,
  index: PropTypes.number.isRequired,
  handleFieldUpdate: PropTypes.func.isRequired,
  handleRemoveField: PropTypes.func.isRequired
}

export default AssayTypeFieldInputCard;