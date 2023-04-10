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
import {faFolder,} from "@fortawesome/free-regular-svg-icons";
import {FolderPermissionsBadges, FolderRootBadges} from "./folderBadges";
import React from "react";
import PropTypes from "prop-types";
import {faEdit, faGears, faTrash} from "@fortawesome/free-solid-svg-icons";

const StorageFolderCard = ({
    folder,
    handleFolderEdit,
    handleFolderDelete
}) => {

  return (
      <Card>
        <Card.Body>
          <Row>

            <Col xs={1} className={"d-flex align-items-center"}>
              <FontAwesomeIcon icon={faFolder} size={"2x"} className={"text-secondary"}/>
            </Col>

            <Col xs={5} className={"d-flex align-items-start"}>
              <div>
                <span className={"fw-bolder text-lg"}>
                  {folder.name}
                </span>
                <br/>
                <span className={"text-muted"}>
                  <code>{folder.path}</code>
                </span>
                <br />
                <span className={"text-muted"}>
                  <strong>{folder.storageDrive.driveType}</strong>: {folder.storageDrive.displayName}
                </span>
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-start"}>
              <div>
                <span className="text-muted">Permissions</span>
                <br />
                <FolderPermissionsBadges folder={folder} />
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-start"}>
              <div>
                <span className="text-muted">Attributes</span>
                <br />
                <FolderRootBadges folder={folder} />
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <Dropdown>
                <Dropdown.Toggle variant="outline-primary">
                  <FontAwesomeIcon icon={faGears} className={"me-1"}/>
                </Dropdown.Toggle>
                <Dropdown.Menu>

                  <Dropdown.Item onClick={() => handleFolderEdit(folder)}>
                    <FontAwesomeIcon icon={faEdit} className={"me-2"}/>
                    Edit
                  </Dropdown.Item>

                  <Dropdown.Item onClick={() => handleFolderDelete(folder)}>
                    <FontAwesomeIcon icon={faTrash} className={"me-2"}/>
                    Remove
                  </Dropdown.Item>

                </Dropdown.Menu>
              </Dropdown>
            </Col>

          </Row>
        </Card.Body>
      </Card>
  )

}

StorageFolderCard.propTypes = {
  folder: PropTypes.object.isRequired,
  handleFolderEdit: PropTypes.func.isRequired,
  handleFolderDelete: PropTypes.func.isRequired
}

export default StorageFolderCard;