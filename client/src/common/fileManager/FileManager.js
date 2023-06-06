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

import React, {useContext, useEffect, useState} from 'react';
import {Col, Container, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import FileManagerMenu from "./FileManagerMenu";
import FileManagerContent from "./FileManagerContent";
import FileManagerContentPlaceholder from "./FileManagerContentPlaceholder";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";
import {useSearchParams} from "react-router-dom";

const FileManager = ({path, rootFolderId}) => {

  const notyf = useContext(NotyfContext);
  const [searchParams, setSearchParams] = useSearchParams();
  const [rootFolders, setRootFolders] = useState([]);
  const [selectedRootFolder, setSelectedRootFolder] = useState(null);

  const handleRootFolderSelect = (rootFolder) => {
    setSelectedRootFolder(rootFolder);
    setSearchParams({rootFolderId: rootFolder.id});
  }

  useEffect(() => {
    axios.get("/api/internal/storage-drive-folders?browserRoot=true")
    .then(response => {
      setRootFolders(response.data);
      if (rootFolderId) {
        setSelectedRootFolder(response.data.find(dataSource => dataSource.id === parseInt(rootFolderId, 10)));
      }
    })
    .catch(error => {
      console.error(error);
      notyf.open({message: "Failed to load data sources", type: "error"});
    });
  }, [rootFolderId, notyf]);

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center mb-2">
          <Col>
            <h3>File Manager</h3>
          </Col>
        </Row>

        <Row className="file-manager">

          <Col xs="4" md="3">

            <FileManagerMenu
                rootFolders={rootFolders}
                handleFolderSelect={handleRootFolderSelect}
                selectedRootFolder={selectedRootFolder}
            />

          </Col>

          <Col xs="8" md="9">
            {
              selectedRootFolder
                  ? <FileManagerContent rootFolder={selectedRootFolder} path={path} />
                  : <FileManagerContentPlaceholder />
            }

          </Col>

        </Row>

      </Container>
  )

}

FileManager.propTypes = {
  path: PropTypes.string
}

export default FileManager;