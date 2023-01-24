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
  faTriangleExclamation
} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import PropTypes from "prop-types";
import {faSquareCheck} from "@fortawesome/free-regular-svg-icons";
import NotyfContext from "../../context/NotyfContext";
import axios from "axios";

const AssayQuickActionsWidget = ({
    assay
}) => {

  const notyf = React.useContext(NotyfContext);

  const handleStatusChange = (status) => {
    axios.post("/api/internal/assay/" + assay.id + "/status", {
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
                      (assay.status === "IN_PLANNING" || assay.status === "NEEDS_ATTENTION") && (
                            <Dropdown.Item onClick={() => handleStatusChange("ACTIVE")}>
                              <FontAwesomeIcon icon={faPersonRunning} className={"me-2"}/>
                              Set status to 'Active'
                            </Dropdown.Item>
                        )
                    }

                    {
                        assay.status === "ACTIVE" && (
                            <Dropdown.Item onClick={() => handleStatusChange("COMPLETE")}>
                              <FontAwesomeIcon icon={faSquareCheck} className={"me-2"}/>
                              Set status to 'Complete'
                            </Dropdown.Item>
                        )
                    }

                    {
                        assay.status === "COMPLETE" && (
                            <Dropdown.Item onClick={() => handleStatusChange("NEEDS_ATTENTION")}>
                              <FontAwesomeIcon icon={faTriangleExclamation} className={"me-2"}/>
                              Set status to 'Needs attention'
                            </Dropdown.Item>
                        )
                    }

                  </Dropdown.Menu>
                </Dropdown>
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

AssayQuickActionsWidget.propTypes = {
  assay: PropTypes.object.isRequired,
}

export default AssayQuickActionsWidget;
