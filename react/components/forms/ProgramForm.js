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
import ReactQuill from "react-quill";
import {LoadingOverlay} from "../loading";
import Select from "react-select";
import Attributes from "./attributes";

export default class ProgramForm extends React.Component {

  constructor(props) {
    super(props);

    if (!!props.program) {
      props.program.lastModifiedBy = this.props.user;
    }

    this.state = {
      program: props.program || {
        active: true,
        createdBy: this.props.user,
        lastModifiedBy: this.props.user,
        attributes: {},
        notebookFolder: {}
      },
      validation: {
        nameIsValid: true,
        nameIsUnique: true,
        descriptionIsValid: true,
        codeIsValid: true,
        programFolderIdIsValid: true
      },
      showLoadingOverlay: false
    };
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleFormUpdate = this.handleFormUpdate.bind(this);
    this.validateForm = this.validateForm.bind(this);
  }

  /**
   * Updates the program state when an input is changed.
   *
   * @param data
   */
  handleFormUpdate(data) {
    const program = {
      ...this.state.program,
      ...data
    };
    console.log(program);
    this.setState({
      program: program
    })
  }

  validateForm(program) {
    let isError = false;
    let validation = this.state.validation;

    // Name
    if (!program.name) {
      isError = true;
      validation.nameIsValid = false;
    } else {
      validation.nameIsValid = true;
    }
    if (!program.id) {
      for (let p of this.props.programs) {
        if (!!program.name && p.name.toLowerCase()
            === program.name.toLowerCase()) {
          isError = true;
          validation.nameIsUnique = false;
        }
      }
    }

    // Description
    if (!program.description) {
      isError = true;
      validation.descriptionIsValid = false;
    } else {
      validation.descriptionIsValid = true;
    }

    // Code
    if (/[^A-Za-z0-9]/.test(program.code)) {
      isError = true;
      validation.codeIsValid = false;
    } else {
      validation.codeIsValid = true;
    }

    // ELN
    if (!program.notebookFolder.referenceId) {
      isError = true;
      validation.programFolderIdIsValid = false;
    } else {
      validation.programFolderIdIsValid = true;
    }

    // if (!program.notebookFolder.name) {
    //   isError = true;
    //   validation.programFolderNameIsValid = false;
    // } else {
    //   validation.programFolderNameIsValid = true;
    // }
    //
    // if (!program.notebookFolder.url) {
    //   isError = true;
    //   validation.programFolderUrlIsValid = false;
    // } else {
    //   validation.programFolderUrlIsValid = true;
    // }

    this.setState({
      validation: validation
    });
    return isError;
  }

  handleSubmit() {

    let isError = this.validateForm(this.state.program);
    console.log(this.state);

    if (isError) {

      swal("Looks like you forgot something...",
          "Check that all of the required inputs have been filled and then try again.",
          "warning");
      console.warn("Validation failed.");

    } else {

      const isUpdate = !!this.state.program.id;
      const url = isUpdate
          ? "/api/program/" + this.state.program.id
          : "/api/program";
      this.setState({showLoadingOverlay: true});

      fetch(url, {
        method: isUpdate ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(this.state.program)
      })
      .then(async response => {

        const json = await response.json();
        console.log(json);
        if (response.ok) {
          history.push("/program/" + json.id);
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
        history.push("/programs");
      }
    });
  }

  render() {

    return (
        <Container fluid className="animated fadeIn max-width-1200">

          <LoadingOverlay
              isVisible={this.state.showLoadingOverlay}
              message={"Creating your program..."}
          />

          <Row>
            <Col>
              {
                !!this.state.program.id
                    ? (
                        <Breadcrumb>
                          <BreadcrumbItem>
                            <a href={"/"}>Home</a>
                          </BreadcrumbItem>
                          <BreadcrumbItem>
                            <a href={"/program/" + this.state.program.id}>
                              Program Detail
                            </a>
                          </BreadcrumbItem>
                          <BreadcrumbItem active>Edit Program</BreadcrumbItem>
                        </Breadcrumb>
                    )
                    : (
                        <Breadcrumb>
                          <BreadcrumbItem>
                            <a href={"/programs"}>Programs</a>
                          </BreadcrumbItem>
                          <BreadcrumbItem active>New Program</BreadcrumbItem>
                        </Breadcrumb>
                    )
              }
            </Col>
          </Row>

          <Row className="justify-content-end align-items-center">
            <Col>
              <h1>{!!this.state.program.id ? "Edit Program"
                  : "New Program"}</h1>
            </Col>
          </Row>

          <Row>
            <Col xs="12">
              <Card>

                <CardHeader>
                  <CardTitle tag="h5">Program Overview</CardTitle>
                  <h6 className="card-subtitle text-muted">
                    Provide a unique name and a brief overview for your program.
                    If this program is no longer active, set the status to
                    'inactive'. Inactive programs will remain in the system,
                    along with their studies, but no new non-legacy studies
                    will be allowed to be created for it.
                  </h6>
                </CardHeader>

                <CardBody>
                  <Form className="program-form">

                    {/*Overview*/}
                    <Row form>

                      <Col md="7">
                        <FormGroup>
                          <Label>Name *</Label>
                          <Input
                              type="text"
                              invalid={!this.state.validation.nameIsValid
                              || !this.state.validation.nameIsUnique}
                              defaultValue={this.state.program.name || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"name": e.target.value})}
                              disabled={!!this.state.program.id}
                          />
                          <FormFeedback>
                            {
                              !this.state.validation.nameIsUnique
                                  ? "A program with this name already exists."
                                  : "Name must not be empty."
                            }
                          </FormFeedback>
                          <FormText>Must be unique.</FormText>
                        </FormGroup>
                      </Col>

                      <Col md="5">
                        <FormGroup>
                          <Label>Is this program active?</Label>
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
                                this.state.program.active ?
                                    {
                                      value: true,
                                      label: "Active"
                                    } : {
                                      value: false,
                                      label: "Inactive"
                                    }
                              }
                              onChange={(selected) => this.handleFormUpdate(
                                  {"active": selected.value})}
                          />
                        </FormGroup>
                      </Col>

