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

import React, {useContext, useEffect, useState} from "react";
import {Button, Col, Form, Modal, Row} from 'react-bootstrap'
import PropTypes from "prop-types";
import NotyfContext from "../../../context/NotyfContext";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import Select from "react-select";
import {FormGroup} from "../../../common/forms/common";

const StorageFolderFormModal = ({
  isOpen,
  setIsOpen,
  selectedFolder,
  handleFormSubmit,
  formikRef
}) => {

  const notyf = useContext(NotyfContext);
  const [integrations, setIntegrations] = useState([]);

  const locationSchema = yup.object().shape({
    integrationInstance: yup.object()
      .required("Integration instance is required"),
    displayName: yup.string()
      .required("Display name is required")
      .max(255, "Display name must be less than 255 characters"),
    rootFolderPath: yup.string()
      .max(1024, "Root folder path must be less than 1024 characters"),
    permissions: yup.string()
      .required("Storage permissions are required."),
  });

  const locationDefault = {
    integrationInstance: null,
    displayName: "",
    rootFolderPath: "",
    permissions: "READ_WRITE",
    defaultStudyLocation: false,
    defaultDataLocation: false,
    active: true
  }

  useEffect(() => {

  }, [])

  let storageServiceOptions = [];
  integrations.forEach(integration => {
    if (["EGNYTE", "AWS_S3", "LOCAL_FILE_SYSTEM"].indexOf(integration.definition.type) > -1) {
      storageServiceOptions.push({
        value: integration.id,
        label: integration.name
      });
    }
  });

  const permissionsOptions = [
    {value: "READ_WRITE", label: "Read/Write"},
    {value: "READ_ONLY", label: "Read Only"},
    // {value: "READ_WRITE_DELETE", label: "Read/Write/Delete"}
  ]

  return (
      <Formik
          initialValues={selectedFolder || locationDefault}
          onSubmit={handleFormSubmit}
          validationSchema={locationSchema}
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
                Add File Storage Location
              </Modal.Header>
              <Modal.Body className={"mb-3"}>
                <FormikForm>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Storage Service *</Form.Label>
                        <Select
                            name={"integrationInstanceId"}
                            className={"react-select-container " + (errors.integrationInstanceId && touched.integrationInstanceId ? "is-invalid" : "")}
                            classNamePrefix="react-select"
                            invalid={errors.integrationInstanceId && touched.integrationInstanceId}
                            defaultValue={values.integrationInstanceId ? storageServiceOptions.find(option => option.value === values.integrationInstanceId) : null}
                            isDisabled={!!values.id}
                            options={storageServiceOptions}
                            onChange={selected => setFieldValue("integrationInstance", integrations.filter(d => d.id === selected.value)[0])}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          You must select a storage service.
                        </Form.Control.Feedback>
                        <Form.Text>
                          Select a storage service to use for this location.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
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
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Folder Path</Form.Label>
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
                          Provide the full, absolute path to the folder where files will be stored.
                          If left blank, the root folder will be used.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Permissions</Form.Label>
                        <Select
                            name={"permissions"}
                            className={"react-select-container " + (errors.permissions && touched.permissions ? "is-invalid" : "")}
                            classNamePrefix="react-select"
                            invalid={errors.permissions && touched.permissions}
                            defaultValue={values.permissions ? permissionsOptions.find(option => option.value === values.permissions) : null}
                            isDisabled={!!values.id}
                            options={permissionsOptions}
                            onChange={selected => setFieldValue("permissions", selected.value)}
                        />
                        <Form.Text>
                          Select the access permissions for this location.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Check
                            type={"switch"}
                            label={"Active"}
                            onChange={(e) => setFieldValue("active", e.target.checked)}
                            defaultChecked={values.active}
                        />
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Check
                            type={"switch"}
                            label={"Default study storage location"}
                            onChange={(e) => setFieldValue("defaultStudyLocation", e.target.checked)}
                            defaultChecked={values.defaultStudyLocation}
                        />
                      </FormGroup>
                    </Col>
                  </Row>

                  {/*<Row>*/}
                  {/*  <Col>*/}
                  {/*    <FormGroup>*/}
                  {/*      <Form.Check*/}
                  {/*          type={"switch"}*/}
                  {/*          label={"Default data storage location"}*/}
                  {/*          onChange={(e) => setFieldValue("defaultDataLocation", e.target.checked)}*/}
                  {/*          defaultChecked={values.defaultDataLocation}*/}
                  {/*      />*/}
                  {/*    </FormGroup>*/}
                  {/*  </Col>*/}
                  {/*</Row>*/}

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
  handleFormSubmit: PropTypes.func.isRequired,
  formikRef: PropTypes.object.isRequired
}

export default StorageFolderFormModal;