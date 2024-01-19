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

import React, {useContext} from "react";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import {Form as FormikForm, Formik} from "formik";
import PropTypes from "prop-types";
import * as yup from "yup";
import {DismissableAlert} from "../errors";
import axios from "axios";
import AsyncSelect from "react-select/async";
import {FormGroup} from "../forms/common";
import NotyfContext from "../../context/NotyfContext";

const FileManagerAddToStudyModal = ({
  isOpen,
  setModalIsOpen,
  rootFolder,
  folder,
  error,
  useStudies = true
}) => {

  const [selectedId, setSelectedId] = React.useState(null);
  const notyf = useContext(NotyfContext);

  const defaultValues = {
    storageDriveId: rootFolder ? rootFolder.storageDrive.id : null,
    name: folder ? folder.name : null,
    path: folder ? folder.path : null,
    writeEnabled: true,
  };

  const formSchema = yup.object({
    storageDriveId: yup.number().required(),
    name: yup.string().required(),
    path: yup.string().required(),
    writeEnabled: yup.boolean().required(),
  });

  const handleSubmit = (values, {setSubmitting}) => {
    console.debug(`Submitting AddFolderToStudy for study: ${setSelectedId}`, values);
    let url = useStudies
      ? `/api/internal/study/${selectedId}/storage`
      : `/api/internal/assay/${selectedId}/storage`;
    axios.patch(url, values)
    .then(response => {
      if (response.status === 200) {
        notyf.open({message: "Successfully added folder", type: "success"});
      } else {
        throw new Error("Error adding folder");
      }
    })
    .catch(error => {
      console.error(error);
      notyf.open({message: "Error adding folder", type: "error"});
    })
    .finally(() => {
      setSubmitting(false);
      setModalIsOpen(false);
    });
  }

  const autocomplete = (input, callback) => {
    const url = useStudies
      ? `/api/internal/autocomplete/study?q=${input}`
      : `/api/internal/autocomplete/assay?q=${input}`;
    axios.get(url)
    .then(response => {
      const options = response.data
      .filter(d => d.active)
      .sort((a, b) => {
        const aLabel = a.code + ": " + a.name;
        const bLabel = b.code + ": " + b.name;
        if (aLabel < bLabel) {
          return -1;
        }
        if (aLabel > bLabel) {
          return 1;
        }
        return 0;
      })
      .map(d => {
        return {
          label: `${d.code}: ${d.name}`,
          value: d.id,
          obj: d
        }
      });
      callback(options);
    }).catch(e => {
      console.error(e);
    })
  }

  console.debug("FileManagerAddToStudyModal: rootFolder: ", rootFolder);
  console.debug("FileManagerAddToStudyModal: folder: ", folder);
  console.debug("FileManagerAddToStudyModal: ID: ", selectedId);

  return (
      <Formik
          initialValues={defaultValues}
          validationSchema={formSchema}
          onSubmit={handleSubmit}
          enableReinitialize={true}
      >
        {({
            handleSubmit,
            isSubmitting,
            setFieldValue,
            values
        }) => (
            <Modal show={isOpen} onHide={() => setModalIsOpen(false)}>

              <Modal.Header closeButton>
                <Modal.Title>Add Folder to { useStudies ? "Study" : "Assay" }</Modal.Title>
              </Modal.Header>

              <Modal.Body className="mb-3">
                <FormikForm>

                  <Row>
                    <Col>
                      <p>
                        Adding a folder to a { useStudies ? "study" : "assay" } will allow users to access
                        it in the { useStudies ? "Study" : "Assay" } Details page's Files tab. You can
                        optionally make the folder read-only in this view, as well.
                      </p>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>{ useStudies ? "Study" : "Assay" }</Form.Label>
                        <AsyncSelect
                          placeholder="Search-for and select the record to add this folder to..."
                          className={"react-select-container"}
                          classNamePrefix="react-select"
                          loadOptions={autocomplete}
                          onChange={(selected) => setSelectedId(selected.value)}
                          controlShouldRenderValue={true}
                          defaultOptions={true}
                        />
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Check
                            type={"switch"}
                            label={"Read-only"}
                            onChange={(e) => {
                              setFieldValue("writeEnabled", !e.target.checked)
                            }}
                            value={!values.writeEnabled}
                        />
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row className={"mt-2"}>
                    <Col>
                      {error && (<DismissableAlert color={"danger"} message={error} />)}
                    </Col>
                  </Row>

                </FormikForm>
              </Modal.Body>

              <Modal.Footer>
                <Button
                    variant={"secondary"}
                    onClick={() => setModalIsOpen(false)}
                >
                  Cancel
                </Button>
                <Button
                    variant={"primary"}
                    onClick={!isSubmitting ? handleSubmit : null}
                    disabled={isSubmitting}
                >
                  {isSubmitting ? "Saving..." : "Save"}
                </Button>
              </Modal.Footer>

            </Modal>
        )}
      </Formik>

  );

}

FileManagerAddToStudyModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setModalIsOpen: PropTypes.func.isRequired,
  rootFolder: PropTypes.object.isRequired,
  folder: PropTypes.object.isRequired,
  error: PropTypes.string,
  useStudies: PropTypes.bool
};

export default FileManagerAddToStudyModal;