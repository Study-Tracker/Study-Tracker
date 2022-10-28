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

import {useNavigate} from "react-router-dom";
import {Button, Col, Dropdown, Row} from "react-bootstrap";
import React from "react";
import {SelectableStatusButton} from "../../common/status";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faBars,
  faEdit,
  faFolderPlus,
  faTrash
} from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";

const StudyDetailHeader = ({
  study,
  handleAddToCollection,
  handleDelete
}) => {

  const navigate = useNavigate();

  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h3>{study.name}</h3>
          <h5 className="text-muted">{study.code}</h5>
        </Col>
        <Col className="col-auto d-flex">
          {
            study.collaborator
                ? (
                    <React.Fragment>
                      <Button
                          className="me-1 mb-1"
                          variant="outline-info"
                          disabled
                      >
                        External Study
                      </Button>
                      &nbsp;&nbsp;
                    </React.Fragment>
                ) : ''
          }
          {
            !study.active
                ? <Button
                    className="me-1 mb-1"
                    variant="outline-danger"
                    disabled
                >
                  Inactive Study
                </Button>
                : ''
          }
          {
            study.legacy
                ? <Button
                    className="me-1 mb-1"
                    variant="outline-warning"
                    disabled
                >
                  Legacy Study
                </Button>
                : ''
          }

          {/* Status button */}
          <SelectableStatusButton status={study.status} studyId={study.id}/>

          {/* Controls  */}
          <Dropdown className="me-1 mb-1">
            <Dropdown.Toggle variant={"outline-secondary"}>
              <FontAwesomeIcon icon={faBars} className={"me-2"}/>
            </Dropdown.Toggle>
            <Dropdown.Menu>

              <Dropdown.Item onClick={handleAddToCollection}>
                <FontAwesomeIcon icon={faFolderPlus} className={"me-2"}/>
                Add to Collection
              </Dropdown.Item>

              <Dropdown.Divider/>

              <Dropdown.Item onClick={() => navigate("/study/" + study.code + "/edit")}>
                <FontAwesomeIcon icon={faEdit} className={"me-2"}/>
                Edit
              </Dropdown.Item>

              <Dropdown.Item onClick={handleDelete}>
                <FontAwesomeIcon icon={faTrash} className={"me-2"}/>
                Remove
              </Dropdown.Item>

            </Dropdown.Menu>
          </Dropdown>

        </Col>
      </Row>
  );
};

StudyDetailHeader.propTypes = {
  study: PropTypes.object.isRequired,
  handleAddToCollection: PropTypes.func.isRequired,
  handleDelete: PropTypes.func.isRequired
};

export default StudyDetailHeader;