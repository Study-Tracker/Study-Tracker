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
import {Button, Col, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-regular-svg-icons";
import NotyfContext from "../../../context/NotyfContext";
import GitLabSettingsDetailsCard from "./GitLabSettingsDetailsCard";
import GitLabIntegrationPlaceholder from "./GitLabIntegrationPlaceholder";
import GitLabIntegrationFormModal from "./GitLabIntegrationFormModal";
import axios from "axios";

const GitlabIntegrationSettings = () => {

  const [settings, setSettings] = useState(null);
  const [loadCount, setLoadCount] = useState(0);
  const [integrationModalIsOpen, setIntegrationModalIsOpen] = useState(false);
  const integrationFormikRef = useRef();
  const notyf = useContext(NotyfContext);

  useEffect(() => {
    axios.get("/api/internal/integrations/gitlab")
    .then(response => {
      if (response.data.length > 0) {
        setSettings(response.data[0]);
      }
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Error loading GitLab integration settings"
      })
    })
  }, [loadCount]);

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
      url: "/api/internal/integrations/gitlab/" + (values.id || ''),
      method: method,
      data: values,
      headers: {"Content-Type": "application/json"}
    })
    .then(response => {
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

        <GitLabIntegrationFormModal
            isOpen={integrationModalIsOpen}
            setIsOpen={setIntegrationModalIsOpen}
            handleFormSubmit={handleIntegrationFormSubmit}
            formikRef={integrationFormikRef}
        />

      </>
  )
}

export default GitlabIntegrationSettings;