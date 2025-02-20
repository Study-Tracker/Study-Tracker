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
import {Button, Card, Col, Row} from "react-bootstrap";
import {
  Activity,
  ChevronsRight,
  Clipboard,
  Star,
  ThumbsUp,
  Users
} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";
import StatWidget from "../../common/widgets/StatWidget";

export const WelcomeBack = () => {
  return (
      <Card className="illustration flex-fill">
        <Card.Body className="p-0 d-flex flex-fill">
          <Row className="g-0 w-100">
            <Col xs="6">
              <div className="illustration-text p-3 m-1">
                <h4 className="illustration-text">
                  Welcome back!
                </h4>
                <p className="mb-0">
                  Ready to get started?
                </p>
                <br/>
                <Button variant={"outline-primary"} href={"/studies/new"}>
                  <FontAwesomeIcon icon={faPlus}/> New Study
                </Button>
              </div>
            </Col>
            <Col xs={6} className="align-self-end text-end">
              <img
                  src={"/static/images/clip/innovation.png"}
                  alt="Ready to work"
                  className="img-fluid illustration-img"
              />
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

export const MyActiveStudies = ({count}) => {

  return (
      <Card className="illustration flex-fill">
        <Card.Body className="p-0 d-flex flex-fill">
          <Row className="g-0 w-100">
            <Col xs="6">
              <div className="illustration-text p-3 m-1">
                <a href={"/studies?myStudy=true&status=IN_PLANNING&status=ACTIVE"}>
                  <p className="mb-0">
                    You have
                  </p>
                  <h1 className="illustration-text">{count}</h1>
                  <p className="mb-0">
                    active studies
                  </p>
                </a>
              </div>
            </Col>
            <Col xs={6} className="align-self-end text-end">
              <img
                  src={"/static/images/clip/information-flow-yellow.png"}
                  alt="Ready to work"
                  className="img-fluid illustration-img"
              />
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )

}

MyActiveStudies.propTypes = {
  count: PropTypes.number.isRequired
}

export const MyCompleteStudies = ({count}) => {

  return (
      <Card className="illustration flex-fill">
        <Card.Body className="p-0 d-flex flex-fill">
          <Row className="g-0 w-100">
            <Col xs="6">
              <div className="illustration-text p-3 m-1">
                <p className="mb-0">
                  You have
                </p>
                <h1 className="illustration-text">
                  <a href={"/studies?myStudy=true&status=COMPLETE"}>
                    {count}
                  </a>
                </h1>
                <p className="mb-0">
                  completed studies
                </p>
              </div>
            </Col>
            <Col xs={6} className="align-self-end text-end">
              <img
                  src={"/static/images/clip/success.png"}
                  alt="Ready to work"
                  className="img-fluid illustration-img"
              />
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )

}

MyCompleteStudies.propTypes = {
  count: PropTypes.number.isRequired
}

export const ActiveStudies = ({count}) => {
  return <StatWidget
      label={"My active studies"}
      icon={ChevronsRight}
      value={count}
      color={"info"}
      url={"/studies?myStudy=true&status=IN_PLANNING&status=ACTIVE"}
  />
}

ActiveStudies.propTypes = {
  count: PropTypes.number.isRequired
}

export const StudyUpdates = ({count}) => {
  return <StatWidget label={"Updates this week"} icon={Activity} value={count}
                     color={"warning"}/>
}

StudyUpdates.propTypes = {
  count: PropTypes.number.isRequired
}

export const ActiveUsers = ({count}) => {
  return <StatWidget label="Active Users" value={count} icon={Users}/>
}

ActiveUsers.propTypes = {
  count: PropTypes.number.isRequired
}

export const NewStudies = ({count}) => {
  return <StatWidget label={"New studies this week"} icon={Star} value={count}
                     color={"warning"}/>
}

NewStudies.propTypes = {
  count: PropTypes.number.isRequired
}

export const CompletedStudies = ({count}) => {
  return <StatWidget label={"Completed studies this month"} icon={ThumbsUp}
                     value={count} color={"success"}/>
}

CompletedStudies.propTypes = {
  count: PropTypes.number.isRequired
}

export const TotalStudies = ({count}) => {
  return <StatWidget label={"Total studies"} icon={Clipboard} value={count}/>
}

TotalStudies.propTypes = {
  count: PropTypes.number.isRequired
}