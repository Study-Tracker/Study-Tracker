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

import React, {useState} from "react";
import 'react-datepicker/dist/react-datepicker.css';
import {
  Breadcrumb,
  Button,
  Card,
  Col,
  Container,
  Form,
  Row
} from "react-bootstrap";
import swal from 'sweetalert2';
import ReactQuill from "react-quill";
import {LoadingOverlay} from "../../common/loading";
import Select from "react-select";
import AttributeInputs from "../../common/forms/AttributeInputs";
import {FormGroup} from "../../common/forms/common";
import PropTypes from "prop-types";
import {Form as FormikForm, Formik} from "formik";
import {useNavigate} from "react-router-dom";
import FormikFormErrorNotification
  from "../../common/forms/FormikFormErrorNotification";
import axios from "axios";
import * as yup from "yup";
import ProgramGitInputs from "./ProgramGitInputs";
import FeatureToggleCard from "../../common/forms/FeatureToggleCard";
import {DismissableAlert} from "../../common/errors";

const ProgramForm = ({
    program,
    programs,
    elnProjects,
    features,
    rootFolders,
    gitGroups
}) => {

  const [showLoadingOverlay, setShowLoadingOverlay] = useState(false);
  const navigate = useNavigate();
  const activeSelectOptions = [
    {
      value: true,
      label: "Active"
    },
    {
      value: false,
      label: "Inactive"
    }
  ];

  const programSchema = yup.object().shape({
    id: yup.number(),
    name: yup.string()
      .required("Name is required.")
      .trim("Name must not have leading or trailing whitespace")
      .strict()
      .max(255, "Name cannot be larger than 255 characters")
      .when("id", {
        is: (id) => id === undefined || id === null,
        then: (schema) => schema
        .required("Name is required.")
        .trim("Name must not have leading or trailing whitespace")
        .strict()
        .max(255, "Name cannot be larger than 255 characters")
        .test(
            "unique",
            "Name must be unique",
            value => !programs.find(p => !!value && p.name.toLowerCase() === value.toLowerCase())
        )
      }),
    description: yup.string()
      .required("Description is required.")
      .notOneOf(["<p></p>", "<p><br></p>"], "Description is required"),
    code: yup.string()
      .required("Code is required.")
      .matches("[A-Za-z0-9]+", "Code must be alphanumeric."),
    active: yup.boolean(),
    parentFolder: yup.object()
      .nullable()
      .when("id", {
        is: (id) => id === undefined || id === null,
        then: (schema) => schema
          .required("Parent folder is required.")
      }),
    notebookFolder: yup.object()
      .nullable()
      .when("useNotebook", {
        is: true,
        then: (schema) => schema
          .shape({
            referenceId: yup.string()
              .required("Notebook folder is required.")
        })
      }),
    attributes: yup.object()
      .test(
          "not empty",
          "Attribute names must not be empty",
          value => Object.keys(value).every(d => d && d.trim() !== '')
      ),
    gitGroup: yup.object()
      .nullable()
      .when("useGit", {
        is: true,
        then: (schema) => schema.required("Git group is required.")
      }),
  });

  const defaultProgramValues = {
    name: '',
    code: '',
    description: '',
    active: true,
    attributes: {},
    notebookFolder: {
      referenceId: null,
      name: null,
      url: null
    },
    parentFolder: null,
    useGit: false,
    useNotebook: features && features.notebook && features.notebook.isEnabled
        && features.notebook.mode !== "none",
    useStorage: true,
    gitGroup: null
  };

  const submitForm = (values, {setSubmitting}) => {
    console.debug(values);
    const isUpdate = !!values.id;
    const url = isUpdate
        ? "/api/internal/program/" + values.id
        : "/api/internal/program";
    setShowLoadingOverlay(true);

    axios({
      url: url,
      method: isUpdate ? "put" : "post",
      headers: {
        "Content-Type": "application/json"
      },
      data: values
    })
    .then(async response => {
      const json = await response.data;
      console.log(json);
      setSubmitting(false);
      if (response.status === 201 || response.status === 200) {
        navigate("/program/" + json.id);
      } else {
        setShowLoadingOverlay(false);
        swal.fire("Something went wrong",
            json.message
                ? "Error: " + json.message :
                "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
        );
        console.error("Request failed.");
      }
    })
    .catch(e => {
      setShowLoadingOverlay(false);
      swal.fire(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    })
    .finally(() => {
      setSubmitting(false);
    });
  };

  const handleCancel = () => {
    swal.fire({
      title: "Are you sure you want to leave the page?",
      text: "Any unsaved work will be lost.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val.isConfirmed) {
        navigate("/programs");
      }
    });
  };

  const elnProjectOptions = elnProjects ? elnProjects
  .sort((a, b) => a.name.localeCompare(b.name))
  .map(p => ({
    label: p.name,
    value: p
  })) : [];

  return (
      <Formik
          initialValues={
              program
                ? {
                    ...program,
                    useGit: program.gitGroups.length > 0,
                    gitGroup: program.gitGroups.length > 0 ? program.gitGroups[0].parentGroup : null
                  }
                : defaultProgramValues
          }
          validationSchema={programSchema}
          onSubmit={submitForm}
          validateOnBlur={false}
          validateOnChange={false}
      >
        {({
          values,
          errors,
          handleChange,
          isSubmitting,
          setFieldValue,
        }) => (
            <Container fluid className="animated fadeIn max-width-1200">

              <LoadingOverlay
                  isVisible={showLoadingOverlay}
                  message={"Saving program..."}
              />

              <FormikFormErrorNotification />

              <FormikForm className="program-form" autoComplete={"off"}>

                <Row>
                  <Col>
                    {
                      program
                          ? (
                              <Breadcrumb>
                                <Breadcrumb.Item href={"/"}>Home</Breadcrumb.Item>
                                <Breadcrumb.Item
                                    href={"/program/" + program.id}>
                                  Program Detail
                                </Breadcrumb.Item>
                                <Breadcrumb.Item active>Edit Program</Breadcrumb.Item>
                              </Breadcrumb>
                          )
                          : (
                              <Breadcrumb>
                                <Breadcrumb.Item href={"/programs"}>
                                  Programs
                                </Breadcrumb.Item>
                                <Breadcrumb.Item active>New Program</Breadcrumb.Item>
                              </Breadcrumb>
                          )
                    }
                  </Col>
                </Row>

                <Row className="justify-content-end align-items-center">
                  <Col>
                    <h3>{program ? "Edit Program" : "New Program"}</h3>
                  </Col>
                </Row>

                <Row>
                  <Col xs={12}>
                    <Card>

                      <Card.Header>
                        <Card.Title tag="h5">Program Overview</Card.Title>
                        <h6 className="card-subtitle text-muted">
                          Provide a unique name and a brief overview for your program.
                          If this program is no longer active, set the status to
                          &apos;inactive&apos;. Inactive programs will remain in the system,
                          along with their studies, but no new non-legacy studies
                          will be allowed to be created for it.
                        </h6>
                      </Card.Header>

                      <Card.Body>

                        {/*Overview*/}
                        <Row>

                          <Col md={7} className={"mb-3"}>
                            <FormGroup>
                              <Form.Label>Name *</Form.Label>
                              <Form.Control
                                  type="text"
                                  isInvalid={!!errors.name}
                                  name="name"
                                  value={values.name}
                                  onChange={handleChange}
                                  disabled={!!values.id}
                              />
                              <Form.Control.Feedback type="invalid">
                                {errors.name}
                              </Form.Control.Feedback>
                              <Form.Text>Must be unique.</Form.Text>
                            </FormGroup>
                          </Col>

                          <Col md={5} className={"mb-3"}>
                            <FormGroup>
                              <Form.Label>Is this program active?</Form.Label>
                              <Select
                                  className="react-select-container"
                                  classNamePrefix="react-select"
                                  options={activeSelectOptions}
                                  name="active"
                                  value={
                                      values.active
                                        ? activeSelectOptions.find(o => o.value === true)
                                        : activeSelectOptions.find(o => o.value === false)
                                  }
                                  onChange={(selected) => setFieldValue("active", selected.value)}
                              />
                            </FormGroup>
                          </Col>

                        </Row>

                        <Row>

                          <Col md={7} className={"mb-3"}>
                            <FormGroup>
                              <Form.Label>Description *</Form.Label>
                              <ReactQuill
                                  theme="snow"
                                  className={"mb-2 " + (errors.description ? "is-invalid" : '')}
                                  name={"description"}
                                  value={values.description}
                                  onChange={content =>
                                      setFieldValue("description", content)}
                              />
                              <Form.Control.Feedback type="invalid">
                                {errors.description}
                              </Form.Control.Feedback>
                              <Form.Text>
                                Provide a brief description of the project.
                              </Form.Text>
                            </FormGroup>
                          </Col>

                          <Col md={5} className={"mb-3"}>
                            <FormGroup>
                              <Form.Label>Code *</Form.Label>
                              <Form.Control
                                  type="text"
                                  isInvalid={!!errors.code}
                                  value={values.code}
                                  name="code"
                                  onChange={handleChange}
                                  disabled={!!values.id}
                              />
                              <Form.Control.Feedback type={"invalid"}>
                                {errors.code}
                              </Form.Control.Feedback>
                              <Form.Text>
                                This code will be used as a prefix when
                                creating new studies. Eg. a code of &apos;PG&apos; would
                                result in a study code such as
                                &apos;PG-10001&apos;.
                              </Form.Text>
                            </FormGroup>
                          </Col>

                        </Row>

                      </Card.Body>
                    </Card>

                    {/* File Storage */}
                    <Card hidden={!!values.id}>
                      <Card.Header>
                        <Card.Title>File Storage</Card.Title>
                        <h6 className="card-subtitle text-muted">
                          Select a root folder location to create the program storage folder in.
                          This will become the default location in which all study and assay files
                          for this program will be created, unless users specify otherwise.
                        </h6>
                      </Card.Header>
                      <Card.Body>

                        {
                          rootFolders && rootFolders.length > 0
                            ? (
                                  <Row>
                                    <Col md={6} className={"mb-3"}>
                                      <FormGroup>
                                        <Form.Label>Parent Folder *</Form.Label>
                                        <Select
                                            className="react-select-container"
                                            classNamePrefix="react-select"
                                            options={
                                              rootFolders
                                              .sort((a, b) => a.name.localeCompare(b.name))
                                              .map(p => ({
                                                label: p.storageDrive.driveType + ": " + p.name,
                                                value: p
                                              }))
                                            }
                                            name="parentFolder"
                                            onChange={(selected) => {
                                              setFieldValue("parentFolder", selected.value);
                                            }}
                                        />
                                        <Form.Control.Feedback type={"invalid"}>
                                          {errors.parentFolder}
                                        </Form.Control.Feedback>
                                        <Form.Text>
                                          Select the parent folder to create the program storage folder in.
                                        </Form.Text>
                                      </FormGroup>
                                    </Col>
                                  </Row>
                              )
                            : (
                                  <Row>
                                    <Col>
                                      <DismissableAlert
                                          header={"No Root Folders Configured"}
                                          message={"Please have your system administrator create at least one 'study root' folder to use for project storage."}
                                          color={"warning"}
                                          dismissable={false}
                                      />
                                    </Col>
                                  </Row>
                              )
                        }

                      </Card.Body>
                    </Card>

                    {/* ELN */}
                    {
                      !values.id
                      && features
                      && features.notebook
                      && features.notebook.isEnabled
                      && features.notebook.mode !== "none" ? (

                        <FeatureToggleCard
                            isActive={values.useNotebook}
                            title={"Electronic Laboratory Notebook (ELN) Folder"}
                            description={"When using an electronic laboratory notebook, all programs require a folder in which all studies and entries will be created. You will have to create the program in the ELN software before Study Tracker can register the study and hook into the ELN platform. Select the program you wish to map your new program to from the dropdown below. If your project is not listed, check with your ELN administrator to ensure the project has been created."}
                            switchLabel={"Does this program need an ELN?"}
                            handleToggle={() => setFieldValue("useNotebook", !values.useNotebook)}
                        >

                          {
                            elnProjects ? (
                                <Row>
                                  <Col md={6} className={"mb-3"}>
                                    <FormGroup>
                                      <Form.Label>ELN Project</Form.Label>
                                      <Select
                                          className="react-select-container"
                                          classNamePrefix="react-select"
                                          options={elnProjectOptions}
                                          name="elnProject"
                                          onChange={(selected) => {
                                            setFieldValue("notebookFolder.name", selected.value.name);
                                            setFieldValue("notebookFolder.url", selected.value.url);
                                            setFieldValue("notebookFolder.referenceId", selected.value.folderId);
                                          }}
                                      />
                                      <Form.Text>
                                        Select an existing project from your ELN to assign your program to.
                                      </Form.Text>
                                    </FormGroup>
                                  </Col>
                                </Row>
                            ) : ""
                          }

                          <Row>

                            <Col md={6} className={"mb-3"}>
                              <FormGroup>
                                <Form.Label>Program Folder ID *</Form.Label>
                                <Form.Control
                                    type="text"
                                    name={"notebookFolder.referenceId"}
                                    isInvalid={!!errors.notebookFolder && !!errors.notebookFolder.referenceId}
                                    value={values.notebookFolder.referenceId}
                                    onChange={handleChange}
                                    disabled={!!elnProjects && elnProjects.length > 0}
                                />
                                <Form.Control.Feedback type={"invalid"}>
                                  Program Folder ID must not be empty.
                                </Form.Control.Feedback>
                                <Form.Text>
                                  This is the ID assigned to the program
                                  folder in the ELN. For example, in Benchling the ID
                                  will take the form of an alphanumeric code with a
                                  prefix of <code>lib_</code>.
                                </Form.Text>
                              </FormGroup>
                            </Col>

                            <Col md={6} className={"mb-3"}>
                              <FormGroup>
                                <Form.Label>Folder Name</Form.Label>
                                <Form.Control
                                    type="text"
                                    name={"notebookFolder.name"}
                                    value={values.notebookFolder.name}
                                    onChange={handleChange}
                                    disabled={!!elnProjects && elnProjects.length > 0}
                                />
                                <Form.Control.Feedback type={"invalid"}>
                                  Folder Name must not be empty.
                                </Form.Control.Feedback>
                                <Form.Text>If different from the program
                                  name.</Form.Text>
                              </FormGroup>
                            </Col>

                            <Col md={6} className={"mb-3"}>
                              <FormGroup>
                                <Form.Label>URL</Form.Label>
                                <Form.Control
                                    type="text"
                                    name={"notebookFolder.url"}
                                    value={values.notebookFolder.url}
                                    onChange={handleChange}
                                    disabled={!!elnProjects && elnProjects.length > 0}
                                />
                                <Form.Control.Feedback type={"invalid"}>
                                  URL must not be empty.
                                </Form.Control.Feedback>
                                <Form.Text>URL for the program in the ELN.</Form.Text>
                              </FormGroup>
                            </Col>

                          </Row>
                        </FeatureToggleCard>

                      ) : ""
                    }

                    {/* Git */}
                    <ProgramGitInputs
                        isActive={values.useGit}
                        gitGroups={gitGroups}
                        selectedGroup={values.gitGroup}
                        defaultGroup={values.gitGroups && values.gitGroups.length > 0 ? values.gitGroups[0].parentGroup : null}
                        onChange={setFieldValue}
                        error={errors.gitGroup}
                    />

                    {/* Attributes */}
                    <Card>
                      <Card.Header>
                        <Card.Title>Program Attributes</Card.Title>
                        <h6 className="card-subtitle text-muted">
                          Key-value attributes for adding additional information
                          about the program, or for adding application-aware
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
                              type={"submit"}
                              className={"me-4"}
                              disabled={isSubmitting}
                          >
                            {isSubmitting ? "Submitting..." : "Submit"}
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

ProgramForm.propTypes = {
  program: PropTypes.object,
  programs: PropTypes.array.isRequired,
  features: PropTypes.object,
  elnProjects: PropTypes.array,
  rootFolders: PropTypes.array,
  gitGroups: PropTypes.array
}

export default ProgramForm;