/*
 * Copyright 2019-2024 the original author or authors.
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

import React, {useContext, useState} from 'react';
import NotyfContext from "../../context/NotyfContext";
import {useQuery} from "react-query";
import axios from "axios";
import LoadingMessage from "../../common/structure/LoadingMessage";
import {Col, Row} from "react-bootstrap";
import StudyFileManagerMenu
  from "../../common/fileManager/StudyFileManagerMenu";
import FileManagerContent from "../../common/fileManager/FileManagerContent";
import FileManagerContentPlaceholder
  from "../../common/fileManager/FileManagerContentPlaceholder";
import PropTypes from "prop-types";

const ProgramFileManagerTab = ({program}) => {

  const notyf = useContext(NotyfContext);
  const [selectedFolder, setSelectedFolder] = useState(null);

  const handleFolderSelect = (folder) => {
    setSelectedFolder(folder);
    console.debug("Selected folder", folder);
  }

  const {data: folders, isLoading} = useQuery(["storageFolders", program.id], () => {
    return axios.get(`/api/internal/program/${program.id}/storage`)
    .then(response => {
      const defaultFolder = response.data.find(f => f.primary);
      setSelectedFolder(defaultFolder ? defaultFolder.storageDriveFolder : null);
      return response.data;
    })
    .catch(e => {
      console.error(e);
      notyf.error("Failed to load data sources: " + e.message);
      return e;
    });
  });

  const repairFolder = () => {
    axios.post(`/api/internal/program/${program.id}/storage/repair`)
    .then(response => {
      notyf.open({message: "Program folder repaired", type: "success"});
      const s = selectedFolder;
      setSelectedFolder(null);
      setSelectedFolder(s);
    })
    .catch(error => {
      console.error(error);
      notyf.open({message: "Failed to repair program folder", type: "error"});
    });
  }

  if (isLoading) return <LoadingMessage />;

  return (
      <>

        <Row className="file-manager">

          <Col xs="4" md="3">

            <StudyFileManagerMenu
                record={program}
                folders={folders}
                handleFolderSelect={handleFolderSelect}
                selectedFolder={selectedFolder}
            />

          </Col>

          <Col xs="8" md="9">
            {
              selectedFolder
                  ? <FileManagerContent
                      rootFolder={selectedFolder}
                      path={selectedFolder.path}
                      handleRepairFolder={repairFolder}
                  />
                  : <FileManagerContentPlaceholder />
            }

          </Col>

        </Row>

      </>
  )

}

ProgramFileManagerTab.propTypes = {
  program: PropTypes.object.isRequired,
}

export default ProgramFileManagerTab;