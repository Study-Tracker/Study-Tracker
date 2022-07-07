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
import {Badge, Button, Card, Col, Container, Row,} from "react-bootstrap";
import BootstrapTable from "react-bootstrap-table-next";
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import paginationFactory from "react-bootstrap-table2-paginator";
import {File} from "react-feather";

const columns = [
  {
    dataField: "name",
    text: "Name",
    sort: true,
    headerStyle: {width: '50%%'},
    formatter: (c, d, i, x) => {
      return (
          <a href={"/program/" + d.id}>{d.name}</a>
      )
    },
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
    headerStyle: {width: '20%'},
    formatter: (cell, d, index, x) => d.code
  },
  {
    dataField: "active",
    text: "Active",
    sort: true,
    headerStyle: {width: '20%'},
    formatter: (c, d, i, x) => {
      if (d.active) {
        return (
            <Badge bg="success">
              Active
            </Badge>
        )
      } else {
        return (
            <Badge bg="warning">
              Inactive
            </Badge>
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
        <Button variant={'primary'} onClick={handleClick}>
          Export to CSV
          &nbsp;
          <File className="feather align-middle ms-2 mb-1"/>
        </Button>
      </span>
  );
};

const ProgramList = ({programs}) => {

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center mb-2">
          <Col>
            <h3>Programs</h3>
          </Col>
        </Row>

        <Row>
          <Col lg={12}>
            <Card>
              <Card.Body>
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
                              dataField: "name",
                              order: "asc"
                            }]}
                            {...props.baseProps}
                        >
                        </BootstrapTable>
                      </div>
                  )}
                </ToolkitProvider>
              </Card.Body>
            </Card>
          </Col>
        </Row>

      </Container>
  );

}

export default ProgramList;