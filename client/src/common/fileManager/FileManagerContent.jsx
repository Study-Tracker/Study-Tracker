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

import {Button, Card, Col, Row} from "react-bootstrap";
import {
  ArrowLeft,
  CornerLeftUp,
  FolderPlus,
  RotateCw,
  Upload
} from "react-feather";
import FileManagerUploadModal from "./FileManagerUploadModal";
import FileManagerNewFolderModal from "./FileManagerNewFolderModal";
import React, {useContext, useEffect, useMemo, useState} from "react";
import axios from "axios";
import FileManagerTable from "./FileManagerTable";
import {useSearchParams} from "react-router-dom";
import NotyfContext from "../../context/NotyfContext";
import {LoadingMessageCard} from "../loading";
import PropTypes from "prop-types";
import FileManagerContentError from "./FileManagerContentError";
import FileManagerPathBreadcrumbs from "./FileManagerPathBreadcrumbs";
import {getPathParts} from "./fileManagerUtils";
import {FolderSizeBadge} from "./folderBadges";

const FileManagerContent = ({
    rootFolder,
    path,
    handleRepairFolder
}) => {

  console.debug("Selected data source: ", rootFolder);
  console.debug("Selected path: ", path);

  const notyf = useContext(NotyfContext);

  const [folder, setFolder] = useState(null);
  const [currentPath, setCurrentPath] = useState(path || rootFolder.path);
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
    axios.get("/api/internal/data-files", {
      params: {
        path: currentPath,
        folderId: rootFolder.id
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
  }, [refreshCount]);

  // Reset the component on data source change
  useEffect(() => {
    setCurrentPath(path || rootFolder.path);
    setHistory([]);
    setRefreshCount(refreshCount + 1);
  }, [rootFolder]);

  const pathParts = useMemo(() => {
    return folder ? getPathParts(folder.path, rootFolder.path) : [];
  }, [folder, rootFolder]);
  const currentPart = folder ? pathParts[pathParts.length - 1] : null;


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
    setRefreshCount(refreshCount + 1);
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
    data.append("folderId", rootFolder.id);
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
    console.debug("Error: ", error);
    content = (
        <FileManagerContentError
          error={error}
          handleRepairFolder={handleRepairFolder}
        />
    );
  } else if (!isLoading && folder) {
    content = (
        <FileManagerTable
          folder={folder}
          handlePathChange={handlePathUpdate}
          rootFolder={rootFolder}
        />
    );
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
                  disabled={!currentPart || currentPart.parentPath === null}
                  onClick={() => handlePathUpdate(currentPart.parentPath)}
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

              {
                rootFolder.writeEnabled ? (
                  <>
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
                  </>
                ) : (
                  <Button variant={"outline-secondary"} disabled={true}>
                    Read Only
                  </Button>
                )
              }

            </div>

          </div>

          <FileManagerUploadModal
              isOpen={uploadModalIsOpen}
              setModalIsOpen={setUploadModalIsOpen}
              handleSuccess={handleUploadSuccess}
              path={currentPath}
              folderId={rootFolder.id}
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
                  <FileManagerPathBreadcrumbs
                      rootFolder={rootFolder}
                      paths={pathParts}
                      handlePathUpdate={handlePathUpdate}
                  />
                </div>
                <div>
                  { folder && <FolderSizeBadge folder={folder} /> }
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
  rootFolder: PropTypes.object.isRequired,
  path: PropTypes.string.isRequired,
  handleRepairFolder: PropTypes.func
}

export default FileManagerContent;