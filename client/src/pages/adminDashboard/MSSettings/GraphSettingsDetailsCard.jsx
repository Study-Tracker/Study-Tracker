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
import PropTypes from "prop-types";
import {Card, Col, Row} from "react-bootstrap";

const GraphSettingsDetailsCard = ({settings}) => {
  return (
      <Card>
        <Card.Body>

          <Row>

            <Col md={6}>
              <h6 className="details-label">Name</h6>
              <p>{settings.name}</p>
            </Col>

            <Col md={6}>
              <h6 className="details-label">Domain</h6>
              <p>
                {settings.domain || 'n/a'}
              </p>
            </Col>

            <Col md={6}>
              <h6 className="details-label">Tenant ID</h6>
              <p>{settings.tenantId}</p>
            </Col>

            <Col md={6}>
              <h6 className="details-label">Client ID</h6>
              <p>
                {settings.clientId}
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

GraphSettingsDetailsCard.propTypes = {
  settings: PropTypes.object.isRequired
}

export default GraphSettingsDetailsCard;