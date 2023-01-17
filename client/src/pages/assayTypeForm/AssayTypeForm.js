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

import React from "react";
import 'react-datepicker/dist/react-datepicker.css';
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import swal from 'sweetalert';
import {LoadingOverlay} from "../../common/loading";
import Select from "react-select";
import AttributeInputs from "../../common/forms/AttributeInputs";
import {Breadcrumbs} from "../../common/common";
import {FormGroup} from "../../common/forms/common";
import {useNavigate} from "react-router-dom";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import axios from "axios";
import PropTypes from "prop-types";
import FormikFormErrorNotification
  from "../../common/forms/FormikFormErrorNotification";
import TaskControlsDraggableCardList
  from "../../common/forms/tasks/TaskControlsDraggableCardList";
import CustomFieldDefinitionDraggableCardList
  from "../../common/forms/customFields/CustomFieldDefinitionDraggableCardList";

const AssayTypeForm = props => {

  const {assayTypes} = props;
  const navigate = useNavigate();

  const assayTypeDefaults = {
    name: "",
    description: "",
    active: true,
    fields: [],
    tasks: [],
    attributes: {}
  };

  const assayTypeSchema = yup.object().shape({
    name: yup.string()
      .required("Name is required")
      .max(255, "Name must be less than 255 characters")
      .when("id", {
        is: id => id === undefined || id === null,
        then: yup.string().test(
            "unique",
            "Name must be unique",
            value => !assayTypes.find(d => !!value && d.name.toLowerCase() === value.toLowerCase())
        )
      }),
    description: yup.string()
      .required("Description is required")
      .notOneOf(["<p></p>", "<p><br></p>"], "Description is required"),
    active: yup.boolean(),
    fields: yup.array().of(yup.object())
      .test(
          "not empty",
          "Field labels must not be empty",
          value => !value.find(d => !d.fieldName || d.fieldName.trim() === '')
      ),
    tasks: yup.array().of(yup.object())
      .test(
          "not empty",
          "Task labels must not be empty",
          value => !value.find(d => !d.label || d.label.trim() === '')
      ),
    attributes: yup.object()
      .test(
          "not empty",
          "Attribute names must not be empty",
          value => Object.keys(value).every(d => d && d.trim() !== '')
      ),
  });

  const handleFormSubmit = (values, {setSubmitting}) => {

    console.debug("Form values: ", values);

    const isUpdate = !!values.id;
    const url = isUpdate
        ? "/api/internal/assaytype/" + values.id
        : "/api/internal/assaytype";

    axios({
      url: url,
      method: isUpdate ? "put" : "post",
      data: values
    })
    .then(async response => {
      console.debug("Response: ", response);
      setSubmitting(false);
      navigate("/admin?active=assay-types");
    })
    .catch(e => {
      setSubmitting(false);
      swal(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    });

  }

  const handleCancel = () => {
    swal({
      title: "Are you sure you want to leave the page?",
      text: "Any unsaved work will be lost.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        navigate(-1);
      }
    });
  }

  return (

      <Formik
          initialValues={props.assayType || assayTypeDefaults}
          validationSchema={assayTypeSchema}
          onSubmit={handleFormSubmit}
          validateOnBlur={false}
          validateOnChange={false}
      >
        {({
          values,
          errors,
          touched,
          handleChange,
          setFieldValue,
          isSubmitting,
        }) => (

            <Container fluid className="animated fadeIn max-width-1200">

              <FormikFormErrorNotification />

              <LoadingOverlay
                  isVisible={isSubmitting}
                  message={"Updating assay type registration..."}
              />

              <Row>
                <Col>
                  {
                    !!values.id
                        ? (
                            <Breadcrumbs
                                crumbs={[
                                  {label: "Home", url: "/"},
                                  {label: "Admin Dashboard", url: "/admin"},
                                  {
                                    label: "Assay Type Detail",
                                    url: "/assaytype/" + values.id
                                  },
                                  {label: "Edit Assay Type"}
                                ]}
                            />
                        )
                        : (
                            <Breadcrumbs
                                crumbs={[
                                  {label: "Home", url: "/"},
                                  {label: "Admin Dashboard", url: "/admin"},
                                  {label: "New Assay Type"}
                                ]}
                            />
                        )
                  }
                </Col>
              </Row>

              <Row className="justify-content-end align-items-center">
                <Col>
                  <h3>
                    {
                      !!values.id
                          ? "Edit Assay Type"
                          : "New Assay Type"
                    }
                  </h3>
                </Col>
              </Row>

              <Row>
                <Col xs={12}>
                  <Card>

                    <Card.Header>
                      <Card.Title tag="h5">Assay Type Details</Card.Title>
                      <h6 className="card-subtitle text-muted">
                        Assay Types must have a unique name. Fields, attributes, and
                        tasks are all optional.
                      </h6>
                    </Card.Header>

                    <Card.Body>
                      <FormikForm className="assay-type-form" autoComplete={"off"}>

                        <Row>

                          <Col md="6">
                            <FormGroup>
                              <Form.Label>Name *</Form.Label>
                              <Form.Control
                                  type="text"
                                  name={"name"}
                                  disabled={!!values.id}
                                  isInvalid={!!errors.name}
                                  value={values.name}
                                  onChange={handleChange}
                                  className={!!errors.name ? "is-invalid" : ""}
                              />
                              <Form.Control.Feedback type={"invalid"}>
                                {errors.name}
                              </Form.Control.Feedback>
                            </FormGroup>
                          </Col>

                          <Col md="6">
                            <FormGroup>
                              <Form.Label>Status</Form.Label>
                              <Select
                                  className={"react-select-container"}
                                  name={"active"}
                                  classNamePrefix="react-select"
                                  options={[
                                    {
                                      value: true,
                                      label: "Active"
                                    },
                                    {
                                      value: false,
                                      label: "Inactive"
                                    }
                                  ]}
                                  defaultValue={
                                    values.active ?
                                        {
                                          value: true,
                                          label: "Active"
                                        } : {
                                          value: false,
                                          label: "Inactive"
                                        }
                                  }
                                  onChange={(selected) => setFieldValue("active", selected.value)}
                              />
                            </FormGroup>
                          </Col>

                          <Col md="6">
                            <FormGroup>
                              <Form.Label>Description *</Form.Label>
                              <Form.Control
                                  as="textarea"
                                  name={"description"}
                                  className={!!errors.description ? "is-invalid" : ""}
                                  value={values.description}
                                  onChange={handleChange}
                                  rows={5}
                              />
                              <Form.Control.Feedback type={"invalid"}>
                                {errors.description}
                              </Form.Control.Feedback>
                              <Form.Text>
                                Describe the intended use of this assay type.
                              </Form.Text>
                            </FormGroup>
                          </Col>

                        </Row>

                        <Row>
                          <Col>
                            <hr/>
                          </Col>
                        </Row>

                        <Row>

                          <Col md="12">
                            <h5 className="card-title">
                              Input Fields
                            </h5>
                            <h6 className="card-subtitle text-muted">
                              If your assay type requires additional information be
                              captured, beyond what is available in the standard
                              assay form, you can add input fields here to capture
                              them. Specify each field's display name, back-end
                              attribute name, data type, and whether it is required.
                              When users create a new assay of this type, input
                              forms will be displayed to capture this information.
                              These values will also be prominantly displayed in the
                              assay details page.
                            </h6>
                            <br/>
                          </Col>

                        </Row>

                        <CustomFieldDefinitionDraggableCardList
                            handleUpdate={(fields) => {
                              setFieldValue("fields", fields)
                            }}
                            fields={values.fields}
                            error={errors.fields}
                        />

                        {/*<AssayTypeFieldInputs*/}
                        {/*    fields={values.fields}*/}
                        {/*    handleUpdate={(fields) => {*/}
                        {/*      setFieldValue("fields", fields)*/}
                        {/*    }}*/}
                        {/*    error={errors.fields}*/}
                        {/*/>*/}

                        <Row>
                          <Col>
                            <hr/>
                          </Col>
                        </Row>

                        <Row>

                          <Col md="12">
                            <h5 className="card-title">Tasks</h5>
                            <h6 className="card-subtitle text-muted">
                              You can predefine a list of tasks for assay types by
                              adding them here. When a user creates a new assay,
                              these tasks will be created as defaults, but can be
                              removed or overwritten if desired. You can
                              drag-and-drop
                              the tasks to change their order.
                            </h6>
                            <br/>
                          </Col>

                        </Row>

                        <TaskControlsDraggableCardList
                            tasks={values.tasks}
                            handleUpdate={(tasks) => setFieldValue("tasks", tasks)}
                            errors={errors}
                            touched={touched}
                        />

                        <Row>
                          <Col>
                            <hr/>
                          </Col>
                        </Row>

                        <Row>

                          <Col md="12">
                            <h5 className="card-title">
                              Attributes
                            </h5>
                            <h6 className="card-subtitle text-muted">
                              Key-value attributes associated with your assay type
                              will not be viewable to users, but will be published
                              in assay-related events, and can be used to perform
                              secondary actions or integrate with external systems.
                            </h6>
                            <br/>
                          </Col>

                        </Row>

                        <AttributeInputs
                            handleUpdate={(attributes) => setFieldValue("attributes", attributes)}
                            attributes={values.attributes}
                            error={errors.attributes}
                        />

                        <Row>
                          <Col>
                            <hr/>
                          </Col>
                        </Row>

                        {/*Buttons*/}
                        <Row>
                          <Col className="text-center">
                            <FormGroup>
                              <Button
                                  size="lg"
                                  variant="primary"
                                  type="submit"
                              >
                                Submit
                              </Button>
                              &nbsp;&nbsp;
                              <Button
                                  size="lg"
                                  variant="secondary"
                                  onClick={handleCancel}
                              >
                                Cancel
                              </Button>
                            </FormGroup>
                          </Col>
                        </Row>

                      </FormikForm>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>

            </Container>

        )}
      </Formik>
  );

}

AssayTypeForm.propTypes = {
  assayTypes: PropTypes.array.isRequired,
  assayType: PropTypes.object,
}

export default AssayTypeForm;