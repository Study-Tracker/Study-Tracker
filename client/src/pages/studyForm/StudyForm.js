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
import {ProgramDropdown} from "../../common/forms/programs";
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import {FormGroup} from "../../common/forms/common";
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import {StatusDropdown} from "../../common/forms/status";
import {statuses} from "../../config/statusConstants";
import UserInputs from "../../common/forms/UserInputs";
import swal from 'sweetalert';
import KeywordInputs from "../../common/forms/KeywordInputs";
import CollaboratorInputs from "../../common/forms/CollaboratorInputs";
import ReactQuill from "react-quill";
import {LoadingOverlay} from "../../common/loading";
import {Breadcrumbs} from "../../common/common";
import {
  NotebookEntryTemplatesDropdown
} from "../../common/forms/notebookEntryTemplates";
import {useNavigate} from "react-router-dom";
import {Form as FormikForm, Formik} from "formik";
import FormikFormErrorNotification
  from "../../common/forms/FormikFormErrorNotification";
import * as yup from "yup";
import axios from "axios";

const StudyForm = props => {

  const navigate = useNavigate();

  const defaultStudyValues = {
    name: '',
    code: null,
    description: '',
    status: statuses.IN_PLANNING.value,
    legacy: false,
    external: false,
    startDate: null,
    endDate: null,
    collaborator: null,
    users: [{
      ...props.user,
      owner: true
    }],
    owner: props.user,
    notebookFolder: {},
    notebookTemplateId: !!props.defaultNotebookTemplate
        ? props.defaultNotebookTemplate.templateId : null
  };

  const studySchema = yup.object().shape({
    name: yup.string()
      .required("Name is required")
      .max(255, "Name cannot be larger than 255 characters"),
    code: yup.string()
      .nullable(true)
      .when("legacy", {
        is: true,
        then: yup.string()
          .typeError("Code is required for legacy studies.")
          .required("Code is required for legacy studies.")
          .matches("^[A-Za-z0-9_-]+$", "Code must contain only alphanumeric, hyphen, and underscore characters.")
          .max(255, "Code cannot be larger than 255 characters")
    }),
    externalCode: yup.string()
      .nullable(true)
      .max(255, "External code cannot be larger than 255 characters")
      .matches("[A-Za-z0-9_-]+", "External code must contain only alphanumeric, hyphen, and underscore characters."),
    status: yup.string().required("Status is required"),
    program: yup.object().required("Program is required"),
    description: yup.string().required("Description is required"),
    legacy: yup.bool(),
    external: yup.bool(),
    startDate: yup.date()
      .typeError("Start date is required")
      .required("Start date is required"),
    endDate: yup.date().nullable(true),
    users: yup.array().of(yup.object()).min(1, "At least one user is required"),
    owner: yup.object().required("Owner is required"),
    collaborator: yup.object()
      .nullable(true)
      .when("external", {
        is: true,
        then: yup.object()
        .required("Collaborator is required for external studies.")
      }),
  });

  /**
   * Handles toggling display of legacy study container when checkbox is checked.
   */
  const handleLegacyToggle = (e) => {
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
  }

  const submitForm = (values, {setSubmitting}) => {

    const isUpdate = !!values.id;
    const url = isUpdate
        ? "/api/study/" + values.id
        : "/api/study";

    axios({
      url: url,
      method: isUpdate ? "put" : "post",
      headers: {
        "Content-Type": "application/json"
      },
      data: values
    })
    .then(async response => {

      const json = response.data;
      console.debug(json);
      if (response.status === 200 || response.status === 201) {
        setSubmitting(false);
        navigate("/study/" + json.code);
      } else {
        setSubmitting(false);
        swal("Something went wrong",
            !!json.message
                ? "Error: " + json.message :
                "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
        );
        console.error("Request failed.");
      }

    }).catch(e => {
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
        navigate("/");
      }
    });
  }

  return (

    <Formik
        initialValues={props.study
            ? {
                ...props.study,
                lastModifiedBy: props.user
            } : defaultStudyValues
        }
        onSubmit={submitForm}
        validationSchema={studySchema}
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
                message={"Saving your study..."}
            />

            <FormikFormErrorNotification />

            <Row>
              <Col>
                {
                  !!props.study
                      ? (
                          <Breadcrumbs crumbs={[
                            {label: "Home", url: "/"},
                            {
                              label: "Study Detail",
                              url: "/study/" + props.study.code
                            },
                            {label: "Edit Study"}
                          ]}/>
                      )
                      : (
                          <Breadcrumbs crumbs={[
                            {label: "Home", url: "/"},
                            {label: "New Study"}
                          ]}/>
                      )
                }
              </Col>
            </Row>

            <Row className="justify-content-end align-items-center">
              <Col>
                <h3>{!!props.study ? "Edit Study" : "New Study"}</h3>
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
                    <FormikForm className="study-form" autoComplete={"off"}>

                      {/*Overview*/}
                      <Row>

                        <Col md={7}>
                          <FormGroup>
                            <Form.Label>Name *</Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                isInvalid={!!errors.name}
                                value={values.name}
                                onChange={handleChange}
                                disabled={!!props.study}
                            />
                            <Form.Control.Feedback type={"invalid"}>
                              {errors.name}
                            </Form.Control.Feedback>
                            <Form.Text>Must be unique.</Form.Text>
                          </FormGroup>
                        </Col>

                        <Col md={5}>
                          <ProgramDropdown
                              programs={props.programs}
                              selectedProgram={!!values.program
                                  ? values.program.id : -1}
                              onChange={(value) => setFieldValue("program", value)}
                              isInvalid={!!errors.program}
                              disabled={!!props.study}
                              isLegacyStudy={values.legacy}
                          />
                        </Col>

                      </Row>

                      <Row>
                        <Col md={7}>
                          <FormGroup>
                            <Form.Label>Description *</Form.Label>
                            <ReactQuill
                                theme="snow"
                                name={"description"}
                                value={values.description}
                                className={(!!errors.description ? " is-invalid" : '')}
                                onChange={content =>
                                    setFieldValue("description", content)}
                            />
                            <Form.Control.Feedback type={"invalid"}>
                              Description must not be empty.
                            </Form.Control.Feedback>
                          </FormGroup>
                        </Col>

                        <Col md={5}>

                          <StatusDropdown
                              selected={values.status}
                              onChange={(value) => setFieldValue("status", value)}
                          />

                          {
                            !props.study
                            && props.features
                            && props.features.notebook
                            && props.features.notebook.isEnabled ? (
                                <NotebookEntryTemplatesDropdown
                                    notebookTemplates={props.notebookTemplates}
                                    defaultTemplate={props.defaultNotebookTemplate}
                                    onChange={selectedItem =>
                                        setFieldValue(
                                            "notebookTemplateId",
                                            selectedItem ? selectedItem.value : ''
                                        )
                                    }
                                />
                            ) : ''
                          }

                          <FormGroup>
                            <Form.Label>Start Date *</Form.Label>
                            <DatePicker
                                maxlength="2"
                                className={"form-control " + (!!errors.startDate ? " is-invalid" : '')}
                                invalid={!!errors.startDate}
                                wrapperClassName="form-control"
                                selected={values.startDate}
                                name="startDate"
                                onChange={(date) => setFieldValue("startDate", date)}
                                isClearable={true}
                                dateFormat=" MM / dd / yyyy"
                                placeholderText="MM / DD / YYYY"
                            />
                            <Form.Control.Feedback type={"invalid"}>
                              You must select a Start Date.
                            </Form.Control.Feedback>
                            <Form.Text>
                              Select the date your study began or is expected to
                              begin.
                            </Form.Text>
                          </FormGroup>

                          <FormGroup>
                            <Form.Label>End Date</Form.Label>
                            <DatePicker
                                maxlength="2"
                                className="form-control"
                                name={"endDate"}
                                wrapperClassName="form-control"
                                selected={values.endDate}
                                onChange={(date) => setFieldValue("endDate", date)}
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
                        !!values.id && !values.legacy
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
                                            onChange={e => {
                                              handleLegacyToggle(e);
                                              setFieldValue("legacy", e.target.checked);
                                            }}
                                            disabled={!!props.study}
                                            defaultChecked={!!props.study
                                                && !!values.legacy}
                                        />
                                      </FormGroup>
                                    </Col>

                                    <Col md={12} id="legacy-input-container"
                                         style={{
                                           display: !!props.study
                                            && !!values.legacy
                                               ? "block"
                                               : "none"
                                         }}>

                                      <Row>

                                        <Col md={6}>
                                          <FormGroup>
                                            <Form.Label>Study Code *</Form.Label>
                                            <Form.Control
                                                type="text"
                                                isInvalid={!!errors.code}
                                                disabled={!!props.study}
                                                name={"code"}
                                                value={values.code}
                                                onChange={handleChange}
                                            />
                                            <Form.Control.Feedback type={"invalid"}>
                                              {errors.code}
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
                                                name={"notebookFolder.url"}
                                                disabled={!!props.study}
                                                value={values.notebookFolder.url}
                                                onChange={handleChange}
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
                          isExternalStudy={values.external}
                          collaborator={values.collaborator}
                          externalCode={values.externalCode}
                          onChange={(key, value) => setFieldValue(key, value)}
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
                              users={values.users}
                              owner={values.owner}
                              onChange={(key, value) => setFieldValue(key, value)}
                              isValid={!errors.users && !errors.owner}
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
                              keywords={values.keywords || []}
                              keywordCategories={props.keywordCategories}
                              onChange={(value) => setFieldValue("keywords", value)}
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

export default StudyForm;