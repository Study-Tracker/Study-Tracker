import React from 'react';
import {
  Col,
  CustomInput,
  FormFeedback,
  FormGroup,
  FormText,
  Input,
  Label,
  Row
} from 'reactstrap'
import DatePicker from "react-datepicker";

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
      <Row form>
        {inputs}
      </Row>
  )
};

// Field type inputs

const StringFieldInput = ({field, value, handleUpdate, isValid, error}) => {
  return (
      <FormGroup>
        <Label>{field.displayName}{field.required ? " *" : ""}</Label>
        <Input
            type="text"
            defaultValue={value || ''}
            onChange={e => handleUpdate(
                {
                  [field.fieldName]: e.target.value
                }
            )}
            valid={!isValid}
        />
        <FormText>{field.description}</FormText>
        <FormFeedback>{error}</FormFeedback>
      </FormGroup>
  )
};

const TextFieldInput = ({field, value, handleUpdate, isValid, error}) => {
  return (
      <FormGroup>
        <Label>{field.displayName}{field.required ? " *" : ""}</Label>
        <Input
            type="textarea"
            size="3"
            defaultValue={value || ''}
            onChange={e => handleUpdate(
                {
                  [field.fieldName]: e.target.value
                }
            )}
            valid={!isValid}
        />
        <FormText>{field.description}</FormText>
        <FormFeedback>{error}</FormFeedback>
      </FormGroup>
  )
};

const NumberFieldInput = ({field, value, handleUpdate, isValid, error}) => {
  return (
      <FormGroup>
        <Label>{field.displayName}{field.required ? " *" : ""}</Label>
        <Input
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
            invalid={!isValid}
        />
        <FormText>{field.description}</FormText>
        <FormFeedback>{error}</FormFeedback>
      </FormGroup>
  )
};

const BooleanFieldInput = ({field, value, handleUpdate}) => {
  return (
      <FormGroup>
        <CustomInput
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
        <FormText>{field.description}</FormText>
      </FormGroup>
  )
};

const DateFieldInput = ({field, value, handleUpdate, isValid, error}) => {
  return (
      <FormGroup>
        <Label>{field.displayName}{field.required ? " *" : ""}</Label>
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
        <FormFeedback>{error}</FormFeedback>
        <FormText>{field.description}</FormText>
      </FormGroup>
  )
};

