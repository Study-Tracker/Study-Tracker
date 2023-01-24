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
import {Col, Form, Row} from "react-bootstrap";
import AsyncSelect from "react-select/async";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimesCircle} from "@fortawesome/free-regular-svg-icons";
import {FormGroup} from "../../common/forms/common";
import axios from "axios";
import PropTypes from "prop-types";

const StudyInputs = props => {

  const {studies, onChange} = props;

  const studyAutocomplete = (input, callback) => {
    axios.get("/api/internal/autocomplete/study?q=" + input)
    .then(response => {
      const options = response.data
      .sort((a, b) => {
        const aLabel = a.code + ": " + a.name;
        const bLabel = b.code + ": " + b.name;
        if (aLabel < bLabel) {
          return -1;
        }
        if (aLabel > bLabel) {
          return 1;
        }
        return 0;
      })
      .map(study => {
        return {
          label: study.code + ": " + study.name,
          value: study.id,
          obj: study
        }
      });
      callback(options);
    }).catch(e => {
      console.error(e);
    })
  }

  const handleStudySelect = (selected) => {
    const study = selected.obj;
    const existing = studies.map(s => s.id);
    if (existing.indexOf(study.id) === -1) {
      onChange([
        ...studies,
        study
      ])
    }
  }

  const handleRemoveStudy = (e) => {
    const selected = parseInt(e.currentTarget.dataset.id, 10);
    console.debug("Selected study", selected);
    onChange(props.studies.filter(study => study.id !== selected));
  }

  // Study list
  const selectedStudies = studies.map(study => {
    return (
        <Row key={"study-" + study.id}>
          <Col xs={10}>
            <Form.Label>{study.code + ": " + study.name}</Form.Label>
          </Col>
          <Col xs={2}>
            <a onClick={handleRemoveStudy} data-id={study.id}>
              <FontAwesomeIcon
                  icon={faTimesCircle}
                  className="align-middle me-2 text-danger"
              />
            </a>
          </Col>
        </Row>
    );
  });

  return (
      <Row>
        <Col lg={6}>
          <FormGroup>
            <Form.Label>Studies</Form.Label>
            <AsyncSelect
                placeholder="Search-for and select studies to add to your collection..."
                className={"react-select-container"}
                classNamePrefix="react-select"
                loadOptions={studyAutocomplete}
                onChange={handleStudySelect}
                controlShouldRenderValue={false}
                defaultOptions={true}
            />
          </FormGroup>
        </Col>
        <Col lg={6}>
          <Row>
            <Col xs={10}>
              <Form.Label>Study</Form.Label>
            </Col>
            <Col xs={2}>
              <Form.Label>Remove</Form.Label>
            </Col>
          </Row>
          {selectedStudies}
        </Col>
      </Row>
  );

}

StudyInputs.propTypes = {
  studies: PropTypes.array.isRequired,
  onChange: PropTypes.func.isRequired
}

export default StudyInputs;