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

import {useNavigate} from "react-router-dom";
import {Badge, Button, Dropdown} from "react-bootstrap";
import React from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faCheckCircle,
  faEdit,
  faGears,
  faInfoCircle,
  faXmarkCircle
} from "@fortawesome/free-solid-svg-icons";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import PropTypes from "prop-types";

const ProgramsTable = ({programs, showModal, handleStatusChange}) => {

  const navigate = useNavigate();

  const columns = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      // headerStyle: {width: '40%'},
      formatter: (c, d, i, x) => <Button variant="link"
                                         onClick={() => showModal(
                                             d)}>{d.name}</Button>,
      sortFunc: (a, b, order, dataField, rowA, rowB) => {
        if (rowA.name > rowB.name) {
          return order === "desc" ? -1 : 1;
        }
        if (rowB.name > rowA.name) {
          return order === "desc" ? 1 : -1;
        }
        return 0;
      },
    },
    {
      dataField: "code",
      text: "Code",
      sort: true,
      // headerStyle: {width: '20%'},
      formatter: (cell, d, index, x) => d.code,
    },
    {
      dataField: "createdAt",
      text: "Created",
      sort: true,
      // headerStyle: {width: '40%'},
      formatter: (c, d, i, x) => new Date(d.createdAt).toLocaleDateString()
    },
    {
      dataField: "createdBy",
      text: "Created By",
      sort: true,
      formatter: (cell, d, index, x) => d.createdBy.displayName,
    },
    {
      dataField: "status",
      text: "Status",
      sort: true,
      // headerStyle: {width: '10%'},
      formatter: (c, d, i, x) => {
        if (d.active) {
          return <Badge bg="success">Active</Badge>
        } else {
          return <Badge bg="danger">Inactive</Badge>
        }
      }
    },
    // {
    //   dataField: "eln",
    //   text: "ELN",
    //   sort: false,
    //   // headerStyle: {width: '40%'},
    //   formatter: (c, d, i, x) => {
    //     if (!!d.notebookFolder) {
    //       if (!!d.notebookFolder.url && d.notebookFolder.url !== "ERROR") {
    //         return <a href={d.notebookFolder.url} target="_blank" rel="noopener noreferrer">ELN Folder</a>
    //       } else {
    //         return <Badge bg="warning">ERROR</Badge>
    //       }
    //     } else {
    //       return "n/a"
    //     }
    //   }
    // },
    // {
    //   dataField: "storage",
    //   text: "File Storage",
    //   sort: false,
    //   // headerStyle: {width: '40%'},
    //   formatter: (c, d, i, x) => {
    //     if (!!d.primaryStorageFolder) {
    //       if (!!d.primaryStorageFolder.url) {
    //         return <a href={d.primaryStorageFolder.url} target="_blank" rel="noopener noreferrer">Files
    //           Folder</a>
    //       } else {
    //         return <Badge bg="warning">ERROR</Badge>
    //       }
    //     } else {
    //       return "n/a"
    //     }
    //   }
    // },
    {
      dataField: "controls",
      text: "",
      sort: false,
      // headerStyle: {width: '10%'},
      formatter: (c, d, i, x) => {
        return (
            <React.Fragment>
              <Dropdown>

                <Dropdown.Toggle variant={"outline-primary"}>
                  <FontAwesomeIcon icon={faGears} className={"me-2"} />
                </Dropdown.Toggle>

                <Dropdown.Menu>

                  <Dropdown.Item onClick={() => showModal(d)}>
                    <FontAwesomeIcon icon={faInfoCircle} className={"me-2"}/>
                    View Details
                  </Dropdown.Item>

                  <Dropdown.Item
                      onClick={() => navigate("/program/" + d.id + "/edit")}
                  >
                    <FontAwesomeIcon icon={faEdit} className={"me-2"}/>
                    Edit Program
                  </Dropdown.Item>

                  {
                    d.active ? (
                        <Dropdown.Item onClick={() => handleStatusChange(d.id, false)}>
                          <FontAwesomeIcon icon={faXmarkCircle} className={"me-2"}/>
                          Set Inactive
                        </Dropdown.Item>
                    ) : (
                        <Dropdown.Item onClick={() => handleStatusChange(d.id, true)}>
                          <FontAwesomeIcon icon={faCheckCircle} className={"me-2"}/>
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
          data={programs}
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
                    dataField: "name",
                    order: "asc"
                  }]}
                  {...props.baseProps}
              >
              </BootstrapTable>
            </div>
        )}
      </ToolkitProvider>
  )

}

ProgramsTable.propTypes = {
  programs: PropTypes.array.isRequired,
  showModal: PropTypes.func.isRequired,
  handleStatusChange: PropTypes.func.isRequired
}

export default ProgramsTable;