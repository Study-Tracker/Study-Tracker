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

import React, {useContext, useEffect} from "react";
import {Button, Col, Form, Modal, Row} from 'react-bootstrap'
import PropTypes from "prop-types";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import Select from "react-select";
import {FormGroup} from "../../../common/forms/common";
import axios from "axios";
import NotyfContext from "../../../context/NotyfContext";

const StorageFolderFormModal = ({
  isOpen,
  setIsOpen,
  selectedFolder,
  handleFormSubmit,
  formikRef
}) => {

  const [selectedDrive, setSelectedDrive] = React.useState(null);
  const [drives, setDrives] = React.useState([]);
  const notyf = useContext(NotyfContext);

  useEffect(() => {
    axios.get("/api/internal/storage-drives")
    .then(response => {
      console.debug("Storage drives", response.data);
      setDrives(response.data);
    })
    .catch(e => {
      console.error(e);
      notyf.open({message: 'Failed to load available storage drives.', type: 'error'});
    });
  }, []);

  const folderSchema = yup.object().shape({
    storageDriveId: yup.number()
      .required("Storage drive is required"),
    path: yup.string()
      .required("Folder path is required"),
    name: yup.string()
      .required("Folder path is required")
      .max(255, "Folder name must be less than 255 characters"),
    browserRoot: yup.boolean(),
    studyRoot: yup.boolean(),
    writeEnabled: yup.boolean(),
    deleteEnabled: yup.boolean(),
  });

  const folderDefault = {
    storageDriveId: null,
    path: null,
    name: null,
    browserRoot: true,
    studyRoot: false,
    writeEnabled: true,
    deleteEnabled: false
  }

  const driveOptions = drives.map(drive => {
    return {
      value: drive.id,
      label: drive.displayName
    }
  });

  return (
      <Formik
          initialValues={selectedFolder || folderDefault}
          onSubmit={handleFormSubmit}
          validationSchema={folderSchema}
          innerRef={formikRef}
          enableReinitialize={true}
      >
        {({
          values,
          handleChange,
          handleSubmit,
          errors,
          touched,
          isSubmitting,
          setFieldValue
        }) => (
            <Modal show={isOpen} onHide={() => setIsOpen(false)}>
              <Modal.Header closeButton>
                Add Root Storage Folder
              </Modal.Header>
              <Modal.Body className={"mb-3"}>
                <FormikForm>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Storage Drive *</Form.Label>
                        <Select
                            name={"storageDriveId"}
                            className={"react-select-container " + (errors.storageDriveId && touched.storageDriveId ? "is-invalid" : "")}
                            classNamePrefix="react-select"
                            invalid={errors.storageDriveId && touched.storageDriveId}
                            defaultValue={values.storageDriveId ? driveOptions.find(option => option.value === values.storageDriveId) : null}
                            isDisabled={!!values.id}
                            options={driveOptions}
                            onChange={selected => {
                              const drive = drives.find(d => d.id === selected.value);
                              setSelectedDrive(drive);
                              setFieldValue("storageDriveId", drive.id);
                              setFieldValue("path", drive.rootPath);
                            }}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          You must select a storage drive.
                        </Form.Control.Feedback>
                        <Form.Text>
                          Select a storage drive to use for this root folder.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Folder Path *</Form.Label>
                        <Form.Control
                          type={"text"}
                          name={"path"}
                          isInvalid={errors.path && touched.path}
                          value={values.path}
                          onChange={e => {
                            let path = e.target.value;
                            if (selectedDrive && !path.startsWith(selectedDrive.rootPath)) {
                              path = selectedDrive.rootPath;
                            }
                            setFieldValue("path", path);
                          }}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.path}
                        </Form.Control.Feedback>
                        <Form.Text>
                          Enter the full path to the folder on the storage drive. If the folder does not exist, it will be created.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Label *</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"name"}
                            isInvalid={errors.name && touched.name}
                            value={values.name}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.name}
                        </Form.Control.Feedback>
                        <Form.Text>
                          Provide a display name for this folder.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Check
                            type={"switch"}
                            label={"Browser root"}
                            onChange={(e) => setFieldValue("browserRoot", e.target.checked)}
                            defaultChecked={values.browserRoot}
                        />
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Check
                            type={"switch"}
                            label={"Study root"}
                            onChange={(e) => setFieldValue("studyRoot", e.target.checked)}
                            defaultChecked={values.studyRoot}
                        />
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Check
                            type={"switch"}
                            label={"Write enabled"}
                            onChange={(e) => setFieldValue("writeEnabled", e.target.checked)}
                            defaultChecked={values.writeEnabled}
                        />
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Check
                            type={"switch"}
                            label={"Delete enabled"}
                            onChange={(e) => setFieldValue("deleteEnabled", e.target.checked)}
                            defaultChecked={values.deleteEnabled}
                        />
                      </FormGroup>
                    </Col>
                  </Row>

                </FormikForm>
              </Modal.Body>

              <Modal.Footer>
                <Button
                    variant={"secondary"}
                    onClick={() => setIsOpen(false)}
                >
                  Cancel
                </Button>
                <Button
                    variant={"primary"}
                    onClick={!isSubmitting ? handleSubmit : null}
                    disabled={isSubmitting}
                >
                  {isSubmitting ? "Submitting..." : "Submit"}
                </Button>
              </Modal.Footer>

            </Modal>
        )}
      </Formik>

  );
}

StorageFolderFormModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
  selectedFolder: PropTypes.object,
  drives: PropTypes.array.isRequired,
  handleFormSubmit: PropTypes.func.isRequired,
  formikRef: PropTypes.object.isRequired
}

export default StorageFolderFormModal;