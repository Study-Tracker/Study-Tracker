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
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  Button,
  DropdownItem,
  DropdownMenu,
  DropdownToggle,
  UncontrolledButtonDropdown
} from "reactstrap";
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

export const StatusButton = ({status}) => {
  const config = statuses[status];
  return (
      <Button size="lg" className="mr-1" color={config.color}>
        <FontAwesomeIcon icon={config.icon}
                         className="align-middle"/> {config.label}
      </Button>
  )
};

export class SelectableStatusButton extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};
    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(e) {
    let url = null;
    if (!!this.props.studyId) {
      url = "/api/study/" + this.props.studyId + "/status";
    } else if (!!this.props.assayId) {
      url = "/api/assay/" + this.props.assayId + "/status"
    }
    fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({status: e.target.dataset.value})
    })
    .then(async response => {
      if (response.ok) {
        window.location.reload();
      } else {
        throw new Error("Request failed");
      }
    }).catch(e => {
      swal(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    });
  }

  render() {
    const {status} = this.props;
    const config = statuses[status];
    const options = [];
    for (const k in statuses) {
      const s = statuses[k];
      options.push(
          <DropdownItem
              key={'status-option-' + s.label}
              onClick={this.handleChange}
              data-value={s.value}
          >
            {s.label}
          </DropdownItem>
      );
    }
    return (
        <UncontrolledButtonDropdown className="mr-1 mb-1">
          <DropdownToggle caret size={"lg"} color={config.color}>
            <FontAwesomeIcon icon={config.icon}/>
            &nbsp;&nbsp;
            {config.label}
          </DropdownToggle>
          <DropdownMenu>
            {options}
          </DropdownMenu>
        </UncontrolledButtonDropdown>
    )
  }
}

export const StatusBadge = ({status}) => {
  const config = statuses[status];
  return (
      <span className={"badge badge-" + config.color}>{config.label}</span>
  )
};

export const StatusIcon = ({status}) => {
  switch (status) {
    case statuses.IN_PLANNING.value:
      return (
          <span title="In planning">
            <Clock
                size={36}
                className="align-middle text-info mr-4"
            />
          </span>
      );
    case statuses.ACTIVE.value:
      return (
          <span title="Active">
            <PlayCircle
                size={36}
                className="align-middle text-primary mr-4"
            />
          </span>
      );
    case statuses.COMPLETE.value:
      return (
          <span title="Complete">
            <CheckCircle
                size={36}
                className="align-middle text-success mr-4"
            />
          </span>
      );
    case statuses.ON_HOLD.value:
      return (
          <span title="On hold">
            <XCircle
                size={36}
                className="align-middle text-warning mr-4"
            />
          </span>
      );
    case statuses.DEPRIORITIZED.value:
      return (
          <span title="Deprioritized">
            <ArrowDownCircle
                size={36}
                className="align-middle text-danger mr-4"
            />
          </span>
      );
    case statuses.NEEDS_ATTENTION.value:
      return (
          <span title="Needs attention">
            <AlertCircle
                size={36}
                className="align-middle text-warning mr-4"
            />
          </span>
      );
    default:
      return (
          <span>
            <HelpCircle
                size={36}
                className="align-middle text-warning mr-4"
                title="Unknown status"
            />
          </span>
      );
  }
};
