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

import React, {useEffect, useState} from "react";

import {Alert, Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import NoNavWrapper from "../../common/structure/NoNavWrapper";
import {useSearchParams} from "react-router-dom";

const PasswordResetView = props => {
  
  const [searchParams, setSearchParams] = useSearchParams();
  const [state, setState] = useState({
    auth: {},
    inputIsValid: false,
    isLoaded: false,
    isError: false
  });

  useEffect(() => {
    if (searchParams.has("token") && searchParams.has("email")) {
      setState(prevState => ({
        ...prevState,
        auth: {
          email: searchParams.get("email"),
          token: searchParams.get("token")
        },
        isLoaded: true
      }))
    }
  }, [searchParams]);

  const handleInputChange = (data) => {
    const auth = {
      ...state.auth,
      ...data
    };
    let inputIsValid = false;
    if (auth.password != null && auth.password !== ''
        && auth.passwordAgain != null && auth.passwordAgain !== ''
        && auth.password === auth.passwordAgain) {
      inputIsValid = true;
    }
    setState(prevState => ({
      ...prevState,
      auth,
      inputIsValid
    }));
  }
  
  let isError = searchParams.has("error");

  return (
      <NoNavWrapper>
        <Container fluid className="animated fadeIn">
          <Row className="justify-content-center">
            <Col xs={12} sm={8} md={8} lg={6} xl={4}>

              <div className="text-center mt-4">
                <h2>Password Reset</h2>
                <p className="lead">
                  Please enter your email and a new password.
                </p>
              </div>

              <Card>
                <Card.Body>
                  <div className="m-sm-4">

                    <div className="text-center mb-4">
                      <img
                          src={"/static/images/clip/password.png"}
                          className="img-fluid"
                          width="150"
                          height="150"
                      />
                    </div>

                    <Form action={"/auth/passwordreset"} method={"post"}>

                      <Form.Group hidden>
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            size="lg"
                            type="text"
                            name="email"
                            defaultValue={state.auth.email}
                        />
                      </Form.Group>

                      <Form.Group hidden>
                        <Form.Control
                            size="lg"
                            type="text"
                            name="token"
                            defaultValue={state.auth.token}
                            style={{display: "none"}}
                        />
                      </Form.Group>

                      <Form.Group>
                        <Form.Label>Password</Form.Label>
                        <Form.Control
                            size="lg"
                            type="password"
                            name="password"
                            placeholder="Enter your password"
                            onChange={e => handleInputChange(
                                {password: e.target.value})}
                        />
                      </Form.Group>

                      <Form.Group>
                        <Form.Label>Password Again</Form.Label>
                        <Form.Control
                            size="lg"
                            type="password"
                            name="passwordAgain"
                            placeholder="Enter your password a second time"
                            onChange={e => handleInputChange(
                                {passwordAgain: e.target.value})}
                        />
                      </Form.Group>

                      {
                        isError
                            ? (
                                <div className="text-center mt-3">
                                  <Alert variant="danger" className="p-3">
                                    Failed to reset your password. Please try
                                    again.
                                  </Alert>
                                </div>
                            )
                            : ''
                      }

                      <div className="text-center mt-3">
                        <a href={"/"} className="btn btn-lg btn-secondary">
                          Cancel
                        </a>
                        &nbsp;&nbsp;
                        <Button
                            size={"lg"}
                            variant="primary"
                            type="submit"
                            disabled={!state.inputIsValid
                                && !state.isLoaded}
                        >
                          Submit
                        </Button>
                      </div>

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

export default PasswordResetView;