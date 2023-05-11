import React from 'react';
import PropTypes from 'prop-types';
import {Badge, Card, Col, Row} from "react-bootstrap";
import TeamMembers from "../../common/detailsPage/TeamMembers";
import {Clipboard} from "react-feather";

const createMarkup = (content) => {
  return {__html: content};
};

const ProgramSummaryCard = ({ program }) => {

  return (
      <Col xs={12} sm={6} lg={4}>
        <Card>
          <Card.Header>
            <Card.Title>{program.name}</Card.Title>
            {
              program.active
                  ? <Badge color="success">Active</Badge>
                  : <Badge color="danger">Inactive</Badge>
            }
          </Card.Header>
          <Card.Body>
            <Row>
              <Col>
                <div dangerouslySetInnerHTML={createMarkup(program.description)}/>
              </Col>
            </Row>
            <Row>
              <Col>
                <TeamMembers users={program.users} />
              </Col>
            </Row>
            <div className={"d-flex align-items-start"}>
              <div className={"flex-grow-1"}>
                <h3 className="mb-2">{program.studies.length}</h3>
                <p className="mb-2">Total studies</p>
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