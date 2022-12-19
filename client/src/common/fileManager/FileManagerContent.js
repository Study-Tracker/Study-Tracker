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

import {Badge, Breadcrumb, Button, Card, Col, Row} from "react-bootstrap";
import {
  ArrowLeft,
  CornerLeftUp,
  FolderPlus,
  RotateCw,
  Upload
} from "react-feather";
import FileManagerUploadModal from "./FileManagerUploadModal";
import FileManagerNewFolderModal from "./FileManagerNewFolderModal";
import React, {useContext, useEffect, useState} from "react";
import axios from "axios";
import ErrorMessage from "../structure/ErrorMessage";
import FileManagerTable from "./FileManagerTable";
import {useSearchParams} from "react-router-dom";
import NotyfContext from "../../context/NotyfContext";
import {LoadingMessageCard} from "../loading";
import PropTypes from "prop-types";

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

const pathIsChild = (path, parent) => {
  console.debug("pathIsChild", path, parent);
  if (!path.startsWith("/")) path = "/" + path;
  if (!parent.startsWith("/")) parent = "/" + parent;
  if (!path.endsWith("/")) path = path + "/";
  if (!parent.endsWith("/")) parent = parent + "/";
  return path.indexOf(parent) > -1;
}

const FileManagerContent = ({location, path}) => {

  console.debug("Selected data source: ", location);
  console.debug("Selected path: ", path);

  const notyf = useContext(NotyfContext);

  const [rootPath, setRootPath] = useState(path || location.rootFolderPath);
  const [folder, setFolder] = useState(null);
  const [currentPath, setCurrentPath] = useState(path || location.rootFolderPath);
  const [history, setHistory] = useState([]);
  const [refreshCount, setRefreshCount] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchParams, setSearchParams] = useSearchParams();
  const [uploadModalIsOpen, setUploadModalIsOpen] = useState(false);
  const [uploadError, setUploadError] = useState(null);
  const [newFolderModalIsOpen, setNewFolderModalIsOpen] = useState(false);
  const [newFolderError, setNewFolderError] = useState(null);

  // Load the folder
  useEffect(() => {
    setIsLoading(true);
    setError(null);
    setRootPath(path || location.rootFolderPath);
    axios.get("/api/internal/data-files", {
      params: {
        path: currentPath,
        locationId: location.id
      }
    })
    .then(response => {
      setFolder(response.data);
    })
    .catch(error => {
      console.error(error);
      setError(error);
      notyf.open({message: "Error loading folder.", type: "error"});
    })
    .finally(() => {
      setIsLoading(false);
    });
  }, [currentPath, refreshCount]);

  // Reset the component on data source change
  useEffect(() => {
    setCurrentPath(path || location.rootFolderPath);
    setHistory([]);
    setRefreshCount(refreshCount + 1);
  }, [location]);

  /**
   * Loads the folder from the selected path and refreshes the UI.
   * @param path the path to load
   * @param newHistory if present, this will be used to replace the current history
   */
  const handlePathUpdate = (path, newHistory) => {
    console.debug("handlePathUpdate -- new path: " + path + ", current path: " + currentPath);
    if (newHistory) {
      setHistory(newHistory);
    } else {
      setHistory(prevHistory => [...prevHistory, currentPath]);
    }
    console.debug("New history: ", history);
    setCurrentPath(path);
    searchParams.set("path", path);
    setSearchParams(searchParams);
  }

  const handleUploadSuccess = () => {
    setUploadError(null);
    setUploadModalIsOpen(false);
    setRefreshCount(refreshCount + 1);
  }

  /**
   * Creates a new folder in the current folder.
   * @param values the form values
   * @param setSubmitting the form setSubmitting function
   * @param resetForm the form resetForm function
   */
  const handleCreateFolder = (values, {setSubmitting, resetForm}) => {
    const data = new FormData();
    data.append("locationId", location.id);
    data.append("path", currentPath);
    data.append("folderName", values.folderName);
    axios.post("/api/internal/data-files/create-folder", data)
    .then(() => {
      setRefreshCount(refreshCount + 1);
      notyf.open({message: "Folder created successfully.", type: "success"});
    })
    .catch(e => {
      console.error(e);
      console.error("Failed to create folder");
      setNewFolderError(e.message);
      notyf.open({message: "Failed to create folder. Please try again.", type: "error"});
    })
    .finally(() => {
      setSubmitting(false);
      resetForm();
      setNewFolderModalIsOpen(false);
    });
  }

  let content = <LoadingMessageCard />;
  if (!isLoading && error) {
    content = <ErrorMessage />;
  } else if (!isLoading && folder) {
    content = <FileManagerTable
        folder={folder}
        handlePathChange={handlePathUpdate}
        dataSource={location}
    />;
  }

  return (
      <Card>

        <Card.Header>

          <div className="d-flex justify-content-between">

            <div className="card-toolbar">

              <Button
                  variant="outline-primary"
                  className="me-2"
                  style={{width: "90px"}}
                  disabled={history.length === 0}
                  onClick={() => {
                    const p = history.pop();
                    handlePathUpdate(p, history);
                  }}
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

              <Button
                  variant="outline-primary"
                  className="me-2"
                  onClick={() => setRefreshCount(refreshCount + 1)}
              >
                <RotateCw size={18} />
                &nbsp;&nbsp;
                Refresh
              </Button>

            </div>

            <div className="card-toolbar">

              <Button
                  variant="outline-primary"
                  className="me-2"
                  onClick={() => setNewFolderModalIsOpen(true)}
              >
                <FolderPlus size={18} />
                &nbsp;&nbsp;
                New Folder
              </Button>

              <Button
                  variant="primary"
                  onClick={() => setUploadModalIsOpen(true)}
              >
                <Upload size={18} />
                &nbsp;&nbsp;
                Upload
              </Button>

            </div>

          </div>

          <FileManagerUploadModal
              isOpen={uploadModalIsOpen}
              setModalIsOpen={setUploadModalIsOpen}
              handleSuccess={handleUploadSuccess}
              path={currentPath}
              locationId={location.id}
          />

          <FileManagerNewFolderModal
              isOpen={newFolderModalIsOpen}
              setModalIsOpen={setNewFolderModalIsOpen}
              handleFormSubmit={handleCreateFolder}
              error={newFolderError}
          />

        </Card.Header>

        <Card.Body>

          <Row>

            <Col xs={12}>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <Breadcrumb>

                    <Breadcrumb.Item
                        onClick={() => handlePathUpdate(rootPath)}
                    >
                      Home
                    </Breadcrumb.Item>

                    {
                      folder ? folder.path.split("/")
                      .map((path, index) => {
                        if (index === 0 && path === "") return null; // empty first folder name
                        if (index === folder.path.split("/").length - 1 && path === "") return null; // empty last folder name
                        const label = path === "" ? "root" : path; // folder name
                        const pathSlice = folder.path.split("/").slice(0, index + 1).join("/");
                        if (!pathIsChild(pathSlice, rootPath)) {
                          return (
                              <Breadcrumb.Item key={index} active={true}>
                                {label}
                              </Breadcrumb.Item>
                          );
                        } else {
                          return (
                              <Breadcrumb.Item
                                  key={index}
                                  onClick={() => handlePathUpdate(pathSlice)}
                                  active={index === folder.path.split("/").length - 1}
                              >
                                {label}
                              </Breadcrumb.Item>
                          );
                        }
                      }) : ""
                    }
                  </Breadcrumb>

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
  )

}

FileManagerContent.propTypes = {
  location: PropTypes.object.isRequired,
  path: PropTypes.string.isRequired,
}

export default FileManagerContent;