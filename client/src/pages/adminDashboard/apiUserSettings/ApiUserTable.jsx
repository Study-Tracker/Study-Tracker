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

import {Badge, Dropdown} from "react-bootstrap";
import React, {useContext} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faCheckCircle,
  faEdit,
  faRedo,
  faTimesCircle
} from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";
import Swal from "sweetalert2";
import axios from "axios";
import NotyfContext from "../../../context/NotyfContext";
import { createColumnHelper } from "@tanstack/react-table";
import DataTable from "../../../common/DataTable";

const ApiUserTable = ({
  users,
  handleUserUpdate,
  triggerRefresh
}) => {

  const notyf = useContext(NotyfContext);

  const resetSecret = (user) => {
    Swal.fire({
      title: "Are you sure you want to reset the secret token for this API user: "
          + user["displayName"] + " (" + user["username"] + ")?",
      text: "This will expire the existing token and generate a new one.",
      icon: "warning",
      showCancelButton: true
    })
    .then(result => {
      if (result.isConfirmed) {
        axios.post("/api/internal/user/" + user["id"] + "/secret-reset")
        .then(response => {
          triggerRefresh();
          Swal.fire({
            title: "Here is your new secret token",
            html: "<h3><code>" + response.data.secret + "</code></h3>"
                + "<p>Be sure to copy and save this somewhere safe. Once you "
                + "close this window, you will not be able to retrieve this "
                + "token. If you lose this token, you can create another one.</p>",
          });
        })
        .catch(error => {
          console.error(error);
          Swal.fire(
              "Request failed",
              "Check the server log for more information.",
              "warning");
        })
      }
    })
  }

  const toggleApiUserActive = (user, active) => {
    Swal.fire({
      title: "Are you sure you want to " + (active ? "enable" : "disable")
          + " API user: " + user["displayName"] + " (" + user["username"] + ")?",
      text: "Disabled users cannot access the API.",
      icon: "warning",
      buttons: true
    })
    .then(result => {
      if (result.isConfirmed) {
        axios.post("/api/internal/user/" + user["id"] + "/status?active=" + active)
        .then(() => {
          notyf.open({
            type: "success",
            message: "API user status updated successfully."
          })
          triggerRefresh();
        })
        .catch(error => {
          console.error(error);
          notyf.open({
            type: "error",
            message: "Failed to update API user status."
          })
        })
      }
    });
  }

  const columnHelper = createColumnHelper();

  const columns = React.useMemo(() => [
    {
      id: "displayName",
      header: "Display Name",
      accessorFn: (d) => d.displayName,
      sortingFn: (a, b) => {
        return a.original.displayName.localeCompare(b.original.displayName);
      },
    },
    {
      id: "username",
      header: "Username",
      accessorFn: (d) => d.username,
      sortingFn: (a, b) => {
        return a.original.username.localeCompare(b.original.username);
      },
    },
    columnHelper.accessor(row => row,{
      id: "admin",
      header: "Role",
      cell: (d) => {
        if (d.getValue().admin) {
          return <Badge bg="danger">Admin</Badge>
        } else {
          return <Badge bg="info">User</Badge>
        }
      }
    }),
    columnHelper.accessor(row => row, {
      id: "status",
      header: "Status",
      cell: (d) => {
        if (d.getValue().locked) {
          return <Badge bg="warning">Locked</Badge>
        } else if (d.getValue().active) {
          return <Badge bg="success">Active</Badge>
        } else if (d.getValue().credentialsExpired) {
          return <Badge bg="warning">Password Expired</Badge>
        } else {
          return <Badge bg="danger">Inactive</Badge>
        }
      }
    }),
    columnHelper.accessor(row => row, {
      id: "controls",
      header: "",
      cell: (cell) => {
        const d = cell.getValue();
        return (
            <React.Fragment>

              <Dropdown>

                <Dropdown.Toggle variant={"outline-primary"}>
                  &nbsp;Options&nbsp;
                </Dropdown.Toggle>

                <Dropdown.Menu>

                  <Dropdown.Item
                      onClick={() => handleUserUpdate(d)}
                  >
                    <FontAwesomeIcon icon={faEdit}/>
                    &nbsp;&nbsp;
                    Edit API User
                  </Dropdown.Item>

                  <Dropdown.Divider/>

                  {
                    d.active ? (
                        <Dropdown.Item
                            className={"text-warning"}
                            onClick={() => toggleApiUserActive(d, false)}
                        >
                          <FontAwesomeIcon icon={faTimesCircle}/>
                          &nbsp;&nbsp;
                          Set Inactive
                        </Dropdown.Item>
                    ) : (
                        <Dropdown.Item
                            className={"text-warning"}
                            onClick={() => toggleApiUserActive(d, true)}
                        >
                          <FontAwesomeIcon icon={faCheckCircle}/>
                          &nbsp;&nbsp;
                          Set Active
                        </Dropdown.Item>
                    )
                  }

                  <Dropdown.Item
                      className={"text-warning"}
                      onClick={() => resetSecret(d)}
                  >
                    <FontAwesomeIcon icon={faRedo}/>
                    &nbsp;&nbsp;
                    Reset Secret Key
                  </Dropdown.Item>

                </Dropdown.Menu>
              </Dropdown>

            </React.Fragment>
        )
      }
    }),
  ], []);

  return (
    <DataTable data={users} columns={columns} />
  )

};

ApiUserTable.propTypes = {
  users: PropTypes.array.isRequired,
  handleUserUpdate: PropTypes.func.isRequired,
  triggerRefresh: PropTypes.func.isRequired,
}

export default ApiUserTable;