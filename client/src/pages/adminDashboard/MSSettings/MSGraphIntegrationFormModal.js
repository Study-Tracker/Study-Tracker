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

const MSGraphIntegrationFormModal = ({
    isOpen,
    setIsOpen,
    selectedIntegration,
    handleFormSubmit,
    formikRef
}) => {

  const integrationSchema = yup.object().shape({
    name: yup.string()
      .required("Name is required"),
    domain: yup.string()
      .nullable(),
    tenantId: yup.string()
      .required("Tenant ID is required"),
    clientId: yup.string()
      .required("Client ID is required"),
    clientSecret: yup.string()
      .required("Client Secret is required"),
    active: yup.boolean()
  });

  const integrationDefault = {
    name: "Microsoft Azure",
    domain: null,
    tenantId: null,
    clientId: null,
    clientSecret: null,
    active: true
  }

  return (
      <Formik
          initialValues={selectedIntegration || integrationDefault}
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
                Register Microsoft Azure Integration
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
                          Provide a name for your Azure tenant.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Domain</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"domain"}
                            isInvalid={errors.domain && touched.domain}
                            value={values.domain}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.domain}
                        </Form.Control.Feedback>
                        <Form.Text>
                          Your Azure tenant domain. eg. <code>contoso.onmicrosoft.com</code>
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Tenant ID *</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"tenantId"}
                            isInvalid={errors.tenantId && touched.tenantId}
                            value={values.tenantId}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.tenantId}
                        </Form.Control.Feedback>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Client ID *</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"clientId"}
                            isInvalid={errors.clientId && touched.clientId}
                            value={values.clientId}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.clientId}
                        </Form.Control.Feedback>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Client Secret *</Form.Label>
                        <Form.Control
                            type={"password"}
                            name={"clientSecret"}
                            isInvalid={errors.clientSecret && touched.clientSecret}
                            value={values.clientSecret}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.clientSecret}
                        </Form.Control.Feedback>
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

MSGraphIntegrationFormModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
  selectedIntegration: PropTypes.object,
  handleFormSubmit: PropTypes.func.isRequired,
  formikRef: PropTypes.object.isRequired
}

export default MSGraphIntegrationFormModal;