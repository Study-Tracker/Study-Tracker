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
import PropTypes from "prop-types";
import Select from "react-select";
import {FormGroup} from "../common";

const CustomFieldDefinitionCard = ({
  field,
  index,
  handleFieldUpdate,
  handleRemoveField
}) => {

  const renderDefaultValueInput = () => {
    let control = '';

    // Return empty for some types
    if (field.type === 'FILE' || field.type === 'DATE') {
      return control;
    }

    if (field.type === "BOOLEAN") {
      control = (
          <Select
              options={[
                {value: true, label: "True"},
                {value: false, label: "False"}
              ]}
              className={"react-select-container"}
              classNamePrefix={"react-select"}
              onChange={selected => handleFieldUpdate({"defaultValue": selected.value}, index)}
          />
      )
    }
    else if (field.type === "INTEGER") {
      control = (
          <Form.Control
              type="number"
              value={field.defaultValue}
              onChange={(e) => {
                let value = parseInt(e.target.value, 10);
                handleFieldUpdate({"defaultValue": value}, index)
              }}
          />
      )
    }
    else if (field.type === "FLOAT") {
      control = (
          <Form.Control
              type="number"
              step="any"
              value={field.defaultValue}
              onChange={(e) => {
                let value = parseFloat(e.target.value);
                handleFieldUpdate({"defaultValue": value}, index)
              }}
          />
      )
    }
    else if (field.type === "DROPDOWN") {
      const options = field.dropdownOptions
          ? field.dropdownOptions.split("\n")
          .map(o => { return {value: o, label: o} })
          : [];
      control = (
          <Select
              options={options}
              className={"react-select-container"}
              classNamePrefix={"react-select"}
              onChange={selected => handleFieldUpdate({"defaultValue": selected.value}, index)}
          />
      );
    }
    else {
      control = (
          <Form.Control
              type="text"
              value={field.defaultValue}
              onChange={(e) => handleFieldUpdate(
                  {"defaultValue": e.target.value}, index)}
          />
      );
    }

    return (
        <Col md={6} lg={4}>
          <FormGroup>
            <Form.Label>Default Value</Form.Label>
            {control}
          </FormGroup>
        </Col>
    )

  }

  return (
      <Card className="mb-3 bg-light cursor-grab border">

        <Card.Header className="bg-light pt-0 pb-0 mt-3 d-flex justify-content-between">
          <div className="text-muted text-lg">#{index+1}</div>
          <div className="card-actions">
            <a className="text-danger" title={"Remove field"}
               onClick={() => handleRemoveField(index)}>
              <XCircle className="align-middle mt-3" size={12}/>
            </a>
          </div>
        </Card.Header>

        <Card.Body className="pb-3 pr-3 pl-3 pt-0">
          <Row>

            <Col md={6} lg={4}>
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

            <Col md={6} lg={4}>
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
                  <option value="DROPDOWN">Dropdown</option>
                  <option value="FILE">File</option>
                </Form.Select>
              </FormGroup>
            </Col>

            <Col md={6} lg={4}>
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

            {
              field.type === "DROPDOWN" && (
                    <Col md={6} lg={4}>
                      <FormGroup>
                        <Form.Label>Dropdown Options *</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={4}
                            value={field.dropdownOptions}
                            onChange={(e) => handleFieldUpdate(
                                {"dropdownOptions": e.target.value}, index)}
                        />
                        <Form.Text>
                          Enter one option per line.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                )
            }

            { renderDefaultValueInput() }

            <Col md={6} lg={4}>
              <Form.Check
                  className="mt-4"
                  type="switch"
                  onChange={(e) => {
                    handleFieldUpdate({"required": e.target.checked}, index)
                  }}
                  checked={field.required}
                  label={"Field is required"}
              />
            </Col>

          </Row>
        </Card.Body>
      </Card>
  )
};

CustomFieldDefinitionCard.propTypes = {
  field: PropTypes.object.isRequired,
  index: PropTypes.number.isRequired,
  handleFieldUpdate: PropTypes.func.isRequired,
  handleRemoveField: PropTypes.func.isRequired
}

export default CustomFieldDefinitionCard;