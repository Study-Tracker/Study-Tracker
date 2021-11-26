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

import React from 'react';
import {Form} from "react-bootstrap";
import Select from "react-select";
import {statuses} from "../../config/statusConstants";
import {FormGroup} from "./common";

export const StatusDropdown = ({onChange, selected}) => {
  let defaultValue = statuses.IN_PLANNING.value;
  const statusOptions = Object.values(statuses).map(status => {
    if (selected === status.value) {
      defaultValue = status;
    }
    return {
      value: status.value,
      label: status.label
    };
  });

  return (
      <FormGroup>
        <Form.Label>Status *</Form.Label>
        <Select
            className="react-select-container"
            classNamePrefix="react-select"
            options={statusOptions}
            defaultValue={defaultValue}
            onChange={(selected) => onChange({"status": selected.value})}
        />
      </FormGroup>
  );
};