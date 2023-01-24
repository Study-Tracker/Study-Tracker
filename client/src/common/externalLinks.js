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

import {Alert, Button, Card, Col, Form, Modal, Row} from "react-bootstrap";
import React, {useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faLink, faTimesCircle} from "@fortawesome/free-solid-svg-icons";
import swal from 'sweetalert';
import {PlusCircle} from "react-feather";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import axios from "axios";

const ExternalLinks = props => {

  const [links, setLinks] = useState(props.links);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [error, setError] = useState(null);
  const linkDefaults = {
    label: '',
    url: ''
  };
  const linkSchema = yup.object().shape({
    label: yup.string()
      .required("Label is required.")
      .max(255, "Label must not be larger than 255 characters."),
    url: yup.string()
      .required("URL is required.")
      .url("URL must be a valid URL.")
  });

  const handleFormSubmit = (values, {setSubmitting}) => {
    axios.post("/api/internal/study/" + props.studyCode + "/links", values)
    .then(response => {
      setSubmitting(false);
      setLinks([...links, response.data]);
      setError(null);
      setModalIsOpen(false);
    })
    .catch(error => {
      setSubmitting(false);
      setError(error.response.data.message);
    })

  }

  const handleLinkDelete = (link) => {
    swal({
      title: "Are you sure you want to delete this link?",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        axios.delete("/api/internal/study/" + props.studyCode + "/links/" + link.id, link)
        .then(response => {
          setLinks(links.filter(l => l.label !== link.label && l.url !== link.url));
        })
        .catch(error => {
          console.error(error);
          setError(error.response.data.message);
        })
      }
    });
  }

  const linkList = links.map(link => {
    return (
        <li key={"external-link-" + link.id}>
          <FontAwesomeIcon icon={faLink}/>
          &nbsp;&nbsp;
          <a href={link.url} target="_blank" rel="noopener noreferrer">{link.label}</a>
          &nbsp;&nbsp;&nbsp;&nbsp;
          {
            !!props.user ? (
                <a onClick={() =>  handleLinkDelete(link)}>
                  <FontAwesomeIcon color={"red"} icon={faTimesCircle}/>
                </a>
            ) : ''
          }
        </li>
    );
  });

  return (
      <div>

        <Card.Title>
          External Links
          <span className="float-end">
            <Button size={"sm"} variant={"link"}
                    onClick={() => setModalIsOpen(true)}>
              Add <PlusCircle className="feather feather-button-sm"/>
            </Button>
          </span>
        </Card.Title>

        {
          linkList.length
              ? (
                  <ul className="list-unstyled">
                    {linkList}
                  </ul>
              )
              : (
                  <p className="text-muted text-center">
                    No external links.
                  </p>
              )
        }

        <Formik
            initialValues={linkDefaults}
            validationSchema={linkSchema}
            onSubmit={handleFormSubmit}
        >
          {({
            values,
            errors,
            touched,
            handleChange,
            handleSubmit,
            isSubmitting,
            setFieldValue,
          }) => (

              <Modal
                  show={modalIsOpen}
                  onHide={() => setModalIsOpen(false)}
              >

                <Modal.Header closeButton>
                  Add New External Link
                </Modal.Header>

                <Modal.Body className="m-3">

                  <FormikForm>

                    <Row>

                      <Col sm={12}>
                        <p>
                          Please provide a complete URL to the target resource, as
                          well as a descriptive label.
                        </p>
                      </Col>

                      <Col sm={12}>
                        <Form.Group>
                          <Form.Label>Label *</Form.Label>
                          <Form.Control
                              type="text"
                              name="label"
                              value={values.label}
                              onChange={handleChange}
                              className={(errors.label && touched.label) ? "is-invalid" : ""}
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.label}
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>

                      <Col sm={12}>
                        <Form.Group>
                          <Form.Label>URL *</Form.Label>
                          <Form.Control
                              type="text"
                              name="url"
                              value={values.url}
                              onChange={handleChange}
                              className={(errors.url && touched.url) ? "is-invalid" : ""}
                          />
                          <Form.Control.Feedback type="invalid">
                            {errors.url}
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
                  <Button variant={"primary"}
                          onClick={handleSubmit}>
                    Save
                  </Button>
                </Modal.Footer>

              </Modal>

            )}
        </Formik>

      </div>
  );

}

export default ExternalLinks;