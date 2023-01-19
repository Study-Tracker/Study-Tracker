/*
 * Copyright 2023 the original author or authors.
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
import {Card, Col, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import React from "react";

const IllustrationWidget = ({header, body, image, color}) => {

  let colorClass = "illustration";
  if (color === "dark") colorClass = "illustration-dark";
  else if (color === "light") colorClass = "illustration-text";

  return (
      <Card className={"flex-fill " + colorClass}>
        <Card.Body className="p-0 d-flex flex-fill">
          <Row className="g-0 w-100">

            <Col xs={6}>
              <div className="illustration-text p-3 m-1">
                <h4 className="illustration-text">
                  {header}
                </h4>
                <p className="mb-0">
                  {body}
                </p>
              </div>
            </Col>

            <Col xs={6} className="align-self-end text-end">
              <img
                  src={image}
                  className="img-fluid illustration-img"
                  alt={header}
              />
            </Col>

          </Row>
        </Card.Body>
      </Card>
  )
}

IllustrationWidget.propTypes = {
  header: PropTypes.string.isRequired,
  body: PropTypes.any,
  image: PropTypes.string.isRequired,
  color: PropTypes.string
}

export default IllustrationWidget;
