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

import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import React from "react";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import {FormGroup} from "../../../common/forms/common";
import Select from "react-select";

const ApiUserFormModal = ({
  modalIsOpen,
  setModalIsOpen,
  user,
  handleFormSubmit,
  formikRef
}) => {

  const userDefaults = {
    username: "",
    displayName: "",
    type: "API_USER",
    admin: false,
    credentialsExpired: true
  }

  const userSchema = yup.object().shape({
    username: yup.string()
      .required("Username is required")
      .max(255, "Username must not be larger than 255 characters")
      .matches(/^[a-zA-Z0-9-_]+$/, "Username must only contain letters, numbers, hyphens, and underscores"),
    displayName: yup.string()
      .required("Display name is required")
      .max(255, "Display name must not be larger than 255 characters"),
  })

  console.log("User to edit", user);

  return (
      <Formik
          initialValues={user || userDefaults}
          onSubmit={handleFormSubmit}
          validationSchema={userSchema}
          innerRef={formikRef}
          enableReinitialize={true}
      >
        {({
          values,
          errors,
          touched,
          handleChange,
          handleSubmit,
          isSubmitting,
          setFieldValue
        }) => (
            <Modal
                show={modalIsOpen}
                onHide={() => setModalIsOpen(false)}
            >
              <Modal.Header closeButton>
                {
                  user ?
                      (
                          <>
                            API User:&nbsp;
                            <strong>{user.displayName}</strong>&nbsp;(<code>{user.username}</code>)
                          </>
                      ) : (
                          <>New API User</>
                      )
                }

              </Modal.Header>
              <Modal.Body>
                <FormikForm autoComplete={"off"}>

                  <Row>

                    <Col md={12}>
                      <FormGroup>
                        <Form.Label>Display Name *</Form.Label>
                        <Form.Control
                            type="text"
                            name={"displayName"}
                            className={(errors.displayName && touched.displayName) ? "is-invalid" : ""}
                            isInvalid={touched.displayName && errors.displayName}
                            value={values.displayName}
                            onChange={e => {
                              setFieldValue("displayName", e.target.value);
                              setFieldValue("username", e.target.value.toLowerCase().replace(/\s+/, "-").replace(/[^a-z0-9-_]/g, ""));
                            }}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.displayName}
                        </Form.Control.Feedback>
                      </FormGroup>
                    </Col>

                    <Col md={12}>
                      <FormGroup>
                        <Form.Label>Username *</Form.Label>
                        <Form.Control
                            type="text"
                            name={"username"}
                            className={(errors.username && touched.username) ? "is-invalid" : ""}
                            isInvalid={touched.username && errors.username}
                            value={values.username}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.username}
                        </Form.Control.Feedback>
                      </FormGroup>
                    </Col>

                    <Col md={12}>
                      <FormGroup>
                        <Form.Label>Role</Form.Label>
                        <Select
                            className="react-select-container"
                            classNamePrefix="react-select"
                            options={[
                              {
                                value: false,
                                label: "User"
                              },
                              {
                                value: true,
                                label: "Admin"
                              }
                            ]}
                            value={
                              values.admin ?
                                  {
                                    value: true,
                                    label: "Admin"
                                  } : {
                                    value: false,
                                    label: "User"
                                  }
                            }
                            onChange={(selected) => setFieldValue("admin", selected.value)}
                        />
                      </FormGroup>
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
                    onClick={handleSubmit}
                    disabled={isSubmitting}
                >
                  { isSubmitting ? "Saving..." : "Save" }
                </Button>

              </Modal.Footer>

            </Modal>
          )}
      </Formik>
  )
}

export default ApiUserFormModal;