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

import React, {useContext, useEffect, useState} from "react";
import {Button, Col, Form, Modal, Row} from 'react-bootstrap'
import PropTypes from "prop-types";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import Select from "react-select";

const StorageLocationFormModal = ({
  isOpen,
  setIsOpen,
  selectedLocation,
  locations
}) => {

  const notyf = useContext(NotyfContext);
  // const [locations, setLocation] = useState([]);
  const [integrations, setIntegrations] = useState([]);

  const locationSchema = yup.object().shape({
    integrationInstanceId: yup.number()
      .required("Integration instance is required"),
    // type: yup.string()
    //   .required("Storage type is required"),
    displayName: yup.string()
      .required("Display name is required")
      .max(255, "Display name must be less than 255 characters"),
    // name: yup.string()
    //   .required("Name is required")
    //   .max(255, "Name must be less than 255 characters"),
    rootFolderPath: yup.string()
      .required("Root folder path is required")
      .max(1024, "Root folder path must be less than 1024 characters"),
    // referenceId: yup.string()
    //   .max(255, "Reference ID must be less than 255 characters"),
    // url: yup.string()
    //   .max(1024, "URL must be less than 1024 characters"),
    permissions: yup.string()
      .required("Storage permissions are required."),
  });

  const locationDefault = {
    integrationInstanceId: null,
    // type: "",
    displayName: "",
    // name: "",
    rootFolderPath: "",
    // referenceId: "",
    // url: "",
    permissions: "READ_WRITE",
    defaultStudyLocation: false,
    defaultDataLocation: false,
    active: true
  }

  useEffect(() => {
    axios.get("/api/internal/integrations")
    .then(response => {
      setIntegrations(response.data);
    })
    .catch(e => {
      console.error(e);
      notyf.open({message: 'Failed to load available integration instances.', type: 'error'});
    })
  }, [])

  const submitForm = (values, {setSubmitting, resetForm}) => {
    const url = selectedLocation
        ? "/api/internal/storage-locations/" + selectedLocation.id
        : "/api/internal/storage-locations";
    const method = selectedLocation ? "PUT" : "POST";
    axios({
      method: method,
      url: url,
      data: values,
      headers: {
        "Content-Type": "application/json"
      },
    })
    .then(response => {
      notyf.open({message: 'Storage location saved.', type: 'success'});
      setIsOpen(false);
    })
    .catch(e => {
      console.error(e);
      notyf.open({message: 'Failed to save storage location.', type: 'error'});
    })
    .finally(() => {
      setSubmitting(false);
      resetForm();
    })
  }

  const storageServiceOptions = integrations.map(integration => {
    if (["EGNYTE", "AWS_S3", "LOCAL_FILE_SYSTEM"].indexOf(integration.definition.type) > -1) {
      return {
        value: integration.id,
        label: integration.name
      }
    }
  });

  const permissionsOptions = [
    {value: "READ_WRITE", label: "Read/Write"},
    {value: "READ_ONLY", label: "Read Only"},
    // {value: "READ_WRITE_DELETE", label: "Read/Write/Delete"}
  ]

  return (
      <Formik
          initialValues={selectedLocation || locationDefault}
          onSubmit={submitForm}
          validationSchema={locationSchema}
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
                Add File Storage Location
              </Modal.Header>
              <Modal.Body className={"mb-3"}>
                <FormikForm>

                  <Row>
                    <Col>
                      <Form.Group>
                        <Form.Label>Storage Service *</Form.Label>
                        <Select
                            name={"integrationInstanceId"}
                            className={"react-select-container " + (errors.integrationInstanceId && touched.integrationInstanceId ? "is-invalid" : "")}
                            classNamePrefix="react-select"
                            invalid={errors.integrationInstanceId && touched.integrationInstanceId}
                            value={storageServiceOptions.find(option => option.value === values.integrationInstanceId)}
                            isDisabled={!!values.id}
                            options={storageServiceOptions}
                            onChange={selected => setFieldValue("integrationInstanceId", selected.value)}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          You must select a storage service.
                        </Form.Control.Feedback>
                        <Form.Text>
                          Select a storage service to use for this location.
                        </Form.Text>
                      </Form.Group>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <Form.Group>
                        <Form.Label>Name *</Form.Label>
                        <Form.Control
                          type={"text"}
                          name={"displayName"}
                          isInvalid={errors.displayName && touched.displayName}
                          value={values.displayName}
                          onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.displayName}
                        </Form.Control.Feedback>
                      </Form.Group>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <Form.Group>
                        <Form.Label>Folder Path *</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"rootFolderPath"}
                            isInvalid={errors.rootFolderPath && touched.rootFolderPath}
                            value={values.rootFolderPath}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.rootFolderPath}
                        </Form.Control.Feedback>
                        <Form.Text>
                          Provide the full absolute path to the folder where files will be stored.
                        </Form.Text>
                      </Form.Group>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <Form.Group>
                        <Form.Label>Permissions</Form.Label>
                        <Select
                            name={"permissions"}
                            className={"react-select-container " + (errors.permissions && touched.permissions ? "is-invalid" : "")}
                            classNamePrefix="react-select"
                            invalid={errors.permissions && touched.permissions}
                            value={storageServiceOptions.find(option => option.value === values.integrationInstanceId)}
                            isDisabled={!!values.id}
                            options={permissionsOptions}
                            onChange={selected => setFieldValue("permissions", selected.value)}
                        />
                        <Form.Text>
                          Select the access permissions for this location.
                        </Form.Text>
                      </Form.Group>
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

StorageLocationFormModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
  selectedLocation: PropTypes.object,
}

export default StorageLocationFormModal;