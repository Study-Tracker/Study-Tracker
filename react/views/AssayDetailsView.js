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

import React from 'react';
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import {StudyTeam} from "../components/studyMetadata";
import {
  Breadcrumb,
  BreadcrumbItem,
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
import {Book, Folder, MoreHorizontal} from "react-feather";
import {SelectableStatusButton} from "../components/status";
import {faEdit, faShare, faTrash} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {history} from '../App';
import StandardWrapper from "../structure/StandardWrapper";
import {connect} from 'react-redux';

class AssayDetailsView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      studyCode: props.match.params.studyCode,
      assayCode: props.match.params.assayCode,
      isLoaded: false,
      isError: false
    };
  }

  componentDidMount() {
    fetch("/api/study/" + this.state.studyCode)
    .then(response => response.json())
    .then(async study => {
      fetch("/api/study/" + this.state.studyCode + "/assays/"
          + this.state.assayCode)
      .then(response => response.json())
      .then(assay => {
        this.setState({
          study: study,
          assay: assay,
          isLoaded: true
        });
        console.log(assay);
      })
      .catch(error => {
        console.error(error);
        this.setState({
          isError: true,
          error: error
        });
      });
    })
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    })
  }

  render() {
    let content = <LoadingMessage/>;
    if (this.state.isError) {
      content = <ErrorMessage/>;
    } else if (this.state.isLoaded) {
      content = <AssayDetails study={this.state.study} assay={this.state.assay}
                              user={this.props.user}/>;
    }
    return (
        <StandardWrapper {...this.props}>
          {content}
        </StandardWrapper>
    );
  }

}

const AssayDetailHeader = ({study, assay}) => {
  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h5 className="text-muted">{study.program.name}</h5>
          <h1>{assay.name}</h1>
          <h4>{assay.code}</h4>
        </Col>
        <Col className="col-auto">
          <SelectableStatusButton status={assay.status} assayId={assay.id}/>
        </Col>
      </Row>
  );
};

class AssayDetails extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      activeTab: "1"
    }
  }

  toggle(tab) {
    if (this.state.activeTab !== tab) {
      this.setState({
        activeTab: tab
      });
    }
  }

  render() {

    const assay = this.props.assay;
    const study = this.props.study;

    return (
        <Container fluid className="animated fadeIn">

          <Row>
            <Col>
              <Breadcrumb>

                <BreadcrumbItem>
                  <a href={"/"}>Home</a>
                </BreadcrumbItem>

                <BreadcrumbItem>
                  <a href={"/study/" + study.code}>
                    Study {study.code}
                  </a>
                </BreadcrumbItem>

                <BreadcrumbItem>
                  Assay {assay.code}
                </BreadcrumbItem>

              </Breadcrumb>
            </Col>
          </Row>

          <AssayDetailHeader assay={assay} study={study}/>

          <Row>

            <Col xs={12}>
              <Card>

                <CardHeader>
                  <div className="card-actions float-right">
                    <UncontrolledDropdown>
                      <DropdownToggle tag="a">
                        <MoreHorizontal/>
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
                                  "/study/" + study.code + "/assay/"
                                  + assay.code + "/edit")}>
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
                    Assay Overview
                  </CardTitle>
                </CardHeader>

                <CardBody>

                  <Row>

                    <Col xl="6" xs="12">
                      <Row>

                        <Col xs="12">
                          <h6 className="details-label">Description</h6>
                          <p>{assay.description}</p>
                        </Col>

                        <Col xs={6}>
                          <h6 className="details-label">Created By</h6>
                          <p>{assay.createdBy.displayName}</p>
                        </Col>

                        <Col xs="6">
                          <h6 className="details-label">Last Updated</h6>
                          <p>{new Date(assay.updatedAt).toLocaleString()}</p>
                        </Col>

                        <Col xs="6">
                          <h6 className="details-label">Start Date</h6>
                          <p>{new Date(assay.startDate).toLocaleString()}</p>
                        </Col>

                        <Col xs="6">
                          <h6 className="details-label">End Date</h6>
                          <p>
                            {
                              !!assay.endDate
                                  ? new Date(assay.endDate).toLocaleString()
                                  : "n/a"
                            }
                          </p>
                        </Col>

                      </Row>
                    </Col>

                    <Col xs={12} xl={6}>
                      <Row>

                        <Col md={6} xs={12}>
                          <h6 className="details-label">Assay Team</h6>
                          <StudyTeam users={assay.users} owner={assay.owner}/>
                        </Col>

                        <Col xs={12}>
                          <h6 className="details-label">Workspaces</h6>
                          {
                            !!assay.storageFolder
                                ? (
                                    <a href={assay.storageFolder.url}
                                       target="_blank"
                                       className="btn btn-info mr-2">
                                      {assay.storageFolder.label}
                                      <Folder
                                          className="feather align-middle ml-2 mb-1"/>
                                    </a>
                                ) : ''
                          }
                          {
                            assay.notebookEntries.length > 0
                                ? (
                                    <a href={assay.notebookEntries[0].url}
                                       target="_blank"
                                       className="btn btn-info mr-2">
                                      {assay.notebookEntries[0].label}
                                      <Book
                                          className="feather align-middle ml-2 mb-1"/>
                                    </a>
                                ) : ''
                          }

                        </Col>

                      </Row>
                    </Col>

                  </Row>
                </CardBody>

              </Card>
            </Col>

          </Row>

          <Row>

            <Col xs="12">
              <div className="tab">
                <Nav tabs>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "1" ? "active" : ''}
                        onClick={() => {
                          this.toggle("1");
                        }}
                    >
                      Files
                    </NavLink>
                  </NavItem>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "2" ? "active" : ''}
                        onClick={() => {
                          this.toggle("2");
                        }}
                    >
                      Samples / Animals
                    </NavLink>
                  </NavItem>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "3" ? "active" : ''}
                        onClick={() => {
                          this.toggle("3");
                        }}
                    >
                      Results
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

                </Nav>

                <TabContent activeTab={this.state.activeTab}>

                  <TabPane tabId="1">
                    <Row className="justify-content-between align-items-center">
                      <div className="col-auto">
                        <p className="text-center">
                          Files will go here.
                        </p>
                      </div>
                    </Row>
                  </TabPane>

                  <TabPane tabId="2">
                    <p className="text-center">
                      Samples will go here.
                    </p>
                  </TabPane>

                  <TabPane tabId="3">
                    <p className="text-center">
                      Results will go here.
                    </p>
                  </TabPane>

                  <TabPane tabId="4">
                    <p className="text-center">
                      Conclusions will go here.
                    </p>
                  </TabPane>

                </TabContent>
              </div>
            </Col>
          </Row>
        </Container>
    );
  }

}

export default connect(store => ({
  user: store.user
}))(AssayDetailsView);