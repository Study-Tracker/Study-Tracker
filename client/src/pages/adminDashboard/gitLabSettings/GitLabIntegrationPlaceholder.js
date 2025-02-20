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
import {Button, Card, Col, Row} from "react-bootstrap";
import PropTypes from "prop-types";

const GitLabIntegrationPlaceholder = ({handleClick}) => {
  return (
      <Card className="illustration">
        <Card.Body>
          <Row>
            <Col>
              <p>
                Connect Study Tracker with a GitLab instance to enable source code repository creation for studies.
                Study Tracker uses GitLab's REST API to create groups and repositories. To enable GitLab integration,
                you can authenticate with user credentials to grant Study Tracker access to your entire GitLab instance,
                or with an Access Token to grant access to just a single Group.
              </p>
            </Col>
          </Row>
          <Row>
            <Col className={"text-center"}>
              <Button
                  size="lg"
                  color={"primary"}
                  onClick={handleClick}
              >
                Register GitLab Integration
              </Button>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

GitLabIntegrationPlaceholder.propTypes = {
  handleClick: PropTypes.func.isRequired
}

export default GitLabIntegrationPlaceholder;