                    </Row>

                    <Row form>

                      <Col md="7">
                        <FormGroup>
                          <Label>Description *</Label>
                          <div>
                            <ReactQuill
                                theme="snow"
                                defaultValue={this.state.program.description
                                || ''}
                                onChange={content => this.handleFormUpdate(
                                    {"description": content})}
                            />
                          </div>
                          <FormFeedback>
                            Description must not be empty.
                          </FormFeedback>
                          <FormText>
                            Provide a brief description of the project.
                          </FormText>
                        </FormGroup>
                      </Col>

                      <Col md="5">
                        <FormGroup>
                          <Label>Code *</Label>
                          <Input
                              type="text"
                              invalid={!this.state.validation.codeIsValid}
                              defaultValue={this.state.program.code || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"code": e.target.value})}
                              disabled={!!this.state.program.id}
                          />
                          <FormFeedback>Code must not be empty and must not
                            contain any whitespace or non-alphanumeric
                            characters.</FormFeedback>
                          <FormText>This code will be used as a prefix when
                            creating new studies. Eg. a code of 'PG' would
                            result in a study code such as
                            'PG-10001'.</FormText>
                        </FormGroup>
                      </Col>

                    </Row>

                    <Row>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    <Row form>

                      <Col md="12">
                        <h5 className="card-title">Electronic Laboratory
                          Notebook Folder</h5>
                        <h6 className="card-subtitle text-muted">
                          When using an electronic laboratory notebook, all
                          programs require a folder in which all studies and
                          entries will be created. You will have to create the
                          program in the ELN software before Study Tracker can
                          register the study and hook into the ELN platform.
                          Provide the unique ID for the ELN program (aka.
                          Program Folder ID), the name of the program as it
                          appears in the ELN, and the URL for creating a link to
                          the program page in the ELN.
                        </h6>
                        <br/>
                      </Col>

                      <Col md="6">
                        <FormGroup>
                          <Label>Program Folder ID *</Label>
                          <Input
                              type="text"
                              invalid={!this.state.validation.programFolderIdIsValid}
                              defaultValue={this.state.program.notebookFolder.referenceId
                              || ''}
                              onChange={(e) => this.handleFormUpdate({
                                "notebookFolder": {
                                  ...this.state.program.notebookFolder,
                                  referenceId: e.target.value
                                }
                              })}
                          />
                          <FormFeedback>Program Folder ID must not be
                            empty.</FormFeedback>
                          <FormText>This is the ID assigned to the program
                            folder in the ELN. For example, in Benchling the ID
                            will take the form of an alphanumeric code with a
                            prefix of <code>lib_</code>.</FormText>
                        </FormGroup>
                      </Col>

                      <Col md="6">
                        <FormGroup>
                          <Label>Folder Name</Label>
                          <Input
                              type="text"
                              // invalid={!this.state.validation.programFolderNameIsValid}
                              defaultValue={this.state.program.notebookFolder.name
                              || ''}
                              onChange={(e) => this.handleFormUpdate({
                                "notebookFolder": {
                                  ...this.state.program.notebookFolder,
                                  name: e.target.value
                                }
                              })}
                          />
                          <FormFeedback>Folder Name must not be
                            empty.</FormFeedback>
                          <FormText>If different from the program
                            name.</FormText>
                        </FormGroup>
                      </Col>

                      <Col md="6">
                        <FormGroup>
                          <Label>URL</Label>
                          <Input
                              type="text"
                              // invalid={!this.state.validation.programFolderUrlIsValid}
                              defaultValue={this.state.program.notebookFolder.url
                              || ''}
                              onChange={(e) => this.handleFormUpdate({
                                "notebookFolder": {
                                  ...this.state.program.notebookFolder,
                                  url: e.target.value
                                }
                              })}
                          />
                          <FormFeedback>URL must not be empty.</FormFeedback>
                          <FormText>URL for the program in the ELN.</FormText>
                        </FormGroup>
                      </Col>

                    </Row>

                    <Row>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    <Row form>

                      <Col md="12">
                        <h5 className="card-title">Program Attributes</h5>
                        <h6 className="card-subtitle text-muted">
                          Key-value attributes for adding additional information
                          about the program, or for adding application-aware
                          attributes for external integrations (for example, ELN
                          identifiers). You can add as many or as few attributes
                          as you'd like. Attribute values should not be left
                          empty. All values are saved as simple character
                          strings.
                        </h6>
                        <br/>
                      </Col>

                    </Row>

                    <Attributes
                        attributes={this.state.program.attributes}
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