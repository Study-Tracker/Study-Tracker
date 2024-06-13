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

import React from "react";
import PropTypes from "prop-types";
import {Card, Col, Row} from "react-bootstrap";
import ProgramSummaryTimelineCard from "./ProgramSummaryTimelineCard";
import PrimaryStorageFolderWidget
  from "../../common/widgets/PrimaryStorageFolderWidget";
import PrimaryNotebookWidget from "../../common/widgets/PrimaryNotebookWidget";
import TeamMembersCompact from "../../common/users/TeamMembersCompact";

const createMarkup = (content) => {
  return {__html: content};
};

const ProgramOverviewTab = ({program}) => {

  return (
      <Row>

        <Col md={8}>

          <Row>
            <Col xs={12}>
              {/* Summary Card */}
              <Card className="details-card">
                <Card.Body>

                  <Row>

                    <Col xs={12}>
                      <div className={"card-title h5"}>Summary</div>
                    </Col>

                    <Col md={12}>
                      <div dangerouslySetInnerHTML={createMarkup(program.description)} />
                    </Col>

                  </Row>

                  <Row>

                    <Col sm={4}>
                      <h6 className="details-label">Name</h6>
                      <p>{program.name}</p>
                    </Col>

                    <Col sm={4}>
                      <h6 className="details-label">Code</h6>
                      <p>{program.code}</p>
                    </Col>

                    <Col sm={4}>
                      <h6 className="details-label">Created</h6>
                      <p>{new Date(program.createdAt).toLocaleString()}</p>
                    </Col>

                    <Col md={12}>
                      <h6 className="details-label">Program Team</h6>
                      <TeamMembersCompact
                          users={program.users || []}
                          owner={program.owner}
                      />
                    </Col>

                  </Row>

                </Card.Body>
              </Card>
            </Col>

            <Col sm={6} className={"d-flex"}>
              <PrimaryStorageFolderWidget record={program} />
            </Col>

            <Col sm={6} className={"d-flex"}>
              <PrimaryNotebookWidget record={program} />
            </Col>

          </Row>

        </Col>

        <Col md={4}>
          <ProgramSummaryTimelineCard program={program} />
        </Col>

      </Row>
  )

}

ProgramOverviewTab.propTypes = {
  program: PropTypes.object.isRequired
}

export default ProgramOverviewTab;
