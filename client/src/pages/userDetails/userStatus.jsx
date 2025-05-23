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

import {Button, Dropdown} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import {statuses} from "../../config/userActivityConstants";
import swal from "sweetalert2";
import axios from "axios";

export const UserStatusButton = ({active}) => {
  const config = !!active ? statuses.ACTIVE : statuses.INACTIVE;
  return (
      <Button size="lg" className="me-1" variant={config.color}>
        <FontAwesomeIcon icon={config.icon}
                         className="align-middle"/> {config.label}
      </Button>
  )
};

export const SelectableUserStatusButton = props => {

  const {active, userId} = props;
  const config = !!active ? statuses.ACTIVE : statuses.INACTIVE;
  const options = [];

  const handleChange = (e) => {
    const active = e.target.dataset.value === statuses.ACTIVE;
    axios.post("/api/internal/user/" + userId + "/status?active=" + active)
    .then(() => window.location.reload())
    .catch(e => {
      swal.fire(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    });
  }

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
        <Dropdown.Toggle size={"lg"} variant={config.color}>
          <FontAwesomeIcon icon={config.icon}/>
          &nbsp;&nbsp;
          {config.label}
        </Dropdown.Toggle>
        <Dropdown.Menu>
          {options}
        </Dropdown.Menu>
      </Dropdown>
  )

}
