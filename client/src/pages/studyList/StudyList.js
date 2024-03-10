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

import React from "react";
import {StatusBadge} from "../../common/status";
import {Button, Card, Col, Container, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import BootstrapTable from "react-bootstrap-table-next";
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import paginationFactory from "react-bootstrap-table2-paginator";
import {ExportToCsv} from "../../common/tables";
import PropTypes from "prop-types";

const columns = [
  {
    dataField: "code",
    text: "Code",
    sort: true,
    headerStyle: {width: '10%'},
    formatter: (cell, d) => {
      return (
          <a href={"/study/" + d.code}>
            {d.code}
          </a>
      )
    },
    sortFunc: (a, b, order, dataField, rowA, rowB) => {
      if (rowA.code > rowB.code) {
        return order === "desc" ? -1 : 1;
      }
      if (rowB.code > rowA.code) {
        return order === "desc" ? 1 : -1;
      }
      return 0;
    },
  },
  {
    dataField: "status",
    text: "Status",
    sort: true,
    headerStyle: {width: '10%'},
    sortFunc: (a, b, order, dataField, rowA, rowB) => {
      if (rowA.status > rowB.status) {
        return order === "desc" ? -1 : 1;
      }
      if (rowB.status > rowA.status) {
        return order === "desc" ? 1 : -1;
      }
      return 0;
    },
    formatter: (c, d) => <StatusBadge status={d.status}/>
  },
  {
    dataField: "program",
    text: "Program",
    sort: true,
    headerStyle: {width: '10%'},
    sortFunc: (a, b, order, dataField, rowA, rowB) => {
      if (rowA.program.name > rowB.program.name) {
        return order === "desc" ? -1 : 1;
      }
      if (rowB.program.name > rowA.program.name) {
        return order === "desc" ? 1 : -1;
      }
      return 0;
    },
    formatter: (cell, d) => d.program.name,
    csvFormatter: (cell, d) => d.program.name
  },
  {
    dataField: "name",
    text: "Name",
    sort: true,
    headerStyle: {width: '25%'},
    formatter: (c, d) => d.name
  },
  {
    dataField: "owner",
    text: "Owner",
    sort: true,
    headerStyle: {width: '10%'},
    formatter: (c, d) => d.owner.displayName,
    csvFormatter: (c, d) => d.owner.displayName
  },
  {
    dataField: "startDate",
    text: "Start Date",
    type: "date",
    sort: true,
    searchable: false,
    headerStyle: {width: '10%'},
    formatter: (c, d) => new Date(d.startDate).toLocaleDateString(),
    csvFormatter: (c, d) => new Date(d.startDate).toLocaleDateString()
  },
  {
    dataField: "createdAt",
    text: "Created",
    type: "date",
    sort: true,
    searchable: false,
    headerStyle: {width: '10%'},
    formatter: (c, d) => new Date(d.createdAt).toLocaleDateString(),
    csvFormatter: (c, d) => new Date(d.createdAt).toLocaleDateString()
  },
  {
    dataField: "updatedAt",
    text: "Last Updated",
    type: "date",
    sort: true,
    searchable: false,
    headerStyle: {width: '10%'},
    formatter: (c, d) => new Date(d.updatedAt).toLocaleDateString(),
    csvFormatter: (c, d) => new Date(d.updatedAt).toLocaleDateString()
  },
  {
    dataField: "cro",
    text: "CRO / Collaborator",
    sort: true,
    headerStyle: {width: '15%'},
    sortFunc: (a, b, order, dataField, rowA, rowB) => {
      const da = !!rowA.collaborator ? rowA.collaborator.organizationName
          : '';
      const db = !!rowB.collaborator ? rowB.collaborator.organizationName
          : '';
      if (da > db) {
        return order === "desc" ? -1 : 1;
      }
      if (db > da) {
        return order === "desc" ? 1 : -1;
      }
      return 0;
    },
    csvFormatter: (c, d) => !!d.collaborator
        ? d.collaborator.organizationName : "",
    formatter: (c, d) => !!d.collaborator
        ? (
            <div>
              <p style={{fontWeight: 'bold', marginBottom: '0.2rem'}}>
                {d.collaborator.organizationName}
              </p>
              <p>
                {d.externalCode}
              </p>
            </div>

        ) : ''
  },
  {
    dataField: 'search',
    text: 'Search',
    sort: false,
    isDummyField: true,
    hidden: true,
    csvExport: false,
    formatter: () => '',
    filterValue: (c, d) => {
      const CRO = !!d.collaborator
          ? d.collaborator.organizationName +
          ' ' +
          d.collaborator.contactName
          : '';
      let text =
          d.name +
          ' ' +
          d.status +
          ' ' +
          d.description +
          ' ' +
          d.program.name +
          ' ' +
          d.code +
          ' ' +
          CRO +
          ' ' +
          (d.owner.displayName || '') +
          ' ' +
          (d.externalCode || '');
      if (d.keywords != null) {
        d.keywords.forEach(keyword => {
          text = text + ' ' + keyword.keyword;
        });
      }
      return text;
    }
  }
];

export const StudyListTable = ({studies}) => {
  return (
      <ToolkitProvider
          keyField="id"
          data={studies}
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
                    dataField: "updatedAt",
                    order: "desc"
                  }]}
                  {...props.baseProps}
              >
              </BootstrapTable>
            </div>
        )}
      </ToolkitProvider>
  )
}

StudyListTable.propTypes = {
  studies: PropTypes.array.isRequired
}

const StudyList = ({studies, user}) => {

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center mb-2">
          <Col xs={8}>
            <h3>Studies</h3>
          </Col>
          <Col className="col-auto">
            {
              !!user
                  ? (
                      <a href="/studies/new">
                        <Button color="primary" className="me-1 mb-1">
                          <FontAwesomeIcon icon={faPlusCircle}/> New Study
                        </Button>
                      </a>
                  ) : ''
            }
          </Col>
        </Row>

        <Row>
          <Col lg={12}>
            <Card>
              <Card.Body>
                <StudyListTable studies={studies}/>
              </Card.Body>
            </Card>
          </Col>
        </Row>

      </Container>
  );

};

StudyList.propTypes = {
  studies: PropTypes.array.isRequired,
  user: PropTypes.object
}

export default StudyList;