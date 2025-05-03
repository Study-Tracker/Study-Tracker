/*
 * Copyright 2019-2025 the original author or authors.
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