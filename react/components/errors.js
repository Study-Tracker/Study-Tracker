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

import {Alert, Col, Row} from 'react-bootstrap'
import React from "react";

export const DismissableAlert = ({header, message, color}) => {
  return (
      <Alert variant={color} dismissible>
        <div className="alert-message">
          {
            !!header ? (<strong>{header}</strong>) : ''
          }
          {message}
        </div>
      </Alert>
  )
};

export const SettingsErrorMessage = () => {
  return (
      <Row>
        <Col>
          <Alert color="danger" className="text-center p-5">
            <Alert.Heading>Error</Alert.Heading>
            <p>
              Failed to load settings content. Please reload the page and try
              again. If the error persist, contact Study Tracker support.
            </p>
          </Alert>
        </Col>
      </Row>
  )
}