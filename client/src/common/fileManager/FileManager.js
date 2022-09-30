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

import React, {useEffect, useState} from 'react';
import axios from 'axios';
import {
  Badge,
  Button,
  Card,
  Col,
  Container,
  Dropdown,
  ListGroup,
  Row
} from "react-bootstrap";
import FileManagerTable from "./FileManagerTable";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faDropbox,
  faGithub,
  faGoogleDrive
} from "@fortawesome/free-brands-svg-icons";
import {faFolder, faFolderPlus} from "@fortawesome/free-solid-svg-icons";
import {ArrowLeft, CornerLeftUp, FolderPlus, Upload} from "react-feather";
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import {useSearchParams} from "react-router-dom";

const FolderSizeBadge = ({folder}) => {
  let count = 0;
  if (folder) {
    count += folder.files.length;
    count += folder.subFolders.length;
  }
  return (
        <Badge bg="info" style={{fontSize: "100%"}}>
          {count} item{count === 0 ? '' : 's'}
        </Badge>
  );
}

const FolderPathBreadcrumbs = ({folder}) => {
  return <Badge bg="info" style={{fontSize: "100%"}}>{folder ? folder.path : ''}</Badge>
}

const FileManager = ({path}) => {

  const [folder, setFolder] = useState(null);
  const [currentPath, setCurrentPath] = useState(path);
  const [previousPath, setPreviousPath] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchParams, setSearchParams] = useSearchParams();

  const handlePathUpdate = (path) => {
    console.debug("handlePathUpdate -- new path: " + path + ", current path: " + currentPath);
    setPreviousPath(currentPath);
    setCurrentPath(path);
    searchParams.set("path", path);
    setSearchParams(searchParams);
  }

  useEffect(() => {
    setIsLoading(true);
    setError(null);
    axios.get("/api/internal/data-files", {params: {path: currentPath}})
    .then(response => {
      setFolder(response.data);
      setIsLoading(false);
    })
    .catch(error => {
      console.error(error);
      setError(error);
      setIsLoading(false);
    });
  }, [currentPath]);

  let content = <LoadingMessage />;
  if (!isLoading && error) {
    content = <ErrorMessage />;
  } else if (!isLoading && folder) {
    content = <FileManagerTable
        folder={folder}
        handlePathChange={handlePathUpdate} />;
  }

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center mb-2">
          <Col>
            <h3>File Manager</h3>
          </Col>
        </Row>

        <Row className="file-manager">

          <Col xs="4" md="3">

            <Card>

              <Card.Header>
                <Card.Title tag="h5" className="mb-0 text-muted">
                  Data Sources
                </Card.Title>
              </Card.Header>

              <ListGroup variant="flush">

                <ListGroup.Item action href={"#my-files"}>
                  <FontAwesomeIcon icon={faFolder} />
                  &nbsp;
                  My Files
                </ListGroup.Item>

                <ListGroup.Item action href={"#dropbox"}>
                  <FontAwesomeIcon icon={faDropbox} />
                  &nbsp;
                  Dropbox
                </ListGroup.Item>

                <ListGroup.Item action href={"#github"}>
                  <FontAwesomeIcon icon={faGithub} />
                  &nbsp;
                  GitHub
                </ListGroup.Item>

                <ListGroup.Item action href={"#google-drive"}>
                  <FontAwesomeIcon icon={faGoogleDrive} />
                  &nbsp;
                  Google Drive
                </ListGroup.Item>

              </ListGroup>

              <Card.Footer>
                <Dropdown className="me-1">
                  <Dropdown.Toggle variant="outline-primary">
                    <FontAwesomeIcon icon={faFolderPlus} />
                    &nbsp;&nbsp;
                    Add Content
                  </Dropdown.Toggle>
                  <Dropdown.Menu>
                    <Dropdown.Item onClick={() => console.log("Click!")}>
                      Option 1
                    </Dropdown.Item>
                    <Dropdown.Item onClick={() => console.log("Click!")}>
                      Option 2
                    </Dropdown.Item>
                    <Dropdown.Item onClick={() => console.log("Click!")}>
                      Option 3
                    </Dropdown.Item>
                  </Dropdown.Menu>
                </Dropdown>
              </Card.Footer>

            </Card>

          </Col>

          <Col xs="8" md="9">
            <Card>

              <Card.Header>

                <div className="d-flex justify-content-between">

                  <div className="card-toolbar">

                    <Button
                        variant="outline-primary"
                        className="me-2"
                        style={{width: "90px"}}
                        disabled={!previousPath}
                        onClick={() => handlePathUpdate(previousPath)}
                    >
                      <ArrowLeft size={18} />
                      &nbsp;&nbsp;
                      Back
                    </Button>

                    <Button
                        variant="outline-primary"
                        className="me-2"
                        style={{width: "90px"}}
                        disabled={!folder || !folder.parentFolder}
                        onClick={() => handlePathUpdate(folder.parentFolder.path)}
                    >
                      <CornerLeftUp size={18} />
                      &nbsp;&nbsp;
                      Up
                    </Button>

                  </div>

                  <div className="card-toolbar">

                    <Button variant="outline-primary" className="me-2">
                      <FolderPlus size={18} />
                      &nbsp;&nbsp;
                      New Folder
                    </Button>

                    <Button variant="primary">
                      <Upload size={18} />
                      &nbsp;&nbsp;
                      Upload
                    </Button>

                  </div>

                </div>

              </Card.Header>

              <Card.Body>

                <Row>

                  <Col xs={12}>
                    <div className="d-flex justify-content-between align-items-center">
                      <div>
                        <FolderPathBreadcrumbs folder={folder} />
                      </div>
                      <div>
                        <FolderSizeBadge folder={folder} />
                      </div>
                    </div>
                  </Col>

                  <Col xs={12}>
                    {content}
                  </Col>

                </Row>

              </Card.Body>

            </Card>
          </Col>

        </Row>

      </Container>
  )

}

export default FileManager;