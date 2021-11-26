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
  Card,
  Col,
  Container,
  Dropdown,
  Nav,
  Row,
  Tab
} from "react-bootstrap";
import React from "react";
import {Menu} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-solid-svg-icons";
import {SelectableUserStatusButton, UserStatusButton} from "./userStatus";
import UserTimelineTab from "./UserTimelineTab";
import UserStudiesTab from "./UserStudiesTab";

const UserDetailHeader = ({targetUser, user}) => {
  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h3>{targetUser.displayName} ({targetUser.username})</h3>
        </Col>
        <Col xs="auto">
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
                <Breadcrumb.Item href={"/users"}>Users</Breadcrumb.Item>
                <Breadcrumb.Item active>User Detail</Breadcrumb.Item>
              </Breadcrumb>
            </Col>
          </Row>

          {/* Header */}
          <UserDetailHeader targetUser={targetUser} user={this.props.user}/>

          <Row>

            <Col lg={5}>
              <Card className="details-card">

                <Card.Header>

                  {
                    !!this.props.user && !!this.props.user.admin ? (
                      <div className="card-actions float-end">
                        <Dropdown align="end">
                          <Dropdown.Toggle as="a" bsPrefix={"-"}>
                            <Menu/>
                          </Dropdown.Toggle>
                          <Dropdown.Menu>
                            <Dropdown.Item href={"/users/" + targetUser.id + "/edit"}>
                              <FontAwesomeIcon icon={faEdit}/>
                              &nbsp;
                              Edit
                            </Dropdown.Item>
                          </Dropdown.Menu>
                        </Dropdown>
                      </div>
                    ) : ''
                  }

                  <Card.Title tag="h5" className="mb-0 text-muted">
                    Summary
                  </Card.Title>

                </Card.Header>

                <Card.Body>
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
                      <p>{targetUser.department || 'n/a'}</p>

                      <h6 className="details-label">Title</h6>
                      <p>{targetUser.title || 'n/a'}</p>

                    </Col>
                  </Row>
                </Card.Body>

              </Card>
            </Col>

            <Col lg={7}>

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
                      <Nav.Link eventKey={"studies"}>
                        Studies
                      </Nav.Link>
                    </Nav.Item>

                  </Nav>

                  <Tab.Content>

                    <Tab.Pane eventKey={"timeline"}>
                      <UserTimelineTab targetUser={targetUser}
                                       user={this.props.user}/>
                    </Tab.Pane>

                    <Tab.Pane eventKey={"studies"}>
                      <UserStudiesTab studies={studies}
                                      user={this.props.user}/>
                    </Tab.Pane>

                  </Tab.Content>

                </Tab.Container>
              </div>
            </Col>
          </Row>
        </Container>
    );
  }

}

export default UserDetails;