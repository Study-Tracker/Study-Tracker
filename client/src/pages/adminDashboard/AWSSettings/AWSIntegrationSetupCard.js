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

const AWSIntegrationSetupCard = ({handleClick}) => {
  return (
      <Card className="illustration">
        <Card.Header>
          <Card.Title tag={"h5"} className={"mb-0"}>
            AWS integration is <span className={"text-secondary"}>DISABLED</span>
          </Card.Title>
        </Card.Header>
        <Card.Body>
          <Row>
            <Col>
              <p>
                Connect Study Tracker with Amazon Web Services to enable file
                management in S3 and event publishing in EventBridge. Study
                Tracker uses the AWS SDK to communicate with services and can
                authenticate using either a secret access key or IAM (when running
                on an EC2 instance).
              </p>
            </Col>
          </Row>
          <Row>
            <Col className={"text-center"}>
              <Button size="lg" color={"primary"} onClick={handleClick}>
                Register AWS Integration
              </Button>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

AWSIntegrationSetupCard.propTypes = {
  handleClick: PropTypes.func.isRequired
}

export default AWSIntegrationSetupCard;