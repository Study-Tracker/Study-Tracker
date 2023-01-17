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
import {Col, Row} from 'react-bootstrap'
import PropTypes from "prop-types";
import StringFieldInput from "./StringFieldInput";
import BooleanFieldInput from "./BooleanFieldInput";
import DateFieldInput from "./DateFieldInput";
import IntegerFieldInput from "./IntegerFieldInput";
import TextFieldInput from "./TextFieldInput";
import DropdownFieldInput from "./DropdownFieldInput";
import FloatFieldInput from "./FloatFieldInput";
import FileFieldInput from "./FileFieldInput";

const CustomFieldCaptureInputList = ({
  fields,
  data,
  handleUpdate,
  errors
}) => {

  let inputs = fields
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
    if (data.hasOwnProperty(f.fieldName)) {
      value = data[f.fieldName];
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
        input = <IntegerFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isInvalid={errors.fields && f.required && !value}
        />;
        break;

      case "FLOAT":
        input = <FloatFieldInput
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

      case "DROPDOWN":
        input = <DropdownFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isInvalid={errors.fields && f.required && !value}
        />;
        break;

      case "FILE":
        input = <FileFieldInput
            field={f}
            value={value}
            handleUpdate={handleUpdate}
            isInvalid={errors.fields && f.required && !value}
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

CustomFieldCaptureInputList.propTypes = {
  fields: PropTypes.array.isRequired,
  data: PropTypes.object.isRequired,
  handleUpdate: PropTypes.func.isRequired,
  errors: PropTypes.object.isRequired
}

export default CustomFieldCaptureInputList;