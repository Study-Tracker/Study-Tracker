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
import NotebookEntryTemplatesDropdown from "./NotebookEntryTemplateDropdown";
import PropTypes from "prop-types";

const NotebookInputs = ({
    isActive,
    onChange,
    selectedProgram
}) => {

  const notebookNodeRef = useRef(null);
  const [cardClass, setCardClass] = React.useState(isActive ? "slide-in-down-exit" : "slide-in-down-enter");
  console.debug("Program: ", selectedProgram);

  return (
    <Card className={"form-card " + (isActive ? "" : "illustration")}>
      <Card.Body>

        <Row>

          <Col sm={12}>
            <h5 className={"card-title"}>Electronic Laboratory Notebook (ELN)</h5>
            <h6 className={"card-subtitle text-muted"}>
              Studies that require an electronic notebook will have a folder created within the
              appropriate program folder of the integrated ELN system. Studies will
              also have a summary notebook entry created for them, either from the
              selected template, or as a blank entry.
            </h6>
          </Col>

          <Col sm={12} className={"feature-toggle"}>
            <FormGroup>
              <Form.Check
                type={"switch"}
                label={"Does this study need an electronic notebook?"}
                onChange={() => onChange("useNotebook", !isActive)}
                defaultChecked={isActive}
              />
            </FormGroup>
          </Col>

          <CSSTransition
            nodeRef={notebookNodeRef}
            in={isActive}
            timeout={300}
            classNames={"slide-in-down"}
          >
            <Col sm={12}
                 ref={notebookNodeRef}
                 className={cardClass}
            >
              <Row>
                <Col md={6}>
                    <NotebookEntryTemplatesDropdown
                        onChange={selected =>
                            onChange("notebookTemplateId", selected || "")
                        }
                    />
                </Col>
              </Row>
            </Col>
          </CSSTransition>

        </Row>

      </Card.Body>
    </Card>
  );
}

NotebookInputs.propTypes = {
  isActive: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired,
  selectedProgram: PropTypes.string
}

export default NotebookInputs;