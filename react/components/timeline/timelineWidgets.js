import React from 'react';
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

export const WelcomeBack = ({}) => {
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
                <br />
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

export const MyActiveStudies =({count}) => {

  return (
      <Card className="illustration flex-fill">
        <Card.Body className="p-0 d-flex flex-fill">
          <Row className="g-0 w-100">
            <Col xs="6">
              <div className="illustration-text p-3 m-1">
                <a href={"/studies?myStudy=true&status=IN_PLANNING,ACTIVE"}>
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

export const MyCompleteStudies =({count}) => {

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

const IllustrationWidget = ({header, text, image}) => {
  return (
      <Card className="flex-fill illustration">
        <Card.Body className="p-0 d-flex flex-fill">
          <Row className="g-0 w-100">

            <Col xs={6}>
              <div className="illustration-text p-3 m-1">
                <h4 className="illustration-text">
                  {header}
                </h4>
                <p className="mb-0">
                  {text}
                </p>
              </div>
            </Col>

            <Col xs={6} className="align-self-end text-end">
              <img
                  src={image}
                  className="img-fluid illustration-img"
              />
            </Col>

          </Row>
        </Card.Body>
      </Card>
  )
}

const StatWidget = ({label, value, icon: Icon, color, url}) => {
  return (
      <Card className="flex-fill">
        <Card.Body className="py-4">

          <div className="d-flex align-items-start">

            <div className="flex-grow-1">
              {
                !!url ? (
                    <a href={url}>
                      <h3 className="mb-2">{value}</h3>
                      <p className="mb-2">{label}</p>
                    </a>
                ) : (
                    <React.Fragment>
                      <h3 className="mb-2">{value}</h3>
                      <p className="mb-2">{label}</p>
                    </React.Fragment>
                )
              }
            </div>

            <div className="d-inline-block ms-3">
              <div className={"stat stat-" + color}>
                <Icon className={"align-md feather-lg "}/>
              </div>
            </div>

          </div>

        </Card.Body>
      </Card>
  )
}

export const ActiveStudies = ({count}) => {
  return <StatWidget
      label={"My active studies"}
      icon={ChevronsRight}
      value={count}
      color={"info"}
      url={"/studies?myStudy=true&status=IN_PLANNING,ACTIVE"}
  />
}

export const StudyUpdates = ({count}) => {
  return <StatWidget label={"Updates this week"} icon={Activity} value={count} color={"warning"} />
}

export const ActiveUsers = ({count}) => {
  return <StatWidget label="Active Users" value={count} icon={Users} />
}

export const NewStudies = ({count}) => {
  return <StatWidget label={"New studies this week"} icon={Star} value={count} color={"warning"} />
}

export const CompletedStudies = ({count}) => {
  return <StatWidget label={"Completed studies this month"} icon={ThumbsUp} value={count} color={"success"} />
}

export const TotalStudies = ({count}) => {
  return <StatWidget label={"Total studies"} icon={Clipboard} value={count} />
}