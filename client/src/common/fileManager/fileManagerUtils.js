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

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFolder, faServer} from "@fortawesome/free-solid-svg-icons";
import {faAws} from "@fortawesome/free-brands-svg-icons";
import React from "react";

export const getDataSourceIcon = (driveType) => {
  switch (driveType) {
    case "EGNYTE":
      return <FontAwesomeIcon icon={faServer} className={"me-2"} />
    case "S3":
      return <FontAwesomeIcon icon={faAws} className={"me-2"} />
    default:
      return <FontAwesomeIcon icon={faFolder} className={"me-2"} />
  }
}

export const getDataSourceLabel = (driveType) => {
  if (!driveType) return "";
  switch (driveType) {
    case "EGNYTE":
      return "Egnyte";
    case "S3":
      return "Amazon S3";
    default:
      return "Local";
  }
}

export const pathIsChild = (path, parent) => {
  if (!path.startsWith("/")) path = "/" + path;
  if (!parent.startsWith("/")) parent = "/" + parent;
  if (!path.endsWith("/")) path = path + "/";
  if (!parent.endsWith("/")) parent = parent + "/";
  return path.startsWith(parent);
}

export const pathIsRoot = (path, parent) => {
  if (!path.startsWith("/")) path = "/" + path;
  if (!parent.startsWith("/")) parent = "/" + parent;
  if (!path.endsWith("/")) path = path + "/";
  if (!parent.endsWith("/")) parent = parent + "/";
  return path === parent;
}

export const getPathParts = (path, rootPath) => {
  const bits = path.replace(/\/$/, "").split("/");
  return bits.map((bit, index) => {
    let currentPath = bits.slice(0, index + 1).join("/");
    if (path.startsWith("/") && !currentPath.startsWith("/")) currentPath = "/" + currentPath;
    let parentPath = bits.slice(0, index).join("/");
    if (path.startsWith("/") && !parentPath.startsWith("/")) parentPath = "/" + parentPath;
    let parts = {
      label: bit === "" ? "root" : bit,
      path: currentPath,
      parentPath: pathIsChild(parentPath, rootPath) && currentPath !== parentPath ? parentPath : null,
      isChild: pathIsChild(currentPath, rootPath),
      isRoot: pathIsRoot(currentPath, rootPath),
      isLast: index === bits.length - 1,
      index: index,
      name: bit
    }
    parts.isBrowsable = parts.isChild || parts.isRoot;
    return parts;
  })
}