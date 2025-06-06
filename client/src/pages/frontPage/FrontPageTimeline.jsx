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

import React from "react";
import {Card, Col, Container, Row} from "react-bootstrap";
import {ActivityStream} from "../../common/activity";
import {
  ActiveStudies,
  CompletedStudies,
  NewStudies,
  TotalStudies,
  WelcomeBack
} from './timelineWidgets';
import {ArrowLeft, ArrowRight} from "react-feather";
import FrontPagePlaceholder from "./FrontPagePlaceholder";

const FrontPageTimeline = ({
  activity,
  stats,
  userStats,
  user,
  pageNumber,
  pageSize,
  hasNextPage,
  hasPreviousPage
}) => {

  // let activityCount = stats.activityCount || 0;
  // let activeUsers = stats.activeUserCount || 0;
  let newStudies = stats.newStudyCount || 0;
  let completedStudies = stats.completedStudyCount || 0;
  let totalStudies = stats.studyCount || 0;

  return (

      <Container fluid className="animated fadeIn">

        <Row className="mb-2 mb-xl-3">

          <Col xs="8" className="d-none d-sm-block">
            <h3>Latest Activity</h3>
          </Col>

        </Row>

        <Row>

          <Col lg={4}>

            <Row className="study-statistics">

              <Col xs={12} sm={6} md={6} lg={12} className="d-flex">
                <WelcomeBack/>
              </Col>

              <Col xs={12} sm={6} md={4} lg={12} className="d-flex">
                <ActiveStudies count={userStats.activeStudyCount}/>
              </Col>

              <Col xs={12} sm={6}md={4} lg={12} className="d-flex">
                <NewStudies count={newStudies}/>
              </Col>

              <Col xs={12} sm={6} md={4} lg={12} className="d-flex">
                <CompletedStudies count={completedStudies}
                                  label={"Completed Studies This Month"}/>
              </Col>

              <Col xs={12} sm={6} md={4} lg={12} className="d-flex">
                <TotalStudies count={totalStudies}/>
              </Col>

            </Row>

          </Col>

          <Col lg={8}>
            {
              activity.length > 0 ? (
                  <Card>
                    <Card.Body>
                      <Row>

                        <Col xs={12}>
                          <ActivityStream activity={activity}/>
                        </Col>

                        <Col xs={12}>
                          <hr/>
                        </Col>

                        <Col xs="auto" className="d-none d-sm-block">
                          {
                            !!hasPreviousPage
                                ? <a
                                    href={"/?size=" + pageSize + "&page=" + (pageNumber
                                        - 1)} className="btn btn-primary">
                                  <ArrowLeft
                                      className="feather align-middle me-2"/> Previous
                                  Page
                                </a>
                                : ''
                          }
                        </Col>

                        <Col xs="auto" className="ms-auto text-end mt-n1">
                          {
                            !!hasNextPage
                                ? <a
                                    href={"/?size=" + pageSize + "&page=" + (pageNumber
                                        + 1)} className="btn btn-primary float-end">
                                  Next Page <ArrowRight
                                    className="feather align-middle me-2"/>
                                </a>
                                : ''
                          }
                        </Col>

                      </Row>
                    </Card.Body>
                  </Card>
              ) : <FrontPagePlaceholder isAdmin={user.admin}/>
            }

          </Col>

        </Row>

      </Container>
  );

};

export default FrontPageTimeline;