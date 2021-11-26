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
import {AssayTypeFieldInputs} from "./assayTypeFieldCreation";
import {TaskInputs} from "./tasks";
import {Breadcrumbs} from "../common";
import {FormGroup} from "./common";

export default class AssayTypeForm extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      assayType: props.assayType || {
        active: true,
        fields: [],
        tasks: [],
        attributes: {}
      },
      assayTypes: props.assayTypes,
      validation: {
        nameIsValid: true,
        nameIsUnique: true
      },
      showLoadingOverlay: false
    };
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleFormUpdate = this.handleFormUpdate.bind(this);
    this.validateForm = this.validateForm.bind(this);
  }

  /**
   * Updates the state when an input is changed.
   *
   * @param data
   */
  handleFormUpdate(data) {
    const assayType = {
      ...this.state.assayType,
      ...data
    };
    console.log(assayType);
    this.setState({
      assayType: assayType
    })
  }

  validateForm(assayType) {
    let isError = false;
    let validation = this.state.validation;

    // Name
    if (!assayType.name) {
      isError = true;
      validation.nameIsValid = false;
    } else {
      validation.nameIsValid = true;
    }

    if (!assayType.id) {
      for (let a of this.state.assayTypes) {
        if (a.name === assayType.name) {
          validation.nameIsUnique = false;
          isError = true;
        }
      }
    }

    this.setState({
      validation: validation
    });
    return isError;
  }

  handleSubmit() {

    let assayType = this.state.assayType;

    let isError = this.validateForm(assayType);

    // Get the order of tasks
    if (!!assayType.tasks && assayType.tasks.length > 0) {
      const tasks = document.getElementById("task-input-container").children;
      if (tasks.length > 0) {
        for (let i = 0; i < tasks.length; i++) {
          let idx = parseInt(tasks[i].dataset.index);
          assayType.tasks[idx].order = i;
        }
      }
    }

    console.log(assayType);

    if (isError) {

      swal("Looks like you forgot something...",
          "Check that all of the required inputs have been filled and then try again.",
          "warning");
      console.warn("Validation failed.");

    } else {

      const isUpdate = !!assayType.id;
      const url = isUpdate
          ? "/api/assaytype/" + assayType.id
          : "/api/assaytype";
      this.setState({showLoadingOverlay: true});

      fetch(url, {
        method: isUpdate ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(assayType)
      })
      .then(async response => {

        if (response.ok) {
          history.push("/admin?active=assay-types");
        } else {
          this.setState({showLoadingOverlay: false})
          const json = await response.json();
          console.log(json);
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
        history.push("/admin?active=assay-types");
      }
    });
  }

  render() {

    return (
        <Container fluid className="animated fadeIn max-width-1200">

          <LoadingOverlay
              isVisible={this.state.showLoadingOverlay}
              message={"Updating assay type registration..."}
          />

          <Row>
            <Col>
              {
                !!this.state.assayType.id
                    ? (
                        <Breadcrumbs
                            crumbs={[
                              {label: "Home", url: "/"},
                              {label: "Admin Dashboard", url: "/admin"},
                              {label: "Assay Type Detail", url: "/assaytype/" + this.state.assayType.id},
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
                  !!this.state.assayType.id
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
                  <Form className="assay-type-form">

                    <Row>

                      <Col md="6">
                        <FormGroup>
                          <Form.Label>Name *</Form.Label>
                          <Form.Control
                              type="text"
                              disabled={!!this.state.assayType.id}
                              isInvalid={!this.state.validation.nameIsValid}
                              defaultValue={this.state.assayType.name || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"name": e.target.value})}
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            {
                              !this.state.validation.nameIsUnique
                                  ? "Name must be unique."
                                  : "Name must not be empty."
                            }
                          </Form.Control.Feedback>
                        </FormGroup>
                      </Col>

                      <Col md="6">
                        <FormGroup>
                          <Form.Label>Status</Form.Label>
                          <Select
                              className="react-select-container"
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
                                this.state.assayType.active ?
                                    {
                                      value: true,
                                      label: "Active"
                                    } : {
                                      value: false,
                                      label: "Inactive"
                                    }
                              }
                              onChange={(selected) => this.handleFormUpdate(
                                  {"admin": selected.value})}
                          />
                        </FormGroup>
                      </Col>

                      <Col md="6">
                        <FormGroup>
                          <Form.Label>Description *</Form.Label>
                          <Form.Control
                              as="textarea"
                              defaultValue={this.state.assayType.description
                              || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"description": e.target.value})}
                              rows={5}
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            Description must not be empty.
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
                        <h5 className="card-title">Input Fields</h5>
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

                    <AssayTypeFieldInputs
                        fields={this.state.assayType.fields}
                        handleUpdate={(fields) => {
                          this.handleFormUpdate({fields: fields})
                        }}
                    />

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

                    <TaskInputs
                        tasks={this.state.assayType.tasks}
                        handleUpdate={(tasks) => {
                          this.handleFormUpdate({tasks: tasks})
                        }}
                    />

                    <Row>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    <Row>

                      <Col md="12">
                        <h5 className="card-title">Attributes</h5>
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
                        handleUpdate={(attributes) => {
                          this.handleFormUpdate({
                            attributes: attributes
                          })
                        }}
                        attributes={this.state.assayType.attributes}
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