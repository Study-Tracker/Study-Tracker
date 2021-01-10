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

import {
  Button,
  CardTitle,
  Col,
  FormGroup,
  Label,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Row,
  UncontrolledAlert
} from "reactstrap";
import React from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimesCircle} from "@fortawesome/free-solid-svg-icons";
import swal from 'sweetalert';
import {relationshipTypes} from "../config/studyRelationshipConstants";
import Select from "react-select";
import AsyncSelect from "react-select/async/dist/react-select.esm";
import {PlusCircle} from "react-feather";

class StudyRelationships extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      relationships: props.relationships,
      modalIsOpen: false,
      newRelationship: {
        studyId: '',
        type: ''
      },
      modalError: null
    };
    this.toggleModal = this.toggleModal.bind(this);
    this.handleNewRelationshipChange = this.handleNewRelationshipChange.bind(
        this);
    this.handleNewRelationshipSubmit = this.handleNewRelationshipSubmit.bind(
        this);
    this.handleRelationshipDelete = this.handleRelationshipDelete.bind(this);
    this.studyAutocomplete = this.studyAutocomplete.bind(this);
  }

  studyAutocomplete(input, callback) {
    if (input.length < 1) {
      return;
    }
    fetch("/api/autocomplete/study?q=" + input)
    .then(response => response.json())
    .then(json => {
      const options = json
      .filter(s => s.code !== this.props.studyCode && s.active)
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

  toggleModal() {
    this.setState({
      modalIsOpen: !this.state.modalIsOpen
    })
  }

  handleNewRelationshipChange(props) {
    this.setState({
      newRelationship: {
        ...this.state.newRelationship,
        ...props
      }
    })
  }

  handleNewRelationshipSubmit() {
    let r = this.state.newRelationship;
    if (!r.type || !r.studyId) {
      this.setState({
        modalError: "One or more required fields are missing. Please check your inputs and then try again."
      });
      return;
    }
    fetch("/api/study/" + this.props.studyCode + "/relationships", {
      method: 'POST',
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(r)
    }).then(response => {
      this.setState({
        relationships: [...this.state.relationships, r],
        newRelationship: {
          type: '',
          studyId: ''
        },
        modalError: null
      });
      this.toggleModal();
    })
    .catch(error => {
      console.error(error);
      this.setState({
        modalError: "Failed to create relationship. Please check your inputs and try again."
      });
    })

  }

  handleRelationshipDelete(relationship) {
    swal({
      title: "Are you sure you want to delete this relationship?",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        fetch("/api/study/" + this.props.studyCode + "/relationships", {
          method: 'DELETE',
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(relationship)
        }).then(response => {
          this.setState({
            relationships: this.state.relationships.filter(
                r => r.type !== relationship.type && r.studyId
                    !== relationship.studyId)
          });
        })
        .catch(error => {
          console.error(error);
          this.setState({
            modalError: "Failed to delete relationship. Please check your inputs and try again."
          });
        })
      }
    });
  }

  render() {

    const relationships = this.state.relationships.map(relationship => {
      const type = relationshipTypes[relationship.type];
      return (
          <li key={"study-relationship-" + relationship.studyId}>
            {type.label}
            &nbsp;&nbsp;
            <a href={"/study/"
            + relationship.studyId}>{relationship.studyId}</a>
            &nbsp;&nbsp;&nbsp;&nbsp;
            {
              !!this.props.user ? (
                  <a onClick={() => this.handleRelationshipDelete(
                      relationship)}>
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

          <CardTitle>
            Study Relationships
            {
              !!this.props.user ? (
                  <span className="float-right">
                  <Button size={"sm"} color={"primary"}
                          onClick={this.toggleModal}>
                    Add <PlusCircle className="feather feather-button-sm"/>
                  </Button>
                </span>
              ) : ''
            }
          </CardTitle>

          {
            relationships.length
                ? (
                    <ul>
                      {relationships}
                    </ul>
                )
                : (
                    <p className="text-muted text-center">
                      No linked studies.
                    </p>
                )
          }

          <Modal
              isOpen={this.state.modalIsOpen}
              toggle={() => this.toggleModal()}
              size={"md"}
          >

            <ModalHeader toggle={() => this.toggleModal()}>
              Add New Study Relationship
            </ModalHeader>

            <ModalBody className="m-3">

              <Row form>

                <Col sm={12}>
                  <p>
                    Please select a study you would like to link and an
                    appropriate relationship type.
                  </p>
                </Col>

                <Col xs={12} sm={4}>
                  <FormGroup>
                    <Label>Relationship *</Label>
                    <Select
                        className="react-select-container"
                        classNamePrefix="react-select"
                        options={relationshipOptions}
                        onChange={(selected) => this.handleNewRelationshipChange(
                            {
                              type: selected.value
                            })}
                    />
                  </FormGroup>
                </Col>

                <Col xs={12} sm={8}>
                  <FormGroup>
                    <Label>Study *</Label>
                    <AsyncSelect
                        placeholder="Search for studies..."
                        className={"react-select-container"}
                        classNamePrefix="react-select"
                        loadOptions={this.studyAutocomplete}
                        onChange={(selected) => this.handleNewRelationshipChange(
                            {
                              studyId: selected.obj.code
                            })}
                    />
                  </FormGroup>
                </Col>

              </Row>
              {
                !!this.state.modalError
                    ? (
                        <Row>
                          <Col sm={12}>
                            <UncontrolledAlert color={"warning"}>
                              <div className="alert-message">
                                {this.state.modalError}
                              </div>
                            </UncontrolledAlert>
                          </Col>
                        </Row>
                    ) : ''
              }

            </ModalBody>

            <ModalFooter>
              <Button color={"secondary"} onClick={() => this.toggleModal()}>
                Cancel
              </Button>
              <Button
                  color={"primary"}
                  onClick={this.handleNewRelationshipSubmit}
              >
                Save
              </Button>
            </ModalFooter>

          </Modal>

        </div>
    );
  }

}

export default StudyRelationships;