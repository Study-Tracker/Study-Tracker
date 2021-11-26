import React from 'react';
import {Col, Form, Row} from 'react-bootstrap'
import DatePicker from "react-datepicker";
import {FormGroup} from "./common";

export const AssayTypeFieldCaptureInputList = ({assayType, assayFields, handleUpdate, fieldValidation}) => {

  let inputs = assayType.fields
  .sort((a, b) => {
    if (a.id > b.id) return 1;
    else if (a.id < b.id) return -1;
    else return 0;
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
            isValid={true}
        />;
        break;

      case "TEXT":
        input = <TextFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isValid={true}
        />;
        break;

      case "INTEGER":
        input = <NumberFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isValid={true}
        />;
        break;

      case "FLOAT":
        input = <NumberFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isValid={true}
        />;
        break;

      case "DATE":
        input = <DateFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isValid={true}
        />;
        break;

      case "BOOLEAN":
        input = <BooleanFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
        />;
        break;
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

const StringFieldInput = ({field, value, handleUpdate, isValid, error}) => {
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
            isValid={!isValid}
        />
        <Form.Text>{field.description}</Form.Text>
        <Form.Control.Feedback type={"invalid"}>{error}</Form.Control.Feedback>
      </FormGroup>
  )
};

const TextFieldInput = ({field, value, handleUpdate, isValid, error}) => {
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
            isValid={!isValid}
        />
        <Form.Text>{field.description}</Form.Text>
        <Form.Control.Feedback>{error}</Form.Control.Feedback>
      </FormGroup>
  )
};

const NumberFieldInput = ({field, value, handleUpdate, isValid, error}) => {
  return (
      <FormGroup>
        <Form.Label>{field.displayName}{field.required ? " *" : ""}</Form.Label>
        <Form.Control
            type="number"
            defaultValue={value || null}
            onChange={e => {
              let value = e.target.value;
              if (field.type === "INTEGER") {
                value = parseInt(value);
              } else if (field.type === "FLOAT") {
                value = parseFloat(value);
              }
              handleUpdate({[field.fieldName]: value})
            }}
            isValid={!isValid}
        />
        <Form.Text>{field.description}</Form.Text>
        <Form.Control.Feedback type={"invalid"}>{error}</Form.Control.Feedback>
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

const DateFieldInput = ({field, value, handleUpdate, isValid, error}) => {
  return (
      <FormGroup>
        <Form.Label>{field.displayName}{field.required ? " *" : ""}</Form.Label>
        <DatePicker
            maxlength="2"
            className="form-control"
            invalid={!isValid}
            wrapperClassName="form-control"
            selected={!!value ? new Date(value) : null}
            onChange={(date) => handleUpdate(
                {
                  [field.fieldName]: date
                }
            )}
            isClearable={true}
            dateFormat=" MM / dd / yyyy"
            placeholderText="MM / DD / YYYY"
        />
        <Form.Control.Feedback type={"invalid"}>{error}</Form.Control.Feedback>
        <Form.Text>{field.description}</Form.Text>
      </FormGroup>
  )
};

