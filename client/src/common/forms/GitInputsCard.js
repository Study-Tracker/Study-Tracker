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

import React from "react";
import {Col, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import FeatureToggleCard from "./FeatureToggleCard";

const GitInputsCard = ({
    isActive,
    onChange,
    selectedProgram
}) => {

  console.debug("Program: ", selectedProgram);

  return (
      <FeatureToggleCard
          isActive={isActive}
          title={"Git Repository"}
          description={"Computational studies that require versioned file management may have a Git repository "
              + "created within the linked source code management system. If enabled, an empty repository "
              + "will be created and linked to the study."}
          switchLabel={"Does this study need a Git repository?"}
          handleToggle={() => onChange("useGit", !isActive)}
      >

        <Row>
          <Col md={12}>
            <p>
              A Git repository will be created in the selected program's group on your Git server and linked on the study details page.
            </p>
          </Col>
        </Row>

      </FeatureToggleCard>
  );
}

GitInputsCard.propTypes = {
  isActive: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired,
  selectedProgram: PropTypes.string
}

export default GitInputsCard;