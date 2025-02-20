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
import React, {useContext} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faCheckCircle,
  faEdit,
  faInfoCircle,
  faRedo,
  faTimesCircle
} from "@fortawesome/free-solid-svg-icons";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import PropTypes from "prop-types";
import swal from "sweetalert";
import axios from "axios";
import {useMutation, useQueryClient} from "react-query";
import NotyfContext from "../../../context/NotyfContext";

const UserSettingsTable = ({users, showModal}) => {

  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const notyf = useContext(NotyfContext);

  const resetUserPassword = (user) => {
    swal({
      title: "Are you sure you want to reset the password for user: "
          + user["displayName"] + " (" + user["email"] + ")?",
      text: "This will override any existing password reset requests and send a "
          + "new notification email to the user.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        axios.post("/api/internal/user/" + user["id"] + "/password-reset")
        .then(() => {
          notyf.success("Password reset successful. A notification email has been sent to the user.")
        })
        .catch(error => {
          console.error(error);
          notyf.error("Failed to send password reset email.");
        })
      }
    })
  }

  const toggleStatusMutation = useMutation(({userId, active}) => {
    return axios.post(`/api/internal/user/${userId}/status?active=${active}`)
  });

  const toggleUserActive = (user, active) => {
    swal({
      title: "Are you sure you want to " + (!!active ? "enable" : "disable")
          + " user: " + user["displayName"] + " (" + user["email"] + ")?",
      text: "Disabled users cannot be added to new studies and assays, but they "
          + "will remain associated with existing studies and assays.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        toggleStatusMutation.mutateAsync({userId: user["id"], active: active}, {
          onSuccess: () => {
            queryClient.invalidateQueries("users");
            notyf.success("User " + (active ? "enabled" : "disabled"));
          },
          onError: (error) => {
            console.error(error);
            notyf.error("Failed to " + (active ? "enable" : "disable"));
          }
        })
      }
    });
  }

  const columns = [
    {
      dataField: "displayName",
      text: "Display Name",
      sort: true,
      headerStyle: {width: '30%'},
      formatter: (cell, d) => d.displayName,
      sortFunc: (a, b, order, dataField, rowA, rowB) => {
        if (rowA.displayName > rowB.displayName) {
          return order === "desc" ? -1 : 1;
        }
        if (rowB.displayName > rowA.displayName) {
          return order === "desc" ? 1 : -1;
        }
        return 0;
      },
    },
    {
      dataField: "username",
      text: "Username",
      sort: true,
      headerStyle: {width: '30%'},
      formatter: (c, d) => <Button variant={"link"}
                                         onClick={() => showModal(d)}>{d.username}</Button>,
      sortFunc: (a, b, order, dataField, rowA, rowB) => {
        if (rowA.username > rowB.username) {
          return order === "desc" ? -1 : 1;
        }
        if (rowB.username > rowA.username) {
          return order === "desc" ? 1 : -1;
        }
        return 0;
      },
    },
    {
      dataField: "type",
      text: "Type",
      sort: true,
      headerStyle: {width: '10%'},
      formatter: (c, d) => {
        if (d.type === "STANDARD_USER") {
          return <Badge bg="success">Standard</Badge>
        } else if (d.type === "API_USER") {
          return <Badge bg="warning">API</Badge>
        } else {
          return <Badge bg="danger">System</Badge>
        }
      }
    },
    {
      dataField: "admin",
      text: "Role",
      sort: true,
      headerStyle: {width: '10%'},
      formatter: (c, d) => {
        if (d.admin) {
          return <Badge bg="danger">Admin</Badge>
        } else {
          return <Badge bg="info">User</Badge>
        }
      }
    },
    {
      dataField: "status",
      text: "Status",
      sort: true,
      headerStyle: {width: '10%'},
      formatter: (c, d) => {
        if (d.locked) {
          return <Badge bg="warning">Locked</Badge>
        } else if (d.active) {
          return <Badge bg="success">Active</Badge>
        } else {
          return <Badge bg="danger">Inactive</Badge>
        }
      }
    },
    {
      dataField: "controls",
      text: "",
      sort: false,
      headerStyle: {width: '10%'},
      formatter: (c, d) => {
        return (
            <React.Fragment>

              <Dropdown>

                <Dropdown.Toggle variant={"outline-primary"}>
                  {/*<FontAwesomeIcon icon={faBars} />*/}
                  &nbsp;Options&nbsp;
                </Dropdown.Toggle>

                <Dropdown.Menu>

                  <Dropdown.Item onClick={() => showModal(d)}>
                    <FontAwesomeIcon icon={faInfoCircle}/>
                    &nbsp;&nbsp;
                    View Details
                  </Dropdown.Item>

                  {d.type === "STANDARD_USER" && (
                      <Dropdown.Item
                          onClick={() => navigate("/users/" + d.id + "/edit")}
                      >
                        <FontAwesomeIcon icon={faEdit}/>
                        &nbsp;&nbsp;
                        Edit User
                      </Dropdown.Item>
                    )
                  }

                  <Dropdown.Divider/>

                  {
                    !!d.active ? (
                        <Dropdown.Item
                            className={"text-warning"}
                            onClick={() => toggleUserActive(d, false)}
                        >
                          <FontAwesomeIcon icon={faTimesCircle}/>
                          &nbsp;&nbsp;
                          Set Inactive
                        </Dropdown.Item>
                    ) : (
                        <Dropdown.Item
                            className={"text-warning"}
                            onClick={() => toggleUserActive(d, true)}
                        >
                          <FontAwesomeIcon icon={faCheckCircle}/>
                          &nbsp;&nbsp;
                          Set Active
                        </Dropdown.Item>
                    )
                  }

                  {d.type === "STANDARD_USER" && (
                      <Dropdown.Item
                          className={"text-warning"}
                          onClick={() => resetUserPassword(d)}
                      >
                        <FontAwesomeIcon icon={faRedo}/>
                        &nbsp;&nbsp;
                        Reset Password
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
          data={users}
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
  )

};

UserSettingsTable.propTypes = {
  users: PropTypes.array.isRequired,
  showModal: PropTypes.func.isRequired,
}

export default UserSettingsTable;