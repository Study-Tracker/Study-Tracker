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

import {FormGroup} from "../common";
import {Form} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";
import Select from "react-select";

const DropdownFieldInput = ({field, value, handleUpdate, isInvalid, error}) => {
  return (
      <FormGroup>
        <Form.Label>{field.displayName}{field.required ? " *" : ""}</Form.Label>
        <Select
          className={"react-select-container"}
          name={field.fieldName}
          classNamePrefix={"react-select"}
          options={
            field.dropdownOptions.split("\n")
              .map(o => { return {value: o, label: o} })
          }
          onChange={selected => handleUpdate(
              { [field.fieldName]: selected.value }
          )}
          defaultValue={() => {
            if (value) return {value: value, label: value}
            else if (field.defaultValue) return {value: field.defaultValue, label: field.defaultValue}
            else return null
          }}
        />
        <Form.Text>{field.description}</Form.Text>
        <Form.Control.Feedback type={"invalid"}>
          {error || field.displayName + " is required"}
        </Form.Control.Feedback>
      </FormGroup>
  )
};

DropdownFieldInput.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.string,
  handleUpdate: PropTypes.func.isRequired,
  isInvalid: PropTypes.bool,
  error: PropTypes.string
}

export default DropdownFieldInput;