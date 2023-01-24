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

import {Card, Col, Dropdown, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faPersonRunning,
  faTriangleExclamation,
} from "@fortawesome/free-solid-svg-icons";
import {
  faFolderOpen,
  faPlusSquare,
  faSquareCheck
} from "@fortawesome/free-regular-svg-icons";
import React from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";

const StudyQuickActionsWidget = ({
    study,
    handleAddToCollection
}) => {

  const navigate = useNavigate();
  const notyf = React.useContext(NotyfContext);

  const handleStatusChange = (status) => {
    axios.post("/api/internal/study/" + study.id + "/status", {
      status: status
    })
    .then(() => window.location.reload())
    .catch(e => {
      notyf.open({
        type: 'error',
        message: 'Error changing status'
      });
      console.error(e);
    });
  }

  return (
      <Card className="illustration flex-fill">
        <Card.Body className="p-0 d-flex flex-fill">
          <Row className="g-0 w-100">
            <Col xs={6} className="align-self-end text-end">
              <img
                  src={"/static/images/clip/user-interface.png"}
                  alt="Quick actions"
                  className="img-fluid illustration-img"
              />
            </Col>
            <Col xs="6">
              <div className="illustration-text p-3 m-1">
                <h4 className="illustration-text">
                  What's next?
                </h4>
                <br/>
                <Dropdown className="me-1 mb-1">
                  <Dropdown.Toggle variant={"outline-primary"}>
                    <FontAwesomeIcon icon={faPersonRunning} className={"me-2"}/>
                    Actions
                  </Dropdown.Toggle>
                  <Dropdown.Menu>

                    {
                      (study.status === "IN_PLANNING" || study.status === "NEEDS_ATTENTION") && (
                            <Dropdown.Item onClick={() => handleStatusChange("ACTIVE")}>
                              <FontAwesomeIcon icon={faPersonRunning} className={"me-2"}/>
                              Set status to 'Active'
                            </Dropdown.Item>
                      )
                    }

                    {
                        study.status === "ACTIVE" && (
                            <Dropdown.Item onClick={() => handleStatusChange("COMPLETE")}>
                              <FontAwesomeIcon icon={faSquareCheck} className={"me-2"}/>
                              Set status to 'Complete'
                            </Dropdown.Item>
                        )
                    }

                    {
                        study.status === "COMPLETE" && (
                            <Dropdown.Item onClick={() => handleStatusChange("NEEDS_ATTENTION")}>
                              <FontAwesomeIcon icon={faTriangleExclamation} className={"me-2"}/>
                              Set status to 'Needs attention'
                            </Dropdown.Item>
                        )
                    }

                    <Dropdown.Item onClick={() => navigate("/study/" + study.code + "/assays/new")}>
                      <FontAwesomeIcon icon={faPlusSquare} className={"me-2"}/>
                      New Assay
                    </Dropdown.Item>

                    <Dropdown.Item onClick={handleAddToCollection}>
                      <FontAwesomeIcon icon={faFolderOpen} className={"me-2"}/>
                      Add to Collection
                    </Dropdown.Item>

                  </Dropdown.Menu>
                </Dropdown>
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

export default StudyQuickActionsWidget;
