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
import ProgramDropdown from "../../common/forms/ProgramDropdown";
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import {FormGroup} from "../../common/forms/common";
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import {StatusDropdown} from "../../common/forms/status";
import {statuses} from "../../config/statusConstants";
import UserInputs from "../../common/forms/UserInputs";
import Swal from "sweetalert2";
import KeywordInputs from "../../common/forms/KeywordInputs";
import CollaboratorInputsCard from "../../common/forms/CollaboratorInputsCard";
import ReactQuill from "react-quill";
import {LoadingOverlay} from "../../common/loading";
import {Breadcrumbs} from "../../common/common";
import {useNavigate} from "react-router-dom";
import {Form as FormikForm, Formik} from "formik";
import FormikFormErrorNotification
  from "../../common/forms/FormikFormErrorNotification";
import * as yup from "yup";
import axios from "axios";
import PropTypes from "prop-types";
import NotebookInputsCard from "../../common/forms/NotebookInputsCard";
import GitInputsCard from "../../common/forms/GitInputsCard";
import LegacyStudyControlsCard from "./LegacyStudyControlsCard";
import S3InputsCard from "../../common/forms/S3InputsCard";

const StudyForm = ({
    study,
    user,
    programs,
    features
}) => {

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
      ...user,
      owner: true
    }],
    owner: user,
    notebookFolder: {},
    notebookTemplateId: null,
    notebookTemplateFields: {},
    useNotebook: true,
    useExistingNotebookFolder: false,
    useGit: false,
    useStorage: true,
    useS3: false,
    s3FolderId: null,
  };

  const studySchema = yup.object().shape({
    name: yup.string()
      .required("Name is required")
      .trim("Name must not have leading or trailing whitespace")
      .strict()
      .max(255, "Name cannot be larger than 255 characters"),
    code: yup.string()
      .nullable()
      .when("legacy", {
        is: (val) => val === true,
        then: (schema) => schema
          .typeError("Code is required for legacy studies.")
          .required("Code is required for legacy studies.")
          .matches("^[A-Za-z0-9_-]+$", "Code must contain only alphanumeric, hyphen, and underscore characters.")
          .max(255, "Code cannot be larger than 255 characters")
    }),
    externalCode: yup.string()
      .nullable()
      .max(255, "External code cannot be larger than 255 characters")
      .matches("[A-Za-z0-9_-]+", "External code must contain only alphanumeric, hyphen, and underscore characters."),
    status: yup.string().required("Status is required"),
    program: yup.object().required("Program is required"),
    description: yup.string()
      .required("Description is required")
      .notOneOf(["<p></p>", "<p><br></p>"], "Description is required"),
    legacy: yup.bool(),
    external: yup.bool(),
    startDate: yup.number()
      .typeError("Start date is required")
      .required("Start date is required"),
    endDate: yup.number().nullable(true),
    users: yup.array().of(yup.object()).min(1, "At least one user is required"),
    owner: yup.object().required("Owner is required"),
    collaborator: yup.object()
      .nullable()
      .when("external", {
        is: val => val === true,
        then: (schema) => schema
        .required("Collaborator is required for external studies.")
      }),
  });

  const submitForm = (values, {setSubmitting}) => {

    const isUpdate = !!values.id;
    const url = isUpdate
        ? "/api/internal/study/" + values.id
        : "/api/internal/study";

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
        Swal.fire("Something went wrong",
            json.message
                ? "Error: " + json.message :
                "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
        );
        console.error("Request failed.");
      }

    }).catch(e => {
      setSubmitting(false);
      Swal.fire(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    });

  }

  const handleCancel = () => {
    Swal.fire({
      title: "Are you sure you want to leave the page?",
      text: "Any unsaved work will be lost.",
      icon: "warning",
      showCancelButton: true
    })
    .then(result => {
      if (result.isConfirmed) {
        navigate("/");
      }
    });
  }

  return (

    <Formik
        initialValues={study
            ? {
                ...study,
                lastModifiedBy: user
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
        handleChange,
        setFieldValue,
        isSubmitting,
        handleSubmit
      }) => (
          <Container fluid className="animated fadeIn max-width-1200">

            <LoadingOverlay
                isVisible={isSubmitting}
                message={"Saving your study..."}
            />

            <FormikFormErrorNotification />

            <FormikForm className="study-form" autoComplete={"off"}>

              <Row>
                <Col>
                  { study
                      ? (
                          <Breadcrumbs crumbs={[
                            {label: "Home", url: "/"},
                            {
                              label: "Study Detail",
                              url: "/study/" + study.code
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
                  <h3>{study ? "Edit Study" : "New Study"}</h3>
                </Col>
              </Row>

              <Row>
                <Col xs={12}>

                  {/*Study Overview*/}
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
                              />
                              <Form.Control.Feedback type={"invalid"}>
                                {errors.name}
                              </Form.Control.Feedback>
                              <Form.Text>Must be unique.</Form.Text>
                            </FormGroup>
                          </Col>

                          <Col md={5}>
                            <ProgramDropdown
                                programs={programs}
                                selectedProgram={values.program?.id}
                                onChange={(value) => setFieldValue("program", value)}
                                isInvalid={!!errors.program}
                                disabled={!!values.id}
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
                                  className={(errors.description ? " is-invalid" : '')}
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

                            <FormGroup>
                              <Form.Label>Start Date *</Form.Label>
                              <DatePicker
                                  maxlength="2"
                                  className={"form-control " + (errors.startDate ? " is-invalid" : '')}
                                  invalid={!!errors.startDate}
                                  wrapperClassName="form-control"
                                  selected={values.startDate}
                                  name="startDate"
                                  onChange={(date) => setFieldValue("startDate", date.getTime())}
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
                                  onChange={(date) => setFieldValue("endDate", date.getTime())}
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

                    </Card.Body>
                  </Card>

                  {/*Study Team*/}
                  <Card>
                    <Card.Header>
                      <Card.Title>Study Team</Card.Title>
                      <h6 className="card-subtitle text-muted">Who will be
                        working on this study? One user must be assigned as
                        the study owner. This person will be the primary
                        contact person for the study.</h6>
                    </Card.Header>

                    <Card.Body>
                      <Row>

                        <Col md={12}>
                          <UserInputs
                              users={values.users}
                              owner={values.owner}
                              onChange={(key, value) => setFieldValue(key, value)}
                              isValid={!errors.users && !errors.owner}
                          />
                        </Col>

                      </Row>

                    </Card.Body>
                  </Card>

                  {/*Legacy studies*/}
                  {
                    !!values.id && !values.legacy ? "" : (
                        <LegacyStudyControlsCard
                            study={study}
                            onChange={(key, value) => setFieldValue(key, value)}
                            values={values}
                            errors={errors}
                        />
                    )
                  }

                  {/*CRO*/}
                  <CollaboratorInputsCard
                      isExternalStudy={values.external}
                      collaborator={values.collaborator}
                      externalCode={values.externalCode}
                      onChange={(key, value) => setFieldValue(key, value)}
                  />

                  {/*Notebook*/}
                  {
                    !values.id
                    && features
                    && features.notebook
                    && features.notebook.isEnabled ? (
                        <NotebookInputsCard
                          isActive={values.useNotebook}
                          selectedProgram={values.program}
                          onChange={(key, value) => setFieldValue(key, value)}
                          useExistingFolder={values.useExistingNotebookFolder}
                          notebookFolder={values.notebookFolder}
                        />
                    ) : ''
                  }

                  {/*Git*/}
                  {
                    !values.id ? (
                        <GitInputsCard
                            onChange={(key, value) => setFieldValue(key, value)}
                            isActive={values.useGit}
                            selectedProgram={values.program}
                        />
                    ) : ''

                  }

                  {/*S3*/}
                  {
                    !values.id ? (
                        <S3InputsCard
                            onChange={(key, value) => setFieldValue(key, value)}
                            isActive={values.useS3}
                            selectedProgram={values.program}
                            errors={errors}
                        />
                    ) : ''

                  }

                  <Card>
                    <Card.Header>
                      <Card.Title>Keywords</Card.Title>
                      <h6 className="card-subtitle text-muted">
                        Tag your study with keywords to make it more searchable and
                        identifiable. Search for existing keywords or create new ones on-demand.
                      </h6>
                    </Card.Header>

                    <Card.Body>
                      <Row>
                        <Col md={12}>
                          <KeywordInputs
                              keywords={values.keywords || []}
                              onChange={(value) => setFieldValue("keywords", value)}
                          />
                        </Col>

                      </Row>
                    </Card.Body>
                  </Card>

                  {/*Buttons*/}
                  <Row>
                    <Col className="text-center">
                      <FormGroup>

                        <Button
                          size="lg"
                          variant="primary"
                          type="submit"
                          className={"me-4"}
                          onClick={handleSubmit}
                        >
                          Submit
                        </Button>

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

                </Col>
              </Row>

            </FormikForm>

          </Container>

        )}

    </Formik>

  );

}

StudyForm.propTypes = {
  study: PropTypes.object,
  programs: PropTypes.array.isRequired,
  user: PropTypes.object,
  features: PropTypes.object,
}

export default StudyForm;