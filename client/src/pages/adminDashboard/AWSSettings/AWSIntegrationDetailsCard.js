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

import {Badge, Card, Col, Row} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";

const AWSIntegrationDetailsCard = ({settings}) => {
  return (
      <Card>
        <Card.Header>
          <Card.Title tag={"h5"} className={"mb-0"}>
            AWS integration is <span className={"text-success"}>ENABLED</span>
          </Card.Title>
        </Card.Header>
        <Card.Body>

          <Row>

            <Col md={6}>
              <h6 className="details-label">Account Name</h6>
              <p>{settings.name}</p>
            </Col>

            <Col md={6}>
              <h6 className="details-label">Account Number</h6>
              <p>
                {settings.accountNumber || 'N/A'}
              </p>
            </Col>

            <Col md={6}>
              <h6 className="details-label">Region</h6>
              <p>{settings.region}</p>
            </Col>

            <Col md={6}>
              <h6 className="details-label">Authentication</h6>
              <p>
                {
                  settings.useIam
                      ? <Badge color={"info"}>IAM</Badge>
                      : <Badge color={"warning"}>Access Key</Badge>
                }
              </p>
            </Col>

          </Row>

          <Row>

            <Col md={6}>
              <h6 className="details-label">Registered</h6>
              <p>{new Date(settings.createdAt).toLocaleString()}</p>
            </Col>

            <Col md={6}>
              <h6 className="details-label">Last Updated</h6>
              <p>{new Date(settings.updatedAt).toLocaleString()}</p>
            </Col>

          </Row>

        </Card.Body>
      </Card>
  )
}

AWSIntegrationDetailsCard.propTypes = {
  settings: PropTypes.object.isRequired
}

export default AWSIntegrationDetailsCard;