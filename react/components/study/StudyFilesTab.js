/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Button, Col, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFile} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import {StorageFolderFileList, UploadFilesModal} from "../files";
import {getCsrfToken} from "../../config/csrf";
import PropTypes from "prop-types";

class StudyFilesTabContent extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      modalIsOpen: false,
      isLoaded: false,
      isError: false,
      showFolder: false,
    };
    this.showModal = this.showModal.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.refreshData = this.refreshData.bind(this);
    this.handleShowFolder = this.handleShowFolder.bind(this);
  }

  handleShowFolder() {
    this.setState({
      showFolder: true
    });
    this.refreshData();
  }

  refreshData() {
    fetch("/api/study/" + this.props.study.id + "/storage")
    .then(response => {
      if (response.ok) {
        return response.json();
      }
      throw new Error("Failed to load study folder.");
    })
    .then(json => {
      this.setState({
        folder: json,
        isLoaded: true
      })
    })
    .catch(e => {
      console.error(e);
      this.setState({
        isError: true,
        error: e.message
      })
    });
  }

  showModal(bool) {
    this.setState({
      modalIsOpen: bool
    })
  }

  handleSubmit(files) {
    console.log(files);
    const requests = files.map(file => {
      const data = new FormData();
      data.set("file", file);
      return fetch('/api/study/' + this.props.study.id + '/storage', {
        method: 'POST',
        headers: {"X-XSRF-TOKEN": getCsrfToken()},
        body: data
      });
    });
    Promise.all(requests)
    .then(() => {
      this.showModal(false);
      this.refreshData();
    })
    .catch(e => {
      console.error(e);
      console.error("Failed to upload files");
    });
  }

  render() {
    return (
        <div>

          <Row className="justify-content-between align-items-center mb-2">
            <Col>
              <span className="float-end">
                <Button variant="info"
                        onClick={() => this.showModal(true)}>
                  Upload Files
                  &nbsp;
                  <FontAwesomeIcon icon={faFile}/>
                </Button>
              </span>
            </Col>
          </Row>

          <Row>

            {
              this.state.showFolder ? (
                  <Col sm={12}>
                    <StorageFolderFileList
                        folder={this.state.folder}
                        isLoaded={this.state.isLoaded}
                        isError={this.state.isError}
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
                      this.props.study.storageFolder.url ? (
                        <React.Fragment>

                          <Button
                              variant="info"
                              target={"_blank noopener noreferrer"}
                              href={this.props.study.storageFolder.url}
                          >
                            View files in Egnyte
                          </Button>

                          &nbsp;&nbsp;or&nbsp;&nbsp;

                        </React.Fragment>
                      ) : ""
                    }

                    <Button
                        variant="primary"
                        onClick={this.handleShowFolder}
                    >
                      Show files here
                    </Button>

                  </Col>
              )
            }

          </Row>

          <UploadFilesModal
              isOpen={this.state.modalIsOpen}
              showModal={this.showModal}
              handleSubmit={this.handleSubmit}
          />

        </div>
    )
  }

}

StudyFilesTabContent.propTypes = {
  study: PropTypes.object,
}

export default StudyFilesTabContent;