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
import {FormFeedback, FormGroup, Label} from "reactstrap";
import Select from "react-select";
import {assayTypes} from "../../config/assayTypeConstants";

export const AssayTypeDropdown = ({selectedType, onChange, isValid}) => {

  const options = Object.values(assayTypes)
  .map(type => {
    return {
      value: type.value,
      label: type.label
    };
  });

  return (
      <FormGroup>
        <Label>Assay Type *</Label>
        <Select
            className={"react-select-container "}
            invalid={!isValid}
            classNamePrefix="react-select"
            defaultValue={options.filter(option => {
              return option.value === selectedType
            })}
            options={options}
            onChange={(selected) => {
              console.log(selected);
              onChange({"assayType": selected.value});
            }}
        />
        <FormFeedback>Select the assay type that best corresponds to the
          experiment to be performed.</FormFeedback>
      </FormGroup>
  );

};