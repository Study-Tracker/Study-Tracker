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

import React from "react";
import PropTypes from "prop-types";
import {Badge, Dropdown} from "react-bootstrap";
import {CheckCircle} from "react-feather";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faCheckCircle,
  faEdit,
  faTimesCircle
} from "@fortawesome/free-solid-svg-icons";
import DriveStatusBadge from "../../../common/fileManager/folderBadges";

const StorageFolderTable = ({
  folders,
  handleFolderEdit,
  handleToggleFolderActive
}) => {

  const columns = [
    {
      dataField: "name",
      text: "Name",
      sort: true
    },
    {
      dataField: "type",
      text: "Type",
      formatter: (c, d) => {
        switch (d.storageDrive.driveType) {
          case "LOCAL":
            return "Local File System";
          case "S3":
            return "Amazon S3";
          case "EGNYTE":
            return "Egnyte API";
          case "ONEDRIVE":
            return "Microsoft OneDrive";
          default:
            return d.type;
        }
      },
      sort: true
    },
    {
      dataField: "name",
      text: "Source",
      formatter: (c, d) => <code>{d.name}</code>,
      sort: true
    },
    {
      dataField: "rootFolderPath",
      text: "Path",
      sort: false
    },
    {
      dataField: "active",
      text: "Status",
      sort: true,
      formatter: (c, d, i, x) => <DriveStatusBadge active={d.active} />
    },
    {
      dataField: "permissions",
      text: "Permissions",
      formatter: (c, d) => {
        if (d.permissions === "READ_WRITE") {
          return <Badge bg="info">Read / Write</Badge>;
        } else {
          return <Badge bg="secondary">Read Only</Badge>;
        }
      },
      sort: true
    },
    {
      dataField: "defaultStudyLocation",
      text: "Study Default",
      sort: false,
      formatter: (c, d, i, x) => {
        return d.defaultStudyLocation ? <CheckCircle className={"text-success"} /> : "";
      }
    },
    {
      dataField: "controls",
      text: "",
      sort: false,
      formatter: (c, d) => {
        return (
            <React.Fragment>

              <Dropdown>

                <Dropdown.Toggle variant={"outline-primary"}>
                  &nbsp;Options&nbsp;
                </Dropdown.Toggle>

                <Dropdown.Menu>

                  <Dropdown.Item
                      onClick={() => handleLocationEdit(d)}
                  >
                    <FontAwesomeIcon icon={faEdit} className={"me-2"}/>
                    Edit Location
                  </Dropdown.Item>

                  <Dropdown.Divider/>

                  {
                    !!d.active ? (
                        <Dropdown.Item
                            className={"text-warning"}
                            onClick={() => handleToggleLocationActive(d, false)}
                        >
                          <FontAwesomeIcon icon={faTimesCircle} className={"me-2"} />
                          Set Inactive
                        </Dropdown.Item>
                    ) : (
                        <Dropdown.Item
                            className={"text-warning"}
                            onClick={() => handleToggleLocationActive(d, true)}
                        >
                          <FontAwesomeIcon icon={faCheckCircle} className={"me-2"} />
                          Set Active
                        </Dropdown.Item>
                    )
                  }

                </Dropdown.Menu>
              </Dropdown>

            </React.Fragment>
        )
      }
    }
  ];

  return (
      <ToolkitProvider
          keyField="id"
          data={locations}
          columns={columns}
          search
          exportCSV
      >
        {props => (
            <div>
              <div className="float-end">
                <Search.SearchBar
                    {...props.searchProps}
                />
              </div>
              <BootstrapTable
                  bootstrap4
                  keyField="id"
                  bordered={false}
                  pagination={paginationFactory({
                    sizePerPage: 10,
                    sizePerPageList: [10, 20, 40, 80]
                  })}
                  defaultSorted={[{
                    dataField: "displayName",
                    order: "asc"
                  }]}
                  {...props.baseProps}
              >
              </BootstrapTable>
            </div>
        )}
      </ToolkitProvider>
  );

}

StorageFolderTable.propTypes = {
  locations: PropTypes.array.isRequired
}

export default StorageFolderTable;
