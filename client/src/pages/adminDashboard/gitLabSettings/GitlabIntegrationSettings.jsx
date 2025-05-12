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

import React, {useContext, useEffect, useRef, useState} from "react";
import {Button, Card, Col, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-regular-svg-icons";
import NotyfContext from "../../../context/NotyfContext";
import GitLabSettingsDetailsCard from "./GitLabSettingsDetailsCard";
import GitLabIntegrationPlaceholder from "./GitLabIntegrationPlaceholder";
import GitLabIntegrationFormModal from "./GitLabIntegrationFormModal";
import axios from "axios";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import GitLabProjectGroupCard from "./GitLabProjectGroupCard";
import GitLabGroupFormModal from "./GitLabGroupFormModal";

const GitlabIntegrationSettings = () => {

  const [settings, setSettings] = useState(null);
  const [loadCount, setLoadCount] = useState(0);
  const [integrationModalIsOpen, setIntegrationModalIsOpen] = useState(false);
  const [groupModalIsOpen, setGroupModalIsOpen] = useState(false);
  const [groups, setGroups] = useState([]);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const integrationFormikRef = useRef();
  const groupFormikRef = useRef();
  const notyf = useContext(NotyfContext);

  useEffect(() => {
    axios.get("/api/internal/integrations/gitlab")
    .then(response => {
      if (response.data.length > 0) {
        const integration = response.data[0];
        axios.get("/api/internal/integrations/gitlab/" + integration.id + "/groups/registered?root=true")
        .then(response2 => {
          setSettings(integration);
          setGroups(response2.data);
        })
      }
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Error loading GitLab integration settings"
      })
    })
  }, [loadCount, notyf]);

  const handleIntegrationFormSubmit = (values, {setSubmitting, resetForm}) => {
    console.debug("GitLab integration form submitted with values: ", values);
    if (values.useToken) {
      values.username = null;
      values.password = null;
    } else {
      values.token = null;
    }
    const method = values.id ? 'PUT' : 'POST';
    axios({
      url: "/api/internal/integrations/gitlab" + (values.id ? "/" + values.id : ''),
      method: method,
      data: values,
      headers: {"Content-Type": "application/json"}
    })
    .then(() => {
      setLoadCount(loadCount + 1);
      setIntegrationModalIsOpen(false);
      resetForm();
      notyf.open({
        type: "success",
        message: "GitLab integration settings saved successfully."
      })
    })
    .catch(error => {
      console.error("Error saving GitLab integration settings: ", error);
      notyf.open({
        type: "error",
        message: "Error saving GitLab integration settings: " + error.message
      })
    })
    .finally(() => {
      setSubmitting(false);
    });
  }

  const handleGroupFormSubmit = (values, {setSubmitting, resetForm}) => {
    const method = values.id ? 'PUT' : 'POST';
    axios({
      url: "/api/internal/integrations/gitlab/" + settings.id + "/groups/registered" + (values.id ? "/" + values.id : ''),
      method: method,
      data: values,
      headers: {"Content-Type": "application/json"}
    })
    .then(() => {
      setLoadCount(loadCount + 1);
      setGroupModalIsOpen(false);
      resetForm();
      notyf.open({
        type: "success",
        message: "GitLab project group saved successfully."
      })
    })
    .catch(error => {
      console.error("Error saving GitLab project group: ", error);
      notyf.open({
        type: "error",
        message: "Error saving GitLab project group: " + error.message
      })
    })
    .finally(() => {
      setSubmitting(false);
    });
  }

  const handleGroupStatusChange = (group, enabled) => {
    axios({
      url: "/api/internal/integrations/gitlab/" + settings.id
          + "/groups/registered/" + group.id + "?status=" + enabled,
      method: "patch"
    })
    .then(response => {
      notyf.open({
        type: "success",
        message: "GitLab project group updated successfully."
      });
      setLoadCount(loadCount + 1);
    })
    .catch(error => {
      console.error("Error saving GitLab project group: ", error);
      notyf.open({
        type: "error",
        message: "Error updating GitLab project group"
      })
    })
  }

  return (
      <>

        <Row className={"mb-3"}>
          {
            settings && settings.active ? (
                <Col className={"d-flex justify-content-between"}>
                  <div>
                    <h3>
                      GitLab integration is
                      &nbsp;
                      <span className={"text-success"}>ENABLED</span>
                    </h3>
                  </div>
                  <div>
                    <Button
                        variant={"outline-warning"}
                        onClick={() => {
                          integrationFormikRef.current.setValues(settings);
                          setIntegrationModalIsOpen(true);
                        }}
                    >
                      <FontAwesomeIcon icon={faEdit} className={"me-2"} />
                      Edit Registration
                    </Button>
                  </div>
                </Col>
            ) : (
                <Col className={"d-flex justify-content-between"}>
                  <h3>
                    GitLab integration is
                    &nbsp;
                    <span className={"text-muted"}>DISABLED</span>
                  </h3>
                </Col>
            )
          }
        </Row>

        <Row>
          <Col>
            {
              settings
                ? <GitLabSettingsDetailsCard settings={settings} />
                : <GitLabIntegrationPlaceholder handleClick={() => setIntegrationModalIsOpen(true)} />
            }
          </Col>
        </Row>

        <Row>
          <Col>
            {
              settings && settings.active && (
                <>

                  <Row className={"mb-3"}>
                    <Col className={"d-flex justify-content-between"}>
                      <div>
                        <h4>Project Groups</h4>
                      </div>
                      <div>
                        <Button
                            variant={"primary"}
                            onClick={() => setGroupModalIsOpen(true)}
                        >
                          <FontAwesomeIcon icon={faPlus} className={"me-2"} />
                          Add Project Group
                        </Button>
                      </div>
                    </Col>
                  </Row>

                  {
                    groups && groups.length > 0 ? groups.map(group => {
                      return (
                          <Row>
                            <Col>
                              <GitLabProjectGroupCard
                                  group={group}
                                  handleEdit={(group) => {
                                    setSelectedGroup(group);
                                    setGroupModalIsOpen(true);
                                  }}
                                  handleStatusChange={handleGroupStatusChange}
                              />
                            </Col>
                          </Row>
                      )
                    }) : (
                        <Card className="illustration mt-2">
                          <Card.Body>
                            <Row>
                              <Col className={"text-center p-3"}>
                                <h5>No project groups have been registered.</h5>
                                <p>
                                  You must register at least one project group before Study Tracker
                                  can create Git repositories for you. Study Tracker will create subgroups
                                  within the registered project groups for each program's study you create
                                  a Git repository for.
                                </p>
                              </Col>
                            </Row>
                          </Card.Body>
                        </Card>
                    )
                  }

                </>
              )
            }
          </Col>
        </Row>

        <GitLabIntegrationFormModal
            isOpen={integrationModalIsOpen}
            setIsOpen={setIntegrationModalIsOpen}
            handleFormSubmit={handleIntegrationFormSubmit}
            formikRef={integrationFormikRef}
        />

        <GitLabGroupFormModal
            isOpen={groupModalIsOpen}
            setIsOpen={setGroupModalIsOpen}
            handleFormSubmit={handleGroupFormSubmit}
            integration={settings}
            formikRef={groupFormikRef}
            selectedGroup={selectedGroup}
        />

      </>
  )
}

export default GitlabIntegrationSettings;