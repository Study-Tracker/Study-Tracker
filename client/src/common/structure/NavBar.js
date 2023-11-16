/*
 * Copyright 2019-2023 the original author or authors.
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

import React, {useEffect} from "react";
import {Button, Form, InputGroup, Nav, Navbar} from "react-bootstrap";
import {LogIn, Search} from "react-feather";
import {Formik} from "formik";
import {useNavigate} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {setSignedInUser} from "../../redux/userSlice";
import {toggleSidebar} from "../../redux/sidebarSlice";
import {setFeatures} from "../../redux/featuresSlice";
import axios from "axios";
import NavbarUser from "./NavbarUser";

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

    axios.get("/api/internal/config/features")
    .then(response => {
      dispatch(setFeatures(response.data));
    })
    .catch(error => {
      console.error(error);
    })

  }, [dispatch]);

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
                        height="30"
                        className="d-inline-block align-top"
                        alt="Study Tracker"
                        src={"/static/images/logo-wide.png"}
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
                        navigate(0);
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
                            autoComplete={"off"}
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

export default NavBar;