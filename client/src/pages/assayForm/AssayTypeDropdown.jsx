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

import React from "react";
import Select from "react-select";
import {Form} from 'react-bootstrap';
import {FormGroup} from "../../common/forms/common";
import PropTypes from "prop-types";

const AssayTypeDropdown = ({
  assayTypes,
  selectedType,
  onChange,
  isInvalid,
  disabled
}) => {

  const handleAssayTypeChange = (selected) => {

    console.debug("Selected assay type", selected);
    const assayType = assayTypes.filter(
        t => t.id === selected.value)[0];

    const fields = {};
    for (let f of assayType.fields) {
      if (f.type === "BOOLEAN") {
        fields[f.fieldName] = f.defaultValue === "true" || false;
      }
      else if (f.type === "INTEGER") {
        fields[f.fieldName] = parseInt(f.defaultValue, 10);
      }
      else if (f.type === "FLOAT") {
        fields[f.fieldName] = parseFloat(f.defaultValue);
      }
      else {
        fields[f.fieldName] = f.defaultValue || null;
      }
    }

    const tasks = [];
    for (let t of assayType.tasks) {
      const taskFields = t.fields ? t.fields.map(f => {
        return {
          ...f,
          id: null
        }
      }) : []
      tasks.push({
        "label": t.label,
        "status": t.status,
        "order": t.order,
        "fields":taskFields
      });
    }

    onChange({
      "assayType": assayType,
      "tasks": tasks,
      "fields": fields
    });

  };

  const options = assayTypes
  .sort((a, b) => {
    if (a.name > b.name) {
      return 1;
    } else if (a.name < b.name) {
      return -1;
    } else {
      return 0;
    }
  })
  .map(type => {
    return {
      value: type.id,
      label: type.name
    };
  });

  return (
      <FormGroup>
        <Form.Label>Assay Type *</Form.Label>
        <Select
            className={"react-select-container " + (isInvalid ? "is-invalid" : "")}
            invalid={isInvalid}
            classNamePrefix="react-select"
            value={options.filter(option => {
              return option.value === selectedType
            })}
            isDisabled={disabled}
            options={options}
            onChange={handleAssayTypeChange}
        />
        <Form.Control.Feedback type={"invalid"}>
          You must select an assay type.
        </Form.Control.Feedback>
        <Form.Text>
          Select the assay type that best corresponds to the
          experiment to be performed.
        </Form.Text>
      </FormGroup>
  );

};

AssayTypeDropdown.propTypes = {
  assayTypes: PropTypes.array.isRequired,
  selectedType: PropTypes.string,
  onChange: PropTypes.func.isRequired,
  isInvalid: PropTypes.bool,
  disabled: PropTypes.bool
};

export default AssayTypeDropdown;