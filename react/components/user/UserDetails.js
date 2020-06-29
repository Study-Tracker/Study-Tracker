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
import {SelectableUserStatusButton, UserStatusButton} from "./userStatus";
import UserTimelineTab from "./UserTimelineTab";
import UserStudiesTab from "./UserStudiesTab";

const UserDetailHeader = ({targetUser, user}) => {
  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h1>{targetUser.displayName} ({targetUser.accountName})</h1>
        </Col>
        <Col className="col-auto">
          {
            !!user && !!user.admin
                ? <SelectableUserStatusButton active={targetUser.active}
                                              userId={targetUser.id}/>
                : <UserStatusButton active={targetUser.active}/>

          }

        </Col>
      </Row>
  );
};

class UserDetails extends React.Component {

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

    const {targetUser, studies} = this.props;

    return (
        <Container fluid className="animated fadeIn">

          {/* Breadcrumb */}
          <Row>
            <Col>
              <Breadcrumb>
                <BreadcrumbItem>
                  <a href={"/users"}>Users</a>
                </BreadcrumbItem>
                <BreadcrumbItem active>
                  User Detail
                </BreadcrumbItem>
              </Breadcrumb>
            </Col>
          </Row>

          {/* Header */}
          <UserDetailHeader targetUser={targetUser} user={this.props.user}/>

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
                          !!this.props.target && !!this.props.user.admin ?
                              <DropdownItem divider/> : ''
                        }
                        {
                          !!this.props.user && !!this.props.user.admin ? (
                              <DropdownItem onClick={() => history.push(
                                  "/user/" + targetUser.id + "/edit")}>
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
                    User
                  </CardTitle>
                </CardHeader>

                <CardBody>
                  <Row>
                    <Col xs={12}>

                      <h3>{targetUser.displayName}</h3>

                      <h6 className="details-label">Email</h6>
                      <p>
                        <a href={"mailto:" + targetUser.email}>
                          {targetUser.email}
                        </a>
                      </p>

                      <h6 className="details-label">Department</h6>
                      <p>{targetUser.department}</p>

                      <h6 className="details-label">Title</h6>
                      <p>{targetUser.title}</p>

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

                </Nav>

                <TabContent activeTab={this.state.activeTab}>

                  <TabPane tabId="1">
                    <UserTimelineTab targetUser={targetUser}
                                     user={this.props.user}/>
                  </TabPane>

                  <TabPane tabId="2">
                    <UserStudiesTab studies={studies}
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

export default UserDetails;