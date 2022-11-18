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
import PropTypes from "prop-types";
import {Badge} from "react-bootstrap";
import {CheckCircle} from "react-feather";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";

const StorageLocationTable = ({locations}) => {

  const columns = [
    {
      dataField: "displayName",
      text: "Name",
      sort: true
    },
    {
      dataField: "type",
      text: "Type",
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
      formatter: (c, d, i, x) => {
        if (d.active) {
          return <Badge bg="success">Active</Badge>
        } else {
          return <Badge bg="danger">Inactive</Badge>
        }
      }
    },
    {
      dataField: "permissions",
      text: "Permissions",
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
      dataField: "defaultDataLocation",
      text: "Data Default",
      sort: false,
      formatter: (c, d, i, x) => {
        return d.defaultDataLocation ? <CheckCircle className={"text-success"} /> : "";
      }
    },
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

StorageLocationTable.propTypes = {
  locations: PropTypes.array.isRequired
}

export default StorageLocationTable;
