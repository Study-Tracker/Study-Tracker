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
import {
  Col,
  Collapse,
  DropdownItem,
  DropdownMenu,
  DropdownToggle,
  ListGroup,
  ListGroupItem,
  Nav,
  Navbar,
  Row,
  UncontrolledDropdown
} from "reactstrap";
import {toggleSidebar} from "../redux/actions/sidebarActions";
import {HelpCircle, LogIn, LogOut, Settings, User} from "react-feather";
import {connect} from "react-redux";
import {setUser} from '../redux/actions/userActions'

const NavbarDropdown = ({
  children,
  count,
  showBadge,
  header,
  footer,
  icon: Icon
}) => (
    <UncontrolledDropdown nav inNavbar className="mr-2">
      <DropdownToggle nav className="nav-icon dropdown-toggle">
        <div className="position-relative">
          <Icon className="align-middle" size={18}/>
          {showBadge ? <span className="indicator">{count}</span> : null}
        </div>
      </DropdownToggle>
      <DropdownMenu right className="dropdown-menu-lg py-0">
        <div className="dropdown-menu-header position-relative">
          {count} {header}
        </div>
        <ListGroup>{children}</ListGroup>
        <DropdownItem header className="dropdown-menu-footer">
          <span className="text-muted">{footer}</span>
        </DropdownItem>
      </DropdownMenu>
    </UncontrolledDropdown>
);

const NavbarDropdownItem = ({icon, title, description, time, spacing}) => (
    <ListGroupItem>
      <Row noGutters className="align-items-center">
        <Col xs={2}>{icon}</Col>
        <Col xs={10} className={spacing ? "pl-2" : null}>
          <div className="text-dark">{title}</div>
          <div className="text-muted small mt-1">{description}</div>
          <div className="text-muted small mt-1">{time}</div>
        </Col>
      </Row>
    </ListGroupItem>
);

class NavBarComponent extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      user: null
    }
  }

  componentDidMount() {
    fetch("/auth/user")
    .then(response => response.json())
    .then(json => {
      if (!!json.user) {
        this.props.dispatch(setUser(json.user));
        this.setState({
          user: json.user
        });
      }
    });
  }

  render() {

    const {dispatch, hideToggle} = this.props;
    return (
        <Navbar color="white" light expand>

          {
            !hideToggle
                ? (
                    <span
                        className="sidebar-toggle d-flex mr-2"
                        onClick={() => {
                          dispatch(toggleSidebar());
                        }}
                    >
                    <i className="hamburger align-self-center"/>
                  </span>
                ) : ''

          }

          <a href="/" className="navbar-brand mx-auto">Study Tracker</a>

          <Collapse navbar>
            <Nav className="ml-auto" navbar>

              {
                !!this.state.user
                    ? (
                        <UncontrolledDropdown nav inNavbar>

                      <span className="d-inline-block d-sm-none">
                        <DropdownToggle nav caret>
                          <Settings size={18} className="align-middle"/>
                        </DropdownToggle>
                      </span>

                          <span className="d-none d-sm-inline-block">
                        <DropdownToggle nav caret>
                          <User/>
                          <span
                              className="text-dark">{this.state.user.displayName}</span>
                        </DropdownToggle>
                      </span>

                          <DropdownMenu right>

                            {
                              !!this.state.user.admin
                                  ? (
                                      <DropdownItem>
                                        <a href="/admin">
                                          <Settings size={18}
                                                    className="align-middle mr-2"/>
                                          Admin Dashboard
                                        </a>
                                      </DropdownItem>
                                  ) : ''
                            }

                            <DropdownItem>
                              <a href={"/user/" + this.state.user.username}>
                                <User size={18} className="align-middle mr-2"/>
                                Profile
                              </a>
                            </DropdownItem>

                            <DropdownItem>
                              <a href="#">
                                <HelpCircle size={18}
                                            className="align-middle mr-2"/>
                                Help
                              </a>
                            </DropdownItem>

                            <DropdownItem>
                              <a href="/logout">
                                <LogOut size={18}
                                        className="align-middle mr-2"/>
                                Sign out
                              </a>
                            </DropdownItem>

                          </DropdownMenu>
                        </UncontrolledDropdown>
                    ) : (
                        <li>
                          <a href="/login" className="btn btn-info">
                            Sign In <LogIn className="feather align-middle mr-2"/>
                          </a>
                        </li>
                    )

              }

            </Nav>
          </Collapse>
        </Navbar>
    );
  }

}

export default connect(
    store => ({
      app: store.app
    })
)(NavBarComponent);