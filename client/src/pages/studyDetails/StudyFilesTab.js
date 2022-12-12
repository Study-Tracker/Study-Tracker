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

import {Button, Card, Col, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFile} from "@fortawesome/free-solid-svg-icons";
import React, {useState} from "react";
import {StorageFolderFileList, UploadFilesModal} from "../../common/files";
import PropTypes from "prop-types";
import axios from "axios";

const StudyFilesTabContent = props => {

  const {study} = props;
  const [folder, setFolder] = useState(null);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [showFolder, setShowFolder] = useState(false);
  const [error, setError] = useState(null);

  const handleShowFolder = () => {
    setShowFolder(true);
    refreshData();
  }

  const refreshData = () => {
    axios.get("/api/internal/study/" + study.id + "/storage")
    .then(response => setFolder(response.data))
    .catch(e => {
      console.error(e);
      setError(e);
    });
  }

  const handleSubmit = (files) => {
    console.debug(files);
    const requests = files.map(file => {
      const data = new FormData();
      data.append("file", file);
      return axios.post('/api/internal/study/' + study.id + '/storage', data);
    });
    Promise.all(requests)
    .then(() => {
      setModalIsOpen(false);
      refreshData();
    })
    .catch(e => {
      console.error(e);
      console.error("Failed to upload files");
    });
  }

  return (
      <Card>
        <Card.Body>

          <Row className="justify-content-between align-items-center mb-2">
            <Col>
              <span className="float-end">
                <Button variant="info"
                        onClick={() => setModalIsOpen(true)}>
                  Upload Files
                  &nbsp;
                  <FontAwesomeIcon icon={faFile}/>
                </Button>
              </span>
            </Col>
          </Row>

          <Row>

            {
              showFolder ? (
                  <Col sm={12}>
                    <StorageFolderFileList
                        folder={folder}
                        isLoaded={folder !== null}
                        isError={error !== null}
                    />
                  </Col>
              ) : (
                  <Col sm={12} className={"text-center"}>

                    <p>
                      <img
                          src={"/static/images/clip/data-storage.png"}
                          alt="File storage"
                          className="img-fluid"
                          width={250}
                      />
                    </p>

                    <p>
                      Study files can be viewed in the native file browser,
                      or viewed as a partial folder tree here. <em>Note:</em>
                      &nbsp;loading and viewing files here may be slow and
                      subject to rate limits.
                    </p>

                  {
                    study.primaryStorageFolder
                    && study.primaryStorageFolder.url ? (
                      <React.Fragment>

                        <Button
                            variant="info"
                            target={"_blank noopener noreferrer"}
                            href={study.primaryStorageFolder.url}
                        >
                          View files in Egnyte
                        </Button>

                          &nbsp;&nbsp;or&nbsp;&nbsp;

                        </React.Fragment>
                      ) : ""
                    }

                    <Button
                        variant="primary"
                        onClick={handleShowFolder}
                    >
                      Show files here
                    </Button>

                  </Col>
              )
            }

          </Row>

          <UploadFilesModal
              isOpen={modalIsOpen}
              showModal={(bool) => setModalIsOpen(bool)}
              handleSubmit={handleSubmit}
          />

        </Card.Body>

      </Card>
  )

}

StudyFilesTabContent.propTypes = {
  study: PropTypes.object,
}

export default StudyFilesTabContent;