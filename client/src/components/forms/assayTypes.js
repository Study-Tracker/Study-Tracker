/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import {FormGroup} from "./common";

export const AssayTypeDropdown = ({
  assayTypes,
  selectedType,
  onChange,
  isValid,
  disabled
}) => {

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
            className={"react-select-container "}
            invalid={!isValid}
            classNamePrefix="react-select"
            defaultValue={options.filter(option => {
              return option.value === selectedType
            })}
            isDisabled={disabled}
            options={options}
            onChange={(selected) => {

              console.log(selected);
              const assayType = assayTypes.filter(
                  t => t.id === selected.value)[0];

              const fields = {};
              for (let f of assayType.fields) {
                fields[f.fieldName] = f.type === "BOOLEAN" ? false : null;
              }

              const tasks = [];
              for (let t of assayType.tasks) {
                tasks.push({
                  "label": t.label,
                  "status": t.status,
                  "order": t.order
                });
              }

              onChange({
                "assayType": assayType,
                "tasks": tasks,
                "fields": fields
              });

            }}
        />
        <Form.Text>Select the assay type that best corresponds to the
          experiment to be performed.</Form.Text>
      </FormGroup>
  );

};