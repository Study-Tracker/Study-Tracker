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
import {StatusBadge} from "../status";
import {Button, Card, CardBody, Col, Container, Row} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import BootstrapTable from "react-bootstrap-table-next";
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import paginationFactory from "react-bootstrap-table2-paginator";
import {File} from "react-feather";

const columns = [
  {
    dataField: "code",
    text: "Code",
    sort: true,
    headerStyle: {width: '10%'},
    formatter: (cell, d, index, x) => {
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
      if (rowA.status.label > rowB.status.label) {
        return order === "desc" ? -1 : 1;
      }
      if (rowB.status.label > rowA.status.label) {
        return order === "desc" ? 1 : -1;
      }
      return 0;
    },
    formatter: (c, d, i, x) => <StatusBadge status={d.status}/>
  },
  {
    dataField: "updatedAt",
    text: "Last Updated",
    sort: true,
    searchable: false,
    headerStyle: {width: '10%'},
    formatter: (c, d, i, x) => new Date(d.updatedAt).toLocaleDateString()
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
    formatter: (cell, d, i, x) => d.program.name
  },
  {
    dataField: "name",
    text: "Name",
    sort: true,
    headerStyle: {width: '25%'},
    formatter: (c, d, i, x) => d.name
  },
  {
    dataField: "owner",
    text: "Owner",
    sort: true,
    headerStyle: {width: '10%'},
    formatter: (c, d, i, x) => d.owner.displayName
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
    formatter: (c, d, i, x) => !!d.collaborator
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
    dataField: "links",
    text: "Links",
    sort: false,
    searchable: false,
    headerStyle: {width: '10%'},
    formatter: (c, d, i, x) => {
      let links = [];
      if (!!d.storageFolder) {
        links.push(
            <a key={'files-links-' + d.id} target="_blank"
               href={d.storageFolder.url}>Files</a>
        )
      }
      if (!!d.notebookEntries && d.notebookEntries.length > 0) {
        const e = d.notebookEntries[0];
        if (links.length > 0) {
          links.push(" | ");
        }
        links.push(
            <a key={'eln-links-' + d.id} target="_blank" href={e.url}>ELN</a>
        )
      }
      return (
          <div>
            {links}
          </div>
      )
    }
  },
  {
    dataField: 'search',
    text: 'Search',
    sort: false,
    isDummyField: true,
    hidden: true,
    formatter: (c, d, i, x) => '',
    filterValue: (c, d, i, x) => {
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
          d.createdBy.displayName +
          ' ' +
          d.owner.displayName +
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

const ExportToCsv = (props) => {
  const handleClick = () => {
    props.onExport();
  };
  return (
      <span>
        <Button color={'primary'} onClick={handleClick}>
          Export to CSV
          &nbsp;
          {/*<FontAwesomeIcon icon={faFile} />*/}
          <File className="feather align-middle ml-2 mb-1"/>
        </Button>
      </span>
  );
};

const StudyList = ({studies, title, filters, user}) => {

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center">
          <Col xs="8">
            <h1>{title}</h1>
          </Col>
          <Col className="col-auto">
            {
              !!user
                  ? (
                      <a href="/studies/new">
                        <Button color="primary" className="mr-1 mb-1">
                          <FontAwesomeIcon icon={faPlusCircle}/> New Study
                        </Button>
                      </a>
                  ) : ''
            }
          </Col>
        </Row>

        <Row>
          <Col lg="12">
            <Card>
              <CardBody>
                <ToolkitProvider
                    keyField="id"
                    data={studies}
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
                            // data={studies}
                            // columns={columns}
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

};

export default StudyList;