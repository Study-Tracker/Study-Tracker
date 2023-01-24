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
import {Card, Col, Row} from "react-bootstrap";
import {useSelector} from "react-redux";

const EgnyteEnabledContent = () => {
  return (
      <Row>
        <Col>
          <h3>Egnyte integration is <span className={"text-success"}>ENABLED</span></h3>
        </Col>
      </Row>
  )
}

const EgnyteDisabledContent = () => {
  return (
      <Row>
        <Col>
          <h3>Egnyte integration is <span className={"text-secondary"}>DISABLED</span></h3>
          <p>
            To enable Egnyte integration, please contact your administrator.
          </p>
        </Col>
      </Row>
  )
}

const EgnyteIntegrationSettings = () => {

  const features = useSelector(s => s.features.value);

  return (
      <Card>
        <Card.Header>
          <Card.Title tag={"h5"} className={"mb-0"}>
            Egnyte
          </Card.Title>
        </Card.Header>
        <Card.Body>
          {
            features
              && features.storage
              && features.storage.mode === "egnyte"
              ? <EgnyteEnabledContent />
              : <EgnyteDisabledContent />
          }
        </Card.Body>
      </Card>
  )
}

export default EgnyteIntegrationSettings;