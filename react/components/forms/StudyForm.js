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
import {ProgramDropdown} from "./programs";
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import {FormGroup} from "./common";
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import {StatusDropdown} from "./status";
import {statuses} from "../../config/statusConstants";
import {UserInputs} from "./users";
import swal from 'sweetalert';
import {history} from '../../App';
import KeywordInputs from "./keywords";
import CollaboratorInputs from "./collaborators";
import ReactQuill from "react-quill";
import {LoadingOverlay} from "../loading";
import {Breadcrumbs} from "../common";

export default class StudyForm extends React.Component {

  constructor(props) {
    super(props);

    if (!!props.study) {
      props.study.lastModifiedBy = this.props.user;
    }

    this.state = {
      study: props.study || {
        status: statuses.IN_PLANNING.value,
        users: [{
          ...this.props.user,
          owner: true
        }],
        owner: this.props.user,
        // createdBy: this.props.user,
        // lastModifiedBy: this.props.user,
        notebookFolder: {}
      },
      validation: {
        nameIsValid: true,
        descriptionIsValid: true,
        programIsValid: true,
        startDateIsValid: true,
        usersIsValid: true,
        ownerIsValid: true,
        collaboratorIsValid: true
      },
      showLoadingOverlay: false
    };
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleFormUpdate = this.handleFormUpdate.bind(this);
    this.handleLegacyToggle = this.handleLegacyToggle.bind(this);
    this.validateForm = this.validateForm.bind(this);
  }

  /**
   * Updates the study state when an input is changed.
   *
   * @param data
   */
  handleFormUpdate(data) {
    const study = {
      ...this.state.study,
      ...data
    };
    console.log(study);
    this.setState({
      study: study
    })
  }

  /**
   * Handles toggling display of legacy study container when checkbox is checked.
   */
  handleLegacyToggle(e) {
    const container = document.getElementById("legacy-input-container");
    if (e.target.checked) {
      container.style.display = "block";
      container.classList.add("animated");
      container.classList.add("fadeIn");
    } else {
      container.classList.remove("fadeIn");
      container.classList.remove("animated");
      container.style.display = "none";
    }
    this.handleFormUpdate({
      legacy: e.target.checked
    })
  }

  validateForm(study) {
    let isError = false;
    let validation = this.state.validation;

    // Name
    if (!study.name) {
      isError = true;
      validation.nameIsValid = false;
    } else {
      validation.nameIsValid = true;
    }

    // Description
    if (!study.description) {
      isError = true;
      validation.descriptionIsValid = false;
    } else {
      validation.descriptionIsValid = true;
    }

    // Start Date
    if (!study.startDate) {
      isError = true;
      validation.startDateIsValid = false;
    } else {
      validation.startDateIsValid = true;
    }

    // Program
    if (!study.program) {
      isError = true;
      validation.programIsValid = false;
    } else {
      validation.programIsValid = true;
    }

    // Study team
    if (!study.users || study.users.length === 0) {
      isError = true;
      validation.usersIsValid = false;
    } else {
      validation.usersIsValid = true;
    }

    // Owner
    if (!!study.owner) {
      validation.isOwnerValid = true;
    } else {
      isError = true;
      validation.ownerIsValid = false;
    }

    // Collaborator
    if (study.collaborator === -1) {
      validation.collaboratorIsValid = false
      isError = true;
    } else {
      validation.collaboratorIsValid = true;
    }

    this.setState({
      validation: validation
    });
    return isError;
  }

