/*
 * Copyright 2023 the original author or authors.
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
import {FormGroup} from "../../common/forms/common";

const AssayTaskFormModal = ({
    modalIsOpen,
    setModalIsOpen,
    task,
    handleFormSubmit,
    formikRef
}) => {

  const taskDefaults = {
    label: "",
    status: "TODO",
  };

  const taskSchema = yup.object().shape({
    label: yup.string()
      .required("Label is required")
      .max(1024, "Label must be less than 1024 characters"),
    status: yup.string()
      .required("Status is required")
  })

  return (
      <Formik
          initialValues={task || taskDefaults}
          validationSchema={taskSchema}
          onSubmit={handleFormSubmit}
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
                <Modal.Title>{task ? "Edit Task" : "New Task"}</Modal.Title>
              </Modal.Header>
              <Modal.Body>
                <FormikForm autoComplete={"off"}>

                  <Row>

                    <Col md={12}>
                      <FormGroup>
                        <Form.Label>Label *</Form.Label>
                        <Form.Control
                            type="text"
                            name={"label"}
                            className={(errors.label && touched.label) ? "is-invalid" : ""}
                            isInvalid={touched.label && errors.label}
                            value={values.label}
                            onChange={e => setFieldValue("label", e.target.value)}
                        />
                        <Form.Control.Feedback type="invalid">
                          {errors.label}
                        </Form.Control.Feedback>
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

AssayTaskFormModal.propTypes = {

}

export default AssayTaskFormModal;
