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
import {FormGroup} from "./common";

export const AssayNotebookTemplatesDropdown = ({
  notebookTemplates,
  selectedTemplate,
  onChange,
}) => {
  const options = notebookTemplates
    .sort((a, b) => {
      if (a.name > b.name) { return 1; }
      if (a.name < b.name) { return -1; }

      return 0;
    })
    .map(({templateId, name}) => {
      return {
        value: templateId,
        label: name,
      };
    });
  const selectedValue = options.find(option => option.value === selectedTemplate);

  return (
    <FormGroup>
      <Form.Label>Notebook File Template</Form.Label>
      <Select
        className="react-select-container"
        classNamePrefix="react-select"

        defaultValue={selectedValue}
        options={options}
        onChange={onChange}
        isClearable={true}
      />
      <Form.Control.Feedback>Select a template for notebook entry.</Form.Control.Feedback>
    </FormGroup>
  );
}