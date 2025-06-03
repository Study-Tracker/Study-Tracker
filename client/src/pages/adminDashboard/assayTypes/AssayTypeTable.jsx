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

import React, {useContext, useState} from "react";
import {Badge, Button, Dropdown} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faCheckCircle,
  faEdit,
  faInfoCircle,
  faTimesCircle
} from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";
import axios from "axios";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import NotyfContext from "../../../context/NotyfContext";
import AssayTypeDetailsModal from "./AssayTypeDetailsModal";
import {useNavigate} from "react-router-dom";
import { createColumnHelper } from "@tanstack/react-table";
import DataTable from "../../../common/DataTable";

const AssayTypeTable = ({assayTypes}) => {

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedAssayType, setSelectedAssayType] = useState(null);
  const queryClient = useQueryClient();
  const notyf = useContext(NotyfContext);
  const navigate = useNavigate();

  const columnHelper = createColumnHelper();

  const columns = React.useMemo(() => [
    columnHelper.accessor(row => row, {
      id: "name",
      header: "Name",
      cell: (d) => (
          <Button variant="link" onClick={() => showModal(d.getValue())}>{d.getValue().name}</Button>
      ),
      sortingFn: (a, b) => {
        return a.original.name.localeCompare(b.original.name);
      }
    }),
    {
      id: "description",
      header: "Description",
      accessorFn: (d) => d.description
    },
    columnHelper.accessor(row => row, {
      id: "active",
      header: "Status",
      cell: (d) => {
        return d.getValue().active
            ? <Badge bg="success">Active</Badge>
            : <Badge bg="warning">Inactive</Badge>
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
                  {/*<FontAwesomeIcon icon={faBars} />*/}
                  &nbsp;Options&nbsp;
                </Dropdown.Toggle>

                <Dropdown.Menu>

                  <Dropdown.Item onClick={() => showModal(d)}>
                    <FontAwesomeIcon icon={faInfoCircle}/>
                    &nbsp;&nbsp;
                    View Details
                  </Dropdown.Item>

                  {
                    d.name === "Generic" ? "" : (
                        <React.Fragment>
                          <Dropdown.Divider/>
                          <Dropdown.Item
                              onClick={() => navigate(
                                  "/assaytypes/" + d.id + "/edit")}
                          >
                            <FontAwesomeIcon icon={faEdit}/>
                            &nbsp;&nbsp;
                            Edit assay type
                          </Dropdown.Item>
                        </React.Fragment>
                    )
                  }

                  {
                    d.name === "Generic" ? "" : (
                        d.active ? (
                            <Dropdown.Item
                                className={"text-warning"}
                                onClick={() => toggleActive(d)}
                            >
                              <FontAwesomeIcon icon={faTimesCircle}/>
                              &nbsp;&nbsp;
                              Set Inactive
                            </Dropdown.Item>
                        ) : (
                            <Dropdown.Item
                                className={"text-warning"}
                                onClick={() => toggleActive(d)}
                            >
                              <FontAwesomeIcon icon={faCheckCircle}/>
                              &nbsp;&nbsp;
                              Set Active
                            </Dropdown.Item>
                        )
                    )
                  }

                </Dropdown.Menu>

              </Dropdown>

            </React.Fragment>
        )
      }
    }),
  ], []);

  const showModal = (selected) => {
    if (selected) {
      setIsModalOpen(true);
      setSelectedAssayType(selected);
    } else {
      setIsModalOpen(false);
    }
  }

  const mutation = useMutation({
    mutationFn: (assayTypeId) => {
      return axios.patch(`/api/internal/assaytype/${assayTypeId}`)
    }
  });

  const toggleActive = (selected) => {
    mutation.mutate(selected.id, {
      onSuccess: () => {
        queryClient.invalidateQueries("assayTypes");
        notyf.success("Assay type updated successfully.")
      },
      onError: (error) => {
        console.error(error);
        notyf.error("An error occurred while updating the assay type.")
      }
    })
  }

  return (
      <>
        <DataTable data={assayTypes} columns={columns} />

        <AssayTypeDetailsModal
            assayType={selectedAssayType}
            isOpen={isModalOpen}
            showModal={showModal}
        />

      </>
  )
}

AssayTypeTable.propTypes = {
  assayTypes: PropTypes.array.isRequired
}

export default AssayTypeTable;