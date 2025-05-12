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
import {Button, Col, Form, Modal, Row} from 'react-bootstrap'
import PropTypes from "prop-types";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import Select from "react-select";
import {FormGroup} from "../../../common/forms/common";
import axios from "axios";
import NotyfContext from "../../../context/NotyfContext";
import FormikFormErrorNotification
  from "../../../common/forms/FormikFormErrorNotification";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";

const StorageFolderFormModal = ({
  isOpen,
  setIsOpen,
  selectedFolder,
  formikRef
}) => {

  // const [selectedDrive, setSelectedDrive] = React.useState(selectedFolder?.storageDrive);
  const notyf = useContext(NotyfContext);
  const queryClient = useQueryClient();

  const {data: drives, isLoading} = useQuery({
    queryKey: ["storageDrives"],
    queryFn: () => {
      return axios.get("/api/internal/storage-drives")
      .then(response => response.data)
      .catch(e => {
        console.error(e);
        notyf.open({
          message: 'Failed to load available storage drives.',
          type: 'error'
        });
      });
    }
  });

  const submitMutation = useMutation({
    mutationFn: (values) => {
      const url = selectedFolder
        ? "/api/internal/storage-drive-folders/" + selectedFolder.id
        : "/api/internal/storage-drive-folders";
      return axios({
        url: url,
        method: values.id ? "PUT" : "POST",
        data: { storageDriveId: values.storageDrive.id, ...values },
        headers: {
          "Content-Type": "application/json"
        },
      })
    }
  });

  const handleSubmitForm = (values, {setSubmitting, resetForm}) => {
    submitMutation.mutate(values, {
      onSuccess: () => {
        notyf.success('Storage folder saved.');
        resetForm();
        setIsOpen(false);
        queryClient.invalidateQueries("rootStorageFolders");
      },
      onError: (e) => {
        console.error(e);
        if (e.response.status === 404) {
          notyf.error('The requested folder does not exist: ' + values.rootFolderPath);
        } else {
          notyf.error('Failed to save storage location.');
        }
      },
      onSettled: () => {
        setSubmitting(false);
      }
    })
  }

  const folderSchema = yup.object().shape({
    id: yup.number()
      .nullable(),
    storageDrive: yup.object()
      .required("Storage drive is required"),
    path: yup.string()
      .max(1024, "Folder path must be less than 1024 characters"),
    name: yup.string()
      .required("Folder path is required")
      .max(255, "Folder name must be less than 255 characters"),
    browserRoot: yup.boolean(),
    studyRoot: yup.boolean(),
    writeEnabled: yup.boolean(),
    deleteEnabled: yup.boolean(),
  });

  const folderDefault = {
    id: null,
    storageDrive: null,
    path: '',
    name: '',
    browserRoot: true,
    studyRoot: false,
    writeEnabled: true,
    deleteEnabled: false,
  }

  const driveOptions = drives ? drives
  .filter(drive => drive.active)
  .map(drive => {
    return {
      value: drive.id,
      label: drive.displayName + " (" + drive.driveType + ")",
      obj: drive
    }
  }) : [];

  if (isLoading) return null;

  return (
      <Formik
          initialValues={selectedFolder || folderDefault}
          onSubmit={handleSubmitForm}
          validationSchema={folderSchema}
          innerRef={formikRef}
          enableReinitialize
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

                  <FormikFormErrorNotification />

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Storage Drive *</Form.Label>
                        <Select
                            name={"storageDrive"}
                            className={"react-select-container " + (errors.storageDrive && touched.storageDrive ? "is-invalid" : "")}
                            classNamePrefix="react-select"
                            invalid={errors.storageDrive && touched.storageDrive}
                            value={values.storageDrive ? driveOptions.find(o => o.value === values.storageDrive.id) : null}
                            isDisabled={!!values.id}
                            options={driveOptions}
                            onChange={selected => {
                              const drive = selected.obj;
                              // setSelectedDrive(drive);
                              setFieldValue("storageDrive", drive);
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
                          disabled={!!values.id}
                          value={values.path}
                          onChange={e => {
                            let path = e.target.value;
                            if (values.storageDrive && !path.startsWith(values.storageDrive.rootPath)) {
                              path = values.storageDrive.rootPath;
                            }
                            setFieldValue("path", path);
                          }}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.path}
                        </Form.Control.Feedback>
                        <Form.Text>
                          Enter the full path to the folder on the storage drive. If the folder does not exist, it will
                          be created. If using the root path of the drive, use <code>/</code> as your folder path.
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
                            checked={values.browserRoot}
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
                            checked={values.studyRoot}
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
                            checked={values.writeEnabled}
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
                            checked={values.deleteEnabled}
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
  formikRef: PropTypes.object.isRequired
}

export default StorageFolderFormModal;