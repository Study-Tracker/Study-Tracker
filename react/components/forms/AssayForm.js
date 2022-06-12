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
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import {StatusDropdown} from "./status";
import {statuses} from "../../config/statusConstants";
import {UserInputs} from "./users";
import swal from 'sweetalert';
import {history} from '../../App';
import {AssayTypeDropdown} from "./assayTypes";
import {NotebookEntryTemplatesDropdown} from './notebookEntryTemplates';
import {AssayTypeFieldCaptureInputList} from "./assayTypeFieldCapture";
import AttributeInputs from "./attributes";
import {TaskInputs} from "./tasks";
import {LoadingOverlay} from "../loading";
import ReactQuill from "react-quill";
import {Breadcrumbs} from "../common";
import {FormGroup} from "./common";
import {getCsrfToken} from "../../config/csrf";
import PropTypes from "prop-types";

export default class AssayForm extends React.Component {

  constructor(props) {

    super(props);

    let assay = props.assay || {
      status: statuses.IN_PLANNING.value,
      users: this.props.study.users,
      owner: this.props.study.owner,
      createdBy: this.props.user,
      lastModifiedBy: this.props.user,
      fields: {},
      tasks: [],
      attributes: {},
      notebookTemplateId: !!props.defaultNotebookTemplate
          ? props.defaultNotebookTemplate.templateId : null
    };
    assay.lastModifiedBy = this.props.user;

    this.state = {
      assay: assay,
      validation: {
        nameIsValid: true,
        descriptionIsValid: true,
        startDateIsValid: true,
        usersIsValid: true,
        ownerIsValid: true
      },
      showLoadingOverlay: false,

      isUpdateModeOn: !!assay.id,
      baseUrl: '/api/study/' + this.props.study.code + '/assays/',
    };

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleFormUpdate = this.handleFormUpdate.bind(this);
    this.handleTemplateSelection = this.handleTemplateSelection.bind(this);
    this.validateForm = this.validateForm.bind(this);
    this.handleFieldUpdate = this.handleFieldUpdate.bind(this);
  }

  get submitUrl() {
    return this.state.isUpdateModeOn
        ? this.state.baseUrl + this.state.assay.id
        : this.state.baseUrl
  }

  get submitMethod() {
    return this.state.isUpdateModeOn
        ? 'PUT'
        : 'POST';
  }

  /**
   * Updates the study state when an input is changed.
   *
   * @param data
   */
  handleFormUpdate(data) {
    console.log(data);
    const assay = {
      ...this.state.assay,
      ...data
    };
    console.log(assay);
    this.setState({
      assay: assay
    })
  }

  handleTemplateSelection(selectedItem) {
    this.setState({
      assay: {
        ...this.state.assay,
        notebookTemplateId: selectedItem
            ? selectedItem.value
            : '',
      },
    })
  }

  handleFieldUpdate(data) {
    const fields = {
      ...this.state.assay.fields,
      ...data
    };
    this.handleFormUpdate({"fields": fields})
  }

  validateForm(assay) {
    let isError = false;
    let validation = this.state.validation;

    // Name
    if (!assay.name) {
      isError = true;
      validation.nameIsValid = false;
    } else {
      validation.nameIsValid = true;
    }

    // Description
    if (!assay.description) {
      isError = true;
      validation.descriptionIsValid = false;
    } else {
      validation.descriptionIsValid = true;
    }

    // Start Date
    if (!assay.startDate) {
      isError = true;
      validation.startDateIsValid = false;
    } else {
      validation.startDateIsValid = true;
    }

    // Study team
    if (!assay.users || assay.users.length === 0) {
      isError = true;
      validation.usersIsValid = false;
    } else {
      validation.usersIsValid = true;
    }

    // Owner
    if (!!assay.owner) {
      validation.isOwnerValid = true;
    } else {
      isError = true;
      validation.ownerIsValid = false;
    }

    this.setState({
      validation: validation
    });
    return isError;
  }

