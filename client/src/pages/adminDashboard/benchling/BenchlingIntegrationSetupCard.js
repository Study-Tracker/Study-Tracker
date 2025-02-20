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

import {Button, Card, Col, Row} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";

const BenchlingIntegrationSetupCard = ({handleClick}) => {
  return (
      <Card className="illustration">
        <Card.Body>
          <Row>
            <Col>
              <p>
                Connect Study Tracker with Benchling to enable workflow automation
                in your electronic laboratory notebook (ELN). Study Tracker
                will create folders in linked projects for new studies & assays
                and allow users to create notebook entries using pre-existing
                templates. You can connect Benchling using user credentials or
                Benchling Apps.
              </p>
            </Col>
          </Row>
          <Row>
            <Col className={"text-center"}>
              <Button size="lg" color={"primary"} onClick={handleClick}>
                Register Benchling Integration
              </Button>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

BenchlingIntegrationSetupCard.propTypes = {
  handleClick: PropTypes.func.isRequired
}

export default BenchlingIntegrationSetupCard;