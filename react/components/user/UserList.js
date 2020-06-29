/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import {Button, Card, CardBody, Col, Container, Row,} from "reactstrap";
import BootstrapTable from "react-bootstrap-table-next";
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import paginationFactory from "react-bootstrap-table2-paginator";
import {File} from "react-feather";

const columns = [
  {
    dataField: "displayName",
    text: "Name",
    sort: true,
    // headerStyle: {width: '20%%'},
    formatter: (c, d, i, x) => {
      return (
          <a href={"/user/" + d.id}>{d.displayName}</a>
      )
    },
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
    dataField: "email",
    text: "Email",
    sort: true,
    // headerStyle: {width: '20%'},
    formatter: (cell, d, index, x) => d.email
  },
  {
    dataField: "department",
    text: "Department",
    sort: true,
    // headerStyle: {width: '20%'},
    formatter: (cell, d, index, x) => d.department
  },
  {
    dataField: "admin",
    text: "Admin",
    sort: true,
    // headerStyle: {width: '10%'},
    formatter: (c, d, i, x) => {
      if (d.admin) {
        return (
            <div className="badge badge-danger">
              Admin
            </div>
        )
      }
    }
  },
  {
    dataField: "active",
    text: "Active",
    sort: true,
    // headerStyle: {width: '10%'},
    formatter: (c, d, i, x) => {
      if (d.active) {
        return (
            <div className="badge badge-success">
              Active
            </div>
        )
      } else {
        return (
            <div className="badge badge-warning">
              Inactive
            </div>
        )
      }
    }
  }
];

const ExportToCsv = (props) => {
  const handleClick = () => {
    props.onExport();
  };
  return (
      <span>
        <Button color={'primary'} onClick={handleClick}>
          Export to CSV
          &nbsp;
          <File className="feather align-middle ml-2 mb-1"/>
        </Button>
      </span>
  );
};

const UserList = ({title, user, users}) => {

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center">
          <Col xs="8">
            <h1>{title}</h1>
          </Col>
          <Col className="col-auto">
            {
              // !!user && !!user.admin
              //     ? (
              //         <a href={"/users/new"}>
              //           <Button color="primary" className="mr-1 mb-1">
              //             <FontAwesomeIcon icon={faPlusCircle}/> New User
              //           </Button>
              //         </a>
              //     ) : ''
            }
          </Col>
        </Row>

        <Row>
          <Col lg="12">
            <Card>
              <CardBody>
                <ToolkitProvider
                    keyField="id"
                    data={users}
                    columns={columns}
                    search
                    exportCSV
                >
                  {props => (
                      <div>
                        <div className="float-right">
                          <ExportToCsv{...props.csvProps} />
                          &nbsp;&nbsp;
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
                              dataField: "updatedAt",
                              order: "desc"
                            }]}
                            {...props.baseProps}
                        >
                        </BootstrapTable>
                      </div>
                  )}
                </ToolkitProvider>
              </CardBody>
            </Card>
          </Col>
        </Row>

      </Container>
  );

}

export default UserList;