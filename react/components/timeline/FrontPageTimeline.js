import React from 'react';
import {Button, Card, CardBody, Col, Container, Row} from "reactstrap";
import {Timeline} from "../activity";
import {
  ActiveUsers,
  CompletedStudies,
  NewStudies,
  StudyUpdates,
  TotalStudies
} from './timelineWidgets';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import {ArrowLeft, ArrowRight} from "react-feather";

const FrontPageTimeline = ({activity, stats, user, pageNumber, pageSize, hasNextPage, hasPreviousPage}) => {

  let activityCount = stats.activityCount || 0;
  let activeUsers = stats.activeUserCount || 0;
  let newStudies = stats.newStudyCount || 0;
  let completedStudies = stats.completedStudyCount || 0;
  let totalStudies = stats.studyCount || 0;

  return (

      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center">
          <Col xs="8">
            <h1>Latest Activity</h1>
          </Col>
          <Col className="col-auto">
            {
              !!user
                  ? (
                      <a href="/studies/new">
                        <Button color="primary" className="mr-1 mb-1">
                          <FontAwesomeIcon icon={faPlusCircle}/> New Study
                        </Button>
                      </a>
                  ) : ''
            }
          </Col>
        </Row>

        <Row>

          <Col lg={3}>

            <Row className="study-statistics">

              <Col xs={6} md={4} lg={12}>
                <StudyUpdates count={activityCount}
                              label={"Updates This Week"}/>
              </Col>

              <Col xs={6} sm={4} md={3} lg={12}>
                <ActiveUsers count={activeUsers}/>
              </Col>

              <Col xs={6} sm={4} md={3} lg={12}>
                <NewStudies count={newStudies} label={"New Studies This Week"}/>
              </Col>

              <Col xs={6} sm={4} md={3} lg={12}>
                <CompletedStudies count={completedStudies}
                                  label={"Completed Studies This Month"}/>
              </Col>

              <Col xs={6} sm={4} md={3} lg={12}>
                <TotalStudies count={totalStudies}/>
              </Col>

            </Row>

          </Col>

          <Col lg={9}>
            <Card>
              <CardBody>
                <Row>

                  <Col xs={12}>
                    <Timeline activities={activity}/>
                  </Col>

                  <Col xs={12}>
                    <hr/>
                  </Col>

                  <Col xs={12}>
                    {
                      !!hasPreviousPage
                          ? <a
                              href={"/?size=" + pageSize + "&page=" + (pageNumber
                                  - 1)} className="btn btn-primary">
                            <ArrowLeft
                                className="feather align-middle mr-2"/> Previous
                            Page
                          </a>
                          : ''
                    }
                    {
                      !!hasNextPage
                          ? <a
                              href={"/?size=" + pageSize + "&page=" + (pageNumber
                                  + 1)} className="btn btn-primary float-right">
                            Next Page <ArrowRight
                              className="feather align-middle mr-2"/>
                          </a>
                          : ''
                    }
                  </Col>

                </Row>
              </CardBody>
            </Card>
          </Col>

        </Row>

      </Container>
  );

};

export default FrontPageTimeline;