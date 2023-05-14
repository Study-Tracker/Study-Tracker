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
import React from "react";
import PropTypes from "prop-types";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle} from "@fortawesome/free-regular-svg-icons";
import {DriveStatusBadge} from "../../../common/fileManager/folderBadges";
import {
  faCancel,
  faEdit,
  faFolderTree
} from "@fortawesome/free-solid-svg-icons";

const GitLabProjectGroupCard = ({group, handleEdit, handleStatusChange}) => {
  return (
      <Card>
        <Card.Body>
          <Row>

            <Col xs={1} className={"d-flex align-items-center"}>
              <FontAwesomeIcon icon={faFolderTree} size={"2x"} className={"text-secondary"}/>
            </Col>

            <Col xs={7} className={"d-flex align-items-center"}>
              <div>
                <span className={"fw-bolder text-lg"}>
                  <a href={group.gitGroup.webUrl} target={"_blank"} rel="noopener noreferrer">{group.gitGroup.displayName}</a>
                </span>
                <br />
                <span className={"text-muted"}>
                  GitLab Group ID: <code>{group.groupId}</code>
                </span>
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <div>
                <span className="text-muted">Status</span>
                <br />
                <DriveStatusBadge active={group.gitGroup.active} />
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <Dropdown>
                <Dropdown.Toggle variant="outline-primary">
                  Actions
                </Dropdown.Toggle>
                <Dropdown.Menu>

                  {
                      group.gitGroup.active && (
                          <Dropdown.Item onClick={() => handleStatusChange(group, false)}>
                            <FontAwesomeIcon icon={faCancel} className={"me-1"} />
                            Set Inactive
                          </Dropdown.Item>
                      )
                  }

                  {
                      !group.gitGroup.active && (
                          <Dropdown.Item onClick={() => handleStatusChange(group, true)}>
                            <FontAwesomeIcon icon={faCheckCircle} className={"me-1"} />
                            Set Active
                          </Dropdown.Item>
                      )
                  }

                  <Dropdown.Item onClick={() => handleEdit(group)}>
                    <FontAwesomeIcon icon={faEdit} className={"me-1"} />
                    Edit
                  </Dropdown.Item>

                </Dropdown.Menu>
              </Dropdown>
            </Col>

          </Row>
        </Card.Body>
      </Card>
  )
}

GitLabProjectGroupCard.propTypes = {
  group: PropTypes.object.isRequired
}

export default GitLabProjectGroupCard;