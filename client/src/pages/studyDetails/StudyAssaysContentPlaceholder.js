/*
 * Copyright 2022 the original author or authors.
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

const StudyAssaysContentPlaceholder = ({handleClick}) => {
  return (
      <Card className="illustration-light flex-fill">
        <Card.Body>
          <Row>
            <Col sm={12} className={"d-flex justify-content-center"}>
              <div className={"text-center"}>
                <h1 className="display-6 illustration-text text-center mb-4">No available assays</h1>
                <p className="text-lg">
                  <Button
                      color={"primary"}
                      onClick={handleClick}
                      className={"ps-5 pe-5"}
                  >
                    Add an assay
                  </Button>
                </p>
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

StudyAssaysContentPlaceholder.propTypes = {
  handleClick: PropTypes.func.isRequired
}

export default StudyAssaysContentPlaceholder;