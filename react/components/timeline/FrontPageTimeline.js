import React from 'react';
import {Card, Col, Container, Row} from "react-bootstrap";
import {Timeline} from "../activity";
import {
  ActiveStudies,
  CompletedStudies,
  NewStudies,
  TotalStudies,
  WelcomeBack
} from './timelineWidgets';
import {ArrowLeft, ArrowRight} from "react-feather";

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

  let activityCount = stats.activityCount || 0;
  let activeUsers = stats.activeUserCount || 0;
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

              <Col xs={12} sm={6} md={4} lg={12} className="d-flex">
                <WelcomeBack />
              </Col>

              <Col xs={12} sm={6} md={4} lg={12} className="d-flex">
                <ActiveStudies count={userStats.activeStudyCount} />
              </Col>

              <Col xs={12} sm={6} sm={4} md={3} lg={12} className="d-flex">
                <NewStudies count={newStudies} />
              </Col>

              <Col xs={12} sm={6} sm={4} md={3} lg={12} className="d-flex">
                <CompletedStudies count={completedStudies}
                                  label={"Completed Studies This Month"}/>
              </Col>

              <Col xs={12} sm={6} sm={4} md={3} lg={12} className="d-flex">
                <TotalStudies count={totalStudies}/>
              </Col>

            </Row>

          </Col>

          <Col lg={8}>
            <Card>
              <Card.Body>
                <Row>

                  <Col xs={12}>
                    <Timeline activities={activity}/>
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
          </Col>

        </Row>

      </Container>
  );

};

export default FrontPageTimeline;