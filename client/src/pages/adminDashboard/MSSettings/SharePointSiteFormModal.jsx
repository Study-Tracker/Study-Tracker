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
import PropTypes from "prop-types";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import {Alert, Button, Col, Form, Modal, Row} from "react-bootstrap";
import FormikFormErrorNotification
  from "../../../common/forms/FormikFormErrorNotification";
import {FormGroup} from "../../../common/forms/common";
import axios from "axios";
import AsyncSelect from "react-select/async";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faInfoCircle} from "@fortawesome/free-solid-svg-icons";

const SharePointSiteFormModal = ({
    isOpen,
    setIsOpen,
    selectedSite,
    handleFormSubmit,
    integration,
    formikRef
}) => {

  const siteSchema = yup.object().shape({
    name: yup.string()
      .max(255, "Name must be less than 255 characters"),
    url: yup.string()
      .nullable(),
    siteId: yup.string()
      .required("Site ID is required"),
    active: yup.boolean()
  });

  const siteDefault = {
    name: null,
    url: null,
    siteId: null,
    active: true
  }

  const siteAutoComplete = (input, callback) => {
    axios.get("/api/internal/integrations/msgraph/" + integration.id + "/sharepoint/available?q=" + input)
    .then(response => {
      const options = response.data
      .map(site => {
        return {label: site.name, value: site.siteId, obj: site}
      })
      .sort((a, b) => {
        if (a.label < b.label) return -1;
        else if (a.label > b.label) return 1;
        else return 0;
      });
      callback(options);
    })
    .catch(e => {
      console.error(e);
    })
  }

  return (
      <Formik
          initialValues={selectedSite || siteDefault}
          onSubmit={handleFormSubmit}
          validationSchema={siteSchema}
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
                {values.id ? 'Edit' : 'Add'} SharePoint Site
              </Modal.Header>
              <Modal.Body className={"mb-3"}>
                <FormikForm autoComplete={"off"}>

                  <FormikFormErrorNotification />

                  <Row>
                    <Col>
                      <Alert variant={"info"} className={"alert-outline"}>
                        <div className="alert-icon">
                          <FontAwesomeIcon icon={faInfoCircle} fixedWidth />
                        </div>
                        <div className="alert-message">
                          You can search for SharePoint sites by name or by site ID. For organizations
                          with restricted site access, SharePoint site options might not appear unless
                          you provide the exact site ID. Reach out to your local Microsoft administrator
                          for more information.
                        </div>
                      </Alert>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>SharePoint Site</Form.Label>
                        <AsyncSelect
                            placeholder="Search-for and select a SharePoint site..."
                            className={"react-select-container"}
                            classNamePrefix="react-select"
                            loadOptions={siteAutoComplete}
                            value={values.site ? {label: values.site.name, value: values.site.siteId, obj: values.site} : null}
                            onChange={(selected) => {
                              setFieldValue("site", selected ? selected.obj : null);
                              setFieldValue("siteId", selected ? selected.obj.siteId : null);
                              setFieldValue("url", selected ? selected.obj.url : null);
                              setFieldValue("name", selected ? selected.obj.name : null);
                            }}
                            defaultOptions={true}
                            isClearable={true}
                        />
                      </FormGroup>
                    </Col>
                  </Row>

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>Display Name *</Form.Label>
                        <Form.Control
                            type={"text"}
                            name={"name"}
                            isInvalid={errors.name && touched.name}
                            value={values.name}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.name}
                        </Form.Control.Feedback>
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

SharePointSiteFormModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
  selectedFolder: PropTypes.object,
  handleFormSubmit: PropTypes.func.isRequired,
  formikRef: PropTypes.object.isRequired,
  integration: PropTypes.object.isRequired
}

export default SharePointSiteFormModal;