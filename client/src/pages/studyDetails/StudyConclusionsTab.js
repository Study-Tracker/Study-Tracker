/*
 * Copyright 2022 the original author or authors.
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
import Conclusions from "../../common/Conclusions";
import axios from "axios";
import PropTypes from "prop-types";
import {Form as FormikForm, Formik} from "formik";
import {Button, Card, Col, Form, Modal, Row} from "react-bootstrap";
import ReactQuill from "react-quill";
import {FormGroup} from "../../common/forms/common";
import FormikFormErrorNotification
  from "../../common/forms/FormikFormErrorNotification";
import * as yup from "yup";

const StudyConclusionsTab = props => {

  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [conclusions, setConclusions] = useState(props.study.conclusions);
  const conclusionsDefaults = {
    id: null,
    content: ""
  }
  const conclusionsSchema = yup.object().shape({
    id: yup.number().nullable(true),
    content: yup.string().required("Please enter the study conclusions")
  });

  const handleFormSubmit = (values, {setSubmitting}) => {
    axios({
      url: "/api/internal/study/" + props.study.code + "/conclusions",
      method: !!values.id ? 'put' : 'post',
      data: values
    })
    .then(response => {
      setSubmitting(false);
      setConclusions(response.data);
      setModalIsOpen(false);
    }).catch(e => {
      setSubmitting(false);
      console.error(e);
    })
  }

  return (
      <Card>
        <Card.Body>

          <Conclusions
              conclusions={conclusions}
              showModal={setModalIsOpen}
              isSignedIn={!!props.user}
          />

          <Formik
              initialValues={conclusions || conclusionsDefaults}
              onSubmit={handleFormSubmit}
              validationSchema={conclusionsSchema}
          >
            {({
                values,
                errors,
                touched,
                handleChange,
                handleSubmit,
                setFieldValue
            }) => (

                <>

                  <FormikFormErrorNotification />

                  <Modal
                      show={modalIsOpen}
                      onHide={() => setModalIsOpen(false)}
                      size={"lg"}
                  >

                    <Modal.Header closeButton>
                      Add Conclusions
                    </Modal.Header>

                    <Modal.Body className="m-3">

                      <Row>

                        <Col sm={12}>
                          <p>
                            Add a brief summary of your study's conclusions. Supporting
                            documents may be uploaded as attachments.
                          </p>
                        </Col>

                        <FormikForm>

                          <Col sm={12}>
                            <FormGroup>
                              <ReactQuill
                                  theme="snow"
                                  name={'content'}
                                  value={values.content}
                                  onChange={(content) => {setFieldValue('content', content)}}
                                  className={(errors.content && touched.content) ? 'is-invalid' : ''}
                              />
                              <Form.Control.Feedback type="invalid">
                                {errors.content}
                              </Form.Control.Feedback>
                            </FormGroup>
                          </Col>

                        </FormikForm>

                      </Row>

                    </Modal.Body>

                    <Modal.Footer>
                      <Button variant={"secondary"}
                              onClick={() => setModalIsOpen(false)}>
                        Cancel
                      </Button>
                      <Button variant={"primary"} onClick={handleSubmit}>
                        Save
                      </Button>
                    </Modal.Footer>

                  </Modal>

                </>
            )}
          </Formik>
        </Card.Body>
      </Card>
  );

}

StudyConclusionsTab.propTypes = {
  study: PropTypes.object.isRequired,
  user: PropTypes.object
}
export default StudyConclusionsTab;