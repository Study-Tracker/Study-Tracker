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

import {Alert, Button, Card, Col, Form, Modal, Row} from "react-bootstrap";
import React, {useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimesCircle} from "@fortawesome/free-solid-svg-icons";
import swal from 'sweetalert';
import {relationshipTypes} from "../config/studyRelationshipConstants";
import Select from "react-select";
import AsyncSelect from "react-select/async/dist/react-select.esm";
import {PlusCircle} from "react-feather";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import axios from "axios";
import PropTypes from "prop-types";

const StudyRelationships = props => {

  const [relationships, setRelationships] = useState(props.relationships);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [error, setError] = useState(null);

  const relationshipDefaults = {
    targetStudyId: '',
    type: ''
  };

  const relationshipSchema = yup.object().shape({
    targetStudyId: yup.string().required('Study is required'),
    type: yup.string().required('Relationship type is required')
  });

  const studyAutocomplete = (input, callback) => {
    axios.get("/api/internal/autocomplete/study?q=" + input)
    .then(response => {
      const options = response.data
      .filter(s => s.code !== props.studyCode && s.active)
      .sort((a, b) => {
        const al = a.code + ": " + a.name;
        const bl = b.code + ": " + b.name;
        if (al > bl) {
          return -1;
        }
        if (al < bl) {
          return 1;
        } else {
          return 0;
        }
      })
      .map(study => {
        return {
          label: study.code + ": " + study.name,
          value: study.code,
          obj: study
        }
      });
      callback(options);
    }).catch(e => {
      console.error(e);
    })
  }

  const handleFormSubmit = (values, {setSubmitting}) => {
    axios.post("/api/internal/study/" + props.studyCode + "/relationships", values)
    .then(response => {
      setSubmitting(false);
      setRelationships([...relationships, response.data]);
      setError(null);
      setModalIsOpen(false);
    })
    .catch(error => {
      console.error(error);
      setError(error.response.data.message);
    })

  }

  const handleRelationshipDelete = (relationship) => {
    swal({
      title: "Are you sure you want to delete this relationship?",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        axios.delete("/api/internal/study/" + props.studyCode + "/relationships/"
            + relationship.id)
        .then(response => {
          setRelationships(relationships.filter(r => r.id !== relationship.id))
        })
        .catch(error => {
          console.error(error);
          setError(error.response.data.message);
        })
      }
    });
  }

  const relationshipList = relationships.map(relationship => {
    const type = relationshipTypes[relationship.type];
    return (
        <li key={"study-relationship-" + relationship.targetStudy.id}>
          {type.label}
          &nbsp;&nbsp;
          <a href={"/study/"
              + relationship.targetStudy.code}>{relationship.targetStudy.code}</a>
          &nbsp;&nbsp;&nbsp;&nbsp;
          {
            !!props.user ? (
                <a onClick={() => handleRelationshipDelete(relationship)}>
                  <FontAwesomeIcon color={"red"} icon={faTimesCircle}/>
                </a>
            ) : ''
          }
        </li>
    );
  });

  const relationshipOptions = Object.values(relationshipTypes);

  return (
      <div>

        <Card.Title>
          Study Relationships
          {
            !!props.user ? (
                <span className="float-end">
                <Button size={"sm"} variant={"primary"}
                        onClick={() => setModalIsOpen(true)}>
                  Add <PlusCircle className="feather feather-button-sm"/>
                </Button>
              </span>
            ) : ''
          }
        </Card.Title>

        {
          relationshipList.length
              ? (
                  <ul>
                    {relationshipList}
                  </ul>
              )
              : (
                  <p className="text-muted text-center">
                    No linked studies.
                  </p>
              )
        }

        <Formik
            initialValues={relationshipDefaults}
            validationSchema={relationshipSchema}
            onSubmit={handleFormSubmit}
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
              <Modal
                  show={modalIsOpen}
                  onHide={() => setModalIsOpen(false)}
              >

                <Modal.Header closeButton>
                  Add New Study Relationship
                </Modal.Header>

                <Modal.Body className="m-3">

                  <FormikForm>

                    <Row>

                      <Col sm={12}>
                        <p>
                          Please select a study you would like to link and an
                          appropriate relationship type.
                        </p>
                      </Col>

                      <Col xs={12} sm={4}>
                        <Form.Group>
                          <Form.Label>Relationship *</Form.Label>
                          <Select
                              className={"react-select-container " + (!!errors.type && touched.type ? "is-invalid" : "")}
                              classNamePrefix="react-select"
                              options={relationshipOptions}
                              name="type"
                              onChange={(selected) => setFieldValue("type", selected.value)}
                              value={values.type}
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.type}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>

                      <Col xs={12} sm={8}>
                        <Form.Group>
                          <Form.Label>Study *</Form.Label>
                          <AsyncSelect
                              placeholder="Search for studies..."
                              name="targetStudyId"
                              className={"react-select-container " + (errors.targetStudyId && touched.targetStudyId ? "is-invalid" : "")}
                              classNamePrefix="react-select"
                              loadOptions={studyAutocomplete}
                              onChange={(selected) => setFieldValue("targetStudyId", selected.obj.id)}
                              defaultOptions={true}
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.targetStudyId}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>

                    </Row>
                    {
                      !!error
                          ? (
                              <Row>
                                <Col sm={12}>
                                  <Alert variant={"warning"}>
                                    <div className="alert-message">
                                      {error}
                                    </div>
                                  </Alert>
                                </Col>
                              </Row>
                          ) : ''
                    }

                  </FormikForm>

                </Modal.Body>

                <Modal.Footer>
                  <Button variant={"secondary"}
                          onClick={() => setModalIsOpen(false)}>
                    Cancel
                  </Button>
                  <Button
                      variant={"primary"}
                      onClick={handleSubmit}
                  >
                    Save
                  </Button>
                </Modal.Footer>

              </Modal>
          )}
        </Formik>

      </div>
  );

}

StudyRelationships.propTypes = {
  studyCode: PropTypes.string.isRequired,
  user: PropTypes.object
}

export default StudyRelationships;