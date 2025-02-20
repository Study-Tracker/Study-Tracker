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
import {Card, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import React from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

const IconWidget = ({header, body, icon, color}) => {

  let colorClass = "illustration";
  if (color === "dark") colorClass = "illustration-dark";
  else if (color === "primary") colorClass = "illustration";
  else if (color === "light") colorClass = "illustration-text";
  else if (color === "warning") colorClass = "illustration-warning";

  return (
      <Card className={"flex-fill " + colorClass}>
        <Card.Body className="p-0 d-flex flex-fill">
          <Row className="g-0 w-100">

            <div className={"col ps-3 pt-3 pb-3 pe-1 m-1"}>
              <div className="illustration-text flex-column">
                <h4 className="illustration-text mb-2">
                  {header}
                </h4>
                <p className="mb-0">
                  {body}
                </p>
              </div>
            </div>

            <div className={"col-auto d-flex align-items-center illustration-text-light ps-1 pt-3 pb-3 pe-3 m-1"}>
              <FontAwesomeIcon icon={icon} size={"6x"} />
            </div>

          </Row>
        </Card.Body>
      </Card>
  )
}

IconWidget.propTypes = {
  header: PropTypes.string.isRequired,
  body: PropTypes.any,
  icon: PropTypes.any.isRequired,
  color: PropTypes.string
}

export default IconWidget;
