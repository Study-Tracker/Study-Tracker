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

import {Alert, Button, Card, Col, Container, Row} from "react-bootstrap"
import React from "react";
import PropTypes from "prop-types";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faCheckCircle,
  faExclamationCircle,
  faExclamationTriangle,
  faHome,
  faInfoCircle,
  faQuestionCircle,
  faRefresh,
  faWarning
} from "@fortawesome/free-solid-svg-icons";
import {useNavigate} from "react-router-dom";

export const DismissableAlert = ({header, message, color="primary", icon, dismissable=true}) => {
  if (!icon) {
    switch (color) {
      case "danger":
        icon = faExclamationCircle;
        break;
      case "warning":
        icon = faExclamationTriangle;
        break;
      case "info":
        icon = faInfoCircle;
        break;
      case "success":
        icon = faCheckCircle;
        break;
      case "secondary":
        icon = faQuestionCircle;
        break;
      default:
        icon = faInfoCircle;
    }
  }
  return (
      <Alert variant={color} className={"alert-outline"} dismissible={dismissable}>
        <div className="alert-icon d-flex align-items-center">
          <FontAwesomeIcon icon={icon} size={"2x"} fixedWidth />
        </div>
        <div className="alert-message">
          {header && (
              <>
                <strong className={"text-large"}>{header}</strong>
                <br />
              </>
          )}
          {message}
        </div>
      </Alert>
  )
};

DismissableAlert.propTypes = {
  header: PropTypes.string,
  message: PropTypes.string.isRequired,
  color: PropTypes.string,
  dismissable: PropTypes.bool,
  icon: PropTypes.element,
}

export const SettingsErrorMessage = () => {
  return (
      <Row>
        <Col>
          <Alert variant={"danger"} className="alert-outline">
            <div className="alert-icon d-flex align-items-center">
              <FontAwesomeIcon icon={faWarning} fixedWidth />
            </div>
            <div className="alert-message">
              <strong className="text-lg">Error</strong>
              <br />
              Failed to load settings content. Please reload the page and try
              again.
            </div>
          </Alert>
        </Col>
      </Row>
  )
}

export const ErrorMessage = ({hideControls = false}) => {

  const navigate = useNavigate();

  return (
      <div className={"text-center pt-5"}>
        <FontAwesomeIcon icon={faWarning} size={"5x"} className={"text-danger"} />
        <h1 className={"mt-4 mb-3"}>Something went wrong...</h1>
        <p>
          Study Tracker encountered an error while trying to load the page.
        </p>
        {
            !hideControls && (
                <div>
                  <Button
                      onClick={() => navigate(0)}
                      variant={"outline-primary"}
                      className={"me-3"}
                  >
                    <FontAwesomeIcon icon={faRefresh} className={"me-2"} />
                    Reload
                  </Button>
                  <Button href={"/"} variant={"outline-primary"}>
                    <FontAwesomeIcon icon={faHome} className={"me-2"} />
                    Home
                  </Button>
                </div>
            )
        }
      </div>
  );
};

ErrorMessage.propTypes = {
  hideControls: PropTypes.bool,
}

export const ContainerErrorMessage = () => {
  return (
      <Container>
        <Row className={"justify-content-center"}>
          <Col lg={6}>
            <ErrorMessage />
          </Col>
        </Row>
      </Container>
  )
}

export const CardErrorMessage = () => {
  return (
      <Card>
        <Card.Body>
          <Row className={"justify-content-center"}>
            <Col lg={6}>
              <ErrorMessage hideControls={true} />
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}