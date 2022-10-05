/*
 * Copyright 2022 the original author or authors.
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
import {Cloud, Folder, Server} from "react-feather";

const FileManagerMenu = ({
  dataSources,
  handleDataSourceSelect
}) => {

  const getDataSourceIcon = (type) => {
    switch (type) {
      case "EGNYTE_API":
        return <Server size={18} className={"me-2"} />; // faServer;
      case "AWS_S3":
        return <Cloud size={18} className={"me-2"} />;  //faCloud;
      default:
        return <Folder size={18} className={"me-2"} />; //faFolder;
    }
  }

  const renderDataSourceMenu = () => {
    return dataSources.map(ds => {
      return (
          <ListGroup.Item
              action
              href={"#" + ds.id}
              onClick={() => handleDataSourceSelect(ds)}
          >
            {getDataSourceIcon(ds.type)}
            {/*<FontAwesomeIcon*/}
            {/*    className={"me-2"}*/}
            {/*    icon={getDataSourceIcon(ds.type)} />*/}
            {ds.label}
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