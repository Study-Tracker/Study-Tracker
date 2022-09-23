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
import {Form} from "react-bootstrap";
import {FormGroup} from "./common";
import AsyncSelect from "react-select/async";
import PropTypes from "prop-types";
import axios from "axios";

const NotebookEntryTemplatesDropdown = ({onChange}) => {

  const templateAutocomplete = (input, callback) => {
    axios.get("/api/internal/autocomplete/notebook-entry-template?q=" + input)
    .then(response => {
      const options = response.data
      .sort((a, b) => {
        if (a.name > b.name) {
          return 1;
        }
        if (a.name < b.name) {
          return -1;
        }
        return 0;
      })
      .map(t => {
        return {
          value: t.referenceId,
          label: t.name
        };
      });
      callback(options);
    })
  }

  return (
      <FormGroup>
        <Form.Label>Notebook Entry Template</Form.Label>
        <AsyncSelect
          placeholder={"Select a template..."}
          className={"react-select-container"}
          classNamePrefix="react-select"
          loadOptions={templateAutocomplete}
          onChange={(selected) => {
            console.debug("Selected template: ", selected);
            onChange(selected.value)
          }}
          defaultOptions={true}
        />
        <Form.Control.Feedback>
          Select a template for notebook entry.
        </Form.Control.Feedback>
      </FormGroup>
  );
}

NotebookEntryTemplatesDropdown.propTypes = {
  onChange: PropTypes.func.isRequired,
}

export default NotebookEntryTemplatesDropdown;