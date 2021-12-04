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

import React from "react";

import {Alert, Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import NoNavWrapper from "../structure/NoNavWrapper";

const qs = require('qs');

export default class SignInView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      auth: {},
      options: {}
    };
    this.handleInputChange = this.handleInputChange.bind(this);
  }

  componentDidMount() {
    fetch("/auth/options", {
      headers: {
        "Accept": "application/json"
      }
    })
    .then(response => response.json())
    .then(data => {
      this.setState({
        options: data
      });
    })
    .catch(e => {
      console.error("Failed to fetch authentication options.");
      console.error(e);
    })
  }

  handleInputChange(data) {
    this.setState({
      auth: {
        ...this.state.auth,
        ...data
      }
    });
  }

  render() {

    const params = qs.parse(this.props.location.search,
        {ignoreQueryPrefix: true});
    const isError = params.hasOwnProperty("error");
    const message = params.message || null;

    return (
        <NoNavWrapper>
          <Container fluid className="animated fadeIn">
            <Row className="justify-content-center">
              <Col xs={12} sm={8} md={8} lg={6} xl={4}>

                <div className="text-center mt-4">
                  <h2>Welcome to Study Tracker</h2>
                  <p className="lead">Please sign-in to continue</p>
                </div>

                <Card>
                  <Card.Body>
                    <div className="m-sm-4">

                      <div className="text-center mb-4">
                        {/*<User size={80} className="align-middle me-2"/>*/}
                        <img
                            src={"/static/images/circle-logo-light-blue-gradient.png"}
                            className="img-fluid"
                            width="130"
                            height="130"
                        />
                      </div>

                      <Form action={"/login"} method={"post"}>

                        <Form.Group>
                          <Form.Label>Username or email</Form.Label>
                          <Form.Control
                              size="lg"
                              type="text"
                              name="username"
                              placeholder="Enter your username or email address"
                              onChange={e => this.handleInputChange(
                                  {username: e.target.value})}
                          />
                        </Form.Group>

                        <Form.Group>
                          <Form.Label>Password</Form.Label>
                          <Form.Control
                              size="lg"
                              type="password"
                              name="password"
                              placeholder="Enter your password"
                              onChange={e => this.handleInputChange(
                                  {password: e.target.value})}
                          />
                        </Form.Group>

                        <div className="text-center mt-3">
                          <small>
                            <a href="/auth/passwordresetrequest">Forgot password?</a>
                          </small>
                        </div>

                        {
                          !!message
                              ? (
                                  <div className="text-center mt-3">
                                    <Alert variant="success" className="p-3">
                                      {message}
                                    </Alert>
                                  </div>
                              )
                              : ''
                        }

                        {
                          isError
                              ? (
                                  <div className="text-center mt-3">
                                    <Alert variant="danger" className="p-3">
                                      Failed to sign you in. Please check your
                                      credentials and try again.
                                    </Alert>
                                  </div>
                              )
                              : ''
                        }

                        <div className="text-center mt-3">
                          <Button href={"/"} size="lg" variant="secondary">
                            Cancel
                          </Button>
                          &nbsp;&nbsp;
                          <Button size="lg" variant="primary"
                                  type="submit">
                            Sign In
                          </Button>
                        </div>

                        {
                          !!this.state.options && !!this.state.options.sso
                          && !!this.state.options.sso.okta
                            ? (
                                  <div className="text-center mt-3">
                                    <Button
                                        href={this.state.options.sso.okta}
                                        size={"lg"}
                                        variant="outline-primary"
                                    >
                                      Sign in with Okta
                                    </Button>
                                  </div>
                            ) : ''
                        }

                      </Form>
                    </div>
                  </Card.Body>
                </Card>

              </Col>
            </Row>
          </Container>
        </NoNavWrapper>
    );

  }

}