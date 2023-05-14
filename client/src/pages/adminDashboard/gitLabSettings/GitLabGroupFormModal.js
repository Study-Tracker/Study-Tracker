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
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import FormikFormErrorNotification
  from "../../../common/forms/FormikFormErrorNotification";
import {FormGroup} from "../../../common/forms/common";
import axios from "axios";
import AsyncSelect from "react-select/async";

const GitLabGroupFormModal = ({
    isOpen,
    setIsOpen,
    selectedGroup,
    handleFormSubmit,
    integration,
    formikRef
}) => {

  const groupSchema = yup.object().shape({
    name: yup.string()
      .required("Name is required")
      .max(255, "Name must be less than 255 characters"),
    path: yup.string()
      .required("Path is required")
      .max(255, "Path must be less than 255 characters"),
    groupId: yup.string()
      .required("Group ID is required"),
    gitGroup: yup.object().shape({
      parentGroupId: yup.number()
        .nullable(),
      displayName: yup.string()
        .required("Display name is required")
        .max(255, "Display name must be less than 255 characters"),
      webUrl: yup.string()
        .required("Web URL is required"),
      active: yup.boolean()
    })
  });

  const groupDefault = {
    name: null,
    path: null,
    groupId: null,
    gitGroup: {
      parentGroupId: null,
      displayName: null,
      webUrl: null,
      active: true,
      gitServiceType: "GITLAB"
    }
  }

  const groupAutoComplete = (input, callback) => {
    axios.get("/api/internal/integrations/gitlab/" + integration.id + "/groups/available?q=" + input)
    .then(response => {
      const options = response.data
      .filter(g => g.parentGroupId === null)
      .map(group => {
        return {label: group.name, value: group.groupId, obj: group}
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
          initialValues={selectedGroup || groupDefault}
          onSubmit={handleFormSubmit}
          validationSchema={groupSchema}
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
                {values.id ? 'Edit' : 'Add'} Project Group
              </Modal.Header>
              <Modal.Body className={"mb-3"}>
                <FormikForm autoComplete={"off"}>

                  <FormikFormErrorNotification />

                  <Row>
                    <Col>
                      <FormGroup>
                        <Form.Label>GitLab Group</Form.Label>
                        <AsyncSelect
                            placeholder="Search-for and select a project group site..."
                            className={"react-select-container"}
                            classNamePrefix="react-select"
                            loadOptions={groupAutoComplete}
                            value={values.group ? {label: values.group.name, value: values.group.groupId, obj: values.group} : null}
                            onChange={(selected) => {
                              setFieldValue("group", selected ? selected.obj : null);
                              setFieldValue("name", selected ? selected.obj.name : null);
                              setFieldValue("path", selected ? selected.obj.path : null);
                              setFieldValue("groupId", selected ? selected.obj.groupId : null);
                              setFieldValue("gitGroup.displayName", selected ? selected.obj.name : null);
                              setFieldValue("gitGroup.webUrl", selected ? selected.obj.webUrl : null);
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
                            name={"gitGroup.displayName"}
                            isInvalid={errors.gitGroup && errors.gitGroup.displayName && touched.gitGroup && touched.gitGroup.displayName}
                            value={values.gitGroup.displayName}
                            onChange={handleChange}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {errors.gitGroup && errors.gitGroup.displayName}
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

GitLabGroupFormModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
  selectedGroup: PropTypes.object,
  handleFormSubmit: PropTypes.func.isRequired,
  formikRef: PropTypes.object.isRequired,
  integration: PropTypes.object.isRequired
}

export default GitLabGroupFormModal;