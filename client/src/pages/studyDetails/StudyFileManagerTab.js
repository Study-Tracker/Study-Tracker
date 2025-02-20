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

import {Col, Row} from "react-bootstrap";
import React, {useContext, useState} from "react";
import PropTypes from "prop-types";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";
import FileManagerContent from "../../common/fileManager/FileManagerContent";
import FileManagerContentPlaceholder
  from "../../common/fileManager/FileManagerContentPlaceholder";
import StudyFileManagerMenu
  from "../../common/fileManager/StudyFileManagerMenu";
import {useQuery} from "react-query";
import LoadingMessage from "../../common/structure/LoadingMessage";

const StudyFileManagerTab = ({study}) => {

  const notyf = useContext(NotyfContext);
  const [selectedFolder, setSelectedFolder] = useState(null);

  const handleFolderSelect = (folder) => {
    setSelectedFolder(folder);
    console.debug("Selected folder", folder);
  }

  const {data: folders, isLoading} = useQuery(["storageFolders", study.id], () => {
    return axios.get(`/api/internal/study/${study.id}/storage`)
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
    axios.post("/api/internal/study/" + study.id + "/storage/repair")
    .then(response => {
      notyf.open({message: "Study folder repaired", type: "success"});
      const s = selectedFolder;
      setSelectedFolder(null);
      setSelectedFolder(s);
    })
    .catch(error => {
      console.error(error);
      notyf.open({message: "Failed to repair study folder", type: "error"});
    });
  }

  if (isLoading) return <LoadingMessage />;

  return (
      <>

        <Row className="file-manager">

          <Col xs="4" md="3">

            <StudyFileManagerMenu
                record={study}
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

StudyFileManagerTab.propTypes = {
  study: PropTypes.object,
}

export default StudyFileManagerTab;