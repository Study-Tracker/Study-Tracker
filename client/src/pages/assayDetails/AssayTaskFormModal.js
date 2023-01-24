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

import {Button, Modal} from "react-bootstrap";
import React from "react";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import TaskControls from "../../common/forms/tasks/TaskControls";

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
    assignedTo: null,
    fields: []
  };

  const taskSchema = yup.object().shape({
    label: yup.string()
      .required("Label is required")
      .max(1024, "Label must be less than 1024 characters"),
    status: yup.string()
      .required("Status is required"),
    assignedTo: yup.object()
      .nullable(true),
    fields: yup.array().of(yup.object())
    .test(
        "not empty",
        "Field labels must not be empty",
        value => !value.find(d => !d.fieldName || d.fieldName.trim() === '')
    ),
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
                size="lg"
            >
              <Modal.Header closeButton>
                <Modal.Title>{task ? "Edit Task" : "New Task"}</Modal.Title>
              </Modal.Header>
              <Modal.Body>
                <FormikForm autoComplete={"off"}>

                  <TaskControls
                      task={values}
                      errors={errors}
                      touched={touched}
                      handleUpdate={(field, value) => setFieldValue(field, value)}
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

AssayTaskFormModal.propTypes = {

}

export default AssayTaskFormModal;
