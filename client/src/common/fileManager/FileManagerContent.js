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

import {Badge, Button, Card, Col, Row} from "react-bootstrap";
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

const FolderPathBreadcrumbs = ({dataSource, folder}) => {
  return <Badge bg="info" style={{fontSize: "100%"}}>{dataSource.name}: {folder ? folder.path : ''}</Badge>
}

const FileManagerContent = ({dataSource, path}) => {

  console.debug("Selected data source: ", dataSource);
  console.debug("Selected path: ", path);

  const notyf = useContext(NotyfContext);

  const [folder, setFolder] = useState(null);
  const [currentPath, setCurrentPath] = useState(path || dataSource.rootFolderPath);
  const [previousPath, setPreviousPath] = useState(null);
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
    axios.get("/api/internal/data-files", {
      params: {
        path: currentPath,
        locationId: dataSource.id
      }
    })
    .then(response => {
      setFolder(response.data);
      setIsLoading(false);
    })
    .catch(error => {
      console.error(error);
      setError(error);
      setIsLoading(false);
      notyf.open({message: "Error loading folder.", type: "error"});
    });
  }, [currentPath, refreshCount]);

  // Reset the component on data source change
  useEffect(() => {
    setCurrentPath(path || dataSource.rootFolderPath);
    setPreviousPath(null);
  }, [dataSource]);

  /**
   * Loads the folder from the selected path and refreshes the UI.
   * @param path the path to load
   */
  const handlePathUpdate = (path) => {
    console.debug("handlePathUpdate -- new path: " + path + ", current path: " + currentPath);
    setPreviousPath(currentPath);
    setCurrentPath(path);
    searchParams.set("path", path);
    setSearchParams(searchParams);
  }

  /**
   * Uploads the provided files to the current folder.
   * @param files
   */
  const handleUploadFiles = (files) => {
    console.debug("Files", files);
    setUploadError(null);
    const requests = files.map(file => {
      const data = new FormData();
      data.append("file", file);
      data.append("locationId", dataSource.id);
      data.append("path", currentPath);
      return axios.post('/api/internal/data-files/upload', data);
    });
    Promise.all(requests)
    .then(() => {
      setUploadModalIsOpen(false);
      setRefreshCount(refreshCount + 1);
      notyf.open({message: "Files uploaded successfully", type: "success"});
    })
    .catch(e => {
      console.error(e);
      console.error("Failed to upload files");
      let errorMessage = e.message;
      if (uploadError) {
        errorMessage = errorMessage + " - " + uploadError;
      }
      setUploadError(errorMessage);
    });
  }

  /**
   * Creates a new folder in the current folder.
   * @param values the form values
   * @param setSubmitting the form setSubmitting function
   */
  const handleCreateFolder = (values, {setSubmitting}) => {
    const data = new FormData();
    data.append("locationId", dataSource.id);
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
      setNewFolderModalIsOpen(false);
    });
  }

  let content = <LoadingMessageCard />;
  if (!isLoading && error) {
    content = <ErrorMessage />;
  } else if (!isLoading && folder) {
    content = <FileManagerTable
        folder={folder}
        handlePathChange={handlePathUpdate} />;
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
              handleSubmit={handleUploadFiles}
              error={uploadError}
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
                  <FolderPathBreadcrumbs dataSource={dataSource} folder={folder} />
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

export default FileManagerContent;