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

import {Col, Row} from "reactstrap";
import React from "react";
import {StorageFolderFileList} from "../files";

class StudyNotebookTabContent extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isLoaded: false,
      isError: false,
    };
    this.fetchUrl = '/api/study/' + this.props.study.code + '/notebook';
    this.refreshData = this.refreshData.bind(this);
  }

  componentDidMount() {
    this.refreshData();
  }

  refreshData() {
    fetch(this.fetchUrl)
    .then(response => {
      if (response.ok) {
        return response.json();
      }
      throw new Error("Failed to load notebook folder.");
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

  render() {
    return (
      <div>
        <Row>
          <Col sm={12}>
            <StorageFolderFileList
              folder={this.state.folder}
              isLoaded={this.state.isLoaded}
              isError={this.state.isError}
              errorMessage={this.state.error}
              folderFileKey={'entries'}
            />
          </Col>
        </Row>
      </div>
    )
  }
}

export default StudyNotebookTabContent;