  handleSubmit() {

    let isError = this.validateForm(this.state.study);
    console.log(this.state);

    if (isError) {

      swal("Looks like you forgot something...",
          "Check that all of the required inputs have been filled and then try again.",
          "warning");
      console.warn("Validation failed.");

    } else {

      const isUpdate = !!this.state.study.id;
      const url = isUpdate
          ? "/api/study/" + this.state.study.id
          : "/api/study";
      this.setState({showLoadingOverlay: true});

      fetch(url, {
        method: isUpdate ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(this.state.study)
      })
      .then(async response => {

        const json = await response.json();
        console.log(json);
        if (response.ok) {
          history.push("/study/" + json.code);
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
              message={"Saving your study..."}
          />

          <Row>
            <Col>
              {
                !!this.state.study.id
                    ? (
                        <Breadcrumbs crumbs={[
                          {label: "Home", url: "/"},
                          {label: "Study Detail", url: "/study/" + this.state.study.code},
                          {label: "Edit Study"}
                        ]} />
                    )
                    : (
                        <Breadcrumbs crumbs={[
                          {label: "Home", url: "/"},
                          {label: "New Study"}
                        ]} />
                    )
              }
            </Col>
          </Row>

          <Row className="justify-content-end align-items-center">
            <Col>
              <h3>{!!this.state.study.id ? "Edit Study" : "New Study"}</h3>
            </Col>
          </Row>

          <Row>
            <Col xs={12}>
              <Card>

                <Card.Header>
                  <Card.Title tag="h5">Study Overview</Card.Title>
                  <h6 className="card-subtitle text-muted">Tell us something
                    about your study. Study names should be unique. Describe the
                    objective of your study in one or two sentences. Select the
                    status that best reflects the current state of your study.
                    Choose the date your study is expected to start. If the
                    study has already completed, you may select an end
                    date.</h6>
                </Card.Header>

                <Card.Body>
                  <Form className="study-form">

                    {/*Overview*/}
                    <Row>

                      <Col md={7}>
                        <FormGroup>
                          <Form.Label>Name *</Form.Label>
                          <Form.Control
                              type="text"
                              isInvalid={!this.state.validation.nameIsValid}
                              defaultValue={this.state.study.name || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"name": e.target.value})}
                              disabled={!!this.state.study.id}
                          />
                          <Form.Control.Feedback type={"invalid"}>Name must not be empty.</Form.Control.Feedback>
                          <Form.Text>Must be unique.</Form.Text>
                        </FormGroup>
                      </Col>

                      <Col md={5}>
                        <ProgramDropdown
                            programs={this.props.programs}
                            selectedProgram={!!this.state.study.program
                                ? this.state.study.program.id : -1}
                            onChange={this.handleFormUpdate}
                            isValid={this.state.validation.programIsValid}
                            disabled={!!this.state.study.id}
                            isLegacyStudy={!!this.state.study.legacy}
                        />
                      </Col>

                    </Row>

                    <Row>
                      <Col md={7}>
                        <FormGroup>
                          <Form.Label>Description *</Form.Label>
                          <ReactQuill
                              theme="snow"
                              defaultValue={this.state.study.description || ''}
                              onChange={content => this.handleFormUpdate(
                                  {"description": content})}
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            Description must not be empty.
                          </Form.Control.Feedback>
                        </FormGroup>
                      </Col>

                      <Col md={5}>

                        <StatusDropdown
                            selected={this.state.study.status}
                            onChange={this.handleFormUpdate}
                        />

                        <FormGroup>
                          <Form.Label>Start Date *</Form.Label>
                          <DatePicker
                              maxlength="2"
                              className={"form-control"}
                              invalid={!this.state.validation.startDateIsValid}
                              wrapperClassName="form-control"
                              selected={this.state.study.startDate || null}
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
                            Select the date your study began or is expected to begin.
                          </Form.Text>
                        </FormGroup>

                        <FormGroup>
                          <Form.Label>End Date</Form.Label>
                          <DatePicker
                              maxlength="2"
                              className="form-control"
                              wrapperClassName="form-control"
                              selected={this.state.study.endDate || null}
                              onChange={(date) => this.handleFormUpdate(
                                  {"endDate": date})}
                              isClearable={true}
                              dateFormat=" MM / dd / yyyy"
                              placeholderText="MM / DD / YYYY"
                          />
                          <Form.Text>
                            Select the date your study was completed.
                          </Form.Text>
                        </FormGroup>

                      </Col>
                    </Row>

                    <Row>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    {/*Legacy studies*/}
                    {
                      !!this.state.study.id && !this.state.study.legacy
                          ? ""
                          : (
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
                                      <Form.Check
                                          id="legacy-check"
                                          type="checkbox"
                                          label="Is this a legacy study?"
                                          onChange={this.handleLegacyToggle}
                                          disabled={!!this.state.study.id}
                                          defaultChecked={!!this.state.study.id
                                          && !!this.state.study.legacy}
                                      />
                                    </FormGroup>
                                  </Col>

                                  <Col md={12} id="legacy-input-container"
                                       style={{
                                         display: !!this.state.study.id
                                         && !!this.state.study.legacy ? "block"
                                             : "none"
                                       }}>

                                    <Row>

                                      <Col md={6}>
                                        <FormGroup>
                                          <Form.Label>Study Code *</Form.Label>
                                          <Form.Control
                                              type="text"
                                              isInvalid={false}
                                              disabled={!!this.state.study.id}
                                              defaultValue={this.state.study.code
                                              || ''}
                                              onChange={(e) => this.handleFormUpdate(
                                                  {"code": e.target.value})}
                                          />
                                          <Form.Control.Feedback type={"invalid"}>
                                            Legacy studies must be provided a
                                            Study Code.
                                          </Form.Control.Feedback>
                                          <Form.Text>
                                            Provide the existing code or ID
                                            for the study.
                                          </Form.Text>
                                        </FormGroup>
                                      </Col>

                                      <Col md={6}>
                                        <FormGroup>
                                          <Form.Label>Notebook URL</Form.Label>
                                          <Form.Control
                                              type="text"
                                              disabled={!!this.state.study.id}
                                              defaultValue={
                                                !!this.state.study.notebookFolder
                                                && !!this.state.study.notebookFolder.url
                                                    ? this.state.study.notebookFolder.url
                                                    : ''
                                              }
                                              onChange={(e) => this.handleFormUpdate(
                                                  {
                                                    "notebookFolder": {
                                                      url: e.target.value
                                                    }
                                                  })}
                                          />
                                          <Form.Text>
                                            If the study already has an ELN
                                            entry, provide the URL here.
                                          </Form.Text>
                                        </FormGroup>
                                      </Col>

                                    </Row>

                                  </Col>

                                </Row>

                                <Row>
                                  <Col>
                                    <hr/>
                                  </Col>
                                </Row>

                              </React.Fragment>
                          )
                    }


                    {/*CRO*/}

                    <CollaboratorInputs
                        collaborator={this.state.study.collaborator}
                        externalCode={this.state.study.externalCode}
                        onChange={this.handleFormUpdate}
                    />

                    <Row>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    {/*Study Team*/}
                    <Row>
                      <Col md={12}>
                        <h5 className="card-title">Study Team</h5>
                        <h6 className="card-subtitle text-muted">Who will be
                          working on this study? One user must be assigned as
                          the study owner. This person will be the primary
                          contact person for the study.</h6>
                        <br/>
                      </Col>

                      <Col md={12}>
                        <UserInputs
                            users={this.state.study.users || []}
                            owner={this.state.study.owner}
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

                    {/*Keywords*/}
                    <Row>
                      <Col md={12}>
                        <h5 className="card-title">Keywords</h5>
                        <h6 className="card-subtitle text-muted">Tag your study
                          with keywords to make it more searchable and
                          identifiable. Select a keyword category and then use
                          the searchable select input to find available keyword
                          terms. You may choose as many keywords as you'd
                          like.</h6>
                        <br/>
                      </Col>

                      <Col md={12}>
                        <KeywordInputs
                            keywords={this.state.study.keywords || []}
                            keywordCategories={this.props.keywordCategories}
                            onChange={this.handleFormUpdate}
                        />
                      </Col>

                    </Row>

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