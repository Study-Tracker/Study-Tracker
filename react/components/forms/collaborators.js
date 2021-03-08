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

import React from "react";
import {
  Button,
  Col,
  CustomInput,
  FormGroup,
  FormText,
  Input,
  Label,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Row,
  UncontrolledAlert
} from "reactstrap";
import Select from "react-select";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";

export default class CollaboratorInputs extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      modalIsOpen: false,
      modalError: null,
      collaborators: [],
      selectedCollaborator: props.collaborator,
      isVisible: !!props.collaborator,
      newCollaborator: {
        label: '',
        organizationName: '',
        organizationLocation: '',
        contactPersonName: '',
        contactEmail: '',
        code: 'DB'
      },
      externalCode: props.externalCode
    };
    this.handleCollaboratorSelect = this.handleCollaboratorSelect.bind(this);
    this.handleExternalCodeChange = this.handleExternalCodeChange.bind(this);
    this.toggleModal = this.toggleModal.bind(this);
    this.handleNewCollaboratorChange = this.handleNewCollaboratorChange.bind(
        this);
    this.handleNewCollaboratorSubmit = this.handleNewCollaboratorSubmit.bind(
        this);
    this.handleShowInputs = this.handleShowInputs.bind(this);
  }

  componentDidMount() {
    fetch("/api/collaborator")
    .then(response => response.json())
    .then(json => {
      this.setState({
        collaborators: json.map(collaborator => {
          return {
            value: collaborator.id,
            ...collaborator
          };
        })
      });
    }).catch(e => {
      console.error("Failed to fetch external contact list.");
      console.error(e);
    })
  }

  toggleModal() {
    this.setState({
      modalIsOpen: !this.state.modalIsOpen
    })
  }

  handleShowInputs() {
    const visible = !this.state.isVisible;
    this.setState({
      isVisible: visible
    });
    if (!this.state.selectedCollaborator) {
      this.props.onChange({
        "collaborator": !!visible ? -1 : null
      });
    }
  }

  handleExternalCodeChange(e) {
    this.setState({
      externalCode: e.target.value
    });
    this.props.onChange({
      "externalCode": e.target.value
    });
  }

  handleCollaboratorSelect(selected) {
    this.setState({
      selectedCollaborator: selected
    });
    this.props.onChange({
      "collaborator": selected
    });
  }

  handleNewCollaboratorChange(props) {
    this.setState({
      newCollaborator: {
        ...this.state.newCollaborator,
        ...props
      }
    })
  }

  handleNewCollaboratorSubmit() {
    console.log(this.state.newCollaborator);
    let c = this.state.newCollaborator;
    if (!c.code || !c.organizationName || !c.label) {
      this.setState({
        modalError: "One or more required fields are missing. Please check your inputs and then try again."
      });
      return;
    }
    return fetch("/api/collaborator?label=" + c.label)
    .then(response => response.json())
    .then(json => {
      if (json.length > 0) {
        this.setState({
          modalError: "Organization labels must be unique."
        });
        return;
      }
      fetch("/api/collaborator", {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify(c)
      })
      .then(response => response.json())
      .then(col => {
        this.setState({
          collaborators: [...this.state.collaborators, col],
          newCollaborator: {
            label: '',
            organizationName: '',
            organizationLocation: '',
            contactPersonName: '',
            contactEmail: '',
            code: 'DB'
          },
          modalError: null
        });
        this.toggleModal();
      }).catch(e => {
        throw e;
      })
    }).catch(e => {
      console.error(e);
      this.setState({
        modalError: e.message
      });
    });
  }

  render() {

    return (
        <Row form>

          <Col sm="12">
            <h5 className="card-title">
              CRO/External Collaborator
            </h5>
            <h6 className="card-subtitle text-muted">Studies being performed
              externally should be assigned a collaborator and external study
              code. Select from a list of registered collaborators or add a new
              one. If no external study code is provided, one will be
              automatically generated using the 'organization code' as a prefix.
            </h6>
            <br/>
          </Col>

          <Col sm="12">
            <FormGroup>
              <CustomInput
                  id="cro-check"
                  type="checkbox"
                  label="Is this study being performed externally?"
                  onChange={() => this.handleShowInputs()}
                  defaultChecked={this.state.isVisible}
              />
            </FormGroup>
          </Col>

          <Col sm="12" id="cro-input-container"
               style={{
                 display: this.state.isVisible ? "block" : "none",
                 zIndex: 1000
               }}
               className={this.state.isVisible ? "animated fadein" : ""}
          >

            <Row form>

              <Col sm={6}>
                <FormGroup>
                  <Label>External Study Code</Label>
                  <Input
                      type="text"
                      defaultValue={this.state.externalCode || ''}
                      onChange={this.handleExternalCodeChange}
                      placeholder={"eg. DB-01234"}
                  />
                  <FormText>If the CRO provided their own study code, enter it
                    here.</FormText>
                </FormGroup>
              </Col>

            </Row>

            <Row form>

              <Col sm={6}>

                <FormGroup>
                  <Label>Registered Organizations</Label>
                  <Select
                      className="react-select-container"
                      classNamePrefix="react-select"
                      options={this.state.collaborators}
                      onChange={this.handleCollaboratorSelect}
                      defaultValue={this.state.selectedCollaborator}
                  />
                </FormGroup>

              </Col>

              <Col sm={6}>

                <div style={{marginTop: "2em"}}>
                  <Button color={"primary"} onClick={this.toggleModal}>
                    <FontAwesomeIcon icon={faPlusCircle}/> Add New Organization
                  </Button>
                </div>

              </Col>

            </Row>

          </Col>

          <Modal
              isOpen={this.state.modalIsOpen}
              toggle={() => this.toggleModal()}
              size={"lg"}
          >

            <ModalHeader toggle={() => this.toggleModal()}>
              Add New Organization
            </ModalHeader>

            <ModalBody className="m-3">

              <Row form>

                <Col sm={12}>
                  <p>
                    Please provide a unique label to identify the organization
                    in the study form. The 'organization code' should be a short
                    alphanumeric prefix, used for generating external study
                    codes.
                    For example, Wuxi could have a code of 'WX', which would
                    produce studies with codes like 'WX-01234'. Multiple
                    organizations can share the same organization code.
                  </p>
                </Col>

                <Col sm="6">
                  <FormGroup>
                    <Label>Label *</Label>
                    <Input
                        type="text"
                        defaultValue={this.state.newCollaborator.label}
                        onChange={(e) => this.handleNewCollaboratorChange({
                          label: e.target.value
                        })}
                    />
                  </FormGroup>
                </Col>

                <Col sm="6">
                  <FormGroup>
                    <Label>Organization Code *</Label>
                    <Input
                        type="text"
                        defaultValue={this.state.newCollaborator.code}
                        onChange={(e) => this.handleNewCollaboratorChange({
                          code: e.target.value
                        })}
                    />
                  </FormGroup>
                </Col>

                <Col sm="6">
                  <FormGroup>
                    <Label>Organization Name *</Label>
                    <Input
                        type="text"
                        defaultValue={this.state.newCollaborator.organizationName}
                        onChange={(e) => this.handleNewCollaboratorChange({
                          organizationName: e.target.value
                        })}
                    />
                  </FormGroup>
                </Col>

                <Col sm="6">
                  <FormGroup>
                    <Label>Organization Location</Label>
                    <Input
                        type="text"
                        defaultValue={this.state.newCollaborator.organizationLocation}
                        onChange={(e) => this.handleNewCollaboratorChange({
                          organizationLocation: e.target.value
                        })}
                    />
                  </FormGroup>
                </Col>

                <Col sm="6">
                  <FormGroup>
                    <Label>Contact Person</Label>
                    <Input
                        type="text"
                        defaultValue={this.state.newCollaborator.contactPersonName}
                        onChange={(e) => this.handleNewCollaboratorChange({
                          contactPersonName: e.target.value
                        })}
                    />
                  </FormGroup>
                </Col>

                <Col sm="6">
                  <FormGroup>
                    <Label>Contact Email</Label>
                    <Input
                        type="text"
                        defaultValue={this.state.newCollaborator.contactEmail}
                        onChange={(e) => this.handleNewCollaboratorChange({
                          contactEmail: e.target.value
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
              <Button color={"primary"}
                      onClick={this.handleNewCollaboratorSubmit}>
                Save
              </Button>
            </ModalFooter>

          </Modal>

        </Row>
    )
  }

}