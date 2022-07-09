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

import React, {useState} from "react";
import {getCsrfToken} from "../../config/csrf";
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
import swal from 'sweetalert';
import ReactQuill from "react-quill";
import {LoadingOverlay} from "../loading";
import Select from "react-select";
import AttributeInputs from "./attributes";
import {FormGroup} from "./common";
import PropTypes from "prop-types";
import {Form as FormikForm, Formik} from "formik";
import {useNavigate} from "react-router-dom";
import FormikFormErrorNotification from "./FormikFormErrorNotification";

const ProgramForm = props => {

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

  // constructor(props) {
  //   super(props);
  //
  //   this.state = {
  //     program: props.program || {
  //       active: true,
  //       attributes: {},
  //       notebookFolder: {}
  //     },
  //     validation: {
  //       nameIsValid: true,
  //       nameIsUnique: true,
  //       descriptionIsValid: true,
  //       codeIsValid: true,
  //       programFolderIdIsValid: true
  //     },
  //     showLoadingOverlay: false
  //   };
  //   this.handleSubmit = this.handleSubmit.bind(this);
  //   this.handleCancel = this.handleCancel.bind(this);
  //   this.handleFormUpdate = this.handleFormUpdate.bind(this);
  //   this.validateForm = this.validateForm.bind(this);
  // }

  // /**
  //  * Updates the program state when an input is changed.
  //  *
  //  * @param data
  //  */
  // const handleFormUpdate = (data) => {
  //   const program = {
  //     ...state.program,
  //     ...data
  //   };
  //   console.log(program);
  //   this.setState({
  //     program: program
  //   })
  // }

  // const validateForm = (program) => {
  //
  //   const errors = {};
  //
  //   // Name
  //   if (!program.name) {
  //     errors.name = 'Name is required';
  //   }
  //   if (!program.id) {
  //     for (let p of this.props.programs) {
  //       if (!!program.name && p.name.toLowerCase()
  //           === program.name.toLowerCase()) {
  //         errors.name = 'Name is not unique';
  //       }
  //     }
  //   }
  //
  //   // Description
  //   if (!program.description) {
  //     errors.description = 'Description is required';
  //   }
  //
  //   // Code
  //   if (/[^A-Za-z0-9]/.test(program.code)) {
  //     errors.code = "Code is invalid";
  //   }
  //
  //   // ELN
  //   if (this.props.features
  //       && this.props.features.notebook.isEnabled
  //       && !program.notebookFolder.referenceId
  //   ) {
  //     errors.notebookFolder.referenceId = "Notebook folder ID is required";
  //   }
  //
  //   return errors;
  //
  // }

  // const handleSubmit = (values, {setSubmitting}) => {
  //
  //   let isError = this.validateForm(this.state.program);
  //   console.log(this.state);
  //
  //   if (isError) {
  //
  //     swal("Looks like you forgot something...",
  //         "Check that all of the required inputs have been filled and then try again.",
  //         "warning");
  //     console.warn("Validation failed.");
  //
  //   } else {
  //
  //     const isUpdate = !!this.state.program.id;
  //     const url = isUpdate
  //         ? "/api/program/" + this.state.program.id
  //         : "/api/program";
  //     this.setState({showLoadingOverlay: true});
  //
  //     fetch(url, {
  //       method: isUpdate ? "PUT" : "POST",
  //       headers: {
  //         "Content-Type": "application/json",
  //         "X-XSRF-TOKEN": getCsrfToken()
  //       },
  //       body: JSON.stringify(this.state.program)
  //     })
  //     .then(async response => {
  //
  //       const json = await response.json();
  //       console.log(json);
  //       if (response.ok) {
  //         history.push("/program/" + json.id);
  //       } else {
  //         this.setState({showLoadingOverlay: false})
  //         swal("Something went wrong",
  //             !!json.message
  //                 ? "Error: " + json.message :
  //                 "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
  //         );
  //         console.error("Request failed.");
  //       }
  //
  //     }).catch(e => {
  //       this.setState({showLoadingOverlay: false})
  //       swal(
  //           "Something went wrong",
  //           "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
  //       );
  //       console.error(e);
  //     });
  //   }
  // };

  const handleCancel = () => {
    swal({
      title: "Are you sure you want to leave the page?",
      text: "Any unsaved work will be lost.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        navigate("/programs");
      }
    });
  };

  return (
      <Container fluid className="animated fadeIn max-width-1200">

        <LoadingOverlay
            isVisible={showLoadingOverlay}
            message={"Saving program..."}
        />

        <Row>
          <Col>
            {
              !!props.program
                  ? (
                      <Breadcrumb>
                        <Breadcrumb.Item href={"/"}>Home</Breadcrumb.Item>
                        <Breadcrumb.Item
                            href={"/program/" + props.program.id}>
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
            <h3>{!!props.program ? "Edit Program"
                : "New Program"}</h3>
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
                  'inactive'. Inactive programs will remain in the system,
                  along with their studies, but no new non-legacy studies
                  will be allowed to be created for it.
                </h6>
              </Card.Header>

              <Card.Body>
                <Formik
                    initialValues={props.program || {
                      active: true,
                      attributes: {},
                      notebookFolder: {},
                    }}
                    validate={(program) => {

                      const errors = {};

                      // Name
                      if (!program.name) {
                        errors.name = 'Name is required';
                      }
                      if (!program.id) {
                        for (let p of props.programs) {
                          if (!!program.name && p.name.toLowerCase()
                              === program.name.toLowerCase()) {
                            errors.name = 'Name is not unique';
                          }
                        }
                      }

                      // Description
                      if (!program.description) {
                        errors.description = 'Description is required';
                      }

                      // Code
                      if (!program.code) {
                        errors.code = "Code is required";
                      }
                      if (/[^A-Za-z0-9]/.test(program.code)) {
                        errors.code = "Code is invalid";
                      }

                      // ELN
                      if (props.features
                          && props.features.notebook.isEnabled
                          && !program.notebookFolder.referenceId
                      ) {
                        errors.notebookFolder.referenceId = "Notebook folder ID is required";
                      }

                      return errors;

                    }}
                    onSubmit={(values, {setSubmitting}) => {
                      console.debug(values);
                      const isUpdate = !!values.id;
                      const url = isUpdate
                          ? "/api/program/" + values.id
                          : "/api/program";
                      setShowLoadingOverlay(true);

                      fetch(url, {
                        method: isUpdate ? "PUT" : "POST",
                        headers: {
                          "Content-Type": "application/json",
                          "X-XSRF-TOKEN": getCsrfToken()
                        },
                        body: JSON.stringify(values)
                      })
                      .then(async response => {
                        const json = await response.json();
                        console.log(json);
                        setSubmitting(false);
                        if (response.ok) {
                          navigate("/program/" + json.id);
                        } else {
                          setShowLoadingOverlay(false);
                          swal("Something went wrong",
                              !!json.message
                                  ? "Error: " + json.message :
                                  "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
                          );
                          console.error("Request failed.");
                        }

                      }).catch(e => {
                        setShowLoadingOverlay(false);
                        swal(
                            "Something went wrong",
                            "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
                        );
                        console.error(e);
                      });
                    }}
                >
                  {({
                    values,
                    errors,
                    touched,
                    handleChange,
                    handleBlur,
                    handleSubmit,
                    isSubmitting,
                    setFieldValue
                  }) => (
                      <FormikForm className="program-form">

                        <FormikFormErrorNotification />

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
                              <div>
                                <ReactQuill
                                    theme="snow"
                                    className={"mb-2"}
                                    name={"description"}
                                    value={values.description}
                                    onChange={content => setFieldValue("description", content)}
                                />
                              </div>
                              <Form.Control.Feedback type="invalid">
                                Description must not be empty.
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
                                Code must not be empty and must not
                                contain any whitespace or non-alphanumeric
                                characters.
                              </Form.Control.Feedback>
                              <Form.Text>
                                This code will be used as a prefix when
                                creating new studies. Eg. a code of 'PG' would
                                result in a study code such as
                                'PG-10001'.
                              </Form.Text>
                            </FormGroup>
                          </Col>

                        </Row>

                        {
                          props.features
                          && props.features.notebook
                          && props.features.notebook.isEnabled ? (

                            <React.Fragment>

                              <Row>
                                <Col>
                                  <hr/>
                                </Col>
                              </Row>

                              <Row>

                                <Col md={12} className={"mb-3"}>
                                  <h5 className="card-title">
                                    Electronic Laboratory Notebook Folder
                                  </h5>
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

                                <Col md={6} className={"mb-3"}>
                                  <FormGroup>
                                    <Form.Label>Program Folder ID *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name={"notebookFolder.referenceId"}
                                        isValid={touched.notebookFolder.referenceId && !errors.notebookFolder.referenceId}
                                        value={values.notebookFolder.referenceId}
                                        onChange={handleChange}
                                    />
                                    <Form.Control.Feedback type={"invalid"}>
                                      Program Folder ID must not be empty.
                                    </Form.Control.Feedback>
                                    <Form.Text>This is the ID assigned to the program
                                      folder in the ELN. For example, in Benchling the ID
                                      will take the form of an alphanumeric code with a
                                      prefix of <code>lib_</code>.</Form.Text>
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
                                    />
                                    <Form.Control.Feedback type={"invalid"}>
                                      URL must not be empty.
                                    </Form.Control.Feedback>
                                    <Form.Text>URL for the program in the ELN.</Form.Text>
                                  </FormGroup>
                                </Col>

                              </Row>

                            </React.Fragment>

                          ) : ""
                        }

                        <Row>
                          <Col>
                            <hr/>
                          </Col>
                        </Row>

                        <Row>

                          <Col md={12} className={"mb-3"}>
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

                        <AttributeInputs
                            attributes={values.attributes}
                            handleUpdate={(attributes) => setFieldValue("attributes", attributes)}
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
                              <Button
                                  size="lg"
                                  variant="primary"
                                  type={"submit"}
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
                    )}
                </Formik>
              </Card.Body>
            </Card>
          </Col>
        </Row>

      </Container>
  );


}

ProgramForm.propTypes = {
  program: PropTypes.object,
  programs: PropTypes.array.isRequired,
  user: PropTypes.object,
  features: PropTypes.object,
}

export default ProgramForm;