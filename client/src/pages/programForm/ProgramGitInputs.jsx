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
import {Col, Form, Row} from "react-bootstrap";
import {FormGroup} from "../../common/forms/common";
import Select from "react-select";
import {DismissableAlert} from "../../common/errors";
import FeatureToggleCard from "../../common/forms/FeatureToggleCard";

const ProgramGitInputs = ({
  isActive,
  gitGroups,
  selectedGroup,
  defaultGroup,
  onChange,
  error
}) => {

  const options = gitGroups ? gitGroups
    .sort((a, b) => a.displayName.localeCompare(b.displayName))
    .map(p => ({
      label: p.displayName + " (" + p.gitServiceType + ")",
      value: p
    })) : [];

  console.debug("Available Git Groups: ", gitGroups);
  console.debug("Selected Git Group: ", selectedGroup);

  return (
      <FeatureToggleCard
          isActive={isActive}
          title={"Git Repository Group"}
          description={"Select a Git project group in which to optionally create source code repositories for studies. This can be added later."}
          switchLabel={"Does this program need a Git project group?"}
          handleToggle={() => onChange("useGit", !isActive)}
      >
        {
          gitGroups && gitGroups.length > 0
              ? (
                  <Row>
                    <Col md={6} className={"mb-3"}>
                      <FormGroup>
                        <Form.Label>Git Project Group *</Form.Label>
                        <Select
                            className="react-select-container"
                            classNamePrefix="react-select"
                            options={options}
                            value={options.filter(option => {
                              if (defaultGroup) return defaultGroup.id === option.value.id;
                              else return selectedGroup && option.value.id === selectedGroup.id
                            })}
                            name="gitGroup"
                            onChange={(selected) => {
                              onChange("gitGroup", selected.value);
                            }}
                        />
                        <Form.Control.Feedback type={"invalid"}>
                          {error}
                        </Form.Control.Feedback>
                      </FormGroup>
                    </Col>
                  </Row>
              )
              : (
                  <Row>
                    <Col>
                      <DismissableAlert
                          color={"warning"}
                          header={"No project groups have been configured."}
                          message={"Project groups can be added by administrators in the admin console."}

                      />
                    </Col>
                  </Row>
              )
        }
      </FeatureToggleCard>
  )
}

ProgramGitInputs.propTypes = {
  isActive: PropTypes.bool.isRequired,
  gitGroups: PropTypes.arrayOf(PropTypes.object).isRequired,
  onChange: PropTypes.func.isRequired,
  error: PropTypes.string
}

export default ProgramGitInputs;
