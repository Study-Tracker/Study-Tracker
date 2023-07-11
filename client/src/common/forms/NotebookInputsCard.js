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

const NotebookInputsCard = ({
    isActive,
    onChange,
    selectedProgram,
    selectedStudy
}) => {

  console.debug("Program: ", selectedProgram);
  const [parentFolder, setParentFolder] = useState(null);

  useEffect(() => {
    if (selectedProgram) {
      axios.get(`/api/internal/program/${selectedProgram.id}/notebook?contents=false`)
      .then(response => {
        setParentFolder(response.data.path || response.data.name);
      })
      .catch(error => {
        setParentFolder(null);
        console.error("Error loading program notebook folder: ", error);
      });
    } else if (selectedStudy) {
      axios.get(`/api/internal/study/${selectedStudy.id}/notebook?contents=false`)
      .then(response => {
        setParentFolder(response.data.path || response.data.name);
      })
      .catch(error => {
        setParentFolder(null);
        console.error("Error loading study notebook folder: ", error);
      });
    }
  }, [selectedProgram, selectedStudy]);

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
        <Row>

          <Col md={6}>
            <NotebookEntryTemplatesDropdown
                onChange={selected => onChange("notebookTemplateId", selected || "") }
            />
          </Col>

          <Col lg={6}>
            <FormGroup>
              <Form.Label>Folder Path</Form.Label>
              <Form.Control
                  type="text"
                  name="notebookFolderPath"
                  value={parentFolder}
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

        </Row>
      </FeatureToggleCard>
  )

}

NotebookInputsCard.propTypes = {
  isActive: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired,
  selectedProgram: PropTypes.string,
  selectedStudy: PropTypes.string
}

export default NotebookInputsCard;