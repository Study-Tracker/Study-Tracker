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
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFolder, faServer} from "@fortawesome/free-solid-svg-icons";
import {faAws} from "@fortawesome/free-brands-svg-icons";

const FileManagerMenu = ({
  locations,
  handleLocationSelect,
  selectedLocation
}) => {

  const getDataSourceIcon = (type) => {
    switch (type) {
      case "EGNYTE_API":
        return <FontAwesomeIcon icon={faServer} className={"me-2"} />
      case "AWS_S3":
        return <FontAwesomeIcon icon={faAws} className={"me-2"} />
      default:
        return <FontAwesomeIcon icon={faFolder} className={"me-2"} />
    }
  }

  const renderDataSourceMenu = () => {
    return locations
    .filter(location => location.active)
    .map(ds => {
      return (
          <ListGroup.Item
              action
              key={ds.id}
              active={selectedLocation ? selectedLocation.id === ds.id : false}
              onClick={() => handleLocationSelect(ds)}
          >
            {getDataSourceIcon(ds.type)}
            {ds.displayName}
          </ListGroup.Item>
      )
    });
  }

  return (
      <Card>

        <Card.Header>
          <Card.Title tag="h5" className="mb-0 text-muted">
            Data Sources
          </Card.Title>
        </Card.Header>

        <ListGroup variant="flush">
          { renderDataSourceMenu() }
        </ListGroup>

      </Card>
  );

}

export default FileManagerMenu;