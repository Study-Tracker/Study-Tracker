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

import {
  faCheckCircle,
  faChevronCircleDown,
  faExclamationTriangle,
  faHandPaper,
  faRunning,
  faStopwatch
} from "@fortawesome/free-solid-svg-icons";

export const statuses = {
  "IN_PLANNING": {
    icon: faStopwatch,
    color: "info",
    label: "In Planning",
    value: "IN_PLANNING"
  },
  "ACTIVE": {
    icon: faRunning,
    color: "primary",
    label: "Active",
    value: "ACTIVE"
  },
  "COMPLETE": {
    icon: faCheckCircle,
    color: "success",
    label: "Complete",
    value: "COMPLETE"
  },
  "ON_HOLD": {
    icon: faHandPaper,
    color: "warning",
    label: "On Hold",
    value: "ON_HOLD"
  },
  "DEPRIORITIZED": {
    icon: faChevronCircleDown,
    color: "danger",
    label: "Deprioritized",
    value: "DEPRIORITIZED"
  },
  "NEEDS_ATTENTION": {
    icon: faExclamationTriangle,
    color: "warning",
    label: "Needs Attention",
    value: "NEEDS_ATTENTION"
  }
};