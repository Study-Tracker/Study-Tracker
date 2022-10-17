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

import React, {useRef} from "react";
import {Card, Col, Form, Row} from "react-bootstrap";
import {FormGroup} from "./common";
import {CSSTransition} from "react-transition-group";
import PropTypes from "prop-types";

const GitInputs = ({
    isActive,
    onChange,
    selectedProgram
}) => {

  const nodeRef = useRef(null);
  const [cardClass, setCardClass] = React.useState(isActive ? "slide-in-down-exit" : "slide-in-down-enter");
  console.debug("Program: ", selectedProgram);

  return (
    <Card className={"form-card " + (isActive ? "" : "illustration")}>
      <Card.Body>

        <Row>

          <Col sm={12}>
            <h5 className={"card-title"}>Git Repository</h5>
            <h6 className={"card-subtitle text-muted"}>
              Computational studies that require versioned file management may have a Git repository
              created within the linked source code management system. If enabled, an empty repository
              will be created and linked to the study.
            </h6>
          </Col>

          <Col sm={12} className={"feature-toggle"}>
            <FormGroup>
              <Form.Check
                type={"switch"}
                label={"Does this study need a Git repository?"}
                onChange={() => onChange("useGit", !isActive)}
                defaultChecked={isActive}
              />
            </FormGroup>
          </Col>

          <CSSTransition
            nodeRef={nodeRef}
            in={isActive}
            timeout={300}
            classNames={"slide-in-down"}
          >
            <Col sm={12}
               ref={nodeRef}
               className={cardClass}
            >
              <Row>
                <Col md={6}>
                    Things will go here.
                </Col>
              </Row>
            </Col>
          </CSSTransition>

        </Row>

      </Card.Body>
    </Card>
  );
}

GitInputs.propTypes = {
  isActive: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired,
  selectedProgram: PropTypes.string
}

export default GitInputs;