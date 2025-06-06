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
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import {StatusDropdown} from "../../common/forms/status";
import {statuses} from "../../config/statusConstants";
import UserInputs from "../../common/forms/UserInputs";
import swal from 'sweetalert2';
import AssayTypeDropdown from "./AssayTypeDropdown";
import CustomFieldCaptureInputList
  from "../../common/forms/customFields/CustomFieldCaptureInputList";
import AttributeInputs from "../../common/forms/AttributeInputs";
import {LoadingOverlay} from "../../common/loading";
import ReactQuill from "react-quill";
import {Breadcrumbs} from "../../common/common";
import {FormGroup} from "../../common/forms/common";
import PropTypes from "prop-types";
import {Form as FormikForm, Formik} from "formik";
import {useNavigate} from "react-router-dom";
import * as yup from "yup";
import axios from "axios";
import FormikFormErrorNotification
  from "../../common/forms/FormikFormErrorNotification";
import NotebookInputsCard from "../../common/forms/NotebookInputsCard";
import TaskControlsDraggableCardList
  from "../../common/forms/tasks/TaskControlsDraggableCardList";
import S3InputsCard from "../../common/forms/S3InputsCard";

const AssayForm = props => {

  const {
    study,
    assay,
    user,
    assayTypes,
    features
  } = props;
  const navigate = useNavigate();

  const assayDefaults = {
    name: "",
    description: "",
    status: statuses.IN_PLANNING.value,
    users: study.users,
    owner: study.owner,
    createdBy: user,
    lastModifiedBy: user,
    fields: {},
    tasks: [],
    attributes: {},
    notebookFolder: {},
    notebookTemplateId: null,
    useNotebook: study.notebookFolders.length > 0,
    useGit: false,
    useStorage: true,
    useExistingNotebookFolder: false,
    useS3: false,
  };

  const assaySchema = yup.object().shape({
    name: yup.string()
      .required("Name is required")
      .trim("Name must not have leading or trailing whitespace")
      .strict()
      .max(255, "Name cannot be larger than 255 characters"),
    status: yup.string().required("Status is required"),
    description: yup.string()
      .required("Description is required")
      .notOneOf(["<p></p>", "<p><br></p>"], "Description is required"),
    users: yup.array().of(yup.object()).min(1, "At least one user is required"),
    owner: yup.object().required("Owner is required"),
    startDate: yup.number()
      .typeError("Start date is required")
      .required("Start date is required."),
    endDate: yup.number().nullable(true),
    assayType: yup.object().required("Assay type is required"),
    attributes: yup.object()
      .test(
          "not empty",
          "Attribute names must not be empty",
          value => Object.keys(value).every(d => d && d.trim() !== '')
      ),
    fields: yup.object()
      .test(
          "required fields",
          "Required assay type fields are missing",
          (value, context) => {
            const requiredFields = context.parent.assayType
                ? context.parent.assayType.fields.filter(f => f.required)
                : [];
            return requiredFields.every(f => {
              return value[f.fieldName] !== undefined
                  && value[f.fieldName] !== null
                  && value[f.fieldName] !== ""
            });
          }
      ),

  });

  const handleFormSubmit = async (values, {setSubmitting}) => {

    console.debug("Submit values: ", values);
    setSubmitting(true);

    // Upload any files
    let uploadErrors = false;
    for (let i in values.assayType.fields) {
      const assayTypeField = values.assayType.fields[i];
      if (assayTypeField.type === "FILE") {
        const file = values.fields[assayTypeField.fieldName];
        if (file) {
          console.debug("Uploading file: ", file);
          const formData = new FormData();
          formData.append("file", file);
          const localPath = await axios.post(
              "/api/internal/data-files/temp-upload", formData)
          .then(response => response.data.filePath)
          .catch(e => {
            console.error(e);
            uploadErrors = true;
          });
          if (uploadErrors) break;
          values.fields[assayTypeField.fieldName] = localPath;
        }
      }
    }
    if (uploadErrors) {
      setSubmitting(false);
      swal.fire(
          "Something went wrong",
          "There was a problem uploading one or more attached files. If this error persists, please contact Study Tracker support."
      );
      return;
    }

    // Submit the assay data
    const isUpdate = !!values.id;
    const url = isUpdate
        ? '/api/internal/study/' + study.code + '/assays/' + values.id
        : '/api/internal/study/' + study.code + '/assays'

    await axios({
      url: url,
      method: isUpdate ? 'put': 'post',
      data: values
    })
    .then(async response => {
      const json = response.data;
      console.debug(json);
      if (response.status === 200 || response.status === 201) {
        navigate("/study/" + study.code + "/assay/" + json.code);
      } else {
        swal.fire({
          title: "Something went wrong",
          text: json.message
            ? "Error: " + json.message :
            "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support.",
          icon: "error",
        });
        console.error("Request failed.");
      }
    })
    .catch(e => {
      swal.fire({
        title: "Something went wrong",
        text: "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support.",
        icon: "error",
      });
      console.error(e);
    })
    .finally(() => {
      setSubmitting(false);
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
      if (val) {
        navigate(-1);
      }
    });
  }

  return (

      <Formik
          initialValues={assay || assayDefaults}
          validationSchema={assaySchema}
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
                message={"Saving your assay..."}
            />

            <FormikFormErrorNotification />

            <FormikForm autoComplete={"off"}>

              <Row>
                <Col>
                  <Breadcrumbs crumbs={[
                    {label: "Home", url: "/"},
                    {
                      label: "Study " + study.code,
                      url: "/study/" + study.code
                    },
                    {label: values.id ? "Edit Assay" : "New Assay"}
                  ]}/>
                </Col>
              </Row>

              <Row className="justify-content-end align-items-center">
                <Col>
                  <h3>{values.id ? "Edit Assay" : "New Assay"}</h3>
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
                        select &apos;Generic&apos;. If Assay names should be descriptive, but
                        do not need to be unique. Describe the
                        objective of your assay in one or two sentences. Select the
                        status that best reflects the current state of your assay.
                        Choose the date your assay is expected to start. If the
                        assay has already completed, you may select an end
                        date.</h6>
                    </Card.Header>

                    <Card.Body>


                      {/*Overview*/}
                      <Row>

                        <Col sm={7}>
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
                          </FormGroup>
                        </Col>

                        <Col sm={5}>
                          <AssayTypeDropdown
                              assayTypes={assayTypes}
                              selectedType={values.assayType?.id}
                              onChange={(data) => {
                                setFieldValue("assayType", data.assayType);
                                setFieldValue("tasks", data.tasks);
                                setFieldValue("fields", data.fields);
                              }}
                              disabled={!!values.id}
                              isInvalid={!!errors.assayType}
                          />
                        </Col>

                      </Row>

                      <Row>
                        <Col sm={7}>
                          <FormGroup>
                            <Form.Label>Description *</Form.Label>
                            <ReactQuill
                                theme="snow"
                                name="description"
                                value={values.description}
                                className={errors.description ? "is-invalid" : ""}
                                onChange={content => setFieldValue("description", content)}
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

                          <StatusDropdown
                              selected={values.status}
                              onChange={(value) => setFieldValue("status", value)}
                          />

                          <FormGroup>
                            <Form.Label>Start Date *</Form.Label>
                            <DatePicker
                                maxlength="2"
                                className={"form-control " + (errors.startDate ? " is-invalid" : "")}
                                invalid={!!errors.startDate}
                                name="startDate"
                                wrapperClassName="form-control"
                                selected={values.startDate}
                                onChange={(date) => setFieldValue("startDate", date.getTime())}
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
                                name="endDate"
                                className="form-control"
                                wrapperClassName="form-control"
                                selected={values.endDate}
                                onChange={(date) => setFieldValue("endDate", date.getTime())}
                                isClearable={true}
                                dateFormat=" MM / dd / yyyy"
                                placeholderText="MM / DD / YYYY"
                            />
                            <Form.Text>
                              Select the date your assay was completed.
                            </Form.Text>
                          </FormGroup>

                        </Col>
                      </Row>
                    </Card.Body>
                  </Card>

                  {/* Assay Team */}
                  <Card>
                    <Card.Header>
                      <Card.Title>Assay Team</Card.Title>
                      <h6 className="card-subtitle text-muted">Who will be
                        working on this assay? One user must be assigned as
                        the assay owner. This person will be the primary
                        contact person for the experiment.</h6>
                      <br/>
                    </Card.Header>
                    <Card.Body>
                      <Row>
                        <Col sm={12}>
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

                  {/*ELN*/}
                  {
                    !values.id
                    && study.notebookFolders.length > 0
                    && features?.notebook?.isEnabled && (
                        <NotebookInputsCard
                            isActive={values.useNotebook}
                            selectedStudy={study}
                            onChange={(key, value) => setFieldValue(key, value)}
                            useExistingFolder={values.useExistingNotebookFolder}
                            notebookFolder={values.notebookFolder}
                        />
                    )
                  }

                  {/* Legacy study assay */}
                  {
                    study.legacy && assay.notbookFolders.length === 0 && (
                        <Card>
                          <Card.Header>
                            <Card.Title>Legacy Study</Card.Title>
                            <h6 className="card-subtitle text-muted">Studies
                              created
                              prior to the introduction of Study Tracker are
                              considered legacy. Enabling this option allows
                              you to
                              specify certain attributes that would
                              otherwise be
                              automatically generated.</h6>
                          </Card.Header>
                          <Card.Body>
                            <Row>
                              <Col md={12}>
                                <FormGroup>
                                  <Form.Label>Notebook URL</Form.Label>
                                  <Form.Control
                                      type="text"
                                      name={"notebookFolder.url"}
                                      value={values.notebookFolder?.url}
                                      onChange={handleChange}
                                  />
                                  <Form.Text>
                                    If the study already has an ELN
                                    entry, provide the URL here.
                                  </Form.Text>
                                </FormGroup>
                              </Col>
                            </Row>
                          </Card.Body>
                        </Card>
                    )
                  }

                  {/* Assay type fields */}
                  {
                    values.assayType && values.assayType.fields.length > 0 && (
                        <Card>
                          <Card.Header>
                            <Card.Title>{values.assayType.name} Fields</Card.Title>
                            <h6 className="card-subtitle text-muted">
                              {values.assayType.description}
                            </h6>
                          </Card.Header>
                          <Card.Body>
                            <CustomFieldCaptureInputList
                                fields={values.assayType.fields}
                                data={values.fields}
                                handleUpdate={data => {
                                  setFieldValue("fields", {
                                    ...values.fields,
                                    ...data
                                  });
                                }}
                                errors={errors}
                            />
                          </Card.Body>
                        </Card>
                    )
                  }

                  {/* Tasks */}
                  <Card>
                    <Card.Header>
                      <Card.Title>Tasks</Card.Title>
                      <h6 className="card-subtitle text-muted">
                        You can define an ordered list of tasks that must be
                        completed for your assay here. Task status changes are
                        captured with user-associated timestamps.
                      </h6>
                    </Card.Header>
                    <Card.Body>
                      <TaskControlsDraggableCardList
                          tasks={values.tasks}
                          handleUpdate={(tasks) => setFieldValue("tasks", tasks)}
                          errors={errors}
                          touched={touched}
                          showAssignments={true}
                      />
                    </Card.Body>
                  </Card>

                  {/*S3*/}
                  { !values.id && (
                      <S3InputsCard
                          onChange={(key, value) => setFieldValue(key, value)}
                          isActive={values.useS3}
                          selectedStudy={study}
                          errors={errors}
                      />
                  )}

                  {/* Attributes */}
                  <Card>
                    <Card.Header>
                      <Card.Title>Assay Attributes</Card.Title>
                      <h6 className="card-subtitle text-muted">
                        Key-value attributes for adding additional information
                        about the assay, or for adding application-aware
                        attributes for external integrations (for example, ELN
                        identifiers). You can add as many or as few attributes
                        as you&apos;d like. Attribute values should not be left
                        empty. All values are saved as simple character
                        strings.
                      </h6>
                    </Card.Header>
                    <Card.Body>
                      <AttributeInputs
                          attributes={values.attributes}
                          handleUpdate={(attributes) => setFieldValue("attributes", attributes)}
                          error={errors.attributes}
                      />
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
                            className={"me-2"}
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

AssayForm.propTypes = {
  features: PropTypes.object,
  study: PropTypes.object.isRequired,
  user: PropTypes.object.isRequired,
  assay: PropTypes.object,
  assayTypes: PropTypes.array.isRequired,
}

export default AssayForm;