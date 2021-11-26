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
import {Button, Dropdown, Form, InputGroup, Nav, Navbar} from "react-bootstrap";
import {toggleSidebar} from "../redux/actions/sidebarActions";
import {LogIn, LogOut, Search, Settings, User} from "react-feather";
import {connect} from "react-redux";
import {setUser} from '../redux/actions/userActions'
import {Formik} from "formik";
import {history} from "../App";

class NavBarComponent extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      user: null,
      q: ""
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
        <Navbar variant="light" className="navbar-bg" expand>

          {
            !hideToggle
                ? (
                    <span
                        className="sidebar-toggle d-flex"
                        onClick={() => {
                          dispatch(toggleSidebar());
                        }}
                    >
                    <i className="hamburger align-self-center"/>
                  </span>
                ) : (
                    <Navbar.Brand href={"/"}>
                      <img
                          width="40"
                          height="30"
                          className="d-inline-block align-top"
                          alt="Study Tracker"
                          src={"/static/images/logo-icon.png"}
                      />
                    </Navbar.Brand>
                )

          }

          <Formik
              initialValues={{q: ''}}
              onSubmit={(values => {
                console.log("Searching for: " + values.q);
                history.push("/search?q=" + values.q);
                history.go(0);
              })}
          >
            {({
              handleSubmit,
              handleChange,
              values
            }) => (
                <Form
                    inline="true"
                    className="d-none d-sm-inline-block"
                    onSubmit={handleSubmit}
                >
                  <InputGroup className="input-group-navbar">
                    <Form.Control
                        type="text"
                        name={"q"}
                        placeholder={"Search"}
                        aria-label={"Search"}
                        onChange={handleChange}
                    />
                    <Button type="submit" variant="">
                      <Search className={"feather"}/>
                    </Button>
                  </InputGroup>
                </Form>
            )}

          </Formik>

          <Navbar.Collapse>
            <Nav className="navbar-align">

              {
                !!this.state.user
                    ? (
                        <NavbarUser
                            isAdmin={this.state.user.admin}
                            userName={this.state.user.username}
                            displayName={this.state.user.displayName}
                        />
                    ) : (
                        <li>
                          <a href="/login" className="btn btn-info">
                            Sign In <LogIn className="feather align-middle me-2"/>
                          </a>
                        </li>
                    )

              }

            </Nav>
          </Navbar.Collapse>
        </Navbar>
    );
  }

}

class NavbarSearch extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      "q": ""
    };
  }

  render() {
    return (
        <Formik
            initialValues={{q: ''}}
            onSubmit={(values => {
              history.push("/search?q=" + values.q);
              history.go(0);
            })}
        >
          <Form inline={true} className="d-none d-sm-inline-block" >
            <InputGroup className="input-group-navbar">
              <Form.Control
                  name={"q"}
                  placeholder={"Search"}
                  aria-label={"Search"}
              />
              <Button variant="">
                <Search className={"feather"}/>
              </Button>
            </InputGroup>
          </Form>
        </Formik>
    )
  }

}

const NavbarUser = ({isAdmin, userName, displayName}) => {
  return (
      <Dropdown className="nav-item" align="end">

        <span className="d-inline-block d-sm-none">
          <Dropdown.Toggle as={"a"} className={"nav-link"}>
            <Settings size={18} className="align-middle"/>
          </Dropdown.Toggle>
        </span>

        <span className="d-none d-sm-inline-block">
          <Dropdown.Toggle as={"a"} className={"nav-link"}>
            <User/>
            <span className="text-dark">
              {displayName}
            </span>
          </Dropdown.Toggle>
        </span>

        <Dropdown.Menu drop={"end"}>

          {
            !!isAdmin
                ? (
                    <Dropdown.Item as={"a"} href={"/admin"}>
                        <Settings size={18}
                                  className="align-middle me-2"/>
                        Admin Dashboard
                    </Dropdown.Item>
                ) : ''
          }

          <Dropdown.Item as={"a"} href={"/user/" + userName}>
              <User size={18} className="align-middle me-2"/>
              Profile
          </Dropdown.Item>

          {/*<DropdownItem>*/}
          {/*  <a href="#">*/}
          {/*    <HelpCircle size={18}*/}
          {/*                className="align-middle me-2"/>*/}
          {/*    Help*/}
          {/*  </a>*/}
          {/*</DropdownItem>*/}

          <Dropdown.Item as={"a"} href={"/logout"}>
              <LogOut size={18}
                      className="align-middle me-2"/>
              Sign out
          </Dropdown.Item>

        </Dropdown.Menu>
      </Dropdown>
  )
}

export default connect(
    store => ({
      app: store.app
    })
)(NavBarComponent);