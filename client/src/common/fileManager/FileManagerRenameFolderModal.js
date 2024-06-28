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
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import {Form as FormikForm, Formik} from "formik";
import PropTypes from "prop-types";
import * as yup from "yup";
import {DismissableAlert} from "../errors";

const formDefaults = {
  name: "",
  path: "",
};
const formSchema = yup.object().shape({
  name: yup.string().required("Folder name is required"),
  path: yup.string().required("Folder path is required"),
});

const FileManagerRenameFolderModal = ({
  folder,
  isOpen,
  setModalIsOpen,
  handleFormSubmit,
  error
}) => {

  console.debug("SelectedFolder", folder);

  return (
      <Formik
          initialValues={folder || formDefaults}
          validationSchema={formSchema}
          onSubmit={handleFormSubmit}
          enableReinitialize={true}
      >
        {({
          values,
          errors,
          touched,
          handleChange,
          handleSubmit,
          isSubmitting,
          setFieldValue,
        }) => (
            <Modal show={isOpen} onHide={() => setModalIsOpen(false)}>

              <Modal.Header closeButton>
                <Modal.Title>Rename Folder</Modal.Title>
              </Modal.Header>

              <Modal.Body className="mb-3">
                <FormikForm>

                  <Row>
                    <Col>
                      <Form.Group>
                        <Form.Label>Folder Name</Form.Label>
                        <Form.Control
                          type="text"
                          name="name"
                          value={values.name}
                          onChange={handleChange}
                          className={touched.name && errors.name ? "is-invalid" : ""}
                        />
                        <Form.Control.Feedback type="invalid">
                          {errors.name}
                        </Form.Control.Feedback>
                      </Form.Group>
                    </Col>
                  </Row>

                  <Row className={"mt-2"}>
                    <Col>
                      {error && (<DismissableAlert color={"danger"} message={error} />)}
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
                    onClick={!isSubmitting ? handleSubmit : null}
                    disabled={isSubmitting}
                >
                  {isSubmitting ? "Renaming..." : "Rename"}
                </Button>
              </Modal.Footer>

            </Modal>
        )}
      </Formik>

  );

}

FileManagerRenameFolderModal.propTypes = {
  folder: PropTypes.object,
  isOpen: PropTypes.bool.isRequired,
  setModalIsOpen: PropTypes.func.isRequired,
  handleFormSubmit: PropTypes.func.isRequired
};

export default FileManagerRenameFolderModal;