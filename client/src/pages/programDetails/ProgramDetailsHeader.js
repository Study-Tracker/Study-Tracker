import {Col, Row} from "react-bootstrap";
import {
  ProgramStatusButton,
  SelectableProgramStatusButton
} from "./programStatus";
import React from "react";
import PropTypes from "prop-types";

const ProgramDetailHeader = ({program, user}) => {
  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h3>Program {program.name} ({program.code})</h3>
        </Col>
        <Col className="col-auto d-flex">
          {
            user && user.admin
                ? <SelectableProgramStatusButton active={program.active}
                                                 programId={program.id}/>
                : <ProgramStatusButton active={program.active}/>

          }

        </Col>
      </Row>
  );
};

ProgramDetailHeader.propTypes = {
  program: PropTypes.object.isRequired,
  user: PropTypes.object.isRequired
}

export default ProgramDetailHeader;