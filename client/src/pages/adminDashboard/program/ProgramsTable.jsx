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
import PropTypes from "prop-types";
import { createColumnHelper } from "@tanstack/react-table";
import DataTable from "../../../common/DataTable";

const ProgramsTable = ({programs, showModal, handleStatusChange}) => {

  const navigate = useNavigate();

  const columnHelper = createColumnHelper();
  const columns = React.useMemo(() => [
    columnHelper.accessor(row => row, {
      id: "name",
      header: "Name",
      cell: (d) => (
        <Button
          variant="link"
          onClick={() => showModal(d)}
        >
          {d.name}
        </Button>
      ),
      sortingFn: (a, b) => {
        return a.original.name.localeCompare(b.original.name);
      },
    }),
    {
      id: "code",
      header: "Code",
      accessorFn: (d) => d.code,
    },
    {
      id: "createdAt",
      header: "Created",
      accessorFn: (d) => new Date(d.createdAt).toLocaleDateString()
    },
    {
      id: "createdBy",
      header: "Created By",
      accessorFn: (d) => d.createdBy.displayName,
    },
    columnHelper.accessor(row => row, {
      id: "status",
      header: "Status",
      cell: (d) => {
        if (d.active) {
          return <Badge bg="success">Active</Badge>
        } else {
          return <Badge bg="danger">Inactive</Badge>
        }
      }
    }),
    columnHelper.accessor(row => row, {
      id: "controls",
      header: "",
      cell: (d) => {
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
    }),
  ], []);

  return (
    <DataTable data={programs} columns={columns} />
  )

}

ProgramsTable.propTypes = {
  programs: PropTypes.array.isRequired,
  showModal: PropTypes.func.isRequired,
  handleStatusChange: PropTypes.func.isRequired
}

export default ProgramsTable;