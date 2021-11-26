/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import {history} from '../../App';
import {LoadingOverlay} from "../loading";
import Select from "react-select";
import AttributeInputs from "./attributes";
import {Breadcrumbs} from "../common";
import {FormGroup} from "./common";

export default class UserForm extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      user: props.user || {
        admin: false,
        attributes: {},
        credentialsExpired: true
      },
      validation: {
        usernameIsValid: true,
        usernameIsUnique: true,
        nameIsValid: true,
        emailIsValid: true,
        emailIsUnique: true
      },
      showLoadingOverlay: false
    };
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleFormUpdate = this.handleFormUpdate.bind(this);
    this.validateForm = this.validateForm.bind(this);
  }

  /**
   * Updates the user state when an input is changed.
   *
   * @param data
   */
  handleFormUpdate(data) {
    const user = {
      ...this.state.user,
      ...data
    };
    console.log(user);
    this.setState({
      user: user
    })
  }

  validateForm(user) {
    let isError = false;
    let validation = this.state.validation;

    // Name
    if (!user.displayName) {
      isError = true;
      validation.nameIsValid = false;
    } else {
      validation.nameIsValid = true;
    }

    // Username
    if (!user.username) {
      isError = true;
      validation.usernameIsValid = false;
    } else {
      validation.usernameIsValid = true;
    }
    if (!user.id) {
      for (let u of this.props.users) {
        if (!!user.username && u.username.toLowerCase()
            === user.username.toLowerCase()) {
          isError = true;
          validation.usernameIsUnique = false;
        }
      }
    }

    // Email
    if (!user.email) {
      isError = true;
      validation.emailIsValid = false;
    } else {
      validation.emailIsValid = true;
    }
    if (!user.id) {
      for (let u of this.props.users) {
        if (!!user.email && u.email.toLowerCase()
            === user.email.toLowerCase()) {
          isError = true;
          validation.emailIsUnique = false;
        }
      }
    }

    this.setState({
      validation: validation
    });
    return isError;
  }

  handleSubmit() {

    let isError = this.validateForm(this.state.user);
    console.log(this.state);

    if (isError) {

      swal("Looks like you forgot something...",
          "Check that all of the required inputs have been filled and then try again.",
          "warning");
      console.warn("Validation failed.");

    } else {

      const isUpdate = !!this.state.user.id;
      const url = isUpdate
          ? "/api/user/" + this.state.user.id
          : "/api/user";
      this.setState({showLoadingOverlay: true});

      fetch(url, {
        method: isUpdate ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(this.state.user)
      })
      .then(async response => {

        const json = await response.json();
        console.log(json);
        if (response.ok) {
          // history.push("/user/" + json.id);
          history.push("/admin?active=users");
        } else {
          this.setState({showLoadingOverlay: false})
          swal("Something went wrong",
              !!json.message
                  ? "Error: " + json.message :
                  "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
          );
          console.error("Request failed.");
        }

      }).catch(e => {
        this.setState({showLoadingOverlay: false})
        swal(
            "Something went wrong",
            "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
        );
        console.error(e);
      });
    }
  }

  handleCancel() {
    swal({
      title: "Are you sure you want to leave the page?",
      text: "Any unsaved work will be lost.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        history.push("/admin?active=users");
      }
    });
  }

  render() {

    return (
        <Container fluid className="animated fadeIn max-width-1200">

          <LoadingOverlay
              isVisible={this.state.showLoadingOverlay}
              message={"Updating user registration..."}
          />

          <Row>
            <Col>
              {
                !!this.state.user.id
                    ? (
                        <Breadcrumbs crumbs={[
                          {label: "Home", url: "/"},
                          {label: "Admin Dashboard", url: "/admin"},
                          {label: "User Details", url: "/user/" + this.state.user.id},
                          {label:" Edit User"}
                        ]} />
                    )
                    : (
                        <Breadcrumbs crumbs={[
                          {label: "Home", url: "/"},
                          {label: "Admin Dashboard", url: "/admin"},
                          {label:" New User"}
                        ]} />
                    )
              }
            </Col>
          </Row>

          <Row className="justify-content-end align-items-center">
            <Col>
              <h3>
                {
                  !!this.state.user.id
                      ? "Edit User"
                      : "New User"
                }
              </h3>
            </Col>
          </Row>

          <Row>
            <Col xs={12}>
              <Card>

                <Card.Header>
                  <Card.Title tag="h5">User Details</Card.Title>
                  <h6 className="card-subtitle text-muted">
                    Users must have unique usernames and email addresses. Users
                    granted admin privileges can create or modify programs,
                    users,
                    and other system attributes.
                  </h6>
                </Card.Header>

                <Card.Body>
                  <Form className="user-form">

                    <Row>

                      <Col md={6}>
                        <FormGroup>
                          <Form.Label>Name *</Form.Label>
                          <Form.Control
                              type="text"
                              isInvalid={!this.state.validation.nameIsValid}
                              defaultValue={this.state.user.displayName || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"displayName": e.target.value})}
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            {"Name must not be empty."}
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
                              defaultValue={
                                this.state.user.admin ?
                                    {
                                      value: true,
                                      label: "Admin"
                                    } : {
                                      value: false,
                                      label: "User"
                                    }
                              }
                              onChange={(selected) => this.handleFormUpdate(
                                  {"admin": selected.value})}
                          />
                        </FormGroup>
                      </Col>

                      <Col md={6}>
                        <FormGroup>
                          <Form.Label>Username *</Form.Label>
                          <Form.Control
                              type="text"
                              isInvalid={!this.state.validation.usernameIsValid
                              || !this.state.validation.usernameIsUnique}
                              defaultValue={this.state.user.username || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"username": e.target.value})}
                              disabled={!!this.state.user.id}
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            {
                              !this.state.validation.usernameIsUnique
                                  ? "A user with this username already exists."
                                  : "Name must not be empty."
                            }
                          </Form.Control.Feedback>
                          <Form.Text>Must be unique.</Form.Text>
                        </FormGroup>
                      </Col>

                      <Col md={6}>
                        <FormGroup>
                          <Form.Label>Email *</Form.Label>
                          <Form.Control
                              type="text"
                              isInvalid={!this.state.validation.emailIsValid
                              || !this.state.validation.emailIsUnique}
                              defaultValue={this.state.user.email || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"email": e.target.value})}
                              disabled={!!this.state.user.id}
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            {
                              !this.state.validation.emailIsUnique
                                  ? "A user with this emil address already exists."
                                  : "Email must not be empty."
                            }
                          </Form.Control.Feedback>
                        </FormGroup>
                      </Col>

                      <Col md={6}>
                        <FormGroup>
                          <Form.Label>Title</Form.Label>
                          <Form.Control
                              type="text"
                              defaultValue={this.state.user.title || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"title": e.target.value})}
                          />
                        </FormGroup>
                      </Col>

                      <Col md={6}>
                        <FormGroup>
                          <Form.Label>Department</Form.Label>
                          <Form.Control
                              type="text"
                              defaultValue={this.state.user.department || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"department": e.target.value})}
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
                          as you'd like. Attribute values should not be left
                          empty. All values are saved as simple character
                          strings.
                        </h6>
                        <br/>
                      </Col>

                    </Row>

                    <AttributeInputs
                        attributes={this.state.user.attributes}
                        handleUpdate={(attributes) => this.handleFormUpdate({
                          attributes: attributes
                        })}
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
                          <Button size="lg" variant="primary"
                                  onClick={this.handleSubmit}>Submit</Button>
                          &nbsp;&nbsp;
                          <Button size="lg" variant="secondary"
                                  onClick={this.handleCancel}>Cancel</Button>
                        </FormGroup>
                      </Col>
                    </Row>

                  </Form>
                </Card.Body>
              </Card>
            </Col>
          </Row>

        </Container>
    );
  }

}