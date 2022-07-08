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

import React, {useState} from "react";

import {Alert, Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import NoNavWrapper from "../structure/NoNavWrapper";
import {useSearchParams} from "react-router-dom";

const PasswordResetRequestView = props => {
  
  const [searchParams, setSearchParams] = useSearchParams();
  const [state, setState] = useState({
    auth: {},
    inputIsValid: false
  });

  const handleInputChange = (data) => {
    const auth = {
      ...state.auth,
      ...data
    };
    let inputIsValid = false;
    if (auth.email != null && auth.email !== '') {
      inputIsValid = true;
    }
    setState(prevState => ({
      ...prevState,
      auth,
      inputIsValid
    }));
  };

  let isError = searchParams.has("error");

  return (
      <NoNavWrapper>
        <Container fluid className="animated fadeIn">
          <Row className="justify-content-center">
            <Col xs={12} sm={8} md={8} lg={6} xl={4}>

              <div className="text-center mt-4">
                <h2>Request Password Reset</h2>
                <p className="lead">Please enter your email and click submit.
                  If your account is registered, you will receive and email
                  with instructions for resetting your password.</p>
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

                    <Form action={"/auth/passwordresetrequest"}
                          method={"post"}>

                      <Form.Group>
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            size="lg"
                            type="text"
                            name="email"
                            placeholder="Enter your email"
                            onChange={e => handleInputChange(
                                {email: e.target.value})}
                        />
                      </Form.Group>

                      {
                        isError
                            ? (
                                <div className="text-center mt-3">
                                  <Alert variant="danger" className="p-3">
                                    There was a problem submitting your request.
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
                            className="btn btn-lg btn-primary"
                            type="submit"
                            disabled={!state.inputIsValid}
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

export default PasswordResetRequestView;