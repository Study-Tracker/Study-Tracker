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
import {Button, Col, Media, Row} from "reactstrap";
import {StatusIcon} from "./status";
import {history} from '../App';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSignInAlt} from "@fortawesome/free-solid-svg-icons";
import {StudyTeam} from "./studyMetadata";

const StudySummaryCard = ({study}) => {
  return (
      <Media className="assay-card">

        <StatusIcon status={study.status}/>

        <Media body>

          <Row>
            <Col xs={12}>

              <span className="float-right">
                <h5>{study.program.name}</h5>
              </span>

              <h6>Study {study.code}</h6>
              <h4>{study.name}</h4>

            </Col>
          </Row>

          <Row>

            <Col xs={12}>
              <h6 className="details-label">Description</h6>
              <p>{study.description}</p>
            </Col>

          </Row>

          <Row className="mt-2">

            <Col sm={4}>
              <h6 className="details-label">Start Date</h6>
              <p>
                {new Date(study.startDate).toLocaleDateString()}
              </p>
            </Col>

            {
              !!study.endDate ? (
                  <Col sm={4}>
                    <span>
                      <h6 className="details-label">End Date</h6>
                      <p>
                        {new Date(study.endDate).toLocaleDateString()}
                      </p>
                    </span>
                  </Col>
              ) : ''
            }

            <Col sm={4}>
              <h6 className="details-label">Last Updated</h6>
              <p>
                {new Date(study.updatedAt).toLocaleDateString()}
              </p>
            </Col>

          </Row>

          <Row className="mt-2">

            <Col xs={12} sm={6}>
              <h6 className="details-label">Study Team</h6>
              <StudyTeam users={study.users} owner={study.owner}/>
            </Col>

          </Row>

          <Row className="mt-2">
            <Col>
              <Button outline size="md" color="primary"
                      onClick={() => history.push("/study/" + study.code)}>
                Details
                &nbsp;
                <FontAwesomeIcon icon={faSignInAlt}/>
              </Button>
            </Col>
          </Row>

        </Media>
      </Media>
  );
};

export const StudySummaryCards = ({studies}) => {
  let content = [];
  if (studies.length === 0) {
    content.push(<hr key={"study-border"}/>);
    content.push(
        <Row className="text-center" key={"no-study-message"}>
          <Col>
            <h4>No studies found</h4>
          </Col>
        </Row>
    );
  } else {
    studies.forEach(study => {
      content.push(<hr key={"study-border-" + study.id}/>);
      content.push(
          <StudySummaryCard
              key={"study-card-" + study.id}
              study={study}
          />
      );
    });
  }

  return (
      <div>
        {content}
      </div>
  );
}