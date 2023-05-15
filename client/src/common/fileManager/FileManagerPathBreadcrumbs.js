import React from "react";
import PropTypes from "prop-types";
import {Breadcrumb} from "react-bootstrap";
import {getPathParts} from "./fileManagerUtils";

const FileManagerPathBreadcrumbs = ({
    rootFolder,
    folder,
    handlePathUpdate
}) => {

  const rootPath = rootFolder.path;
  const paths = folder ? getPathParts(folder.path, rootPath) : [];

  console.debug("Path breadcrumbs", paths);

  return (
      <Breadcrumb>

        <Breadcrumb.Item onClick={() => handlePathUpdate(rootPath)}>
          Home
        </Breadcrumb.Item>

        {
          paths
          .filter(path => !(path.index === 0 && path.path === "")) // empty first folder name
          .filter(path => !path.isRoot) // same as the root folder
          .map((path) => {
            if (path.isLast) {
              return (
                  <Breadcrumb.Item key={path.index} active={true}>
                    {path.label}
                  </Breadcrumb.Item>
              )
            }
            else if (path.isChild) {
              return (
                  <Breadcrumb.Item
                      key={path.index}
                      onClick={() => handlePathUpdate(path.path)}
                      active={false}
                  >
                    {path.label}
                  </Breadcrumb.Item>
              );
            } else {
              return (
                  <Breadcrumb.Item key={path.index} active={false}>
                    {path.label}
                  </Breadcrumb.Item>
              );
            }
          })
        }
      </Breadcrumb>
  )

}

FileManagerPathBreadcrumbs.propTypes = {
  rootFolder: PropTypes.object.isRequired,
  folder: PropTypes.object,
  handlePathUpdate: PropTypes.func.isRequired,
}

export default FileManagerPathBreadcrumbs;