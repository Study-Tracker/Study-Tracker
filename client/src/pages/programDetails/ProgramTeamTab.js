/*
 * Copyright 2019-2024 the original author or authors.
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
import TeamMembersList from "../../common/users/TeamMemberList";
import PropTypes from "prop-types";

const ProgramTeamTab = ({ program }) => {
  return (
      <Card>
        <Card.Body>
          <Row>
            <Col>
              <h5>All Team Members</h5>
              <TeamMembersList users={program.users} owner={program.owner} />
            </Col>
          </Row>
        </Card.Body>
      </Card>
  );
};

ProgramTeamTab.propTypes = {
  program: PropTypes.object.isRequired,
};

export default ProgramTeamTab;