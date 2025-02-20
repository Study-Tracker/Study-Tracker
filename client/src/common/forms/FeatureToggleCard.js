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

import React, {useRef} from "react";
import PropTypes from "prop-types";
import {Card, Col, Form, Row} from "react-bootstrap";
import {FormGroup} from "./common";
import {CSSTransition} from "react-transition-group";

const FeatureToggleCard = props => {

  const {
    isActive,
    isDisabled,
    title,
    description,
    switchLabel,
    handleToggle
  } = props;
  const nodeRef = useRef(null);
  const cardClass = isActive ? "slide-in-down-exit" : "slide-in-down-enter";

  return (
      <Card className={"form-card " + (isActive ? "" : "illustration")}>
        <Card.Body>
          <Row>

            <Col sm={12}>
              <h5 className={"card-title"}>{title}</h5>
              {
                description && (
                    <h6 className={"card-subtitle text-muted"}>
                      {description}
                    </h6>
                  )
              }
            </Col>

            <Col sm={12} className={"feature-toggle"}>

              <FormGroup>
                <Form.Check
                  type={"switch"}
                  label={switchLabel}
                  onChange={(e) => handleToggle(e.target.checked)}
                  defaultChecked={isActive}
                  disabled={isDisabled}
                />
              </FormGroup>

              <CSSTransition
                  nodeRef={nodeRef}
                  in={isActive}
                  timeout={300}
                  classNames={"slide-in-down"}
              >
                <Col
                    md={12}
                    ref={nodeRef}
                    className={cardClass}
                >
                  {props.children}
                </Col>
              </CSSTransition>

            </Col>

          </Row>
        </Card.Body>
      </Card>
  )
}

FeatureToggleCard.propTypes = {
  isActive: PropTypes.bool.isRequired,
  isDisabled: PropTypes.bool,
  title: PropTypes.string.isRequired,
  description: PropTypes.string,
  switchLabel: PropTypes.string.isRequired,
  handleToggle: PropTypes.func.isRequired
}

export default FeatureToggleCard;