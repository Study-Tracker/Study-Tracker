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
import React from "react";
import {Menu} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faShare, faTrash} from "@fortawesome/free-solid-svg-icons";
import {history} from "../../App";
import {
  ProgramStatusButton,
  SelectableProgramStatusButton
} from "./programStatus";
import {ProgramKeywords, ProgramTeam} from "./programMetadata";
import ProgramTimelineTab from "./ProgramTimelineTab";
import ProgramStudiesTab from "./ProgramStudiesTab";
import ProgramCalendarTab from "./ProgramCalendarTab";

const ProgramDetailHeader = ({program, user}) => {
  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h1>Program {program.name} ({program.code})</h1>
        </Col>
        <Col className="col-auto">
          {
            !!user && !!user.admin
                ? <SelectableProgramStatusButton active={program.active}
                                                 programId={program.id}/>
                : <ProgramStatusButton active={program.active}/>

          }

        </Col>
      </Row>
  );
};

class ProgramDetails extends React.Component {

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

    const {program, studies} = this.props;
    const createMarkup = (content) => {
      return {__html: content};
    };

    let attributes = [];
    for (let key of Object.keys(program.attributes)) {
      attributes.push(
          <React.Fragment key={'attribute-' + key}>
            <h6 className="details-label">{key}</h6>
            <p>{program.attributes[key]}</p>
          </React.Fragment>
      )
    }

    return (
        <Container fluid className="animated fadeIn">

          {/* Breadcrumb */}
          <Row>
            <Col>
              <Breadcrumb>
                <BreadcrumbItem>
                  <a href={"/programs"}>Programs</a>
                </BreadcrumbItem>
                <BreadcrumbItem active>
                  Program Detail
                </BreadcrumbItem>
              </Breadcrumb>
            </Col>
          </Row>

          {/* Header */}
          <ProgramDetailHeader program={program} user={this.props.user}/>

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
                          !!this.props.user && !!this.props.user.admin ?
                              <DropdownItem divider/> : ''
                        }
                        {
                          !!this.props.user && !!this.props.user.admin ? (
                              <DropdownItem onClick={() => history.push(
                                  "/program/" + program.id + "/edit")}>
                                <FontAwesomeIcon icon={faEdit}/>
                                &nbsp;
                                Edit
                              </DropdownItem>
                          ) : ''
                        }
                        {
                          !!this.props.user && !!this.props.user.admin ? (
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
                    Summary
                  </CardTitle>
                </CardHeader>

                <CardBody>
                  <Row>
                    <Col xs={12}>

                      <h3>{program.name}</h3>

                      <h6 className="details-label">Description</h6>
                      <div dangerouslySetInnerHTML={
                        createMarkup(program.description)
                      }/>

                      <h6 className="details-label">Created By</h6>
                      <p>{program.createdBy.displayName}</p>

                      <h6 className="details-label">Last Updated</h6>
                      <p>{new Date(program.updatedAt).toLocaleString()}</p>

                      {attributes}

                    </Col>
                  </Row>
                </CardBody>

                <CardBody>
                  <Row>
                    <Col xs={12}>
                      <CardTitle>Program Team</CardTitle>
                      <ProgramTeam program={program} studies={studies}/>
                    </Col>
                  </Row>
                </CardBody>

                <CardBody>
                  <Row>
                    <Col xs={12}>
                      <CardTitle>Keywords</CardTitle>
                      <ProgramKeywords studies={studies}/>
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
                      Studies
                    </NavLink>
                  </NavItem>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "3" ? "active" : ''}
                        onClick={() => {
                          this.toggle("3");
                        }}
                    >
                      Calendar
                    </NavLink>
                  </NavItem>

                </Nav>

                <TabContent activeTab={this.state.activeTab}>

                  <TabPane tabId="1">
                    <ProgramTimelineTab program={program}
                                        user={this.props.user}/>
                  </TabPane>

                  <TabPane tabId="2">
                    <ProgramStudiesTab studies={studies}
                                       user={this.props.user}/>
                  </TabPane>

                  <TabPane tabId="3">
                    <ProgramCalendarTab program={program} studies={studies}
                                        user={this.props.user}/>
                  </TabPane>

                </TabContent>
              </div>
            </Col>
          </Row>
        </Container>
    );
  }

}

export default ProgramDetails;