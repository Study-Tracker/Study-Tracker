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

import React, {useContext} from "react";
import {Badge, Button, Card, Col, Row, Table} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";
import {useQuery} from "@tanstack/react-query";
import {SettingsLoadingMessage} from "../../common/loading";

const StudyCollectionsTab = props => {

  const {study, showCollectionModal} = props;
  // const [collections, setCollections] = useState([]);
  const notyf = useContext(NotyfContext);

  const {data: collections, isLoading, error} = useQuery(["studyCollections", study.id], async () => {
    return axios.get("/api/internal/study/" + study.id + "/studycollection")
    .then(response => response.data)
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to load study collections"
      });
      return error;
    });
  })

  let rows = (collections || []).sort((a, b) => {
    if (a.name > b.name) {
      return 1;
    } else if (a.name < b.name) {
      return -1;
    } else {
      return 0;
    }
  })
  .map((collection, i) => {
    return (
        <tr key={'cr-' + i}>
          <td>
            <a href={"/collection/" + collection.id}>{collection.name}</a>
          </td>
          <td>{collection.studies.length}</td>
          <td>
            {
              !!collection.shared
                  ? <Badge bg="success">Public</Badge>
                  : <Badge bg="warning">Private</Badge>
            }
          </td>
        </tr>
    )
  });

  return (
      <Card>
        <Card.Body>

          <Row className="justify-content-between align-items-center mb-4">
            <Col>
              <span className="float-end">
                <Button
                    variant="info"
                    onClick={() => showCollectionModal(true)}
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
                  isLoading ? (
                    <Col>
                      <SettingsLoadingMessage />
                    </Col>
                  ) : rows.length > 0 ? (
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
                            {rows}
                          </tbody>
                        </Table>
                      </Col>
                  ) : (
                      <div className={"text-center"}>
                        <h4>This study does not belong to any collections.</h4>
                      </div>
                  )
              }
            </Col>
          </Row>
        </Card.Body>
      </Card>
  );

}

export default StudyCollectionsTab;