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

import React, {useEffect, useState} from "react";
import {Col, Form, Row} from "react-bootstrap";
import {FormGroup} from "./common";
import NotebookEntryTemplatesDropdown from "./NotebookEntryTemplateDropdown";
import PropTypes from "prop-types";
import FeatureToggleCard from "./FeatureToggleCard";
import axios from "axios";
import NotebookFolderDropdown from "./NotebookFolderDropdown";

const NotebookInputsCard = ({
  isActive,
  onChange,
  selectedProgram,
  selectedStudy,
  useExistingFolder,
  notebookFolder
}) => {

  const [parentFolder, setParentFolder] = useState(null);
  const [selectedOption, setSelectedOption] = useState('create');

  useEffect(() => {
    let url = null;
    if (selectedProgram) url = `/api/internal/program/${selectedProgram.id}/notebook?contents=false`;
    else if (selectedStudy) url = `/api/internal/study/${selectedStudy.id}/notebook?contents=false`;
    if (url !== null) {
      axios.get(url)
      .then(response => {
        setParentFolder(response.data);
      })
      .catch(error => {
        setParentFolder(null);
        console.error("Error loading notebook folder: ", error);
      });
    }
  }, [selectedProgram, selectedStudy]);

  let placeholderName = parentFolder ? `${parentFolder.path || parentFolder.name} / ${selectedStudy ? selectedStudy.code : selectedProgram.code}-XXX: ${selectedStudy ? "Assay" : "Study"} Name` : "";

  return (
      <FeatureToggleCard
        isActive={isActive}
        title={"Electronic Laboratory Notebook (ELN)"}
        description={(selectedProgram ? "Studies" : "Assays") + " that require "
            + "an electronic notebook will have a folder created within the "
            + "appropriate " + (selectedProgram ? "program" : "study") + " folder "
            + "of the integrated ELN system. " + (selectedProgram ? "Studies" : "Assays")
            + " will also have a summary notebook entry created for them, either "
            + "from the selected template, or as a blank entry."}
        switchLabel={"Does this " + (selectedProgram ? "study" : "assay") + " need an electronic notebook?"}
        handleToggle={() => onChange("useNotebook", !isActive)}
      >
        <Row className={"mb-2"}>

          <Col sm={6}>

            <Form.Group>
              <Form.Label>Notebook Folder</Form.Label>
            </Form.Group>

            <Form.Group>
              <Form.Check
                label={"Create new notebook folder"}
                type={"radio"}
                name={"notebookFolderOption"}
                defaultChecked={!useExistingFolder}
                onChange={(e) => {
                  onChange("useExistingNotebookFolder", !e.target.checked);
                  setSelectedOption('create');
                }}
              />
            </Form.Group>

            <Form.Group>
              <Form.Check
                label={"Select existing notebook folder"}
                type={"radio"}
                name={"notebookFolderOption"}
                defaultChecked={useExistingFolder}
                onChange={(e) => {
                  onChange("useExistingNotebookFolder", e.target.checked);
                  setSelectedOption('select');
                }}
              />
            </Form.Group>

            <Form.Group>
              <Form.Check
                  label={"Find notebook folder by URL"}
                  type={"radio"}
                  name={"notebookFolderOption"}
                  defaultChecked={useExistingFolder}
                  onChange={(e) => {
                    onChange("useExistingNotebookFolder", e.target.checked);
                    setSelectedOption('url');
                  }}
              />
            </Form.Group>

          </Col>

          <Col sm={6} hidden={selectedOption !== 'create'}>
            <FormGroup>
              <Form.Label>Notebook Folder Path</Form.Label>
              <Form.Control
                type="text"
                name="notebookFolderPath"
                value={placeholderName}
                disabled={true}
                isInvalid={!parentFolder}
              />
              <Form.Control.Feedback type={"invalid"}>
                {
                  (selectedProgram || selectedStudy) && !parentFolder
                    ? "The selected program does not have a notebook folder."
                    : "You must select a program to associate the study with."
                }
              </Form.Control.Feedback>
              <Form.Text>The notebook folder will be created at this location in the ELN.</Form.Text>
            </FormGroup>
          </Col>

          <Col sm={6} hidden={selectedOption !== 'select'}>
            <NotebookFolderDropdown
              onChange={(d) => onChange("notebookFolder", d)}
              parentFolder={parentFolder}
            />
          </Col>

          <Col sm={6} hidden={selectedOption !== 'url'}>
            <FormGroup>
              <Form.Label>Notebook Folder URL</Form.Label>
              <Form.Control
                  type="text"
                  name={"notebookFolder.url"}
                  value={notebookFolder.url}
                  onChange={(e) => onChange("notebookFolder.url", e.target.value)}
              />
              <Form.Text>
                Provide the Benchling URL for the notebook entry or folder you'd like to use.
              </Form.Text>
            </FormGroup>
          </Col>

        </Row>

        <Row>
          <Col md={6}>
            <NotebookEntryTemplatesDropdown
                onChange={selected => onChange("notebookTemplateId", selected || "") }
            />
          </Col>
        </Row>
      </FeatureToggleCard>
  )

}

NotebookInputsCard.propTypes = {
  isActive: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired,
  selectedProgram: PropTypes.object,
  selectedStudy: PropTypes.object,
  useExistingFolder: PropTypes.bool,
  notebookFolder: PropTypes.object
}

export default NotebookInputsCard;