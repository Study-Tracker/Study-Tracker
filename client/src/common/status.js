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
import {Badge} from 'react-bootstrap';
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
