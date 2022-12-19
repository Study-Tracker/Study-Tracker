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

import {Card, Col, Dropdown, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPersonRunning, faPlusSquare} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import {useNavigate} from "react-router-dom";
import PropTypes from "prop-types";

const AssayQuickActionsWidget = ({
    assay
}) => {

  const navigate = useNavigate();

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
                {/*<p className="mb-0">*/}
                {/*  Jump to the next step in your assay workflow.*/}
                {/*</p>*/}
                <br/>
                <Dropdown className="me-1 mb-1">
                  <Dropdown.Toggle variant={"outline-primary"}>
                    <FontAwesomeIcon icon={faPersonRunning} className={"me-2"}/>
                    Actions
                  </Dropdown.Toggle>
                  <Dropdown.Menu>

                    <Dropdown.Item onClick={() => console.log("Click")}>
                      <FontAwesomeIcon icon={faPlusSquare} className={"me-2"}/>
                      TBD
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

AssayQuickActionsWidget.propTypes = {
  assay: PropTypes.object.isRequired,
}

export default AssayQuickActionsWidget;
