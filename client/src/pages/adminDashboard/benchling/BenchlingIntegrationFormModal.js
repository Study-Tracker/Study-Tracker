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

import React, {useContext} from "react";
import PropTypes from "prop-types";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import NotyfContext from "../../../context/NotyfContext";
import {useMutation, useQueryClient} from "react-query";
import * as yup from "yup";
import axios from "axios";
import {Form as FormikForm, Formik} from "formik";
import FormikFormErrorNotification from "../../../common/forms/FormikFormErrorNotification";
import {FormGroup} from "../../../common/forms/common";
import CharacterCounter from "../../../common/forms/CharacterCounter";

const BenchlingIntegrationFormModal = ({
  isOpen,
  setIsOpen,
  selectedIntegration,
  formikRef
}) => {

  const notyf = useContext(NotyfContext);
  const queryClient = useQueryClient();

  const integrationSchema = yup.object().shape({
    id: yup.string()
      .nullable(),
    name: yup.string()
      .max(64)
      .required("Name is required"),
    tenantName: yup.string()
      .max(128)
      .required("Tenant name is required"),
    rootUrl: yup.string(),
    clientId: yup.string()
      .required("Client ID is required"),
    clientSecret: yup.string()
      .required("Client Secret is required"),
    enabled: yup.boolean()
  });

  const integrationDefault = {
    id: null,
    name: "Benchling",
    tenantName: "",
    enabled: true,
    rootUrl: "",
    clientId: "",
    clientSecret: "",
  }

  const saveIntegrationMutation = useMutation(async values => {
    const url = "/api/internal/integrations/benchling" + (values.id ? `/${values.id}` : '');
    const method = values.id ? 'PUT' : 'POST';
    return axios({
      url: url,
      method: method,
      data: values,
      headers: {"Content-Type": "application/json"}
    })
  });

  const handleIntegrationFormSubmit = async (values, {setSubmitting, resetForm, setErrors}) => {
    setSubmitting(true);
    console.debug("Saving Benchling integration settings", values);
    saveIntegrationMutation.mutate(values, {
      onSuccess: () => {
        queryClient.invalidateQueries({queryKey: "benchlingSettings"});
        setIsOpen(false);
        resetForm();
        notyf.success("Benchling integration settings saved");
      },
      onError: (error) => {
        console.error(error);
        setErrors({"submit": error.response.data.message});
        notyf.error("Failed to save Benchling integration settings: " + error.response.data.message);
      },
      onSettled: () => {
        setSubmitting(false);
      }
    });
  };

  return (
    <Formik
      initialValues={selectedIntegration || integrationDefault}
      onSubmit={handleIntegrationFormSubmit}
      validationSchema={integrationSchema}
      innerRef={formikRef}
      enableReinitialize={true}
    >
      {({
        values,
        handleChange,
        handleSubmit,
        errors,
        touched,
        isSubmitting,
        setFieldValue
      }) => (
        <Modal show={isOpen} onHide={() => setIsOpen(false)}>
          <Modal.Header closeButton>
            Register Benchling Integration
          </Modal.Header>
          <Modal.Body className={"mb-3"}>
            <FormikForm autoComplete={"off"}>

              <FormikFormErrorNotification />

              <Row>
                <Col>
                  <FormGroup>
                    <Form.Label>Display Name</Form.Label>
                    <Form.Control
                      type={"text"}
                      name={"name"}
                      isInvalid={errors.name && touched.name}
                      value={values.name}
                      onChange={handleChange}
                    />
                    <Form.Text>
                      Provide a display name for your Benchling instance.
                    </Form.Text>
                    <CharacterCounter count={values.name.length} limit={64} />
                    <Form.Control.Feedback type={"invalid"}>
                      {errors.name}
                    </Form.Control.Feedback>
                  </FormGroup>
                </Col>
              </Row>

              <Row>
                <Col>
                  <FormGroup>
                    <Form.Label>Tenant Name</Form.Label>
                    <Form.Control
                      type={"text"}
                      name={"tenantName"}
                      isInvalid={errors.tenantName && touched.tenantName}
                      value={values.tenantName}
                      onChange={e => {
                        const t = e.target.value.toLowerCase();
                        setFieldValue("tenantName", t);
                        setFieldValue("rootUrl", `https://${t}.benchling.com`);
                      }}
                    />
                    <Form.Text>
                      The name of your Benchling tenant. This is the subdomain
                      of your Benchling URL. For example, if your Benchling URL
                      is <code>https://mycompany.benchling.com</code>, then your
                      tenant name is <code>mycompany</code>.
                    </Form.Text>
                    <Form.Control.Feedback type={"invalid"}>
                      {errors.tenantName}
                    </Form.Control.Feedback>
                  </FormGroup>
                </Col>
              </Row>

              <Row>
                <Col>
                  <FormGroup>
                    <Form.Label>Tenant URL</Form.Label>
                    <Form.Control
                      type={"text"}
                      name={"rootUrl"}
                      value={values.rootUrl}
                      disabled={true}
                      onChange={handleChange}
                    />
                    <Form.Control.Feedback type={"invalid"}>
                      {errors.rootUrl}
                    </Form.Control.Feedback>
                  </FormGroup>
                </Col>
              </Row>

              <Row>
                <Col>
                  <FormGroup>
                    <Form.Label>App Client ID</Form.Label>
                    <Form.Control
                      type={"text"}
                      name={"clientId"}
                      isInvalid={errors.clientId && touched.clientId}
                      value={values.clientId}
                      onChange={handleChange}
                    />
                    <Form.Control.Feedback type={"invalid"}>
                      {errors.clientId}
                    </Form.Control.Feedback>
                    {
                      values.id && (
                        <Form.Text className={"text-danger"}>
                          Please reenter your app Client ID.<br />
                        </Form.Text>
                      )
                    }
                    <Form.Text>
                      Your Benchling App's Client ID.
                    </Form.Text>
                  </FormGroup>
                </Col>
              </Row>

              <Row>
                <Col>
                  <FormGroup>
                    <Form.Label>App Client Secret</Form.Label>
                    <Form.Control
                      type={"password"}
                      name={"clientSecret"}
                      isInvalid={errors.clientSecret && touched.clientSecret}
                      value={values.clientSecret}
                      onChange={handleChange}
                    />
                    <Form.Control.Feedback type={"invalid"}>
                      {errors.clientSecret}
                    </Form.Control.Feedback>
                    {
                      values.id && (
                        <Form.Text className={"text-danger"}>
                          Please reenter your app Client Secret.<br />
                        </Form.Text>
                      )
                    }
                    <Form.Text>
                      You Benchling App's client secret.
                    </Form.Text>
                  </FormGroup>
                </Col>
              </Row>

            </FormikForm>
          </Modal.Body>
          <Modal.Footer>
            <Button
              variant={"secondary"}
              onClick={() => setIsOpen(false)}
            >
              Cancel
            </Button>
            <Button
              variant={"primary"}
              onClick={!isSubmitting ? handleSubmit : null}
              disabled={isSubmitting}
            >
              {isSubmitting ? "Submitting..." : "Submit"}
            </Button>
          </Modal.Footer>
        </Modal>
      )}
    </Formik>
  )

}

BenchlingIntegrationFormModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
  formikRef: PropTypes.object.isRequired,
  selectedIntegration: PropTypes.object
}

export default BenchlingIntegrationFormModal;