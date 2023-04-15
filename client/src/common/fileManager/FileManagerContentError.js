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

import React from "react";
import {Button, Card, Col, Row} from "react-bootstrap";
import PropTypes from "prop-types";

const FileManagerContentError = ({error, handleRepairFolder}) => {

  let content = (
      <p>
        There was a problem loading the folder contents. Please try again later.
      </p>
  );
  if (error && error.response.status === 404) {
    if (!!handleRepairFolder) {
      content = (
          <>
            <p>
              The requested folder could not be found. If you believe Study Tracker failed to
              create this folder, click the button below to attempt to repair it.
            </p>
            <p className={"text-center"}>
              <Button
                  variant={"primary"}
                  onClick={handleRepairFolder}
                  size={"lg"}
              >
                Repair Folder
              </Button>
            </p>
          </>
      );
    } else {
      content = (
          <p>
            The requested folder could not be found. Please try again later.
          </p>
      );
    }
  }

  return (
      <Card className="illustration-warning flex-fill">
        <Card.Body>
          <Row>
            <Col sm={6} className={"d-flex"}>
              <div className={"align-self-center"}>
                <h1 className="display-5 illustration-text text-center mb-4">Something went wrong...</h1>
                {content}
              </div>
            </Col>
            <Col sm={6}>
              <img src="/static/images/clip/information-flow-yellow.png" alt="Error" className="img-fluid"/>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

FileManagerContentError.propTypes = {
  error: PropTypes.object.isRequired,
  handleRepairFolder: PropTypes.func
}

export default FileManagerContentError;