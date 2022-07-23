/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  Breadcrumb,
  Button,
  Card,
  Col,
  Container,
  Dropdown,
  Nav,
  Row,
  Tab
} from 'react-bootstrap';
import {SelectableStatusButton, StatusButton} from "../../common/status";
import React, {useState} from "react";
import {Menu} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faFolderPlus, faTrash} from "@fortawesome/free-solid-svg-icons";
import {
  StudyCollaborator,
  StudyKeywords,
  StudyTeam
} from "../../common/studyMetadata";
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

const StudyDetailHeader = ({study, user}) => {
  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h3>{study.name}</h3>
          <h5 className="text-muted">{study.code}</h5>
        </Col>
        <Col className="col-auto">
          {
            !!study.collaborator
                ? (
                    <React.Fragment>
                      <Button
                          className="me-1 mb-1"
                          variant="outline-info"
                          disabled
                      >
                        External Study
                      </Button>
                      &nbsp;&nbsp;
                    </React.Fragment>
                ) : ''
          }
          {
            !study.active
                ? <Button
                    className="me-1 mb-1"
                    variant="outline-danger"
                    disabled
                >
                  Inactive Study
                </Button>
                : ''
          }
          {
            study.legacy
                ? <Button
                    className="me-1 mb-1"
                    variant="outline-warning"
                    disabled
                >
                  Legacy Study
                </Button>
                : ''
          }
          {
            !!user
                ? <SelectableStatusButton status={study.status}
                                          studyId={study.id}/>
                : <StatusButton status={study.status}/>

          }

        </Col>
      </Row>
  );
};

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
          url: "/api/study/" + study.code,
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
        <StudyDetailHeader study={study} user={user}/>

        <Row>

          <Col lg={5}>
            <Card className="details-card">

              <Card.Header>
                <div className="card-actions float-end">
                  <Dropdown align="end">
                    <Dropdown.Toggle as="a" bsPrefix="-">
                      <Menu/>
                    </Dropdown.Toggle>
                    <Dropdown.Menu>

                      <Dropdown.Item
                          onClick={() => setShowCollectionModal(true)}>
                        <FontAwesomeIcon icon={faFolderPlus}/>
                        &nbsp;
                        Add to Collection
                      </Dropdown.Item>

                      {/*<DropdownItem onClick={() => console.log("Share!")}>*/}
                      {/*  <FontAwesomeIcon icon={faShare}/>*/}
                      {/*  &nbsp;*/}
                      {/*  Share*/}
                      {/*</DropdownItem>*/}

                      <Dropdown.Divider/>

                      <Dropdown.Item onClick={() =>
                          navigate("/study/" + study.code + "/edit")}>
                        <FontAwesomeIcon icon={faEdit}/>
                        &nbsp;
                        Edit
                      </Dropdown.Item>

                      <Dropdown.Item onClick={handleStudyDelete}>
                        <FontAwesomeIcon icon={faTrash}/>
                        &nbsp;
                        Remove
                      </Dropdown.Item>

                    </Dropdown.Menu>
                  </Dropdown>
                </div>

                <Card.Title tag="h5" className="mb-0 text-muted">
                  Summary
                </Card.Title>

              </Card.Header>

              <Card.Body>
                <Row>
                  <Col xs={12}>

                    <h6 className="details-label">Program</h6>
                    <p>{study.program.name}</p>

                    <h6 className="details-label">Code</h6>
                    <p>{study.code}</p>

                    {
                      !!study.externalCode
                          ? (
                              <React.Fragment>
                                <h6 className="details-label">External Code</h6>
                                <p>{study.externalCode}</p>
                              </React.Fragment>
                          ) : ''
                    }

                    <h6 className="details-label">Description</h6>
                    <div dangerouslySetInnerHTML={createMarkup(
                        study.description)}/>

                    <h6 className="details-label">Created By</h6>
                    <p>{study.createdBy.displayName}</p>

                    <h6 className="details-label">Last Updated</h6>
                    <p>{new Date(study.updatedAt).toLocaleString()}</p>

                    <h6 className="details-label">Start Date</h6>
                    <p>{new Date(study.startDate).toLocaleString()}</p>

                    <h6 className="details-label">End Date</h6>
                    <p>
                      {
                        !!study.endDate
                            ? new Date(study.endDate).toLocaleString()
                            : "n/a"
                      }
                    </p>

                  </Col>
                </Row>
              </Card.Body>

              {
                study.collaborator
                    ? (
                        <Card.Body>
                          <Row>
                            <Col xs={12}>
                              <div>
                                <Card.Title>CRO/Collaborator</Card.Title>
                                <StudyCollaborator
                                    collaborator={study.collaborator}
                                    externalCode={study.externalCode}
                                />
                              </div>
                            </Col>
                          </Row>
                        </Card.Body>
                    ) : ''
              }

              <Card.Body>
                <Row>
                  <Col xs={12}>
                    <Card.Title>Study Team</Card.Title>
                    <StudyTeam users={study.users} owner={study.owner}/>
                  </Col>
                </Row>
              </Card.Body>

              <Card.Body>
                <Row>
                  <Col xs={12}>

                    <Card.Title>Workspaces</Card.Title>

                    <RepairableStorageFolderButton
                        folder={study.storageFolder}
                        repairUrl={"/api/study/" + study.id + "/storage/repair"}
                    />

                    &nbsp;&nbsp;

                    {
                      features
                      && features.notebook
                      && features.notebook.isEnabled ? (
                          <RepairableNotebookFolderButton
                              folder={study.notebookFolder}
                              repairUrl={"/api/study/" + study.id + "/notebook/repair"}
                          />
                      ) : ""
                    }

                  </Col>
                </Row>
              </Card.Body>

              <Card.Body>
                <Row>
                  <Col xs={12}>
                    <StudyKeywords keywords={study.keywords} studyId={study.id} />
                  </Col>
                </Row>
              </Card.Body>

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

            </Card>
          </Col>

          <Col lg="7">

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