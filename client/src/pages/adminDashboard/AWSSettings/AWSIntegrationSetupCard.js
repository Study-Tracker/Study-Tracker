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

const AWSIntegrationSetupCard = ({}) => {
  return (
      <Card className="illustration">
        <Card.Header>
          <Card.Title tag={"h5"} className={"mb-0"}>
            AWS integration is <span className={"text-secondary"}>DISABLED</span>
          </Card.Title>
        </Card.Header>
        <Card.Body>
          <Row>
            <Col className={"text-center"}>
              <Button size="lg" color={"primary"} onClick={() => console.log("Click")}>
                Register AWS Integration
              </Button>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

AWSIntegrationSetupCard.propTypes = {

}

export default AWSIntegrationSetupCard;