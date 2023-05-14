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

import {Card, Col, Row} from "react-bootstrap";
import KeywordBadges from "../../common/detailsPage/KeywordBadges";
import TeamMembers from "../../common/detailsPage/TeamMembers";
import Collaborator from "./Collaborator";
import ExternalLinks from "../../common/externalLinks";
import StudyRelationships from "../../common/studyRelationships";
import StudySummaryTimelineCard from "./StudySummaryTimelineCard";
import React from "react";
import PropTypes from "prop-types";
import StudyQuickActionsWidget
  from "../../common/widgets/StudyQuickActionsWidget";
import PrimaryStorageFolderWidget
  from "../../common/widgets/PrimaryStorageFolderWidget";
import PrimaryNotebookWidget from "../../common/widgets/PrimaryNotebookWidget";
import GitRepositoryWidget from "../../common/widgets/GitRepositoryWidget";

const createMarkup = (content) => {
  return {__html: content};
};

const StudyOverviewTab = ({
  study,
  user,
  features,
  handleAddToCollection,
}) => {

  return (
      <Row>

        <Col md={8}>

          <Row>

            <Col xs={12}>

              {/*Summary Card*/}
              <Card className="details-card">
                <Card.Body>

                  <Row>

                    <Col xs={12}>
                      <div className={"card-title h5"}>Summary</div>
                    </Col>

                    <Col md={12}>
                      <div dangerouslySetInnerHTML={createMarkup(study.description)}/>
                      <KeywordBadges keywords={study.keywords} />
                    </Col>

                  </Row>

                  <Row>

                    <Col sm={4}>
                      <h6 className="details-label">Program</h6>
                      <p>{study.program.name}</p>
                    </Col>

                    <Col sm={4}>
                      <h6 className="details-label">Code</h6>
                      <p>{study.code}</p>
                    </Col>

                    {
                        study.externalCode && (
                            <Col sm={4}>
                              <h6 className="details-label">External Code</h6>
                              <p>{study.externalCode}</p>
                            </Col>
                        )
                    }

                  </Row>

                  <Row>

                    <Col sm={4}>
                      <h6 className="details-label">Created</h6>
                      <p>{new Date(study.createdAt).toLocaleString()}</p>
                    </Col>

                    <Col sm={4}>
                      <h6 className="details-label">Start Date</h6>
                      <p>{new Date(study.startDate).toLocaleString()}</p>
                    </Col>

                    {
                      !!study.endDate
                          ? (
                              <Col sm={4}>
                                <h6 className="details-label">End Date</h6>
                                <p>{new Date(study.endDate).toLocaleString()}</p>
                              </Col>
                          ) : ""
                    }

                    <Col md={12}>
                      <h6 className="details-label">Study Team</h6>
                      <TeamMembers owner={study.owner} users={study.users} />
                    </Col>

                  </Row>

                  {
                      study.collaborator && (
                          <Row>
                            <Col xs={12}>
                              <Collaborator
                                  collaborator={study.collaborator}
                                  externalCode={study.externalCode}
                              />
                            </Col>
                          </Row>
                      )
                  }

                </Card.Body>

              </Card> {/* End Summary Card */}

            </Col>

            <Col sm={6} className={"d-flex"}>
              <PrimaryStorageFolderWidget record={study} />
            </Col>

            <Col sm={6} className={"d-flex"}>
              <PrimaryNotebookWidget record={study} />
            </Col>

            {
              features && features.git.isEnabled && (
                    <Col sm={6} className={"d-flex"}>
                      <GitRepositoryWidget record={study} />
                    </Col>
              )
            }

            <Col xs={12}>

              {/*External links Card*/}
              <Card>
                <Card.Body>
                  <Row>
                    <Col xs={12}>
                      <ExternalLinks
                          links={study.externalLinks || []}
                          studyCode={study.code}
                          user={user}
                      />
                    </Col>
                  </Row>
                </Card.Body>
              </Card> {/* End External Links Card */}

            </Col>

            <Col xs={12}>

              {/*Study relationships card*/}
              <Card>
                <Card.Body>
                  <Row>
                    <Col xs={12}>
                      <StudyRelationships
                          relationships={study.studyRelationships}
                          studyCode={study.code}
                          user={user}
                      />
                    </Col>
                  </Row>
                </Card.Body>
              </Card> {/* End Study Relationships Card */}

            </Col>

          </Row>

        </Col>

        <Col md={4}>

          <StudyQuickActionsWidget
              study={study}
              handleAddToCollection={handleAddToCollection}
          />

          <StudySummaryTimelineCard study={study} />

        </Col>

      </Row>
  );

}

StudyOverviewTab.propTypes = {
  study: PropTypes.object.isRequired,
  user: PropTypes.object.isRequired,
  features: PropTypes.object.isRequired
}

export default StudyOverviewTab;