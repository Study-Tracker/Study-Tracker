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

import {Button, Col, Row} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFile} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import {StorageFolderFileList, UploadFilesModal} from "../files";

class StudyFilesTabContent extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      modalIsOpen: false,
      isLoaded: false,
      isError: false
    };
    this.toggleModal = this.toggleModal.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.refreshData = this.refreshData.bind(this);
  }

  componentDidMount() {
    this.refreshData();
  }

  refreshData() {
    fetch("/api/study/" + this.props.study.code + "/storage")
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

  toggleModal() {
    this.setState({
      modalIsOpen: !this.state.modalIsOpen
    })
  }

  handleSubmit(files) {
    console.log(files);
    const requests = files.map(file => {
      const data = new FormData();
      data.set("file", file);
      return fetch('/api/study/' + this.props.study.code + '/storage', {
        method: 'POST',
        body: data
      });
    });
    Promise.all(requests)
    .then(() => {
      this.toggleModal();
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

          <Row className="justify-content-between align-items-center">
            <Col>
              {
                !!this.props.user
                    ? (
                        <span className="float-right">
                          <Button color="info" onClick={this.toggleModal}>
                            Upload Files
                            &nbsp;
                            <FontAwesomeIcon icon={faFile}/>
                          </Button>
                        </span>
                    ) : ''
              }
            </Col>
          </Row>

          <Row>
            <Col sm={12}>
              <StorageFolderFileList
                  folder={this.state.folder}
                  isLoaded={this.state.isLoaded}
                  isError={this.state.isError}
              />
            </Col>
          </Row>

          <UploadFilesModal
              isOpen={this.state.modalIsOpen}
              toggleModal={this.toggleModal}
              handleSubmit={this.handleSubmit}
          />

        </div>
    )
  }

}

export default StudyFilesTabContent;