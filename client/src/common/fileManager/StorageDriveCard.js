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

import {Card, Col, Dropdown, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faHardDrive} from "@fortawesome/free-regular-svg-icons";
import DriveStatusBadge from "./DriveStatusBadge";
import {faCancel} from "@fortawesome/free-solid-svg-icons";
import React from "react";

const StorageDriveCard = ({drive}) => {

  return (
      <Card>
        <Card.Body>
          <Row>

            <Col xs={1} className={"d-flex align-items-center"}>
              <FontAwesomeIcon icon={faHardDrive} size={"2x"} className={"text-secondary"}/>
            </Col>

            <Col xs={5} className={"d-flex align-items-center"}>
              <div>
                <span className={"fw-bolder text-lg"}>
                  {drive.displayName}
                </span>
                <br/>
                <span className={"text-muted"}>
                  {drive.rootPath}
                </span>
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <div>
                <span className="text-muted">Type</span>
                <br />
                {drive.driveType}
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <div>
                <span className="text-muted">Status</span>
                <br />
                <DriveStatusBadge active={drive.active} />
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
  )

}