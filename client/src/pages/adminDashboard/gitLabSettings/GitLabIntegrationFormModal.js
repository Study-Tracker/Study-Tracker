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

import React from "react";
import PropTypes from "prop-types";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import FormikFormErrorNotification
  from "../../../common/forms/FormikFormErrorNotification";
import {FormGroup} from "../../../common/forms/common";

const GitLabIntegrationFormModal = ({
    isOpen,
    setIsOpen,
    handleFormSubmit,
    selectedIntegration,
    formikRef
}) => {

  const integrationSchema = yup.object().shape({
    name: yup.string()
      .required("Name is required"),
    rootUrl: yup.string()
      .required("GitLab tenant URL is required"),
    username: yup.string()
      .nullable(true)
      .when("useToken", {
        is: false,
        then: yup.string()
          .typeError("Username is required when not using an access token.")
          .required("Username is required when not using an access token.")
          .max(255, "Username cannot be longer than 255 characters")
      }),
    password: yup.string()
      .nullable(true)
      .when("useToken", {
        is: false,
        then: yup.string()
        .typeError("Password is required when not using an access token.")
        .required("Password is required when not using an access token.")
        .max(255, "Password cannot be longer than 255 characters")
      }),
    accessToken: yup.string()
      .nullable(true)
      .when("useToken", {
        is: true,
        then: yup.string()
        .typeError("Access token is required.")
        .required("Access token is required.")
        .max(1024, "Access token cannot be longer than 1024 characters.")
      }),
    useToken: yup.boolean(),
    active: yup.boolean()
  });

  const integrationDefault = {
    name: "GitLab",
    rootUrl: null,
    username: null,
    password: null,
    accessToken: null,
    useToken: true,
    active: true
  }

  return (
      <Formik
          initialValues={selectedIntegration ? {...selectedIntegration, useToken: true} : integrationDefault}
          onSubmit={handleFormSubmit}
          validationSchema={integrationSchema}
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
                Register GitLab Integration
              </Modal.Header>
              <Modal.Body className={"mb-3"}>
                <FormikForm autoComplete={"off"}>

                  <FormikFormErrorNotification />

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Name *</Form.Label>
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
                          Provide a name for your GitLab tenant.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>URL *</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"rootUrl"}
                            isInvalid={errors.rootUrl && touched.rootUrl}
                            value={values.rootUrl}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.rootUrl}
                        </Form.Control.Feedback>
                        <Form.Text>
                          Root URL of your GitLab tenant.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Check
                            type={"switch"}
                            label={"Use Access Token for authentication"}
                            onChange={(e) => setFieldValue("useToken", e.target.checked)}
                            defaultChecked={values.useToken}
                        />
                        <Form.Text>
                          Use Access Token for authentication. If this is checked,
                          Study Tracker will use the provided user, project, or group access
                          token to authenticate with GitLab. This is the preferred authentication
                          method.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row hidden={!values.useToken}>
                    <Col>
                      <FormGroup>
                        <Form.Label>Access Token *</Form.Label>
                        <Form.Control
                            type={"password"}
                            name={"accessToken"}
                            isInvalid={errors.accessToken && touched.accessToken}
                            value={values.accessToken}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.accessToken}
                        </Form.Control.Feedback>
                        {
                            values.id && (
                                <Form.Text className={"text-danger"}>
                                  Please reenter your access token.<br/>
                                </Form.Text>
                            )
                        }
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row hidden={values.useToken}>
                    <Col>
                      <FormGroup>
                        <Form.Label>Username *</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"username"}
                            isInvalid={errors.username && touched.username}
                            value={values.username}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.username}
                        </Form.Control.Feedback>
                        {
                            values.id && (
                                <Form.Text className={"text-danger"}>
                                  Please reenter your username.<br/>
                                </Form.Text>
                            )
                        }
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Password *</Form.Label>
                        <Form.Control
                            type={"password"}
                            name={"password"}
                            isInvalid={errors.password && touched.password}
                            value={values.password}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.password}
                        </Form.Control.Feedback>
                        {
                          values.id && (
                                <Form.Text className={"text-danger"}>
                                  Please reenter your password.<br />
                                </Form.Text>
                          )
                        }
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
  )

}

GitLabIntegrationFormModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
  handleFormSubmit: PropTypes.func.isRequired,
  formikRef: PropTypes.object.isRequired,
  selectedIntegration: PropTypes.object
}

export default GitLabIntegrationFormModal;