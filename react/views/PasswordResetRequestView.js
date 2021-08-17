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

export default class PasswordResetRequestView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      auth: {},
      inputIsValid: false
    };
    this.handleInputChange = this.handleInputChange.bind(this);
  }

  handleInputChange(data) {
    const auth = {
      ...this.state.auth,
      ...data
    };
    let inputIsValid = false;
    if (auth.email != null && auth.email !== '') {
      inputIsValid = true;
    }
    this.setState({
      auth,
      inputIsValid
    });
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
                  <h2>Request Password Reset</h2>
                  <p className="lead">Please enter your email and click submit.
                    If your account is registered, you will receive and email
                    with instructions for resetting your password.</p>
                </div>

                <Card>
                  <CardBody>
                    <div className="m-sm-4">

                      <div className="text-center">
                        <User size={80} className="align-middle mr-2"/>
                      </div>

                      <Form action={"/auth/passwordresetrequest"} method={"post"}>

                        <FormGroup>
                          <Label>Email</Label>
                          <Input
                              bsSize="lg"
                              type="text"
                              name="email"
                              placeholder="Enter your email"
                              onChange={e => this.handleInputChange(
                                  {email: e.target.value})}
                          />
                        </FormGroup>

                        {
                          isError
                              ? (
                                  <div className="text-center mt-3">
                                    <Alert color="danger" className="p-3">
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
                          <button
                              className="btn btn-lg btn-primary"
                              type="submit"
                              disabled={!this.state.inputIsValid}
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