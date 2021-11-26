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
import {Col, Row} from 'react-bootstrap';
import BlankPageWrapper from "../structure/BlankPageWrapper";

export class ErrorBoundary extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      error: null,
      errorInfo: null
    };
  }

  componentDidCatch(error, errorInfo) {
    this.setState({
      error: error,
      errorInfo: errorInfo
    });
  }

  render() {
    if (this.state.errorInfo) {
      return <Error/>;
    }
    return this.props.children;
  }

}

const Error = ({code}) => {

  let content = <ServerError/>;
  if (code === 404) {
    content = <PageNotFound/>;
  } else if (code === 401) {
    content = <Unauthorized/>;
  }

  return (
      <BlankPageWrapper>
        <div className="middle-box text-center animated fadeInDown mt-5">
          <Row>
            <Col sm={3} md={4}></Col>
            <Col sm={6} md={4}>
              {content}
            </Col>
            <Col sm={3} md={4}></Col>
          </Row>
        </div>
      </BlankPageWrapper>
  );

};

export default Error;

const PageNotFound = () => {
  return (
      <div className="text-center">
        <h1 className="display-1 font-weight-bold">404</h1>
        <p className="h1">Page Not Found</p>
        <p className="h4 font-weight-normal mt-3 mb-4">
          The requested resource could not be found or has been removed.
        </p>
        <a className="btn btn-lg btn-primary"
           style={{paddingLeft: "50", paddingRight: "50"}} href="/">Home</a>
      </div>
  );
};

const Unauthorized = () => {
  return (
      <div className="text-center">
        <h1 className="display-1 font-weight-bold">401</h1>
        <p className="h1">Unauthorized</p>
        <p className="h4 font-weight-normal mt-3 mb-4">
          You do not have permission to access this page.
        </p>
        <a className="btn btn-lg btn-primary"
           style={{paddingLeft: "50", paddingRight: "50"}} href="/">Home</a>
      </div>
  );
};

const ServerError = () => {
  return (
      <div className="text-center">
        {/*<h1 className="display-1 font-weight-bold">500</h1>*/}
        <p className="h1">Something went wrong...</p>
        <p className="h4 font-weight-normal mt-3 mb-4">
          Study Tracker has encountered an error it was not able to handle.
          Please
          reload the page and try again. If the error persists, contact your
          local
          help desk.
        </p>
        <a className="btn btn-lg btn-primary"
           style={{paddingLeft: "50", paddingRight: "50"}} href="/">Home</a>
      </div>
  );
};