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
import {StatusDropdown} from "./status";
import {statuses} from "../../config/statusConstants";
import {UserInputs} from "./users";
import swal from 'sweetalert';
import {history} from '../../App';
import {AssayTypeDropdown} from "./assayTypes";

export default class AssayForm extends React.Component {

  constructor(props) {

    super(props);

    let assay = props.assay || {
      status: statuses.IN_PLANNING.value,
      users: this.props.study.users,
      owner: this.props.study.owner,
      createdBy: this.props.user,
      lastModifiedBy: this.props.user
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
      }
    };

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleFormUpdate = this.handleFormUpdate.bind(this);
    this.validateForm = this.validateForm.bind(this);

  }

  /**
   * Updates the study state when an input is changed.
   *
   * @param data
   */
  handleFormUpdate(data) {
    const assay = {
      ...this.state.assay,
      ...data
    };
    console.log(assay);
    this.setState({
      assay: assay
    })
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

    let isError = this.validateForm(this.state.assay);
    console.log(this.state);

    if (isError) {

      swal("Looks like you forgot something...",
          "Check that all of the required inputs have been filled and then try again.",
          "warning");
      console.warn("Validation failed.");

    } else {

      const isUpdate = !!this.state.assay.id;

      const url = isUpdate
          ? "/api/study/" + this.props.study.code + "/assay/"
          + this.state.assay.id
          : "/api/study/" + this.props.study.code + "/assays/";

      fetch(url, {
        method: isUpdate ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(this.state.assay)
      })
      .then(async response => {

        const json = await response.json();
        console.log(json);
        if (response.ok) {
          history.push(
              "/study/" + this.props.study.code + "/assay/" + json.code);
        } else {
          swal("Something went wrong",
              !!json.message
                  ? "Error: " + json.message :
                  "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
          );
          console.error("Request failed.");
        }

      }).catch(e => {
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

          <Row>
            <Col>
              <Breadcrumb>

                <BreadcrumbItem>
                  <a href={"/"}>Home</a>
                </BreadcrumbItem>

                <BreadcrumbItem>
                  <a href={"/study/" + this.props.study.code}>
                    Study {this.props.study.code}
                  </a>
                </BreadcrumbItem>

                {
                  !!this.state.assay.id
                      ? (<BreadcrumbItem active>Edit Assay</BreadcrumbItem>)
                      : (<BreadcrumbItem active>New Assay</BreadcrumbItem>)
                }

              </Breadcrumb>
            </Col>
          </Row>

          <Row className="justify-content-end align-items-center">
            <Col>
              <h1>{!!this.state.assay.id ? "Edit Assay" : "New Assay"}</h1>
            </Col>
          </Row>

          <Row>
            <Col xs="12">
              <Card>

                <CardHeader>
                  <CardTitle tag="h5">Assay Overview</CardTitle>
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
                </CardHeader>

                <CardBody>
                  <Form>

                    {/*Overview*/}
                    <Row form>

                      <Col sm="7">
                        <FormGroup>
                          <Label>Name *</Label>
                          <Input
                              type="text"
                              invalid={!this.state.validation.nameIsValid}
                              defaultValue={this.state.assay.name || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"name": e.target.value})}
                          />
                          <FormFeedback>Name must not be empty.</FormFeedback>
                        </FormGroup>
                      </Col>

                      <Col sm="5">
                        <AssayTypeDropdown
                            selectedType={!!this.state.assay.assayType
                                ? this.state.assay.assayType.id : -1}
                            onChange={this.handleFormUpdate}
                        />
                      </Col>

                    </Row>

                    <Row form>
                      <Col sm="7">
                        <FormGroup>
                          <Label>Description *</Label>
                          <Input
                              type="textarea"
                              invalid={!this.state.validation.descriptionIsValid}
                              rows="5"
                              defaultValue={this.state.assay.description || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"description": e.target.value})}
                          />
                          <FormFeedback>Description must not be
                            empty.</FormFeedback>
                          <FormText>Provide a brief description of your
                            assay.</FormText>
                        </FormGroup>
                      </Col>
                      <Col sm="5">

                        <StatusDropdown
                            selected={this.state.assay.status}
                            onChange={this.handleFormUpdate}
                        />

                        <FormGroup>
                          <Label>Start Date *</Label>
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
                          <FormFeedback>You must select a Start
                            Date.</FormFeedback>
                          <FormText>Select the date your assay began or is
                            expected to begin.</FormText>
                        </FormGroup>

                        <FormGroup>
                          <Label>End Date</Label>
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
                          <FormText>Select the date your assay was
                            completed.</FormText>
                        </FormGroup>

                      </Col>
                    </Row>

                    <Row form>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    {/*CRO*/}

                    {/*<CollaboratorInputs*/}
                    {/*    collaborator={this.state.study.collaborator}*/}
                    {/*    externalCode={this.state.study.externalCode}*/}
                    {/*    onChange={this.handleFormUpdate}*/}
                    {/*/>*/}

                    {/*<Row>*/}
                    {/*  <Col>*/}
                    {/*    <hr/>*/}
                    {/*  </Col>*/}
                    {/*</Row>*/}

                    {/*Study Team*/}
                    <Row form>
                      <Col sm="12">
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