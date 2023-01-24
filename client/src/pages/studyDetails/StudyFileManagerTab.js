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

import {Col, Container, Row} from "react-bootstrap";
import React, {useContext, useEffect, useState} from "react";
import PropTypes from "prop-types";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";
import FileManagerContent from "../../common/fileManager/FileManagerContent";
import FileManagerContentPlaceholder
  from "../../common/fileManager/FileManagerContentPlaceholder";
import StudyFileManagerMenu
  from "../../common/fileManager/StudyFileManagerMenu";

const StudyFileManagerTab = ({study}) => {

  const notyf = useContext(NotyfContext);
  const [folders, setFolders] = useState([]);
  const [locations, setLocations] = useState([]);
  const [selectedFolder, setSelectedFolder] = useState(null);
  const [selectedLocation, setSelectedLocation] = useState(null);

  const handleFolderSelect = (folder) => {
    setSelectedFolder(folder);
    const location = locations.find(location => location.id === folder.fileStorageLocationId);
    setSelectedLocation(location);
    console.debug("Selected folder", folder);
    console.debug("Selected location", location);
  }

  useEffect(() => {
    axios.get("/api/internal/study/" + study.id + "/storage")
    .then(response => {
      setFolders(response.data);
      axios.get("/api/internal/data-files/locations")
      .then(response2 => {
        setLocations(response2.data);
      });
    })
    .catch(error => {
      console.error(error);
      notyf.open({message: "Failed to load data sources", type: "error"});
    });
  }, []);

  return (
      <Container fluid className="animated fadeIn">

        <Row className="file-manager">

          <Col xs="4" md="3">

            <StudyFileManagerMenu
                folders={folders}
                locations={locations}
                handleFolderSelect={handleFolderSelect}
                selectedFolder={selectedFolder}
            />

          </Col>

          <Col xs="8" md="9">
            {
              selectedFolder && selectedLocation
                  ? <FileManagerContent location={selectedLocation} path={selectedFolder.path} />
                  : <FileManagerContentPlaceholder />
            }

          </Col>

        </Row>

      </Container>
  )

}

StudyFileManagerTab.propTypes = {
  study: PropTypes.object,
}

export default StudyFileManagerTab;