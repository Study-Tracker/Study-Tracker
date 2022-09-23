/*
 * Copyright 2022 the original author or authors.
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
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {Badge, Button, Dropdown} from 'react-bootstrap';
import swal from "sweetalert";
import {
  AlertCircle,
  ArrowDownCircle,
  CheckCircle,
  Clock,
  HelpCircle,
  PlayCircle,
  XCircle
} from 'react-feather';
import {statuses} from "../config/statusConstants";
import axios from "axios";

export const StatusButton = ({status}) => {
  const config = statuses[status];
  return (
      <Button className="me-1" variant={config.color} disabled>
        <FontAwesomeIcon icon={config.icon}
                         className="align-middle"/> {config.label}
      </Button>
  )
};

export const SelectableStatusButton = props => {

  const handleChange = (e) => {
    let url = null;
    if (!!props.studyId) {
      url = "/api/internal/study/" + props.studyId + "/status";
    } else if (!!props.assayId) {
      url = "/api/internal/assay/" + props.assayId + "/status"
    }
    axios.post(url, {status: e.target.dataset.value})
    .then(() => window.location.reload())
    .catch(e => {
      swal(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    });
  }

  const {status} = props;
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

export const StatusBadge = ({status}) => {
  const config = statuses[status];
  return (
      <Badge bg={config.color}>{config.label}</Badge>
  )
};

export const StatusIcon = ({status}) => {
  switch (status) {
    case statuses.IN_PLANNING.value:
      return (
          <span title="In planning">
            <Clock
                size={36}
                className="align-middle text-info me-4"
            />
          </span>
      );
    case statuses.ACTIVE.value:
      return (
          <span title="Active">
            <PlayCircle
                size={36}
                className="align-middle text-primary me-4"
            />
          </span>
      );
    case statuses.COMPLETE.value:
      return (
          <span title="Complete">
            <CheckCircle
                size={36}
                className="align-middle text-success me-4"
            />
          </span>
      );
    case statuses.ON_HOLD.value:
      return (
          <span title="On hold">
            <XCircle
                size={36}
                className="align-middle text-warning me-4"
            />
          </span>
      );
    case statuses.DEPRIORITIZED.value:
      return (
          <span title="Deprioritized">
            <ArrowDownCircle
                size={36}
                className="align-middle text-danger me-4"
            />
          </span>
      );
    case statuses.NEEDS_ATTENTION.value:
      return (
          <span title="Needs attention">
            <AlertCircle
                size={36}
                className="align-middle text-warning me-4"
            />
          </span>
      );
    default:
      return (
          <span>
            <HelpCircle
                size={36}
                className="align-middle text-warning me-4"
                title="Unknown status"
            />
          </span>
      );
  }
};
