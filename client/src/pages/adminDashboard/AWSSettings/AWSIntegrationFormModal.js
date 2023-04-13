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

const AWSIntegrationFormModal = ({
    isOpen,
    setIsOpen,
    handleFormSubmit,
    selectedIntegration,
    formikRef
}) => {

  const integrationSchema = yup.object().shape({
    name: yup.string()
      .required("Name is required"),
    accountNumber: yup.string()
      .nullable(),
    region: yup.string()
      .required("Region is required"),
    accessKeyId: yup.string(),
    secretAccessKey: yup.string(),
    useIam: yup.boolean(),
    active: yup.boolean()
  });

  const integrationDefault = {
    name: "Amazon Web Services",
    accountNumber: null,
    region: null,
    accessKeyId: null,
    secretAccessKey: null,
    useIam: false,
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
                Register Amazon Web Services Integration
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
                          Provide a name for your AWS tenant.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Account Number</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"accountNumber"}
                            isInvalid={errors.accountNumber && touched.accountNumber}
                            value={values.accountNumber}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.accountNumber}
                        </Form.Control.Feedback>
                        <Form.Text>
                          Your AWS account number. This is used for identifying your account.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Region *</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"region"}
                            isInvalid={errors.region && touched.region}
                            value={values.region}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.region}
                        </Form.Control.Feedback>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Access Key ID</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"accessKeyId"}
                            isInvalid={errors.accessKeyId && touched.accessKeyId}
                            value={values.accessKeyId}
                            onChange={handleChange}
                            disabled={values.useIam}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.accessKeyId}
                        </Form.Control.Feedback>
                        {
                            values.id && (
                                <Form.Text className={"text-danger"}>
                                  Please reenter your access key ID.<br/>
                                </Form.Text>
                            )
                        }
                        <Form.Text>
                          Your AWS access key ID. This is used for authenticating
                          your account when IAM is not used.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Secret Access Key</Form.Label>
                        <Form.Control
                            type={"password"}
                            name={"secretAccessKey"}
                            isInvalid={errors.secretAccessKey && touched.secretAccessKey}
                            value={values.secretAccessKey}
                            onChange={handleChange}
                            disabled={values.useIam}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.secretAccessKey}
                        </Form.Control.Feedback>
                        {
                          values.id && (
                                <Form.Text className={"text-danger"}>
                                  Please reenter your secret access key.<br />
                                </Form.Text>
                          )
                        }
                        <Form.Text>
                          Your AWS secret access key. This is used for authenticating
                          your account when IAM is not used.
                        </Form.Text>
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Check
                            type={"switch"}
                            label={"Use IAM for authentication"}
                            onChange={(e) => setFieldValue("useIam", e.target.checked)}
                            defaultChecked={values.useIam}
                        />
                        <Form.Text>
                          Use IAM for authentication. If this is checked, the
                          Study Tracker will use the IAM role attached to the
                          EC2 instance to authenticate with AWS.
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

AWSIntegrationFormModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
  handleFormSubmit: PropTypes.func.isRequired,
  formikRef: PropTypes.object.isRequired,
  selectedIntegration: PropTypes.object
}

export default AWSIntegrationFormModal;