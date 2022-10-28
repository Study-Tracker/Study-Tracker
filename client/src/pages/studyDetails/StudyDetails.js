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

import {Breadcrumb, Card, Col, Container, Nav, Row, Tab} from 'react-bootstrap';
import React, {useState} from "react";
import ExternalLinks from "../../common/externalLinks";
import StudyRelationships from "../../common/studyRelationships";
import StudyAssaysTab from "./StudyAssaysTab";
import StudyFilesTab from "./StudyFilesTab";
import StudyNotebookTab from './StudyNotebookTab';
import StudyConclusionsTab from "./StudyConclusionsTab";
import StudyCommentsTab from "./StudyCommentsTab";
import StudyTimelineTab from "./StudyTimelineTab";
import swal from "sweetalert";
import AddToStudyCollectionModal
  from "../../common/modals/AddToStudyCollectionModal";
import StudyCollectionsTab from "./StudyCollectionsTab";
import {RepairableStorageFolderButton} from "../../common/files";
import {RepairableNotebookFolderButton} from "../../common/eln";
import PropTypes from "prop-types";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import StudyDetailHeader from "./StudyDetailsHeader";
import TeamMembers from "../../common/detailsPage/TeamMembers";
import KeywordBadges from "../../common/detailsPage/KeywordBadges";
import StudySummaryTimelineCard from "./StudySummaryTimelineCard";
import Collaborator from "./Collaborator";

const StudyDetails = props => {

  const {study, user, features} = props;
  const [showCollectionModal, setShowCollectionModal] = useState(false);
  const [modalError, setModalError] = useState(null);
  const navigate = useNavigate();

  const handleStudyDelete = () => {
    swal({
      title: "Are you sure you want to remove this study?",
      text: "Removed studies will be hidden from view, but their records will not be deleted. Studies can be recovered in the admin dashboard.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        axios({
          url: "/api/internal/study/" + study.code,
          method: 'delete',
          headers: {
            "Content-Type": "application/json"
          }
        }).then(response => {
          navigate("/studies")
        })
        .catch(error => {
          console.error(error);
          setModalError("Failed to remove study. Please try again.");
        })
      }
    });
  }

  const createMarkup = (content) => {
    return {__html: content};
  };

  return (
      <Container fluid className="animated fadeIn">

        {/* Breadcrumb */}
        <Row>
          <Col>
            <Breadcrumb>
              <Breadcrumb.Item href={"/"}>Home</Breadcrumb.Item>
              <Breadcrumb.Item active>Study Detail</Breadcrumb.Item>
            </Breadcrumb>
          </Col>
        </Row>

        {/* Header */}
        <StudyDetailHeader
            study={study}
            handleAddToCollection={() => setShowCollectionModal(true)}
            handleDelete={handleStudyDelete}
        />

        <Row>

          <Col md={8}>

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

                <Row>
                  <Col xs={12}>
                    <Collaborator
                        collaborator={study.collaborator}
                        externalCode={study.externalCode}
                    />
                  </Col>
                </Row>

              </Card.Body>

            </Card> {/* End Summary Card */}

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

          <Col md={4}>

            <StudySummaryTimelineCard study={study} />

            {/*Workspaces Card*/}
            <Card>
              <Card.Body>
                <Row>
                  <Col xs={12}>

                    <Card.Title>Workspaces</Card.Title>

                    <div className={"d-flex flex-column align-items-center"}>

                      <RepairableStorageFolderButton
                          folder={study.storageFolder}
                          repairUrl={"/api/internal/study/" + study.id + "/storage/repair"}
                      />

                      {
                        features
                        && features.notebook
                        && features.notebook.isEnabled ? (
                            <RepairableNotebookFolderButton
                                folder={study.notebookFolder}
                                repairUrl={"/api/internal/study/" + study.id + "/notebook/repair"}
                            />
                        ) : ""
                      }

                    </div>

                  </Col>
                </Row>
              </Card.Body>
            </Card>

          </Col>

          <Col md={12}>

            {/* Tabs */}
            <div className="tab">
              <Tab.Container defaultActiveKey="timeline">
                <Nav variant="tabs">
                  <Nav.Item>
                    <Nav.Link eventKey={"timeline"}>
                      Timeline
                    </Nav.Link>
                  </Nav.Item>

                  <Nav.Item>
                    <Nav.Link eventKey={"assays"}>
                      Assays
                    </Nav.Link>
                  </Nav.Item>

                  <Nav.Item>
                    <Nav.Link eventKey={"files"}>
                      Files
                    </Nav.Link>
                  </Nav.Item>

                  {
                    study.notebookFolder ? (
                        <Nav.Item>
                          <Nav.Link eventKey={"notebook"}>
                            Notebook
                          </Nav.Link>
                        </Nav.Item>
                    ) : ""
                  }

                  <Nav.Item>
                    <Nav.Link eventKey={"conclusions"}>
                      Conclusions
                    </Nav.Link>
                  </Nav.Item>

                  <Nav.Item>
                    <Nav.Link eventKey={"comments"}>
                      Comments
                    </Nav.Link>
                  </Nav.Item>

                  <Nav.Item>
                    <Nav.Link eventKey={"collections"}>
                      Collections
                    </Nav.Link>
                  </Nav.Item>

                </Nav>

                <Tab.Content>

                  {/* Assay Tab */}
                  <Tab.Pane eventKey={"timeline"}>
                    <StudyTimelineTab study={study} user={user}/>
                  </Tab.Pane>

                  <Tab.Pane eventKey={"assays"}>
                    <StudyAssaysTab study={study} user={user}/>
                  </Tab.Pane>

                  <Tab.Pane eventKey={"files"}>
                    <StudyFilesTab study={study} user={user}/>
                  </Tab.Pane>

                  {
                    features
                    && features.notebook
                    && features.notebook.isEnabled
                    && study.notebookFolder ? (
                        <Tab.Pane eventKey={"notebook"}>
                          <StudyNotebookTab study={study} user={user}/>
                        </Tab.Pane>
                    ) : ""
                  }

                  <Tab.Pane eventKey={"conclusions"}>
                    <StudyConclusionsTab study={study} user={user}/>
                  </Tab.Pane>

                  <Tab.Pane eventKey={"comments"}>
                    <StudyCommentsTab study={study} user={user}/>
                  </Tab.Pane>

                  <Tab.Pane eventKey={"collections"}>
                    <StudyCollectionsTab
                        study={study}
                        showCollectionModal={setShowCollectionModal}
                    />
                  </Tab.Pane>

                </Tab.Content>
              </Tab.Container>
            </div>
          </Col>
        </Row>

        {/* Add to collection modal */}
        <AddToStudyCollectionModal
            showModal={setShowCollectionModal}
            isOpen={showCollectionModal}
            study={study}
        />

      </Container>
  );

}

StudyDetails.propTypes = {
  user: PropTypes.object,
  study: PropTypes.object.isRequired,
  features: PropTypes.object,
}

export default StudyDetails;