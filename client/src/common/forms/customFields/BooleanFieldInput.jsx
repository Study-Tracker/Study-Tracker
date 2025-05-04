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

import {FormGroup} from "../common";
import {Form} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";

const BooleanFieldInput = ({field, value, handleUpdate}) => {
  return (
      <FormGroup>
        <Form.Check
            id={"assay-field-" + field.fieldName + "-check"}
            type="switch"
            label={field.displayName}
            defaultChecked={value != null ? value : false}
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

BooleanFieldInput.propTypes = {
  field: PropTypes.object.isRequired,
  value: PropTypes.bool,
  handleUpdate: PropTypes.func.isRequired
}

export default BooleanFieldInput;