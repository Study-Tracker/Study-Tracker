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
  Alert,
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

const qs = require('qs');

export default class PasswordResetView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      auth: {},
      inputIsValid: false,
      isLoaded: false,
      isError: false
    };
    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentDidMount() {
    const params = qs.parse(this.props.location.search,
        {ignoreQueryPrefix: true});
    if (params.hasOwnProperty("token") && params.hasOwnProperty("email")) {
      this.setState({
        auth: {
          email: params.email,
          token: params.token
        },
        isLoaded: true
      })
    }
  }

  handleInputChange(data) {
    const auth = {
      ...this.state.auth,
      ...data
    };
    let inputIsValid = false;
    if (auth.password != null && auth.password !== ''
        && auth.passwordAgain != null && auth.passwordAgain !== ''
        && auth.password === auth.passwordAgain) {
      inputIsValid = true;
    }
    this.setState({
      auth,
      inputIsValid
    });
  }

  handleSubmit() {

  }

  render() {

    const params = qs.parse(this.props.location.search,
        {ignoreQueryPrefix: true});
    let isError = params.hasOwnProperty("error");

    return (
        <NoNavWrapper>
          <Container fluid className="animated fadeIn">
            <Row className="justify-content-center">
              <Col xs="12" sm="8" md="8" lg="6" xl="4">

                <div className="text-center mt-4">
                  <h2>Password Reset</h2>
                  <p className="lead">
                    Please enter your username and a new password.
                  </p>
                </div>

                <Card>
                  <CardBody>
                    <div className="m-sm-4">

                      <div className="text-center">
                        <User size={80} className="align-middle mr-2"/>
                      </div>

                      <Form action={"/auth/passwordreset"} method={"post"}>

                        <FormGroup hidden>
                          <Label>Email</Label>
                          <Input
                              bsSize="lg"
                              type="text"
                              name="email"
                              defaultValue={this.state.auth.email}
                              // disabled={true}
                          />
                        </FormGroup>

                        <FormGroup hidden>
                          <Input
                              bsSize="lg"
                              type="text"
                              name="token"
                              defaultValue={this.state.auth.token}
                              // disabled={true}
                              style={{display: "none"}}
                          />
                        </FormGroup>

                        <FormGroup>
                          <Label>Password</Label>
                          <Input
                              bsSize="lg"
                              type="password"
                              name="password"
                              placeholder="Enter your password"
                              onChange={e => this.handleInputChange(
                                  {password: e.target.value})}
                          />
                        </FormGroup>

                        <FormGroup>
                          <Label>Password Again</Label>
                          <Input
                              bsSize="lg"
                              type="password"
                              name="passwordAgain"
                              placeholder="Enter your password a second time"
                              onChange={e => this.handleInputChange(
                                  {passwordAgain: e.target.value})}
                          />
                        </FormGroup>

                        {
                          isError
                              ? (
                                  <div className="text-center mt-3">
                                    <Alert color="danger" className="p-3">
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
                          <button
                              className="btn btn-lg btn-primary"
                              type="submit"
                              disabled={!this.state.inputIsValid && !this.state.isLoaded}
                          >
                            Submit
                          </button>
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

  }

}