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

import {
  Button,
  Card,
  CardBody,
  Col,
  Container,
  Form,
  FormGroup,
  Input,
  Label,
  Row
} from "reactstrap";
import {User} from "react-feather";
import NoNavWrapper from "../structure/NoNavWrapper";

const SignIn = () => (
    <NoNavWrapper>
      <Container fluid className="animated fadeIn">
        <Row className="justify-content-center">
          <Col xs="12" sm="8" md="6" xl="4">
            <div className="text-center mt-4">
              <h2>Welcome to Study Tracker</h2>
              <p className="lead">Sign in with your Decibel account to
                continue</p>
            </div>

            <Card>
              <CardBody>
                <div className="m-sm-4">
                  <div className="text-center">
                    <User size={80} className="align-middle mr-2"/>
                  </div>
                  <Form>
                    <FormGroup>
                      <Label>Email</Label>
                      <Input
                          bsSize="lg"
                          type="email"
                          name="email"
                          placeholder="Enter your email"
                      />
                    </FormGroup>
                    <FormGroup>
                      <Label>Password</Label>
                      <Input
                          bsSize="lg"
                          type="password"
                          name="password"
                          placeholder="Enter your password"
                      />
                    </FormGroup>
                    <div className="text-center mt-3">
                      <Button color="primary" size="lg">
                        Sign in
                      </Button>
                    </div>
                  </Form>
                </div>
              </CardBody>
            </Card>
          </Col>
        </Row>
      </Container>
    </NoNavWrapper>
);

export default SignIn;