import {useNavigate} from "react-router-dom";
import {Badge, Button, Dropdown} from "react-bootstrap";
import React from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faGears, faInfoCircle} from "@fortawesome/free-solid-svg-icons";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import PropTypes from "prop-types";

const ProgramsTable = ({programs, showModal}) => {

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
                    <FontAwesomeIcon icon={faInfoCircle}/>
                    &nbsp;&nbsp;
                    View Details
                  </Dropdown.Item>

                  <Dropdown.Item
                      onClick={() => navigate("/program/" + d.id + "/edit")}
                  >
                    <FontAwesomeIcon icon={faEdit}/>
                    &nbsp;&nbsp;
                    Edit Program
                  </Dropdown.Item>

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
  showModal: PropTypes.func.isRequired
}

export default ProgramsTable;