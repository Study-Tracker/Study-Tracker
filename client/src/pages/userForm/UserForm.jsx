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
import 'react-datepicker/dist/react-datepicker.css';
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import swal from 'sweetalert2';
import {LoadingOverlay} from "../../common/loading";
import Select from "react-select";
import AttributeInputs from "../../common/forms/AttributeInputs";
import {Breadcrumbs} from "../../common/common";
import {FormGroup} from "../../common/forms/common";
import {useNavigate} from "react-router-dom";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import PropTypes from "prop-types";
import axios from "axios";
import FormikFormErrorNotification
  from "../../common/forms/FormikFormErrorNotification";

const UserForm = ({user, users}) => {

  const navigate = useNavigate();
  const userDefaults = {
    displayName: "",
    username: "",
    email: "",
    type: "STANDARD_USER",
    title: "",
    department: "",
    admin: false,
    attributes: {},
    credentialsExpired: true
  };

  const userSchema = yup.object().shape({
    displayName: yup.string().required("Name is required"),
    admin: yup.boolean(),
    username: yup.string()
      .required("Username is required")
      .when("type", {
        is: "STANDARD_USER",
        then: (schema) => schema.test(
            "username matches email",
            "Standard users must have matching usernames and emails.",
            (value, context) => value === context.parent.email
        )
      }),
    email: yup.string()
      .email("Invalid email address")
      .required("Email is required")
      .when("id", {
        is: (value) => !value,
        then: (schema) =>  schema.test(
            "unique",
            "A user with this email is already registered.",
            value => !users.find(u => !!value && u.email && u.email.toLowerCase() === value.toLowerCase())
        )
      }),
    type: yup.string()
      .required("Type is required")
      .oneOf(["STANDARD_USER", "SYSTEM_USER", "API_USER"]),
    title: yup.string()
      .nullable(),
    department: yup.string()
      .nullable(),
    attributes: yup.object()
      .test(
          "not empty",
          "Attribute names must not be empty",
          value => Object.keys(value).every(d => d && d.trim() !== '')
      ),
  });

  const handleFormSubmit = (values, {setSubmitting}) => {

    axios({
      url: `/api/internal/user${values.id ? "/" + values.id : ""}`,
      method: values.id ? "put" : "post",
      data: values
    })
    .then(response => {
      setSubmitting(false);
      const json = response.data;
      console.debug("User", json);
      navigate("/admin?active=users");
    })
    .catch(e => {
      setSubmitting(false);
      swal.fire(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    });

  }

  const handleCancel = () => {
    swal.fire({
      title: "Are you sure you want to leave the page?",
      text: "Any unsaved work will be lost.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val.isConfirmed) {
        navigate(-1);
      }
    });
  }

  return (
      <Formik
          initialValues={user || userDefaults}
          validationSchema={userSchema}
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

              <LoadingOverlay
                  isVisible={isSubmitting}
                  message={"Updating user registration..."}
              />

              <FormikFormErrorNotification />

              <Row>
                <Col>
                  {
                    values.id
                        ? (
                            <Breadcrumbs crumbs={[
                              {label: "Home", url: "/"},
                              {label: "Admin Dashboard", url: "/admin"},
                              {
                                label: "User Details",
                                url: "/user/" + values.id
                              },
                              {label: " Edit User"}
                            ]}/>
                        )
                        : (
                            <Breadcrumbs crumbs={[
                              {label: "Home", url: "/"},
                              {label: "Admin Dashboard", url: "/admin"},
                              {label: " New User"}
                            ]}/>
                        )
                  }
                </Col>
              </Row>

              <Row className="justify-content-end align-items-center">
                <Col>
                  <h3>
                    {values.id ? "Edit User" : "New User"}
                  </h3>
                </Col>
              </Row>

              <Row>
                <Col xs={12}>
                  <Card>

                    <Card.Header>
                      <Card.Title tag="h5">User Details</Card.Title>
                      <h6 className="card-subtitle text-muted">
                        Users must have unique email addresses. Users
                        granted admin privileges can create or modify programs,
                        users, and other system attributes.
                      </h6>
                    </Card.Header>

                    <Card.Body>
                      <FormikForm className="user-form" autoComplete={"off"}>

                        <Row>

                          <Col md={6}>
                            <FormGroup>
                              <Form.Label>Name *</Form.Label>
                              <Form.Control
                                  type="text"
                                  name={"displayName"}
                                  className={(errors.displayName && touched.displayName) ? "is-invalid" : ""}
                                  isInvalid={!!errors.displayName}
                                  value={values.displayName}
                                  onChange={handleChange}
                              />
                              <Form.Control.Feedback type={"invalid"}>
                                {errors.displayName}
                              </Form.Control.Feedback>
                            </FormGroup>
                          </Col>

                          <Col md={6}>
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

                          <Col md={6}>
                            <FormGroup>
                              <Form.Label>Email *</Form.Label>
                              <Form.Control
                                  type="text"
                                  name={"email"}
                                  isInvalid={!!errors.email}
                                  value={values.email}
                                  onChange={e => {
                                    setFieldValue("email", e.target.value);
                                    setFieldValue("username", e.target.value);
                                  }}
                              />
                              <Form.Control.Feedback type={"invalid"}>
                                {errors.email}
                              </Form.Control.Feedback>
                            </FormGroup>
                          </Col>

                          <Col md={6}>
                            <FormGroup>
                              <Form.Label>Username *</Form.Label>
                              <Form.Control
                                  type="text"
                                  name={"username"}
                                  isInvalid={!!errors.username}
                                  value={values.username}
                                  onChange={handleChange}
                                  disabled={true}
                              />
                              <Form.Control.Feedback type={"invalid"}>
                                {errors.username}
                              </Form.Control.Feedback>
                            </FormGroup>
                          </Col>

                          <Col md={6}>
                            <FormGroup>
                              <Form.Label>Title</Form.Label>
                              <Form.Control
                                  type="text"
                                  name={"title"}
                                  value={values.title}
                                  onChange={handleChange}
                              />
                            </FormGroup>
                          </Col>

                          <Col md={6}>
                            <FormGroup>
                              <Form.Label>Department</Form.Label>
                              <Form.Control
                                  type="text"
                                  name={"department"}
                                  value={values.department}
                                  onChange={handleChange}
                              />
                            </FormGroup>
                          </Col>

                        </Row>

                        <Row>
                          <Col>
                            <hr/>
                          </Col>
                        </Row>

                        <Row>

                          <Col md={12}>
                            <h5 className="card-title">User Attributes</h5>
                            <h6 className="card-subtitle text-muted">
                              Key-value attributes for adding additional information
                              about the user, or for adding application-aware
                              attributes for external integrations (for example, ELN
                              user names or identifiers). You can add as many or as
                              few attributes
                              as you&apos;d like. Attribute values should not be left
                              empty. All values are saved as simple character
                              strings.
                            </h6>
                            <br/>
                          </Col>

                        </Row>

                        <AttributeInputs
                            attributes={values.attributes}
                            handleUpdate={(attributes) => setFieldValue("attributes", attributes)}
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

UserForm.propTypes = {
  user: PropTypes.object,
  users: PropTypes.array,
}

export default UserForm;