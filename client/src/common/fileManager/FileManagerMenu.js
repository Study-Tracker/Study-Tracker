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
import {DataSourceIcon} from "./fileManagerUtils";

const FileManagerMenu = ({
  rootFolders,
  handleFolderSelect,
  selectedRootFolder
}) => {

  return (
      <Card>

        <Card.Header>
          <Card.Title tag="h5" className="mb-0 text-muted">
            Data Sources
          </Card.Title>
        </Card.Header>

        <ListGroup variant="flush">
          {
            rootFolders
            .filter(rootFolder => rootFolder.storageDrive.active)
            .map(f => {
              return (
                <ListGroup.Item
                  action
                  key={f.id}
                  active={selectedRootFolder ? selectedRootFolder.id === f.id : false}
                  onClick={() => handleFolderSelect(f)}
                >
                  <div>
                    <DataSourceIcon driveType={f.storageDrive.driveType} />
                    <span>{f.storageDrive.driveType}</span>
                  </div>

                  <div>{f.name}</div>
                </ListGroup.Item>
              )
            })
          }
        </ListGroup>

      </Card>
  );

}

FileManagerMenu.propTypes = {
  rootFolders: PropTypes.array.isRequired,
  handleFolderSelect: PropTypes.func.isRequired,
  selectedRootFolder: PropTypes.object
}

export default FileManagerMenu;