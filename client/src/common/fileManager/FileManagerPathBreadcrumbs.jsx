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

import React, {useMemo} from "react";
import PropTypes from "prop-types";
import {Breadcrumb} from "react-bootstrap";

const FileManagerPathBreadcrumbs = ({
    rootFolder,
    paths,
    handlePathUpdate
}) => {

  const rootPath = rootFolder.path;

  const breadcrumbs = useMemo(() => paths
  .filter(path => !(path.index === 0 && path.path === "")) // empty first folder name
  .filter(path => !!path.isBrowsable)
  .map((path) => {
    console.debug("Path", path);
    if (path.isLast) {
      return (
          <Breadcrumb.Item key={path.index} active={true}>
            {path.label}
          </Breadcrumb.Item>
      )
    }
    else {
      return (
          <Breadcrumb.Item
              key={path.index}
              onClick={() => handlePathUpdate(path.path)}
              active={false}
          >
            {path.label}
          </Breadcrumb.Item>
      );
    }
  }), [paths, handlePathUpdate]);

  console.debug("Path breadcrumbs", paths);

  return (
      <Breadcrumb>

        <Breadcrumb.Item onClick={() => handlePathUpdate(rootPath)}>
          Home
        </Breadcrumb.Item>

        { breadcrumbs }

      </Breadcrumb>
  )

}

FileManagerPathBreadcrumbs.propTypes = {
  rootFolder: PropTypes.object.isRequired,
  paths: PropTypes.array.isRequired,
  handlePathUpdate: PropTypes.func.isRequired,
}

export default FileManagerPathBreadcrumbs;