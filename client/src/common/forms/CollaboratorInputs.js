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

import React, {useEffect, useState} from "react";
import {Alert, Button, Col, Form, Modal, Row} from "react-bootstrap";
import {FormGroup} from "./common";
import Select from "react-select";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import PropTypes from "prop-types";

const CollaboratorInputs = ({
  isExternalStudy,
  collaborator,
  externalCode,
  onChange
}) => {

  const [collaborators, setCollaborators] = useState([]);
  const [modalState, setModalState] = useState({isOpen:false});
  // const [isVisible, setIsVisible] = useState(isExternalStudy);
  const [code, setCode] = useState(externalCode);
  const [selectedCollaborator, setSelectedCollaborator] = useState(collaborator);
  const newCollaboratorDefaults = {
    label: '',
    organizationName: '',
    organizationLocation: '',
    contactPersonName: '',
    contactEmail: '',
    code: 'EXTERNAL'
  };
  const [newCollaborator, setNewCollaborator] = useState(newCollaboratorDefaults);

  useEffect(() => {
    axios.get("/api/internal/collaborator")
    .then(response => {
      setCollaborators(response.data.map(collaborator => {
        return {
          value: collaborator.id,
          ...collaborator
        };
      }));
    }).catch(e => {
      console.error("Failed to fetch external contact list.");
      console.error(e);
    })
  }, []);

  const showModal = (bool) => {
    setModalState({isOpen: bool});
  }

  const handleShowInputs = () => {
    const visible = !isExternalStudy;
    // setIsVisible(visible);
    if (!selectedCollaborator) {
      onChange("collaborator", !!visible ? -1 : null);
    }
    onChange("external", visible);
  }

  const handleExternalCodeChange = (e) => {
    setCode(e.target.value);
    onChange("externalCode", e.target.value);
  }

  const handleCollaboratorSelect = (selected) => {
    setSelectedCollaborator(selected);
    onChange("collaborator", selected);
  }

  const handleNewCollaboratorChange = (props) => {
    setNewCollaborator(prevState => ({
      ...prevState,
      ...props
    }))
  }

  const handleNewCollaboratorSubmit = () => {

    console.debug(newCollaborator);

    // Validate the new collaborator
    let c = newCollaborator;
    if (!c.code || !c.organizationName || !c.label) {
      setModalState(prevState => ({
        ...prevState,
        error: "One or more required fields are missing. Please check your inputs and then try again."
      }));
      return;
    }

    return axios.get("/api/internal/collaborator?label=" + c.label)
    .then(response => {
      if (response.data.length > 0) {
        setModalState(prevState => ({
          ...prevState,
          error: "Organization labels must be unique."
        }));
        return;
      }
      axios({
        url: "/api/internal/collaborator",
        method: "post",
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          // "X-XSRF-TOKEN": getCsrfToken()
        },
        data: c
      })
      .then(response => {
        setCollaborators(prevState => ([...prevState, response.data]))
        setNewCollaborator(newCollaboratorDefaults);
        setModalState(prevState => ({...prevState, error: null }));
        showModal();
      }).catch(e => {
        throw e;
      })
    }).catch(e => {
      console.error(e);
      setModalState(prevState => ({
        ...prevState,
        error: e.message
      }));
    });
  }

  return (
      <Row>

        <Col sm={12}>
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

        <Col sm={12}>
          <FormGroup>
            <Form.Check
                id="cro-check"
                type="checkbox"
                label="Is this study being performed externally?"
                onChange={() => handleShowInputs()}
                defaultChecked={isExternalStudy}
            />
          </FormGroup>
        </Col>

        <Col sm={12} id="cro-input-container"
             style={{
               display: isExternalStudy ? "block" : "none",
               zIndex: 1000
             }}
             className={isExternalStudy ? "animated fadein" : ""}
        >

          <Row>

            <Col sm={6}>
              <FormGroup>
                <Form.Label>External Study Code</Form.Label>
                <Form.Control
                    type="text"
                    defaultValue={code}
                    onChange={handleExternalCodeChange}
                    placeholder={"eg. EX-01234"}
                />
                <Form.Text>If the CRO provided their own study code, enter it
                  here.</Form.Text>
              </FormGroup>
            </Col>

          </Row>

          <Row>

            <Col sm={6}>

              <FormGroup>
                <Form.Label>Registered Organizations</Form.Label>
                <Select
                    className="react-select-container"
                    classNamePrefix="react-select"
                    options={collaborators}
                    onChange={handleCollaboratorSelect}
                    defaultValue={selectedCollaborator}
                />
              </FormGroup>

            </Col>

            <Col sm={6}>

              <div style={{marginTop: "2em"}}>
                <Button variant={"primary"}
                        onClick={() => showModal(true)}>
                  <FontAwesomeIcon icon={faPlusCircle}/> Add New Organization
                </Button>
              </div>

            </Col>

          </Row>

        </Col>

        <Modal
            show={modalState.isOpen}
            onHide={() => showModal(false)}
            size={"lg"}
        >

          <Modal.Header closeButton>
            Add New Organization
          </Modal.Header>

          <Modal.Body className="m-3">

            <Row>

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

              <Col sm={6}>
                <FormGroup>
                  <Form.Label>Label *</Form.Label>
                  <Form.Control
                      type="text"
                      defaultValue={newCollaborator.label}
                      onChange={(e) => handleNewCollaboratorChange({
                        label: e.target.value
                      })}
                  />
                </FormGroup>
              </Col>

              <Col sm={6}>
                <FormGroup>
                  <Form.Label>Organization Code *</Form.Label>
                  <Form.Control
                      type="text"
                      defaultValue={newCollaborator.code}
                      onChange={(e) => handleNewCollaboratorChange({
                        code: e.target.value
                      })}
                  />
                </FormGroup>
              </Col>

              <Col sm={6}>
                <FormGroup>
                  <Form.Label>Organization Name *</Form.Label>
                  <Form.Control
                      type="text"
                      defaultValue={newCollaborator.organizationName}
                      onChange={(e) => handleNewCollaboratorChange({
                        organizationName: e.target.value
                      })}
                  />
                </FormGroup>
              </Col>

              <Col sm={6}>
                <FormGroup>
                  <Form.Label>Organization Location</Form.Label>
                  <Form.Control
                      type="text"
                      defaultValue={newCollaborator.organizationLocation}
                      onChange={(e) => handleNewCollaboratorChange({
                        organizationLocation: e.target.value
                      })}
                  />
                </FormGroup>
              </Col>

              <Col sm={6}>
                <FormGroup>
                  <Form.Label>Contact Person</Form.Label>
                  <Form.Control
                      type="text"
                      defaultValue={newCollaborator.contactPersonName}
                      onChange={(e) => handleNewCollaboratorChange({
                        contactPersonName: e.target.value
                      })}
                  />
                </FormGroup>
              </Col>

              <Col sm={6}>
                <FormGroup>
                  <Form.Label>Contact Email</Form.Label>
                  <Form.Control
                      type="text"
                      defaultValue={newCollaborator.contactEmail}
                      onChange={(e) => handleNewCollaboratorChange({
                        contactEmail: e.target.value
                      })}
                  />
                </FormGroup>
              </Col>

            </Row>
            {
              !!modalState.error
                  ? (
                      <Row>
                        <Col sm={12}>
                          <Alert variant={"warning"}>
                            <div className="alert-message">
                              {modalState.error}
                            </div>
                          </Alert>
                        </Col>
                      </Row>
                  ) : ''
            }

          </Modal.Body>

          <Modal.Footer>
            <Button variant={"secondary"}
                    onClick={() => showModal(false)}>
              Cancel
            </Button>
            <Button variant={"primary"}
                    onClick={handleNewCollaboratorSubmit}>
              Save
            </Button>
          </Modal.Footer>

        </Modal>

      </Row>
  );

}

CollaboratorInputs.propTypes = {
  externalCode: PropTypes.string,
  collaborator: PropTypes.object,
  onChange: PropTypes.func,
}

export default CollaboratorInputs;