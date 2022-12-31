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

TextFieldInput.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.string,
  handleUpdate: PropTypes.func.isRequired,
  isInvalid: PropTypes.bool,
  error: PropTypes.string
}

export default TextFieldInput;