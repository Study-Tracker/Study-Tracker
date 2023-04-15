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

const EgnyteIntegrationFormModal = ({
    isOpen,
    setIsOpen,
    handleFormSubmit,
    selectedIntegration,
    formikRef
}) => {

  const integrationSchema = yup.object().shape({
    tenantName: yup.string()
      .required("Tenant name is required")
      .max(255, "Tenant name must be less than 255 characters"),
    rootUrl: yup.string()
      .nullable(),
    qps: yup.number()
      .nullable()
      .min(1, "QPS must be greater than 0"),
    apiToken: yup.string()
      .required("API token is required"),
    active: yup.boolean()
  });

  const integrationDefault = {
    tenantName: null,
    rootUrl: null,
    qps: 1,
    apiToken: null,
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
                Register Egnyte Integration
              </Modal.Header>
              <Modal.Body className={"mb-3"}>
                <FormikForm autoComplete={"off"}>

                  <FormikFormErrorNotification />

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Tenant Name *</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"tenantName"}
                            isInvalid={errors.tenantName && touched.tenantName}
                            value={values.tenantName}
                            onChange={event => {
                              const val = event.target.value;
                              setFieldValue("tenantName", val);
                              setFieldValue("rootUrl", `https://${val}.egnyte.com`);
                            }}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.tenantName}
                        </Form.Control.Feedback>
                        <Form.Text>
                          Your Egnyte tenant name. For example, if your Egnyte
                          URL is <code>myorg.egnyte.com</code>, then the tenant
                          name will be <code>myorg</code>.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Root URL</Form.Label>
                        <Form.Control
                            type={"url"}
                            name={"rootUrl"}
                            isInvalid={errors.rootUrl && touched.rootUrl}
                            value={values.rootUrl}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.rootUrl}
                        </Form.Control.Feedback>
                        <Form.Text>
                          The root URL of your tenant. eg. <code>https://myorg.egnyte.com</code>
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>API Token *</Form.Label>
                        <Form.Control
                            type={"password"}
                            name={"apiToken"}
                            isInvalid={errors.apiToken && touched.apiToken}
                            value={values.apiToken}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.apiToken}
                        </Form.Control.Feedback>
                        {
                            values.id && (
                                <Form.Text className={"text-danger"}>
                                  Please reenter your API Token.<br/>
                                </Form.Text>
                            )
                        }
                        <Form.Text>
                          Your Egnyte API token, used for authenticating API requests.
                          This can be acquired from the Egnyte Developer portal.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Max. API Queries-per-second (QPS)</Form.Label>
                        <Form.Control
                            type={"number"}
                            name={"qps"}
                            isInvalid={errors.qps && touched.qps}
                            value={values.qps}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.qps}
                        </Form.Control.Feedback>
                        <Form.Text>
                          The maximum number of API queries-per-second that will
                          be allowed to be made to Egnyte. This number will be
                          provided to you when generating an API token. Setting a value
                          higher than the number provided may result in errors.
                        </Form.Text>
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

EgnyteIntegrationFormModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
  handleFormSubmit: PropTypes.func.isRequired,
  formikRef: PropTypes.object.isRequired,
  selectedIntegration: PropTypes.object
}

export default EgnyteIntegrationFormModal;