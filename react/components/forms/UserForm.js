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
import {
  Breadcrumb,
  BreadcrumbItem,
  Button,
  Card,
  CardBody,
  CardHeader,
  CardTitle,
  Col,
  Container,
  Form,
  FormFeedback,
  FormGroup,
  FormText,
  Input,
  Label,
  Row
} from "reactstrap";
import swal from 'sweetalert';
import {history} from '../../App';
import {LoadingOverlay} from "../loading";
import Select from "react-select";

export default class UserForm extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      user: props.user || {
        admin: false,
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
          history.push("/user/" + json.id);
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
        history.push("/users");
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
                        <Breadcrumb>
                          <BreadcrumbItem>
                            <a href={"/"}>Home</a>
                          </BreadcrumbItem>
                          <BreadcrumbItem>
                            <a href={"/user/" + this.state.user.id}>
                              User Detail
                            </a>
                          </BreadcrumbItem>
                          <BreadcrumbItem active>Edit User</BreadcrumbItem>
                        </Breadcrumb>
                    )
                    : (
                        <Breadcrumb>
                          <BreadcrumbItem>
                            <a href={"/users"}>Users</a>
                          </BreadcrumbItem>
                          <BreadcrumbItem active>New User</BreadcrumbItem>
                        </Breadcrumb>
                    )
              }
            </Col>
          </Row>

          <Row className="justify-content-end align-items-center">
            <Col>
              <h1>
                {
                  !!this.state.user.id
                      ? "Edit User"
                      : "New User"
                }
              </h1>
            </Col>
          </Row>

          <Row>
            <Col xs="12">
              <Card>

                <CardHeader>
                  <CardTitle tag="h5">User Details</CardTitle>
                  <h6 className="card-subtitle text-muted">
                    Users must have unique usernames and email addresses. Users
                    granted admin privileges can create or modify programs,
                    users,
                    and other system attributes.
                  </h6>
                </CardHeader>

                <CardBody>
                  <Form className="user-form">

                    <Row form>

                      <Col md="6">
                        <FormGroup>
                          <Label>Name *</Label>
                          <Input
                              type="text"
                              invalid={!this.state.validation.nameIsValid}
                              defaultValue={this.state.user.displayName || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"displayName": e.target.value})}
                          />
                          <FormFeedback>
                            {"Name must not be empty."}
                          </FormFeedback>
                        </FormGroup>
                      </Col>

                      <Col md="6">
                        <FormGroup>
                          <Label>Role</Label>
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
                          <Label>Username *</Label>
                          <Input
                              type="text"
                              invalid={!this.state.validation.usernameIsValid
                              || !this.state.validation.usernameIsUnique}
                              defaultValue={this.state.user.username || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"username": e.target.value})}
                              disabled={!!this.state.user.id}
                          />
                          <FormFeedback>
                            {
                              !this.state.validation.usernameIsUnique
                                  ? "A user with this username already exists."
                                  : "Name must not be empty."
                            }
                          </FormFeedback>
                          <FormText>Must be unique.</FormText>
                        </FormGroup>
                      </Col>

                      <Col md={6}>
                        <FormGroup>
                          <Label>Email *</Label>
                          <Input
                              type="text"
                              invalid={!this.state.validation.emailIsValid
                              || !this.state.validation.emailIsUnique}
                              defaultValue={this.state.user.email || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"email": e.target.value})}
                              disabled={!!this.state.user.id}
                          />
                          <FormFeedback>
                            {
                              !this.state.validation.emailIsUnique
                                  ? "A user with this emil address already exists."
                                  : "Email must not be empty."
                            }
                          </FormFeedback>
                        </FormGroup>
                      </Col>

                      <Col md={6}>
                        <FormGroup>
                          <Label>Title</Label>
                          <Input
                              type="text"
                              defaultValue={this.state.user.title || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"title": e.target.value})}
                          />
                        </FormGroup>
                      </Col>

                      <Col md={6}>
                        <FormGroup>
                          <Label>Department</Label>
                          <Input
                              type="text"
                              defaultValue={this.state.user.department || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"department": e.target.value})}
                          />
                        </FormGroup>
                      </Col>

                    </Row>

                    {/*Buttons*/}
                    <Row form>
                      <Col className="text-center">
                        <FormGroup>
                          <Button size="lg" color="primary"
                                  onClick={this.handleSubmit}>Submit</Button>
                          &nbsp;&nbsp;
                          <Button size="lg" color="secondary"
                                  onClick={this.handleCancel}>Cancel</Button>
                        </FormGroup>
                      </Col>
                    </Row>

                  </Form>
                </CardBody>
              </Card>
            </Col>
          </Row>

        </Container>
    );
  }

}