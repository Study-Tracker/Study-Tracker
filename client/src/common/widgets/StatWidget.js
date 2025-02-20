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

import {Card} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";

const StatWidget = ({label, value, icon: Icon, color, url, onClick}) => {

  let component = (
      <React.Fragment>
        <h3 className="mb-2">{value}</h3>
        <p className="mb-2">{label}</p>
      </React.Fragment>
  );
  if (!!url) {
    component = (
        <a href={url}>
          <h3 className="mb-2">{value}</h3>
          <p className="mb-2">{label}</p>
        </a>
    )
  } else if (!!onClick) {
    component = (
        <a onClick={onClick}>
          <h3 className="mb-2">{value}</h3>
          <p className="mb-2">{label}</p>
        </a>
    );
  }

  return (
      <Card className="flex-fill">
        <Card.Body className="py-4">

          <div className="d-flex align-items-start">

            <div className="flex-grow-1">
              {component}
            </div>

            <div className="d-inline-block ms-3">
              <div className={"stat stat-" + color}>
                <Icon className={"align-md feather-lg "}/>
              </div>
            </div>

          </div>

        </Card.Body>
      </Card>
  )
}

StatWidget.propTypes = {
  label: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired,
  icon: PropTypes.elementType.isRequired,
  color: PropTypes.string.isRequired,
  url: PropTypes.string
}

export default StatWidget;