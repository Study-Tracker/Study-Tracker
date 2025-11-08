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

import React, { useState } from "react";
import { Col, Form, Row } from "react-bootstrap";
import {FormGroup} from "./common";
import AsyncSelect from "react-select/async";
import PropTypes from "prop-types";
import axios from "axios";
import { useQuery } from "@tanstack/react-query";
import LoadingMessage from "@/common/structure/LoadingMessage";
import { DismissableAlert } from "@/common/errors";
import {
  BenchlingFieldInput
} from "@/common/forms/BenchlingEntryTemplateFieldInputs";


const NotebookEntryTemplatesDropdown = ({onChange}) => {

  const [selectedTemplate, setSelectedTemplate] = useState(null);

  const templateAutocomplete = (input, callback) => {
    axios.get("/api/internal/autocomplete/notebook/entry-template?q=" + input)
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
          value: t.id,
          label: t.name,
          obj: t,
        };
      });
      callback(options);
    })
  }

  const { data: template, isLoading, error } = useQuery({
    queryKey: ['notebookEntryTemplate', selectedTemplate],
    queryFn: () => axios
    .get(`/api/internal/integrations/benchling/entry-templates/${selectedTemplate}`)
    .then(res => res.data),
    enabled: !!selectedTemplate,
  });

  return (
    <>
      <Row>
        <Col md={6}>
          <FormGroup>
            <Form.Label>Notebook Entry Template</Form.Label>
            <AsyncSelect
              placeholder={"Select a template..."}
              className={"react-select-container"}
              classNamePrefix="react-select"
              loadOptions={templateAutocomplete}
              onChange={(selected) => {
                console.debug("Selected template: ", selected);
                onChange("notebookTemplateId", selected.value || "");
                onChange("notebookTemplateFields", {});
                setSelectedTemplate(selected.value);
              }}
              defaultOptions={true}
              menuPortalTarget={document.body}
            />
            <Form.Text>
              Select a template for the summary notebook entry. If no template is selected, a blank entry will be created.
            </Form.Text>
          </FormGroup>
        </Col>
      </Row>

      {isLoading && <LoadingMessage />}

      {error && <DismissableAlert message="Error loading notebook entry template" color="warning" />}

      {template && template.schema?.fieldDefinitions && template.schema.fieldDefinitions.length > 0 && (
        <Row>
          <Col xs={12}>
            <hr />
            <h4>Template Input Fields</h4>
            <p>The selected template has the following input fields:</p>
          </Col>
          {template.schema.fieldDefinitions.map((field, index) => (
            <Col key={index} md={6} className={"mb-3"}>
              <BenchlingFieldInput
                field={field}
                handleUpdate={(key, value) => onChange("notebookTemplateFields." + key, value)}
              />
            </Col>
          ))}
        </Row>
      )}

    </>
  );
}

NotebookEntryTemplatesDropdown.propTypes = {
  onChange: PropTypes.func.isRequired,
}

export default NotebookEntryTemplatesDropdown;