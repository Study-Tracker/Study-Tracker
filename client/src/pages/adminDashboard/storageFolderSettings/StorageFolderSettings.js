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

import React, {useContext, useRef, useState} from "react";
import axios from "axios";
import {Button, Card, Col, Dropdown, Row} from "react-bootstrap";
import {FolderPlus} from "react-feather";
import {SettingsLoadingMessage} from "../../../common/loading";
import {SettingsErrorMessage} from "../../../common/errors";
import StorageFolderFormModal from "./StorageFolderFormModal";
import NotyfContext from "../../../context/NotyfContext";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import StorageFolderCard from "../../../common/fileManager/StorageFolderCard";
import StorageFoldersPlaceholder from "./StorageFoldersPlaceholder";
import {faFilter} from "@fortawesome/free-solid-svg-icons";
import swal from "sweetalert";
import {useMutation, useQuery, useQueryClient} from "react-query";

const StorageFolderSettings = () => {

  const notyf = useContext(NotyfContext);
  const queryClient = useQueryClient();
  const [showModal, setShowModal] = useState(false);
  const [selectedFolder, setSelectedFolder] = useState(null);
  const [filter, setFilter] = useState("SHOW_ALL");
  const formikRef = useRef();

  const filterLabels = {
    SHOW_ALL: "Show All",
    STUDY_ROOT: "Study Root Only",
    BROWSER_ROOT: "Browser Root Only"
  }

  const {data: folders, isLoading, error} = useQuery("rootStorageFolders", () => {
    return axios.get("/api/internal/storage-drive-folders?root=true")
    .then(response => response.data)
    .catch(e => {
      console.error(e);
      notyf.error('Failed to load available storage folders.');
      return e;
    })
  });

  const handleFolderEdit = (folder) => {
    console.debug("Edit folder: ", folder);
    formikRef.current?.resetForm();
    setSelectedFolder(folder);
    setShowModal(true);
  }

  const deleteMutation = useMutation((folder) => {
    return axios.delete("/api/internal/storage-drive-folders/" + folder.id)
  });

  const handleFolderDelete = (folder) => {
    swal({
      title: "Are you sure you want to delete this folder?",
      text: "Removing root folders will not delete the folder in the file system or "
          + "affect any subfolders created from this folder. You can re-register this "
          + "folder at any time to continue using the existing folder in the file system.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        deleteMutation.mutate(folder, {
          onSuccess: (data) => {
            notyf.success('Storage folder deleted.');
            queryClient.invalidateQueries("rootStorageFolders");
          },
          onError: (e) => {
            console.error(e);
            notyf.error('Failed to delete storage folder.');
          }
        });
      }
    });
  }

  const handleFilterChange = (filter) => {
    console.debug("Filter change: ", filter);
    setFilter(filter);
  }

  return (
      <>

        <Row className={"mb-3 justify-content-around"}>

          <Col>
            <h3>Storage Folders</h3>
          </Col>

          <Col>
            <div className="d-flex justify-content-end">

              <span>
                <Dropdown className="me-2 mb-1">
                  <Dropdown.Toggle variant={"outline-info"}>
                    <FontAwesomeIcon icon={faFilter} className={"me-2"}/>
                    {filterLabels[filter]}
                  </Dropdown.Toggle>
                  <Dropdown.Menu>

                    <Dropdown.Item onClick={() => handleFilterChange("SHOW_ALL")}>
                      {filterLabels["SHOW_ALL"]}
                    </Dropdown.Item>

                    <Dropdown.Item onClick={() => handleFilterChange("STUDY_ROOT")}>
                      {filterLabels["STUDY_ROOT"]}
                    </Dropdown.Item>

                    <Dropdown.Item onClick={() => handleFilterChange("BROWSER_ROOT")}>
                      {filterLabels["BROWSER_ROOT"]}
                    </Dropdown.Item>

                  </Dropdown.Menu>
                </Dropdown>
              </span>

              <span>
                <Button
                    variant={"primary"}
                    onClick={() => {
                      formikRef.current?.resetForm();
                      setSelectedFolder(null);
                      setShowModal(true);
                    }}
                >
                  <FolderPlus className="feather align-middle me-2 mb-1"/>
                  Add Folder
                </Button>
              </span>

            </div>
          </Col>
        </Row>

        <Row>
          <Col>
            <Card className={"illustration"}>
              <Card.Body>
                <Row>
                  <Col>
                    Root folders are folders in local file systems or cloud services that Study Tracker
                    users can use to store their data. A root study folder will be created on
                    application startup, dependent on the <code>storage.mode</code> setting used, which
                    will determine where program, study, and assay folders are created by default. You
                    can add additional root folders here from the available configured storage systems.
                  </Col>
                </Row>
              </Card.Body>
            </Card>
          </Col>
        </Row>

        {
            isLoading && !error && <SettingsLoadingMessage />
        }

        {
            error && <SettingsErrorMessage />
        }

        {
            folders && folders.length > 0 && (
                folders
                .filter(folder => {
                  if (filter === "SHOW_ALL") {
                    return true;
                  } else if (filter === "STUDY_ROOT") {
                    return folder.studyRoot;
                  } else {
                    return folder.browserRoot;
                  }
                })
                .map((folder, index) => (
                    <Row key={"storage-folder-card-" + index}>
                      <Col>
                        <StorageFolderCard
                            folder={folder}
                            handleFolderEdit={handleFolderEdit}
                            handleFolderDelete={handleFolderDelete}
                        />
                      </Col>
                    </Row>
                )
            ))
        }

        {
          (!folders || folders.length === 0) && (
              <Row>
                <Col>
                  <StorageFoldersPlaceholder handleClick={() => {
                    formikRef.current?.resetForm();
                    setSelectedFolder(null);
                    setShowModal(true);
                  }} />
                </Col>
              </Row>
          )
        }

        <StorageFolderFormModal
            isOpen={showModal}
            setIsOpen={setShowModal}
            selectedFolder={selectedFolder}
            formikRef={formikRef}
        />

      </>

  )

}

export default StorageFolderSettings;