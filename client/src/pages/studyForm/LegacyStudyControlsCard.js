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
import {FormGroup} from "../../common/forms/common";
import {CSSTransition} from "react-transition-group";

const LegacyStudyControlsCard = ({
  study,
  values,
  errors,
  onChange
}) => {

  const nodeRef = useRef(null);

  return (
      <Card className={"form-card " + (values.legacy ? "" : "illustration")}>

        <Card.Body>
          <Row>

            <Col md={12}>

              <h5 className="card-title">Legacy Study</h5>

              <h6 className="card-subtitle text-muted">
                Studies created prior to the introduction of Study Tracker are
                considered legacy. Enabling this option allows you to
                specify certain attributes that would otherwise be
                automatically generated.
              </h6>

            </Col>

            <Col md={12} className={"feature-toggle"}>

              <FormGroup>
                <Form.Check
                    id="legacy-check"
                    type="switch"
                    label="Is this a legacy study?"
                    onChange={e => onChange("legacy", e.target.checked)}
                    disabled={!!study}
                    defaultChecked={!!study && !!values.legacy}
                />
              </FormGroup>
            </Col>

            <CSSTransition
                nodeRef={nodeRef}
                in={values.legacy}
                timeout={300}
                classNames={"slide-in-down"}
            >

              <Col
                  md={12}
                  ref={nodeRef}
                  className={"slide-in-down-enter"}
              >

                <Row>

                  <Col md={6}>
                    <FormGroup>
                      <Form.Label>Study Code *</Form.Label>
                      <Form.Control
                          type="text"
                          isInvalid={!!errors.code}
                          disabled={!!study}
                          name={"code"}
                          value={values.code}
                          onChange={(e) => onChange("code", e.target.value)}
                      />
                      <Form.Control.Feedback type={"invalid"}>
                        {errors.code}
                      </Form.Control.Feedback>
                      <Form.Text>
                        Provide the existing code or ID
                        for the study.
                      </Form.Text>
                    </FormGroup>
                  </Col>

                </Row>

              </Col>

            </CSSTransition>

          </Row>
        </Card.Body>
      </Card>
  )

}

LegacyStudyControlsCard.propTypes = {
  study: PropTypes.object.isRequired,
  values: PropTypes.object.isRequired,
  errors: PropTypes.object,
  onChange: PropTypes.func.isRequired
}

export default LegacyStudyControlsCard;