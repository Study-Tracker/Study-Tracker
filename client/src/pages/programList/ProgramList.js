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
import {Col, Container, Row,} from "react-bootstrap";
import PropTypes from "prop-types";
import ProgramSummaryCard from "./ProgramSummaryCard";

const ProgramList = ({programs}) => {

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center mb-2">
          <Col>
            <h3>Programs</h3>
          </Col>
        </Row>

        {/*<Row>*/}
        {/*  <Col lg={12}>*/}
        {/*    <Card>*/}
        {/*      <Card.Body>*/}
        {/*        <ProgramListTable programs={programs} />*/}
        {/*      </Card.Body>*/}
        {/*    </Card>*/}
        {/*  </Col>*/}
        {/*</Row>*/}

        <Row>
          {
            programs.map(p => {
              return <ProgramSummaryCard program={p} key={p.id}/>;
            })
          }
        </Row>

      </Container>
  );

}

ProgramList.propTypes = {
  programs: PropTypes.array.isRequired
}

export default ProgramList;