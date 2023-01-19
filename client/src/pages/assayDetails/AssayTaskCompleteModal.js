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

import {Button, Col, Modal, Row} from "react-bootstrap";
import React from "react";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import CustomFieldCaptureInputList
  from "../../common/forms/customFields/CustomFieldCaptureInputList";
import {DismissableAlert} from "../../common/errors";

const AssayTaskCompleteModal = ({
    modalIsOpen,
    setModalIsOpen,
    task,
    handleFormSubmit,
    formikRef
}) => {

  const defaultValues = {
    label: "",
    fields: [],
    data: {}
  };

  const taskSchema = yup.object().shape({
    data: yup.object()
    .test(
        "required fields",
        "Required field inputs are missing",
        (value, context) => {
          const requiredFields = context.parent.fields
              ? context.parent.fields.filter(f => f.required)
              : [];
          return requiredFields.every(f => {
            return value[f.fieldName] !== undefined
                && value[f.fieldName] !== null
                && value[f.fieldName] !== ""
          });
        }
    ),
  })

  return (
      <Formik
          initialValues={task || defaultValues}
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
                <Modal.Title>Complete task: {values.label}</Modal.Title>
              </Modal.Header>
              <Modal.Body>
                <FormikForm autoComplete={"off"}>

                  <Row>
                    <Col>
                      <p>Please provide any required input to complete the task.</p>
                    </Col>
                  </Row>

                  {
                    errors && errors.data && (
                        <DismissableAlert color={"warning"} message={"One or more required fields has errors."}/>
                      )
                  }

                  <CustomFieldCaptureInputList
                      fields={values.fields}
                      data={values.data}
                      handleUpdate={data => {
                        setFieldValue("data", {
                          ...values.data,
                          ...data
                        });
                      }}
                      errors={errors}
                      colWidth={12}
                  />

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

AssayTaskCompleteModal.propTypes = {

}

export default AssayTaskCompleteModal;
