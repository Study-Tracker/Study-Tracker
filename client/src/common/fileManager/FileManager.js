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

import React, {useContext, useEffect, useState} from 'react';
import {Col, Container, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import FileManagerMenu from "./FileManagerMenu";
import FileManagerContent from "./FileManagerContent";
import FileManagerContentPlaceholder from "./FileManagerContentPlaceholder";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";
import {useSearchParams} from "react-router-dom";

const FileManager = ({path, locationId}) => {

  const notyf = useContext(NotyfContext);
  const [searchParams, setSearchParams] = useSearchParams();
  const [dataSources, setDataSources] = useState([]);
  const [selectedDataSource, setSelectedDataSource] = useState(null);

  const handleDataSourceSelect = (dataSource) => {
    setSelectedDataSource(dataSource);
    setSearchParams({locationId: dataSource.id});
  }

  useEffect(() => {
    axios.get("/api/internal/data-files/locations")
    .then(response => {
      setDataSources(response.data);
      if (locationId) {
        setSelectedDataSource(response.data.find(dataSource => dataSource.id === parseInt(locationId)));
      }
    })
    .catch(error => {
      console.error(error);
      notyf.open({message: "Failed to load data sources", type: "error"});
    });
  }, []);

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
                dataSources={dataSources}
                handleDataSourceSelect={handleDataSourceSelect}
                selectedDataSource={selectedDataSource}
            />

          </Col>

          <Col xs="8" md="9">
            {
              selectedDataSource
                  ? <FileManagerContent dataSource={selectedDataSource} path={path} />
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