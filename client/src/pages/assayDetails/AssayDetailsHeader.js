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

import {Button, Col, Dropdown, Row} from "react-bootstrap";
import SelectableStatusButton
  from "../../common/detailsPage/SelectableStatusButton";
import React from "react";
import PropTypes from "prop-types";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBars, faEdit, faTrash} from "@fortawesome/free-solid-svg-icons";
import {useNavigate} from "react-router-dom";

const AssayDetailsHeader = ({assay, study, handleDelete}) => {

  const navigate = useNavigate();

  return (
      <Row className="justify-content-between align-items-center">

        <Col>
          {/*<h5 className="text-muted">{assay.assayType.name} Assay</h5>*/}
          <h3>{assay.name}</h3>
          {/*<h4>{assay.code}</h4>*/}
        </Col>

        <Col xs={"auto"} className={"d-flex"}>

          {
            !assay.active
                ? (
                    <Button className="me-1 mb-1" variant="danger" disabled>
                      Inactive Assay
                    </Button>
                ) : ''
          }

          {/* Status button */}
          <SelectableStatusButton status={assay.status}
                                          assayId={assay.id}/>

          {/* Controls */}
          <Dropdown className="ms-1 mb-1">

            <Dropdown.Toggle variant="outline-secondary">
              <FontAwesomeIcon icon={faBars} className={"me-2"} />
            </Dropdown.Toggle>

            <Dropdown.Menu>

              <Dropdown.Item onClick={() => navigate("/study/" + study.code +
                  "/assay/" + assay.code + "/edit")}>
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

AssayDetailsHeader.propTypes = {
  assay: PropTypes.object.isRequired,
  study: PropTypes.object.isRequired,
  handleDelete: PropTypes.func.isRequired
}

export default AssayDetailsHeader;