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

import React from "react";
import {Button, Col, Row} from "react-bootstrap";
import {StatusIcon} from "./status";
import {history} from '../App';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSignInAlt} from "@fortawesome/free-solid-svg-icons";
import {StudyTeam} from "./studyMetadata";

const createMarkup = (content) => {
  return {__html: content};
};

const StudySummaryCard = ({study}) => {
  return (
      <div className="d-flex assay-card">

        <div className="stat stat-transparent">
          <StatusIcon status={study.status}/>
        </div>

        <div className="flex-grow-1 ms-3">

          <Row>
            <Col xs={12}>

              <span className="float-end">
                <h5>{study.program.name}</h5>
              </span>

              <h6>Study {study.code}</h6>
              <h4>{study.name}</h4>

            </Col>
          </Row>

          <Row>

            <Col xs={12}>
              <h6 className="details-label">Description</h6>
              <div dangerouslySetInnerHTML={createMarkup(study.description)}/>
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

          {
            !!study.users ? (
                <Row className="mt-2">

                  <Col xs={12} sm={6}>
                    <h6 className="details-label">Study Team</h6>
                    <StudyTeam users={study.users} owner={study.owner}/>
                  </Col>

                </Row>
            ) : ""
          }

          <Row className="mt-2">
            <Col>
              <Button size="md" variant="outline-primary"
                      onClick={() => history.push("/study/" + study.code)}>
                Details
                &nbsp;
                <FontAwesomeIcon icon={faSignInAlt}/>
              </Button>
            </Col>
          </Row>

        </div>
      </div>
  );
};

const StudySlimCard = ({study}) => {
  return (
      <div className="d-flex assay-card">

        <div className="stat stat-transparent">
          <StatusIcon status={study.status}/>
        </div>

        <div className="flex-grow-1 ms-3">

          <Row>

            <Col xs={12}>

              <h6 className="text-muted">{study.program.name}</h6>

              <h4>
                <a href={"/study/" + study.code}>{study.code}: {study.name}</a>
              </h4>

            </Col>

            <Col xs={12}>
              <div dangerouslySetInnerHTML={createMarkup(study.description)}/>
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

        </div>
      </div>
  );
};

export const StudySummaryCards = ({studies, showDetails}) => {
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
    content = studies.sort((a, b) => {
      if (a.updatedAt > b.updatedAt) {
        return -1;
      } else if (a.updatedAt < b.updatedAt) {
        return 1;
      } else {
        return 0;
      }
    }).map((study, i) => {
      let card;
      if (!!showDetails) {
        card = <StudySummaryCard key={"study-card-" + study.id} study={study}/>;
      } else {
        card = <StudySlimCard key={"study-card-" + study.id} study={study}/>;
      }
      return (
          <React.Fragment>
            {i > 0 ? <hr key={"study-border-" + study.id}/> : ''}
            {card}
          </React.Fragment>
      )
    });
  }

  return (
      <div>
        {content}
      </div>
  );
}