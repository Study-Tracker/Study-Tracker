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

import React, {useEffect} from "react";
import {Button, Dropdown, Form, InputGroup, Nav, Navbar} from "react-bootstrap";
import {LogIn, LogOut, Search, Settings, User} from "react-feather";
import {Formik} from "formik";
import {useNavigate} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {setSignedInUser} from "../../redux/userSlice";
import {toggleSidebar} from "../../redux/sidebarSlice";
import {setFeatures} from "../../redux/featuresSlice";
import axios from "axios";

const NavBar = props => {

  const navigate = useNavigate();
  const dispatch = useDispatch();
  const user = useSelector(state => state.user.value);
  const features = useSelector(state => state.features.value);

  useEffect(() => {

    // Get the signed-in user
    axios.get("/auth/user")
    .then(response => {
      if (response.data.user) {
        dispatch(setSignedInUser(response.data.user));
      }
    });

    axios.get("/api/config/features")
    .then(response => {
      dispatch(setFeatures(response.data));
    })
    .catch(error => {
      console.error(error);
    })

  }, []);

  const {hideToggle} = props;
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

        {
          features && features.search && features.search.isEnabled
            ? (
                  <Formik
                      initialValues={{q: ''}}
                      onSubmit={(values => {
                        console.log("Searching for: " + values.q);
                        navigate("/search?q=" + values.q);
                      })}
                  >
                    {({
                      handleSubmit,
                      handleChange,
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
            ) : ""
        }

        <Navbar.Collapse>
          <Nav className="navbar-align">

            {
              user
                  ? (
                      <NavbarUser
                          isAdmin={user.admin}
                          userId={user.id}
                          displayName={user.displayName}
                          logoutUrl={
                            features && features.auth && features.auth.logoutUrl
                                ? features.auth.logoutUrl : "/logout"
                          }
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

const NavbarUser = ({isAdmin, userId, displayName, logoutUrl}) => {
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

          <Dropdown.Item as={"a"} href={"/user/" + userId}>
            <User size={18} className="align-middle me-2"/>
            Profile
          </Dropdown.Item>

          <Dropdown.Item as={"a"} href={logoutUrl}>
            <LogOut size={18}
                    className="align-middle me-2"/>
            Sign out
          </Dropdown.Item>

        </Dropdown.Menu>
      </Dropdown>
  )
}

export default NavBar;