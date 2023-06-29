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
import axios from "axios";
import AsyncSelect from "react-select/async";

const S3BucketFormModal = ({
    isOpen,
    setIsOpen,
    selectedBucket,
    handleFormSubmit,
    formikRef
}) => {

  const bucketSchema = yup.object().shape({
    displayName: yup.string()
      .required("Display name is required")
      .max(255, "Display name must be less than 255 characters"),
    bucketName: yup.string()
    .required("Bucket name is required")
    .max(255, "Bucket name must be less than 255 characters"),
    type: yup.string(),
    active: yup.boolean()
  });

  const bucketDefault = {
    displayName: null,
    bucketName: null,
    active: true
  }

  const bucketAutocomplete = (input, callback) => {
    axios.get("/api/internal/autocomplete/aws/s3?q=" + input)
    .then(response => {
      const options = response.data
      .map(bucket => {
        return {label: bucket, value: bucket, obj: bucket}
      })
      .sort((a, b) => {
        if (a.label < b.label) return -1;
        else if (a.label > b.label) return 1;
        else return 0;
      });
      callback(options);
    })
    .catch(e => {
      console.error(e);
    })
  }

  return (
      <Formik
          initialValues={selectedBucket || bucketDefault}
          onSubmit={handleFormSubmit}
          validationSchema={bucketSchema}
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
                {values.id ? 'Edit' : 'Add'} S3 Bucket
              </Modal.Header>
              <Modal.Body className={"mb-3"}>
                <FormikForm autoComplete={"off"}>

                  <FormikFormErrorNotification />

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Bucket Name *</Form.Label>
                        <AsyncSelect
                            placeholder="Search-for and select an S3 bucket..."
                            className={"react-select-container"}
                            classNamePrefix="react-select"
                            loadOptions={bucketAutocomplete}
                            value={values.bucketName ? {label: values.bucketName, value: values.bucketName, obj: values.bucketName} : null}
                            onChange={(selected) => {
                              setFieldValue("bucketName", selected ? selected.obj : null);
                            }}
                            isDisabled={!!values.id}
                            defaultOptions={true}
                            isClearable={true}
                        />
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Display Name *</Form.Label>
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
                        <Form.Text>
                          Provide a display name for your bucket.
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

S3BucketFormModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
  selectedBucket: PropTypes.object,
  handleFormSubmit: PropTypes.func.isRequired,
  formikRef: PropTypes.object.isRequired
}

export default S3BucketFormModal;