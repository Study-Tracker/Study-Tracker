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

import React from 'react';
import PropTypes from 'prop-types';
import {Badge, Card, Col, Row} from "react-bootstrap";
import TeamMembers from "../../common/detailsPage/TeamMembers";
import {Clipboard} from "react-feather";
import {useNavigate} from "react-router-dom";

const ProgramSummaryCard = ({ program }) => {

  const navigate = useNavigate();

  const cleanupDescription = (str) => {
    const cleaned = str.replace(/<[^>]*>?/gm, '');
    return cleaned.length > 255 ? cleaned.substring(0, 255) + "..." : cleaned;
  }

  return (
      <Col xs={12} sm={6} lg={4}>
        <Card
            onClick={() => navigate("/program/" + program.id)}
            className={"program-card"}
        >
          <Card.Header>
            <Card.Title>{program.name}</Card.Title>
            {
              program.active
                  ? <Badge bg="success">Active</Badge>
                  : <Badge bg="danger">Inactive</Badge>
            }
          </Card.Header>
          <Card.Body>
            <Row>
              <Col>
                {cleanupDescription(program.description)}
              </Col>
            </Row>
            <Row>
              <Col>
                <TeamMembers users={program.users} />
              </Col>
            </Row>
            <div className={"d-flex align-items-start"}>
              <div className={"flex-grow-1"}>
                <a href={"/studies?program=" + program.id}>
                  <h3 className="mb-2">{program.studies.length}</h3>
                  <p className="mb-2">Total studies</p>
                </a>
              </div>
              <div className={"d-inline-block ms-3"}>
                <div className={"stat"}>
                  <Clipboard className={"align-md feather-lg"} />
                </div>
              </div>
            </div>
          </Card.Body>
        </Card>
      </Col>
  )
}

ProgramSummaryCard.propTypes = {
  program: PropTypes.object.isRequired
}

export default ProgramSummaryCard;