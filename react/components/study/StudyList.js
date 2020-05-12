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
import {Button, Card, CardBody, Col, Container, Media, Row} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import BootstrapTable from "react-bootstrap-table-next";
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import paginationFactory from "react-bootstrap-table2-paginator";
import {Activity, Clipboard, File, Star, ThumbsUp, Users} from "react-feather";
import {Timeline} from "../activity";
import {studyActions} from "../../config/activityConstants";
import {statuses} from "../../config/statusConstants";

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

  // Get study activity
  let activities = [];
  studies.forEach(s => {
    activities = [...activities, ...s.activity]
  });
  activities.sort((a, b) => {
    if (a.date > b.date) {
      return -1;
    } else if (a.date < b.date) {
      return 1;
    } else {
      return 0;
    }
  });

  // Get count of recent activity
  let activityCount = 0;
  let completed = 0;
  let newStudies = 0;
  let activeUsers = [];
  const day = 24 * 60 * 60 * 60;
  const week = day * 7;
  const month = day * 30;
  activities.forEach(a => {
    if (a.date >= day) {
      activityCount = activityCount + 1;
    }
    if (a.action === studyActions.NEW_STUDY && a.date
        >= week) {
      newStudies = newStudies + 1;
    }
    if (a.action === studyActions.STUDY_STATUS_CHANGED
        && a.data === statuses.COMPLETE.value && a.date
        >= month) {
      completed = completed + 1;
    }
    if (a.date >= month && activeUsers.indexOf(a.userAccountName)
        === -1) {
      activeUsers.push(a.userAccountName);
    }
  });

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

        <Row>

          <Col lg={3}>

            <Row className="study-statistics">

              <Col xs={6} md={4} lg={12}>
                <Card className="flex-fill">
                  <CardBody className="py-4">
                    <Media>
                      <div className="d-inline-block mt-2 mr-3">
                        <Activity className="feather-lg text-warning"/>
                      </div>
                      <Media body>
                        <h3 className="mb-2">{activityCount}</h3>
                        <div className="mb-0">Study Updates Today</div>
                      </Media>
                    </Media>
                  </CardBody>
                </Card>
              </Col>

              <Col xs={6} sm={4} md={3} lg={12}>
                <Card className="flex-fill">
                  <CardBody className="py-4">
                    <Media>
                      <div className="d-inline-block mt-2 mr-3">
                        <Users className="feather-lg text-primary"/>
                      </div>
                      <Media body>
                        <h3 className="mb-2">{activeUsers.length}</h3>
                        <div className="mb-0">Active Users</div>
                      </Media>
                    </Media>
                  </CardBody>
                </Card>
              </Col>

              <Col xs={6} sm={4} md={3} lg={12}>
                <Card className="flex-fill">
                  <CardBody className="py-4">
                    <Media>
                      <div className="d-inline-block mt-2 mr-3">
                        <Star className="feather-lg text-warning"/>
                      </div>
                      <Media body>
                        <h3 className="mb-2">{newStudies}</h3>
                        <div className="mb-0">New Studies This Week</div>
                      </Media>
                    </Media>
                  </CardBody>
                </Card>
              </Col>

              <Col xs={6} sm={4} md={3} lg={12}>
                <Card className="flex-fill">
                  <CardBody className="py-4">
                    <Media>
                      <div className="d-inline-block mt-2 mr-3">
                        <ThumbsUp className="feather-lg text-success"/>
                      </div>
                      <Media body>
                        <h3 className="mb-2">{completed}</h3>
                        <div className="mb-0">Studies Completed This Month</div>
                      </Media>
                    </Media>
                  </CardBody>
                </Card>
              </Col>

              <Col xs={6} sm={4} md={3} lg={12}>
                <Card className="flex-fill">
                  <CardBody className="py-4">
                    <Media>
                      <div className="d-inline-block mt-2 mr-3">
                        <Clipboard className="feather-lg text-primary"/>
                      </div>
                      <Media body>
                        <h3 className="mb-2">{studies.length}</h3>
                        <div className="mb-0">Total Studies</div>
                      </Media>
                    </Media>
                  </CardBody>
                </Card>
              </Col>

            </Row>

          </Col>

          <Col lg={9}>
            <Card>
              <CardBody>
                <Row>

                  <Col xs={12}>
                    <h3>Latest Activity</h3>
                  </Col>

                  <Col xs={12}>
                    <hr/>
                  </Col>

                  <Col xs={12}>
                    <Timeline activities={activities}/>
                  </Col>
                </Row>
              </CardBody>
            </Card>
          </Col>

        </Row>

      </Container>
  );

};

export default StudyList;