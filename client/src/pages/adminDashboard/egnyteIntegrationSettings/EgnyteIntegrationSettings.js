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
import {Card, Col, Dropdown, Row} from "react-bootstrap";
import axios from "axios";
import NotyfContext from "../../../context/NotyfContext";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faHardDrive} from "@fortawesome/free-regular-svg-icons";
import DriveStatusBadge from "../../../common/fileManager/DriveStatusBadge";
import {faCancel} from "@fortawesome/free-solid-svg-icons";
import EgnyteIntegrationDetailsCard from "./EgnyteIntegrationSetupCard";
import EgnyteIntegrationSetupCard from "./EgnyteIntegrationSetupCard";

const EgnyteIntegrationSettings = () => {

  const [settings, setSettings] = useState(null);
  const [drives, setDrives] = useState([]);
  const [loadCount, setLoadCount] = useState(0);
  const notyf = useContext(NotyfContext);

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
  }, [loadCount]);

  return (
      <>

        {
          !!settings
              ? <EgnyteIntegrationDetailsCard settings={settings} />
              : <EgnyteIntegrationSetupCard />
        }

        {
          drives.length > 0 && (
              drives.map(drive => (
                  <Card className={"mt-3"}>
                    <Card.Header>
                      <Card.Title tag={"h5"} className={"mb-0"}>
                        Egnyte drives
                      </Card.Title>
                    </Card.Header>
                    <Card.Body>
                      <Row>

                        <Col xs={1} className={"d-flex align-items-center"}>
                          <FontAwesomeIcon icon={faHardDrive} size={"2x"} className={"text-secondary"}/>
                        </Col>

                        <Col xs={7} className={"d-flex align-items-center"}>
                          <div>
                            <span className={"fw-bolder text-lg"}>
                              {drive.storageDrive.displayName}
                            </span>
                            <br/>
                            <span className={"text-muted"}>
                              {drive.storageDrive.rootPath}
                            </span>
                          </div>
                        </Col>

                        <Col xs={2} className={"d-flex align-items-center"}>
                          <div>
                            <span className="text-muted">Status</span>
                            <br />
                            <DriveStatusBadge active={drive.storageDrive.active} />
                          </div>
                        </Col>

                        <Col xs={2} className={"d-flex align-items-center"}>
                          <Dropdown>
                            <Dropdown.Toggle variant="outline-primary">
                              Actions
                            </Dropdown.Toggle>
                            <Dropdown.Menu>

                              {
                                  drive.active && (
                                      <Dropdown.Item onClick={() => console.log("Click")}>
                                        <FontAwesomeIcon icon={faCancel} className={"me-1"} />
                                        Set Inactive
                                      </Dropdown.Item>
                                  )
                              }

                              {
                                  !drive.active && (
                                      <Dropdown.Item onClick={() => console.log("Click")}>
                                        <FontAwesomeIcon icon={faCheckCircle} className={"me-1"} />
                                        Set Active
                                      </Dropdown.Item>
                                  )
                              }

                            </Dropdown.Menu>
                          </Dropdown>
                        </Col>

                      </Row>
                    </Card.Body>
                  </Card>
              ))
          )
        }

      </>
  )
}

export default EgnyteIntegrationSettings;