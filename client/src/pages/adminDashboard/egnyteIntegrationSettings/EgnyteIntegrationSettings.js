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

import React, {useContext, useEffect, useState} from "react";
import {Col, Dropdown, Row} from "react-bootstrap";
import axios from "axios";
import NotyfContext from "../../../context/NotyfContext";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faCircleXmark, faEdit} from "@fortawesome/free-regular-svg-icons";
import {faGears, faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import EgnyteIntegrationDetailsCard from "./EgnyteIntegrationDetailsCard";
import EgnyteIntegrationSetupCard from "./EgnyteIntegrationSetupCard";
import EgnyteDriveCard from "./EgnyteDriveCard";
import EgnyteIntegrationFormModal from "./EgnyteIntegrationFormModal";

const EgnyteIntegrationSettings = () => {

  const [settings, setSettings] = useState(null);
  const [drives, setDrives] = useState([]);
  const [integrationModalIsOpen, setIntegrationModalIsOpen] = useState(false);
  const [loadCount, setLoadCount] = useState(0);
  const notyf = useContext(NotyfContext);
  const integrationFormikRef = React.createRef();

  useEffect(() => {
    axios.get("/api/internal/integrations/egnyte/")
    .then(response => {

      console.debug("Egnyte settings loaded", response.data);

      if (response.data.length === 0) {
        console.warn("No Egnyte settings found");
        setSettings(null);
      } else if (response.data.length === 1) {
        setSettings(response.data[0]);
      } else {
        console.warn("Multiple Egnyte settings found", response.data);
        setSettings(response.data[0]);
      }

      axios.get("/api/internal/drives/egnyte/")
      .then(response => {
        console.debug("Egnyte drives loaded", response.data);
        setDrives(response.data);
      })

    })
    .catch(error => {
      console.error("Failed to load Egnyte settings", error);
      notyf.open({
        type: "error",
        message: "Failed to load Egnyte settings"
      });
    });
  }, [loadCount, notyf]);

  const handleIntegrationFormSubmit = (values, {setSubmitting, resetForm}) => {
    console.debug("Saving Egnyte integration settings", values);
    const url = "/api/internal/integrations/egnyte/" + (values.id || '');
    const method = values.id ? 'PUT' : 'POST';
    axios({
      url: url,
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
        message: "Egnyte integration settings saved"
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to save Egnyte integration settings: " + error.response.data.message
      });
    })
    .finally(() => {
      setSubmitting(false);
    });
  }

  const handleStatusToggle = (active) => {
    axios.patch("/api/internal/integrations/egnyte/" + settings.id + "?active=" + active)
    .then(response => {
      setLoadCount(loadCount + 1);
      notyf.open({
        type: "success",
        message: "Egnyte integration settings saved"
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to save Egnyte integration settings: " + error.response.data.message
      });
    });
  }

  const handleDriveStatusUpdate = (id, active) => {
    axios.patch("/api/internal/drives/egnyte/" + id + "?active=" + active)
    .then(response => {
      setLoadCount(loadCount + 1);
      notyf.open({
        type: "success",
        message: "Egnyte drive status updated"
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to update Egnyte drive status: " + error.response.data.message
      });
    });
  }

  return (
      <>

        <Row className={"mb-3"}>
          <Col className={"d-flex justify-content-between"}>

            <div>
              {
                settings && settings.active ? (
                    <h3>
                      Egnyte integration is
                      &nbsp;
                      <span className={"text-success"}>ENABLED</span>
                    </h3>
                ) : (
                    <h3>
                      Egnyte integration is
                      &nbsp;
                      <span className={"text-muted"}>DISABLED</span>
                    </h3>
                )
              }
            </div>

            <div>
              <Dropdown>

                <Dropdown.Toggle variant={"primary"} id="dropdown-basic">
                  <FontAwesomeIcon icon={faGears} className={"me-2"} />
                  Settings
                  &nbsp;&nbsp;
                </Dropdown.Toggle>

                <Dropdown.Menu>

                  {
                    settings ? (
                        <Dropdown.Item onClick={() => {
                          integrationFormikRef.current.setValues(settings);
                          setIntegrationModalIsOpen(true);
                        }}>
                          <FontAwesomeIcon icon={faEdit} className={"me-2"} />
                          Edit Registration
                        </Dropdown.Item>
                    ) : (
                        <Dropdown.Item onClick={() => {
                          setIntegrationModalIsOpen(true);
                        }}>
                          <FontAwesomeIcon icon={faPlusCircle} className={"me-2"} />
                          Add Registration
                        </Dropdown.Item>
                    )
                  }

                  {
                    settings && settings.active ? (
                        <Dropdown.Item onClick={() => handleStatusToggle(false)}>
                          <FontAwesomeIcon icon={faCircleXmark} className={"me-2"} />
                          Disable Integration
                        </Dropdown.Item>
                    ) : (
                        <Dropdown.Item onClick={() => handleStatusToggle(true)}>
                          <FontAwesomeIcon icon={faCheckCircle} className={"me-2"} />
                          Re-enable Integration
                        </Dropdown.Item>
                    )
                  }

                </Dropdown.Menu>

              </Dropdown>
            </div>

          </Col>
        </Row>

        {
          settings
              ? <EgnyteIntegrationDetailsCard settings={settings} />
              : <EgnyteIntegrationSetupCard handleClick={() => setIntegrationModalIsOpen(true)} />
        }

        {
            settings && (
                <Row className={"mb-3"}>
                  <Col className={"d-flex justify-content-between"}>
                    <div>
                      <h4>Egnyte Drives</h4>
                    </div>
                  </Col>
                </Row>
            )
        }

        {
          drives.length > 0 && (
              drives.map(drive => (
                  <EgnyteDriveCard
                      drive={drive}
                      handleDriveStatusChange={handleDriveStatusUpdate}
                  />
              )
          ))
        }

        <EgnyteIntegrationFormModal
            isOpen={integrationModalIsOpen}
            setIsOpen={setIntegrationModalIsOpen}
            handleFormSubmit={handleIntegrationFormSubmit}
            formikRef={integrationFormikRef}
        />

      </>
  )
}

export default EgnyteIntegrationSettings;