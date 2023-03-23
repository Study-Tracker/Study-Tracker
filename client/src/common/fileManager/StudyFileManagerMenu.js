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

import {Card, ListGroup} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";
import {getDataSourceIcon} from "./fileManagerUtils";

const StudyFileManagerMenu = ({
  folders,
  handleFolderSelect,
  selectedFolder
}) => {

  console.debug("Study folders", folders);

  const renderMenu = () => {
    return folders
    .sort((a, b) => {
      if (a.name > b.name) return -1;
      else if (b.name > a.name) return 1;
      else return 0;
    })
    .map(f => {
      console.debug("Rendering menu folder: ", f);
      return (
          <ListGroup.Item
              action
              key={f.id}
              active={selectedFolder ? selectedFolder.id === f.id : false}
              onClick={() => handleFolderSelect(f)}
          >
            {getDataSourceIcon(f.storageDrive.driveType)}
            <span className="fw-bolder me-2">{f.storageDrive.driveType}:</span>
            {f.name}
            {f.primary && <span className="badge bg-info ms-2">default</span>}
          </ListGroup.Item>
      )
    });
  }

  return (
      <Card>

        <Card.Header>
          <Card.Title tag="h5" className="mb-0 text-muted">
            Storage Folders
          </Card.Title>
        </Card.Header>

        <ListGroup variant="flush">
          { renderMenu() }
        </ListGroup>

      </Card>
  );

}

StudyFileManagerMenu.propTypes = {
  folders: PropTypes.array.isRequired,
  handleFolderSelect: PropTypes.func.isRequired,
  selectedFolder: PropTypes.object
}

export default StudyFileManagerMenu;