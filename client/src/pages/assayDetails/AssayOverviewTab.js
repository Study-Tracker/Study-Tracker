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
import {Card, Col, Row} from "react-bootstrap";
import TeamMembers from "../../common/detailsPage/TeamMembers";
import AssayQuickActionsWidget
  from "../../common/widgets/AssayQuickActionsWidget";
import SummaryTimelineCard from "../../common/detailsPage/SummaryTimelineCard";
import AssayFieldDataTable from "./AssayFieldDataTable";
import AssayTasksWidget from "./AssayTasksWidget";
import PrimaryStorageFolderWidget
  from "../../common/widgets/PrimaryStorageFolderWidget";
import PrimaryNotebookWidget from "../../common/widgets/PrimaryNotebookWidget";

const createMarkup = (content) => {
  return {__html: content};
};

const AssayOverviewTab = ({
  assay,
  study,
  handleTabSelect
}) => {

  return (
      <Row>

        <Col md={8}>

          <Card className={"details-card"}>
              <Card.Body>

                <Row>

                  <Col xs={12}>
                    <div className={"card-title h5"}>Summary</div>
                  </Col>

                  <Col xs={12}>
                    <div dangerouslySetInnerHTML={createMarkup(assay.description)}/>
                  </Col>

                </Row>

                <Row>

                  <Col sm={4}>
                    <h6 className={"details-label"}>Assay Type</h6>
                    <p>{assay.assayType.name}</p>
                  </Col>

                  <Col sm={4}>
                    <h6 className={"details-label"}>Code</h6>
                    <p>{assay.code}</p>
                  </Col>

                </Row>

                <Row>

                  <Col sm={4}>
                    <h6 className="details-label">Created</h6>
                    <p>{new Date(assay.createdAt).toLocaleString()}</p>
                  </Col>

                  <Col sm={4}>
                    <h6 className="details-label">Start Date</h6>
                    <p>{new Date(assay.startDate).toLocaleString()}</p>
                  </Col>

                  {
                    !!assay.endDate
                        ? (
                            <Col sm={4}>
                              <h6 className="details-label">End Date</h6>
                              <p>{new Date(assay.endDate).toLocaleString()}</p>
                            </Col>
                        ) : ""
                  }

                  <Col md={12}>
                    <h6 className="details-label">Study Team</h6>
                    <TeamMembers owner={assay.owner} users={assay.users} />
                  </Col>

                </Row>

              </Card.Body>
          </Card>

          {/* Assay fields */}
          {
            Object.keys(assay.fields).length > 0
                ? (
                    <Card>
                      <Card.Body>
                        <Row>
                          <Col xs={12}>
                            <Card.Title>{assay.assayType.name} Metadata</Card.Title>
                            <AssayFieldDataTable assay={assay}/>
                          </Col>
                        </Row>
                      </Card.Body>
                    </Card>
                ) : ""
          }

          <Row>

            <Col xs={12} sm={6} className="d-flex">
              <PrimaryStorageFolderWidget record={assay} />
            </Col>

            <Col xs={12} sm={6} className="d-flex">
              <PrimaryNotebookWidget record={assay} />
            </Col>

            <Col xs={12} sm={6} className="d-flex">
              <AssayTasksWidget
                  tasks={assay.tasks}
                  handleClick={() => handleTabSelect("tasks")}
              />
            </Col>

          </Row>

        </Col>

        <Col md={4}>

          <AssayQuickActionsWidget assay={assay} study={study} />

          <SummaryTimelineCard assay={assay} />

        </Col>

      </Row>
  );

};

AssayOverviewTab.propTypes = {
  assay: PropTypes.object.isRequired,
  study: PropTypes.object.isRequired,
  handleTabSelect: PropTypes.func.isRequired
}

export default AssayOverviewTab;
