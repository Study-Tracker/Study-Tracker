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
import {Col, Form, Row} from 'react-bootstrap'
import DatePicker from "react-datepicker";
import {FormGroup} from "./common";

export const AssayTypeFieldCaptureInputList = ({
  assayType,
  assayFields,
  handleUpdate,
  errors
}) => {

  let inputs = assayType.fields
  .sort((a, b) => {
    if (a.id > b.id) {
      return 1;
    } else if (a.id < b.id) {
      return -1;
    } else {
      return 0;
    }
  })
  .map(f => {

    let value = null;
    if (assayFields.hasOwnProperty(f.fieldName)) {
      value = assayFields[f.fieldName];
    }

    let input = null;

    switch (f.type) {
      case "STRING":
        input = <StringFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isInvalid={errors.fields && f.required && !value}
        />;
        break;

      case "TEXT":
        input = <TextFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isInvalid={errors.fields && f.required && !value}
        />;
        break;

      case "INTEGER":
        input = <NumberFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isInvalid={errors.fields && f.required && !value}
        />;
        break;

      case "FLOAT":
        input = <NumberFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isInvalid={errors.fields && f.required && !value}
        />;
        break;

      case "DATE":
        input = <DateFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isInvalid={errors.fields && f.required && !value}
        />;
        break;

      case "BOOLEAN":
        input = <BooleanFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
        />;
        break;

      default:
        input = <StringFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isInvalid={errors.fields && f.required && !value}
        />;

    }

    return (
        <Col key={"field-" + f.fieldName + "-key"} sm={12} md={6}>
          {input}
        </Col>
    );

  });

  return (
      <Row>
        {inputs}
      </Row>
  )
};

// Field type inputs

const StringFieldInput = ({field, value, handleUpdate, isInvalid, error}) => {
  return (
      <FormGroup>
        <Form.Label>{field.displayName}{field.required ? " *" : ""}</Form.Label>
        <Form.Control
            type="text"
            defaultValue={value || ''}
            onChange={e => handleUpdate(
                {
                  [field.fieldName]: e.target.value
                }
            )}
            isInvalid={isInvalid}
        />
        <Form.Text>{field.description}</Form.Text>
        <Form.Control.Feedback type={"invalid"}>{error || field.displayName + " is required"}</Form.Control.Feedback>
      </FormGroup>
  )
};

const TextFieldInput = ({field, value, handleUpdate, isInvalid, error}) => {
  return (
      <FormGroup>
        <Form.Label>{field.displayName}{field.required ? " *" : ""}</Form.Label>
        <Form.Control
            as="textarea"
            rows={3}
            defaultValue={value || ''}
            onChange={e => handleUpdate(
                {
                  [field.fieldName]: e.target.value
                }
            )}
            isInvalid={isInvalid}
        />
        <Form.Text>{field.description}</Form.Text>
        <Form.Control.Feedback type={"invalid"}>
          {error || field.displayName + " is required"}
        </Form.Control.Feedback>
      </FormGroup>
  )
};

const NumberFieldInput = ({field, value, handleUpdate, isInvalid, error}) => {
  return (
      <FormGroup>
        <Form.Label>{field.displayName}{field.required ? " *" : ""}</Form.Label>
        <Form.Control
            type="number"
            defaultValue={value || null}
            onChange={e => {
              let value = e.target.value;
              if (field.type === "INTEGER") {
                value = parseInt(value, 10);
              } else if (field.type === "FLOAT") {
                value = parseFloat(value);
              }
              handleUpdate({[field.fieldName]: value})
            }}
            isInvalid={isInvalid}
        />
        <Form.Text>{field.description}</Form.Text>
        <Form.Control.Feedback type={"invalid"}>
          {error || field.displayName + " is required"}
        </Form.Control.Feedback>
      </FormGroup>
  )
};

const BooleanFieldInput = ({field, value, handleUpdate}) => {
  return (
      <FormGroup>
        <Form.Check
            id={"assay-field-" + field.fieldName + "-check"}
            type="checkbox"
            label={field.displayName}
            checked={value === true}
            onChange={e => handleUpdate(
                {
                  [field.fieldName]: e.target.checked
                }
            )}
        />
        <Form.Text>{field.description}</Form.Text>
      </FormGroup>
  )
};

const DateFieldInput = ({field, value, handleUpdate, isInvalid, error}) => {
  return (
      <FormGroup>
        <Form.Label>{field.displayName}{field.required ? " *" : ""}</Form.Label>
        <DatePicker
            maxlength="2"
            className="form-control"
            invalid={isInvalid}
            wrapperClassName="form-control"
            // selected={!!value ? new Date(value) : null}
            selected={value}
            onChange={(date) => handleUpdate(
                {
                  [field.fieldName]: date.getTime()
                }
            )}
            isClearable={true}
            dateFormat=" MM / dd / yyyy"
            placeholderText="MM / DD / YYYY"
        />
        <Form.Control.Feedback type={"invalid"}>
          {error || field.displayName + " is required"}
        </Form.Control.Feedback>
        <Form.Text>{field.description}</Form.Text>
      </FormGroup>
  )
};

