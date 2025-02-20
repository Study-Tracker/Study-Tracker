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

import {Card, Col, Row} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import {StorageFolderFileList} from "../../common/files";
import axios from "axios";
import PropTypes from "prop-types";

const AssayNotebookTabContent = props => {

  const {assay} = props;
  const [folder, setFolder] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    refreshData();
  }, []);

  const refreshData = () => {
    axios.get(`/api/internal/assay/${assay.code}/notebook?contents=true`, {timeout: 30000})
    .then(response => {
      setFolder(response.data);
    })
    .catch(e => {
      console.error(e);
      setError(e.message);
    });
  }

  return (
      <Card>
        <Card.Body>
          <Row>
            <Col sm={12}>
              <StorageFolderFileList
                  folder={folder}
                  isLoaded={!!folder}
                  isError={!!error}
                  errorMessage={error}
                  folderFileKey={'entries'}
              />
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )

}

AssayNotebookTabContent.propTypes = {
  assay: PropTypes.object.isRequired
}

export default AssayNotebookTabContent;
