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

import React from 'react';
import {Col, FormGroup, Label, Row} from "reactstrap";
import AsyncSelect from "react-select/async";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimesCircle} from "@fortawesome/free-regular-svg-icons";

export class StudyInputs extends React.Component {

  constructor(props) {
    super(props);
    this.handleStudySelect = this.handleStudySelect.bind(this);
    this.handleRemoveStudy = this.handleRemoveStudy.bind(this);
  }

  studyAutocomplete(input, callback) {
    if (input.length < 1) {
      return;
    }
    fetch("/api/autocomplete/study?q=" + input)
    .then(response => response.json())
    .then(json => {
      const options = json.map(study => {
        return {label: study.code + ": " + study.name, value: study.id, obj: study}
      });
      callback(options);
    }).catch(e => {
      console.error(e);
    })
  }

  handleStudySelect(selected) {
    const study = selected.obj;
    const existing = this.props.studies.map(s => s.id);
    if (existing.indexOf(study.id) === -1) {
      this.props.onChange({
        studies: [
          ...this.props.studies,
          study
        ]
      })
    }
  }

  handleRemoveStudy(e) {
    const selected = parseInt(e.currentTarget.dataset.id);
    console.log(selected);
    const studies = this.props.studies.filter(study => study.id !== selected);
    this.props.onChange({
      studies: studies
    });
  }

  render() {

    // Study list
    const selectedStudies = this.props.studies.map(study => {
      return (
          <Row key={"study-" + study.id}>
            <Col xs="10">
              <Label>{study.code + ": " + study.name}</Label>
            </Col>
            <Col xs="2">
              <a
                  onClick={this.handleRemoveStudy}
                  data-id={study.id}
              >
                <FontAwesomeIcon
                    icon={faTimesCircle}
                    className="align-middle mr-2 text-danger"
                />
              </a>
            </Col>
          </Row>
      );
    });

    return (
        <Row form>
          <Col lg="6">
            <FormGroup>
              <Label>Studies</Label>
              <AsyncSelect
                  placeholder="Search-for and select studies to add to your collection..."
                  className={"react-select-container"}
                  classNamePrefix="react-select"
                  loadOptions={this.studyAutocomplete}
                  onChange={this.handleStudySelect}
                  controlShouldRenderValue={false}
              />
            </FormGroup>
          </Col>
          <Col lg="6">
            <Row>
              <Col xs="10">
                <Label>Study</Label>
              </Col>
              <Col xs="2">
                <Label>Remove</Label>
              </Col>
            </Row>
            {selectedStudies}
          </Col>
        </Row>
    );
  }

}