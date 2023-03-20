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

import React, {useContext, useEffect, useRef, useState} from "react";
import axios from "axios";
import {Button, Card, Col, Row} from "react-bootstrap";
import {FolderPlus} from "react-feather";
import {SettingsLoadingMessage} from "../../../common/loading";
import {SettingsErrorMessage} from "../../../common/errors";
import StorageFolderTable from "./StorageFolderTable";
import StorageFolderFormModal from "./StorageFolderFormModal";
import NotyfContext from "../../../context/NotyfContext";

const StorageFolderSettings = () => {

  const notyf = useContext(NotyfContext);

  const [folders, setFolders] = useState([]);
  const [loadCounter, setLoadCounter] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedFolder, setSelectedFolder] = useState(null);
  const formikRef = useRef();

  useEffect(() => {
    setIsLoading(true);
    axios.get("/api/internal/storage-drive-folders/")
    .then(response => {
      setFolders(response.data);
    })
    .catch(e => {
      console.error(e);
      setError(e)
      notyf.open({message: 'Failed to load available storage folders.', type: 'error'});
    })
    .finally(() => {
      setIsLoading(false);
    });
  }, [loadCounter]);

  const handleSubmitForm = (values, {setSubmitting, resetForm}) => {
    const url = selectedFolder
        ? "/api/internal/storage-drive-folders/" + selectedFolder.id
        : "/api/internal/storage-drive-folders/";
    const method = selectedFolder ? "PUT" : "POST";
    axios({
      method: method,
      url: url,
      data: values,
      headers: {
        "Content-Type": "application/json"
      },
    })
    .then(response => {
      notyf.open({message: 'Storage folder saved.', type: 'success'});
      resetForm();
      setShowModal(false);
    })
    .catch(e => {
      console.error(e);
      if (e.response.status === 404) {
        notyf.open({message: 'The requested folder does not exist: ' + values.rootFolderPath, type: 'error'});
      } else {
        notyf.open({message: 'Failed to save storage location.', type: 'error'});
      }
    })
    .finally(() => {
      setSubmitting(false);
      setLoadCounter(loadCounter + 1);
    })
  }

  const handleFolderEdit = (folder) => {
    console.debug("Edit folder: ", folder);
    formikRef.current?.resetForm();
    setSelectedFolder(folder);
    setShowModal(true);
  }

  let content = '';
  if (isLoading) content = <SettingsLoadingMessage />;
  else if (error) content = <SettingsErrorMessage />;
  else content = <StorageFolderTable
          folders={folders}
          handleFolderEdit={handleFolderEdit()}
          handleToggleFolderActive={null}
      />;

  return (
      <Card>

        <Card.Header>
          <Card.Title tag={"h5"}>
            Storage Folders
            <span className="float-end">
              <Button
                  variant={"primary"}
                  onClick={() => {
                    formikRef.current?.resetForm();
                    setSelectedFolder(null);
                    setShowModal(true);
                  }}
              >
                Add Folder
                &nbsp;
                <FolderPlus className="feather align-middle ms-2 mb-1"/>
              </Button>
            </span>
          </Card.Title>
        </Card.Header>

        <Card.Body>

          <Row>
            <Col>
              <div className="info-alert">
                Root folders are folders in local file systems or cloud services that Study Tracker
                users can use to store their data. A root study folder will be created on
                application startup, dependent on the <code>storage.mode</code> setting used, which
                will determine where program, study, and assay folders are created by default. You
                can add additional root folders here from the available configured storage systems.
              </div>
            </Col>
          </Row>

          <Row>
            <Col>
              {content}
            </Col>
          </Row>

          <StorageFolderFormModal
              isOpen={showModal} 
              setIsOpen={setShowModal}
              selectedLocation={selectedFolder}
              handleFormSubmit={handleSubmitForm}
              formikRef={formikRef}
          />
        </Card.Body>

      </Card>

  )

}

export default StorageFolderSettings;