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
import {Card, Col, Row, Spinner} from "react-bootstrap";

const LoadingMessageWidget = ({message}) => {
  return (
      <Card className={"flex-fill illustration-light"}>
        <Card.Body className="p-0 d-flex flex-fill">
          <Row className="g-0 w-100">
            <Col className={"d-flex justify-content-center"}>
              <div className={"align-self-center"}>
                <h1 className={"pt-5 pb-5 d-flex align-items-center"}>
                  <Spinner animation="border" variant={'primary'} className="me-2"/>
                  <span>Loading...</span>
                </h1>
                {
                  message && (<p>{message}</p>)
                }
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

LoadingMessageWidget.propTypes = {
  message: PropTypes.string
}

export default LoadingMessageWidget;