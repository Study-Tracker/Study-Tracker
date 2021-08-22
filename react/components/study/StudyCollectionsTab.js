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

import React from 'react';
import {Button, Col, Row, Table} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";

class StudyCollectionsTab extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      collections: [],
      isError: false
    };
  }

  componentDidMount() {
    fetch("/api/study/" + this.props.study.id + "/studycollection")
    .then(response => response.json())
    .then(collections => {
      this.setState({
        collections
      })
    })
  }

  render() {

    let collections = this.state.collections.sort((a, b) => {
      if (a.name > b.name) {
        return 1;
      } else if (a.name < b.name) {
        return -1;
      } else {
        return 0;
      }
    }).map((collection, i) => {
      return (
          <tr key={'cr-' + i}>
            <td>
              <a href={"/collection/" + collection.id}>{collection.name}</a>
            </td>
            <td>{collection.studies.length}</td>
            <td>
              {
                !!collection.shared
                  ? <div className="badge badge-success">Public</div>
                  : <div className="badge badge-warning">Private</div>
              }
            </td>
          </tr>
      )
    });

    return (
        <div>

          <Row className="justify-content-between align-items-center mb-4">
            <Col>
              <span className="float-right">
                <Button
                    color="info"
                    onClick={() => this.props.toggleCollectionModal()}
                >
                  Add to Collection
                  &nbsp;
                  <FontAwesomeIcon icon={faPlusCircle}/>
                </Button>
              </span>
            </Col>
          </Row>

          <Row>
            <Col xs={12}>
              {
                collections.length > 0
                    ? (
                        <Col xs={12}>
                          <Table striped style={{fontSize: "inherit"}}>
                            <thead>
                              <tr>
                                <th>Name</th>
                                <th># Studies</th>
                                <th>Visibility</th>
                              </tr>
                            </thead>
                            <tbody>
                              {collections}
                            </tbody>
                          </Table>
                        </Col>
                    )
                    : (
                        <div className={"text-center"}>
                          <h4>This study does not belong to any collections.</h4>
                        </div>
                    )
              }
            </Col>
          </Row>
        </div>
    );
  }

}

export default StudyCollectionsTab;