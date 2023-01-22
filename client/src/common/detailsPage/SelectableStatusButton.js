/*
 * Copyright 2023 the original author or authors.
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

import React, {useContext} from "react";
import axios from "axios";
import {statuses} from "../../config/statusConstants";
import {Dropdown} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import PropTypes from "prop-types";
import NotyfContext from "../../context/NotyfContext";

const SelectableStatusButton = ({
    status,
    studyId,
    assayId
}) => {

  const notyf = useContext(NotyfContext);

  const handleChange = (e) => {
    let url = null;
    if (!!studyId) {
      url = "/api/internal/study/" + studyId + "/status";
    } else if (!!assayId) {
      url = "/api/internal/assay/" + assayId + "/status"
    }
    axios.post(url, {status: e.target.dataset.value})
    .then(() => window.location.reload())
    .catch(e => {
      notyf.open({
        type: 'error',
        message: 'Error changing status'
      });
      console.error(e);
    });
  }

  const config = statuses[status];
  const options = [];
  for (const k in statuses) {
    const s = statuses[k];
    options.push(
        <Dropdown.Item
            key={'status-option-' + s.label}
            onClick={handleChange}
            data-value={s.value}
        >
          {s.label}
        </Dropdown.Item>
    );
  }
  return (
      <Dropdown className="me-1 mb-1">
        <Dropdown.Toggle variant={config.color}>
          <FontAwesomeIcon icon={config.icon} className={"me-2"}/>
          <span className={"me-2"}>{config.label}</span>
        </Dropdown.Toggle>
        <Dropdown.Menu>
          {options}
        </Dropdown.Menu>
      </Dropdown>
  )

}

SelectableStatusButton.propTypes = {
  status: PropTypes.string.isRequired,
  studyId: PropTypes.string,
  assayId: PropTypes.string
}

export default SelectableStatusButton;