  handleSubmit() {

    let assay = this.state.assay;

    let isError = this.validateForm(assay);
    console.log(this.state);

    if (isError) {

      swal("Looks like you forgot something...",
          "Check that all of the required inputs have been filled and then try again.",
          "warning");
      console.warn("Validation failed.");

    } else {

      // Sort the tasks
      if (!!assay.tasks && assay.tasks.length > 0) {
        const tasks = document.getElementById("task-input-container").children;
        if (tasks.length > 0) {
          for (let i = 0; i < tasks.length; i++) {
            let idx = parseInt(tasks[i].dataset.index);
            assay.tasks[idx].order = i;
          }
        }
      }

      this.setState({showLoadingOverlay: true});

      fetch(this.submitUrl, {
        method: this.submitMethod,
        headers: {
          "Content-Type": "application/json",
          "X-XSRF-TOKEN": getCsrfToken()
        },
        body: JSON.stringify(assay)
      })
      .then(async response => {

        const json = await response.json();
        console.log(json);
        if (response.ok) {
          history.push(
              "/study/" + this.props.study.code + "/assay/" + json.code);
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
        history.push("/");
      }
    });
  }

  render() {

    return (
        <Container fluid className="animated fadeIn max-width-1200">

          <LoadingOverlay
              isVisible={this.state.showLoadingOverlay}
              message={"Saving your assay..."}
          />

          <Row>
            <Col>
              <Breadcrumbs crumbs={[
                {label: "Home", url: "/"},
                {
                  label: "Study " + this.props.study.code,
                  url: "/study/" + this.props.study.code
                },
                {label: !!this.state.assay.id ? "Edit Assay" : "New Assay"}
              ]}/>
            </Col>
          </Row>

          <Row className="justify-content-end align-items-center">
            <Col>
              <h3>{!!this.state.assay.id ? "Edit Assay" : "New Assay"}</h3>
            </Col>
          </Row>

          <Row>
            <Col xs={12}>
              <Card>

                <Card.Header>
                  <Card.Title tag="h5">Assay Overview</Card.Title>
                  <h6 className="card-subtitle text-muted">Select the assay type
                    that best reflects the experiment being done. If an accurate
                    option does not exist, or if this is a new assay type,
                    select 'Generic'. If Assay names should be descriptive, but
                    do not need to be unique. Describe the
                    objective of your assay in one or two sentences. Select the
                    status that best reflects the current state of your assay.
                    Choose the date your assay is expected to start. If the
                    assay has already completed, you may select an end
                    date.</h6>
                </Card.Header>

                <Card.Body>
                  <Form>

                    {/*Overview*/}
                    <Row>

                      <Col sm={7}>
                        <FormGroup>
                          <Form.Label>Name *</Form.Label>
                          <Form.Control
                              type="text"
                              isInvalid={!this.state.validation.nameIsValid}
                              defaultValue={this.state.assay.name || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"name": e.target.value})}
                              disabled={!!this.state.assay.id}
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            Name must not be empty.
                          </Form.Control.Feedback>
                        </FormGroup>
                      </Col>

                      <Col sm={5}>
                        <AssayTypeDropdown
                            assayTypes={this.props.assayTypes}
                            selectedType={!!this.state.assay.assayType
                                ? this.state.assay.assayType.id : -1}
                            onChange={this.handleFormUpdate}
                            disabled={!!this.state.assay.id}
                        />
                      </Col>

                    </Row>

                    <Row>
                      <Col sm={7}>
                        <FormGroup>
                          <Form.Label>Description *</Form.Label>
                          <ReactQuill
                              theme="snow"
                              defaultValue={this.state.assay.description || ''}
                              onChange={content => this.handleFormUpdate(
                                  {"description": content})}
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            Description must not be empty.
                          </Form.Control.Feedback>
                          <Form.Text>
                            Provide a brief description of your assay.
                          </Form.Text>
                        </FormGroup>
                      </Col>
                      <Col sm={5}>
                        {
                          this.props.features
                          && this.props.features.notebook
                          && this.props.features.notebook.isEnabled
                          && !this.state.isUpdateModeOn ? (
                            <NotebookEntryTemplatesDropdown
                                notebookTemplates={this.props.notebookTemplates}
                                defaultTemplate={this.props.defaultNotebookTemplate}
                                onChange={this.handleTemplateSelection}
                            />
                          ): ""
                        }

                        <StatusDropdown
                            selected={this.state.assay.status}
                            onChange={this.handleFormUpdate}
                        />

                        <FormGroup>
                          <Form.Label>Start Date *</Form.Label>
                          <DatePicker
                              maxlength="2"
                              className={"form-control"}
                              invalid={!this.state.validation.startDateIsValid}
                              wrapperClassName="form-control"
                              selected={this.state.assay.startDate}
                              onChange={(date) => this.handleFormUpdate(
                                  {"startDate": date})}
                              isClearable={true}
                              dateFormat=" MM / dd / yyyy"
                              placeholderText="MM / DD / YYYY"
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            You must select a Start Date.
                          </Form.Control.Feedback>
                          <Form.Text>
                            Select the date your assay began or is expected to
                            begin.
                          </Form.Text>
                        </FormGroup>

                        <FormGroup>
                          <Form.Label>End Date</Form.Label>
                          <DatePicker
                              maxlength="2"
                              className="form-control"
                              wrapperClassName="form-control"
                              selected={this.state.assay.endDate}
                              onChange={(date) => this.handleFormUpdate(
                                  {"endDate": date})}
                              isClearable={true}
                              dateFormat=" MM / dd / yyyy"
                              placeholderText="MM / DD / YYYY"
                          />
                          <Form.Text>Select the date your assay was
                            completed.</Form.Text>
                        </FormGroup>

                      </Col>
                    </Row>

                    <Row>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    {/* Legacy study assay */}

                    {
                      !!this.props.study.legacy
                          ? (
                              <React.Fragment>
                                <Row>

                                  <Col md={12}>
                                    <h5 className="card-title">Legacy Study</h5>
                                    <h6 className="card-subtitle text-muted">Studies
                                      created
                                      prior to the introduction of Study Tracker are
                                      considered legacy. Enabling this option allows
                                      you to
                                      specify certain attributes that would
                                      otherwise be
                                      automatically generated.</h6>
                                    <br/>
                                  </Col>

                                  <Col md={12}>

                                    <FormGroup>
                                      <Form.Label>Notebook URL</Form.Label>
                                      <Form.Control
                                          type="text"
                                          defaultValue={
                                            !!this.state.assay.notebookFolder
                                            && !!this.state.assay.notebookFolder.url
                                                ? this.state.assay.notebookFolder.url
                                                : ''
                                          }
                                          onChange={(e) => this.handleFormUpdate(
                                              {
                                                "notebookEntry": {
                                                  label: "ELN",
                                                  url: e.target.value
                                                }
                                              })}
                                      />
                                      <Form.Text>If the study already has an ELN
                                        entry,
                                        provide the URL here.</Form.Text>
                                    </FormGroup>
                                  </Col>

                                </Row>

                                <Row>
                                  <Col>
                                    <hr/>
                                  </Col>
                                </Row>

                              </React.Fragment>
                          ) : ''
                    }

                    {/* Assay type fields */}

                    {
                      !!this.state.assay.assayType
                      && this.state.assay.assayType.fields.length > 0
                          ? (
                              <React.Fragment>

                                <Row>

                                  <Col md={12}>
                                    <h5 className="card-title">
                                      {this.state.assay.assayType.name} Fields
                                    </h5>
                                    <h6 className="card-subtitle text-muted">
                                      {this.state.assay.assayType.description}
                                    </h6>
                                    <br/>
                                  </Col>

                                </Row>

                                <AssayTypeFieldCaptureInputList
                                    assayType={this.state.assay.assayType}
                                    assayFields={this.state.assay.fields}
                                    handleUpdate={this.handleFieldUpdate}
                                />

                                <Row>
                                  <Col>
                                    <hr/>
                                  </Col>
                                </Row>

                              </React.Fragment>
                          )
                          : ''
                    }

                    {/* Tasks */}

                    <Row>
                      <Col sm={12}>
                        <h5 className="card-title">Tasks</h5>
                        <h6 className="card-subtitle text-muted">
                          You can define an ordered list of tasks that must be
                          completed for your assay here. Task status changes are
                          captured with user-associated timestamps.
                        </h6>
                        <br/>
                      </Col>
                    </Row>

                    <TaskInputs
                        tasks={this.state.assay.tasks}
                        handleUpdate={(tasks) => {
                          this.handleFormUpdate({tasks: tasks})
                        }}
                    />

                    <Row>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    {/* Assay Team */}
                    <Row>
                      <Col sm={12}>
                        <h5 className="card-title">Assay Team</h5>
                        <h6 className="card-subtitle text-muted">Who will be
                          working on this assay? One user must be assigned as
                          the assay owner. This person will be the primary
                          contact person for the experiment.</h6>
                        <br/>
                      </Col>

                      <Col sm={12}>
                        <UserInputs
                            users={this.state.assay.users || []}
                            owner={this.state.assay.owner}
                            onChange={this.handleFormUpdate}
                            isValid={this.state.validation.usersIsValid
                                && this.state.validation.ownerIsValid}
                        />
                      </Col>

                    </Row>

                    <Row>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    {/* Attributes */}

                    <Row>

                      <Col md={12}>
                        <h5 className="card-title">Assay Attributes</h5>
                        <h6 className="card-subtitle text-muted">
                          Key-value attributes for adding additional information
                          about the assay, or for adding application-aware
                          attributes for external integrations (for example, ELN
                          identifiers). You can add as many or as few attributes
                          as you'd like. Attribute values should not be left
                          empty. All values are saved as simple character
                          strings.
                        </h6>
                        <br/>
                      </Col>

                    </Row>

                    <AttributeInputs
                        attributes={this.state.assay.attributes}
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

AssayForm.propTypes = {
  features: PropTypes.object,
}