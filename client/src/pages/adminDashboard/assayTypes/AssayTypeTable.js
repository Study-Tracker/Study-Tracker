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

import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
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
import {useMutation, useQueryClient} from "react-query";
import NotyfContext from "../../../context/NotyfContext";
import AssayTypeDetailsModal from "./AssayTypeDetailsModal";
import {useNavigate} from "react-router-dom";

const AssayTypeTable = ({assayTypes}) => {

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedAssayType, setSelectedAssayType] = useState(null);
  const queryClient = useQueryClient();
  const notyf = useContext(NotyfContext);
  const navigate = useNavigate();

  const columns = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      headerStyle: {width: '20%'},
      formatter: (c, d, i, x) => (
          <Button variant="link" onClick={() => showModal(d)}>{d.name}</Button>
      ),
      sortFunc: (a, b, order, dataField, rowA, rowB) => {
        if (rowA.name > rowB.name) {
          return order === "desc" ? -1 : 1;
        }
        if (rowB.name > rowA.name) {
          return order === "desc" ? 1 : -1;
        }
        return 0;
      }
    },
    {
      dataField: "description",
      text: "Description",
      sort: false,
      headerStyle: {width: '40%'},
      formatter: (c, d, i, x) => d.description
    },
    {
      dataField: "active",
      text: "Status",
      sort: false,
      headerStyle: {width: '20%'},
      formatter: (c, d, i, x) => {
        return !!d.active
            ? <Badge bg="success">Active</Badge>
            : <Badge bg="warning">Inactive</Badge>
      }
    },
    {
      dataField: "controls",
      text: "",
      sort: false,
      headerStyle: {width: '20%'},
      formatter: (c, d, i, x) => {
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
                        !!d.active ? (
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
    }
  ];

  const showModal = (selected) => {
    if (!!selected) {
      setIsModalOpen(true);
      setSelectedAssayType(selected);
    } else {
      setIsModalOpen(false);
    }
  }

  const mutation = useMutation((assayTypeId) => {
    return axios.patch(`/api/internal/assaytype/${assayTypeId}`)
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
        <ToolkitProvider
            keyField="id"
            data={assayTypes}
            columns={columns}
            search
        >
          {props => (
              <div>
                <div className="float-end">
                  <Search.SearchBar{...props.searchProps} />
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