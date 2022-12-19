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

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFolder, faServer} from "@fortawesome/free-solid-svg-icons";
import {faAws} from "@fortawesome/free-brands-svg-icons";
import React from "react";

export const getDataSourceIcon = (location) => {
  if (!location) return <FontAwesomeIcon icon={faFolder} className={"me-2"} />;
  switch (location.type) {
    case "EGNYTE_API":
      return <FontAwesomeIcon icon={faServer} className={"me-2"} />
    case "AWS_S3":
      return <FontAwesomeIcon icon={faAws} className={"me-2"} />
    default:
      return <FontAwesomeIcon icon={faFolder} className={"me-2"} />
  }
}

export const getDataSourceLabel = (location) => {
  if (!location) return "";
  switch (location.type) {
    case "EGNYTE_API":
      return "Egnyte";
    case "AWS_S3":
      return "Amazon S3";
    default:
      return "Local";
  }
}