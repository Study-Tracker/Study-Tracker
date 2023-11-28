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

import {Button, Card, Col, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import {useQuery} from "react-query";
import axios from "axios";
import {LoadingMessageCard} from "../../common/loading";
import {CardErrorMessage} from "../../common/errors";
import PropTypes from "prop-types";
import {StatusBadge} from "../../common/status";
import {ExportToCsv} from "../../common/tables";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";

const ProgramStudiesTab = ({program}) => {

  const {data: studies, isLoading, error} = useQuery(["programStudies", program.id], () => {
    return axios.get(`/api/internal/study?program=${program.id}`)
    .then(response => response.data);
  });

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

  if (isLoading) return <LoadingMessageCard/>;

  if (error) return <CardErrorMessage error={error}/>;

  return (
      <Card>
        <Card.Body>

          <Row className="justify-content-between align-items-center mb-3">
            <Col>
              <span className="float-end">
                <Button variant="info" href={"/studies/new"}>
                  New Study
                  &nbsp;
                  <FontAwesomeIcon icon={faPlusCircle}/>
                </Button>
              </span>
            </Col>
          </Row>

          <Row>
            <Col>
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
            </Col>
          </Row>

        </Card.Body>
      </Card>
  );

};

ProgramStudiesTab.propTypes = {
  program: PropTypes.object.isRequired
}

export default ProgramStudiesTab;