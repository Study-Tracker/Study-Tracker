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
  BreadcrumbItem,
  Button,
  Card,
  CardBody,
  CardHeader,
  CardTitle,
  Col,
  Container,
  DropdownItem,
  DropdownMenu,
  DropdownToggle,
  Nav,
  NavItem,
  NavLink,
  Row,
  TabContent,
  TabPane,
  UncontrolledDropdown
} from "reactstrap";
import {SelectableStatusButton, StatusButton} from "../status";
import React from "react";
import {Book, Folder, Menu} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faShare, faTrash} from "@fortawesome/free-solid-svg-icons";
import {history} from "../../App";
import {StudyCollaborator, StudyKeywords, StudyTeam} from "../studyMetadata";
import ExternalLinks from "../externalLinks";
import StudyRelationships from "../studyRelationships";
import StudyAssaysTab from "./StudyAssaysTab";
import StudyFilesTab from "./StudyFilesTab";
import StudyConclusionsTab from "./StudyConclusionsTab";
import StudyCommentsTab from "./StudyCommentsTab";
import StudyTimelineTab from "./StudyTimelineTab";

const StudyDetailHeader = ({study, user}) => {
  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h1>Study {study.code}</h1>
        </Col>
        <Col className="col-auto">
          {
            !!study.collaborator
                ? <Button
                    size="lg"
                    className="mr-1 mb-1"
                    color="info">
                  External Study
                </Button>
                : ''
          }
          {
            !study.active ? <Button size="lg" className="mr-1 mb-1"
                                    color="danger">Innactive Study</Button> : ''
          }
          {
            study.legacy ? <Button size="lg" className="mr-1 mb-1"
                                   color="warning">Legacy Study</Button> : ''
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

class StudyDetails extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      activeTab: "1"
    };
  }

  toggle(tab) {
    if (this.state.activeTab !== tab) {
      this.setState({
        activeTab: tab
      });
    }
  }

  render() {

    const study = this.props.study;
    const createMarkup = (content) => {
      return {__html: content};
    };

    return (
        <Container fluid className="animated fadeIn">

          {/* Breadcrumb */}
          <Row>
            <Col>
              <Breadcrumb>
                <BreadcrumbItem>
                  <a href={"/"}>Home</a>
                </BreadcrumbItem>
                <BreadcrumbItem active>
                  Study Detail
                </BreadcrumbItem>
              </Breadcrumb>
            </Col>
          </Row>

          {/* Header */}
          <StudyDetailHeader study={study} user={this.props.user}/>

          <Row>

            <Col lg={5}>
              <Card className="details-card">

                <CardHeader>
                  <div className="card-actions float-right">
                    <UncontrolledDropdown>
                      <DropdownToggle tag="a">
                        <Menu/>
                      </DropdownToggle>
                      <DropdownMenu right>
                        <DropdownItem onClick={() => console.log("Share!")}>
                          <FontAwesomeIcon icon={faShare}/>
                          &nbsp;
                          Share
                        </DropdownItem>
                        {
                          !!this.props.user ? <DropdownItem divider/> : ''
                        }
                        {
                          !!this.props.user ? (
                              <DropdownItem onClick={() => history.push(
                                  "/study/" + study.code + "/edit")}>
                                <FontAwesomeIcon icon={faEdit}/>
                                &nbsp;
                                Edit
                              </DropdownItem>
                          ) : ''
                        }
                        {
                          !!this.props.user ? (
                              <DropdownItem
                                  onClick={() => console.log("Delete!")}>
                                <FontAwesomeIcon icon={faTrash}/>
                                &nbsp;
                                Delete
                              </DropdownItem>
                          ) : ''
                        }
                      </DropdownMenu>
                    </UncontrolledDropdown>
                  </div>
                  <CardTitle tag="h5" className="mb-0 text-muted">
                    {study.program.name}
                  </CardTitle>
                </CardHeader>

                <CardBody>
                  <Row>
                    <Col xs={12}>

                      {/*<h5 className="text-muted">{study.program.name}</h5>*/}
                      <h3>{study.name}</h3>

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
                </CardBody>

                {
                  !!study.collaborator
                      ? (
                          <CardBody>
                            <Row>
                              <Col xs={12}>
                                <div>
                                  <h6 className="details-label">CRO/Collaborator</h6>
                                  <StudyCollaborator
                                      collaborator={study.collaborator}
                                      externalCode={study.externalCode}
                                  />
                                </div>
                              </Col>
                            </Row>
                          </CardBody>
                      ) : ''
                }

                <CardBody>
                  <Row>
                    <Col xs={12}>
                      <h6 className="details-label">Study Team</h6>
                      <StudyTeam users={study.users} owner={study.owner}/>
                    </Col>
                  </Row>
                </CardBody>

                <CardBody>
                  <Row>
                    <Col xs={12}>
                      <h6 className="details-label">Keywords</h6>
                      <StudyKeywords keywords={study.keywords}/>
                    </Col>
                  </Row>
                </CardBody>

                <CardBody>
                  <Row>
                    <Col xs={12}>

                      <h6 className="details-label">Workspaces</h6>
                      {
                        !!study.storageFolder
                            ? (
                                <a href={study.storageFolder.url}
                                   target="_blank"
                                   className="btn btn-info mt-2 mr-2">
                                  Study Storage Folder
                                  <Folder
                                      className="feather align-middle ml-2 mb-1"/>
                                </a>
                            ) : ''
                      }
                      {
                        !!study.notebookFolder
                            ? (
                                <a href={study.notebookFolder.url}
                                   target="_blank"
                                   className="btn btn-info mt-2 mr-2">
                                  Study ELN Folder
                                  <Book
                                      className="feather align-middle ml-2 mb-1"/>
                                </a>
                            ) : ''
                      }

                    </Col>
                  </Row>
                </CardBody>

                <CardBody>
                  <Row>
                    <Col xs="12">
                      <ExternalLinks
                          links={study.externalLinks || []}
                          studyCode={study.code}
                          user={this.props.user}
                      />
                    </Col>
                  </Row>
                </CardBody>

                <CardBody>
                  <Row>
                    <Col xs={12}>
                      <StudyRelationships
                          relationships={study.studyRelationships}
                          studyCode={study.code}
                          user={this.props.user}
                      />
                    </Col>
                  </Row>
                </CardBody>

              </Card>
            </Col>

            <Col lg="7">

              {/* Tabs */}
              <div className="tab">
                <Nav tabs>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "1" ? "active" : ''}
                        onClick={() => {
                          this.toggle("1");
                        }}
                    >
                      Timeline
                    </NavLink>
                  </NavItem>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "2" ? "active" : ''}
                        onClick={() => {
                          this.toggle("2");
                        }}
                    >
                      Assays
                    </NavLink>
                  </NavItem>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "3" ? "active" : ''}
                        onClick={() => {
                          this.toggle("3");
                        }}
                    >
                      Files
                    </NavLink>
                  </NavItem>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "4" ? "active" : ''}
                        onClick={() => {
                          this.toggle("4");
                        }}
                    >
                      Conclusions
                    </NavLink>
                  </NavItem>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "5" ? "active" : ''}
                        onClick={() => {
                          this.toggle("5");
                        }}
                    >
                      Comments
                    </NavLink>
                  </NavItem>

                </Nav>

                <TabContent activeTab={this.state.activeTab}>

                  {/* Assay Tab */}
                  <TabPane tabId="1">
                    <StudyTimelineTab study={study} user={this.props.user}/>
                  </TabPane>

                  <TabPane tabId="2">
                    <StudyAssaysTab study={study} user={this.props.user}/>
                  </TabPane>

                  <TabPane tabId="3">
                    <StudyFilesTab study={study} user={this.props.user}/>
                  </TabPane>

                  <TabPane tabId="4">
                    <StudyConclusionsTab study={study} user={this.props.user}/>
                  </TabPane>

                  <TabPane tabId="5">
                    <StudyCommentsTab study={study} user={this.props.user}/>
                  </TabPane>

                </TabContent>
              </div>
            </Col>
          </Row>
        </Container>
    );
  }

}

export default StudyDetails;