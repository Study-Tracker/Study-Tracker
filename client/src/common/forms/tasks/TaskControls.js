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
import {Col, Form, Row} from "react-bootstrap";
import {FormGroup} from "../common";
import DatePicker from "react-datepicker";
import AsyncSelect from "react-select/async";
import axios from "axios";
import CustomFieldDefinitionDraggableCardList
  from "../customFields/CustomFieldDefinitionDraggableCardList";

const userAutocomplete = (input, callback) => {
  axios.get("/api/internal/autocomplete/user?q=" + input)
  .then(response => {
    const options = response.data
    .map(user => {
      return {label: user.displayName, value: user.id, obj: user}
    })
    .sort((a, b) => {
      if (a.label < b.label) return -1;
      else if (a.label > b.label) return 1;
      else return 0;
    });
    callback(options);
  }).catch(e => {
    console.error(e);
  })
}

const TaskControls = ({
    task,
    errors,
    touched,
    handleUpdate,
    colWidth,
    showAssignments = false
}) => {

  const width = colWidth || 12;

  return (
      <>
        <Row>

          <Col md={width}>
            <FormGroup>
              <Form.Label>Label *</Form.Label>
              <Form.Control
                  type="text"
                  name={"label"}
                  className={(errors.label && touched.label) ? "is-invalid" : ""}
                  isInvalid={touched.label && errors.label}
                  value={task.label}
                  onChange={e => handleUpdate("label", e.target.value)}
              />
              <Form.Control.Feedback type="invalid">
                {errors.label}
              </Form.Control.Feedback>
            </FormGroup>
          </Col>

          <Col md={width} hidden={!showAssignments}>
            <FormGroup>
              <Form.Label>Due Date</Form.Label>
              <DatePicker
                  maxlength="2"
                  className={"form-control " + (!!errors.dueDate ? " is-invalid" : '')}
                  invalid={!!errors.dueDate}
                  wrapperClassName="form-control"
                  selected={task.dueDate}
                  name="dueDate"
                  onChange={(date) => handleUpdate("dueDate", !!date ? date.getTime() : null)}
                  isClearable={true}
                  dateFormat=" MM / dd / yyyy"
                  placeholderText="MM / DD / YYYY"
              />
            </FormGroup>
          </Col>

          <Col sm={width} hidden={!showAssignments}>
            <FormGroup>
              <Form.Label>Assigned To</Form.Label>
              <AsyncSelect
                  placeholder="Search-for and select a team member..."
                  className={"react-select-container"}
                  classNamePrefix="react-select"
                  loadOptions={userAutocomplete}
                  value={task.assignedTo ? {label: task.assignedTo.displayName, value: task.assignedTo.id, obj: task.assignedTo} : null}
                  onChange={(selected) => {
                    handleUpdate("assignedTo", selected ? selected.obj : null);
                  }}
                  defaultOptions={true}
                  isClearable={true}
              />
            </FormGroup>
          </Col>

        </Row>

        <Row>
          <Col xs={12}>
            <CustomFieldDefinitionDraggableCardList
                fields={task.fields}
                handleUpdate={(fields) => handleUpdate("fields", fields)}
            />
          </Col>
        </Row>

      </>
  )

}

TaskControls.propTypes = {
  task: PropTypes.object.isRequired,
  errors: PropTypes.object,
  touched: PropTypes.object,
  handleUpdate: PropTypes.func.isRequired,
  colWidth: PropTypes.number,
    showAssignments: PropTypes.bool
};

export default TaskControls;