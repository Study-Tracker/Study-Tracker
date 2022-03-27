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

import {
  Breadcrumb,
  Button,
  Card,
  Col,
  Container,
  Dropdown,
  Row
} from "react-bootstrap";
import React from "react";
import {File, Menu, XCircle} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-solid-svg-icons";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import {StatusBadge} from "../status";

const StudyCollectionDetailsHeader = ({collection}) => {
  return (
      <Row className="justify-content-between align-items-center">
        <Col>
          <h3>{collection.name}</h3>
        </Col>
      </Row>
  );
};

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

class StudyCollectionDetails extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      activeTab: "1"
    };
  }

  toggle(tab) {
    if (this.state.activeTab !== tab) {
      this.setState({
        activeTab: tab
      });
    }
  }

  render() {

    const {collection} = this.props;

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
        dataField: "remove",
        text: "Remove",
        sort: false,
        searchable: false,
        headerStyle: {width: '10%'},
        formatter: (c, d, i, x) => {
          return (
              <div>
                <a className="text-danger" title={"Remove study from collection"}
                   onClick={() => this.props.handleRemoveStudy(d.id)}>
                  <XCircle className="align-middle me-1" size={18}/>
                </a>
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
              d.collaborator.contactPersonName
              : '';
          return (
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
              (d.externalCode || '')
          );
        }
      }
    ];

    return (
        <Container fluid className="animated fadeIn">

          {/* Breadcrumb */}
          <Row>
            <Col>
              <Breadcrumb>
                <Breadcrumb.Item href={"/collections"}>
                  Collections
                </Breadcrumb.Item>
                <Breadcrumb.Item active>
                  Collection Detail
                </Breadcrumb.Item>
              </Breadcrumb>
            </Col>
          </Row>

          {/* Header */}
          <StudyCollectionDetailsHeader collection={collection} user={this.props.user}/>

          <Row>

            <Col xs={12} md={6}>
              <Card className="details-card">

                <Card.Header>
                  <div className="card-actions float-end">
                    <Dropdown align="end">
                      <Dropdown.Toggle as="a" bsPrefix="-">
                        <Menu/>
                      </Dropdown.Toggle>
                      <Dropdown.Menu>

                        {/*<DropdownItem onClick={() => console.log("Share!")}>*/}
                        {/*  <FontAwesomeIcon icon={faShare}/>*/}
                        {/*  &nbsp;*/}
                        {/*  Share*/}
                        {/*</DropdownItem>*/}
                        {/*{*/}
                        {/*  !!this.props.user && !!this.props.user.admin ?*/}
                        {/*      <DropdownItem divider/> : ''*/}
                        {/*}*/}

                        <Dropdown.Item href={"/collection/" + collection.id + "/edit"}>
                          <FontAwesomeIcon icon={faEdit}/>
                          &nbsp;
                          Edit
                        </Dropdown.Item>

                        {/*{*/}
                        {/*  !!this.props.user && !!this.props.user.admin ? (*/}
                        {/*      <DropdownItem*/}
                        {/*          onClick={() => console.log("Delete!")}>*/}
                        {/*        <FontAwesomeIcon icon={faTrash}/>*/}
                        {/*        &nbsp;*/}
                        {/*        Delete*/}
                        {/*      </DropdownItem>*/}
                        {/*  ) : ''*/}
                        {/*}*/}

                      </Dropdown.Menu>
                    </Dropdown>
                  </div>
                  <Card.Title tag="h5" className="mb-0 text-muted">
                    Summary
                  </Card.Title>
                </Card.Header>

                <Card.Body>

                  <Row>

                    <Col xs={12}>
                      <h6 className="details-label">Description</h6>
                      <p>{collection.name}</p>
                    </Col>

                  </Row>

                  <Row>

                    <Col xs={6} sm={4}>
                      <h6 className="details-label">Created By</h6>
                      <p>{collection.createdBy.displayName}</p>
                    </Col>

                    <Col xs={6} sm={4}>
                      <h6 className="details-label">Last Modified By</h6>
                      <p>{collection.lastModifiedBy.displayName}</p>
                    </Col>

                  </Row>

                  <Row>

                    <Col xs={6} sm={4}>
                      <h6 className="details-label">Date Created</h6>
                      <p>{new Date(collection.createdAt).toLocaleString()}</p>
                    </Col>

                    <Col xs={6} sm={4}>
                      <h6 className="details-label">Last Updated</h6>
                      <p>{new Date(collection.updatedAt).toLocaleString()}</p>
                    </Col>

                  </Row>

                </Card.Body>

              </Card>
            </Col>

            <Col xs={12}>
              <Card className="details-card">

                <Card.Header>
                  <Card.Title tag="h5" className="mb-0 text-muted">
                    Studies
                  </Card.Title>
                </Card.Header>

                <Card.Body>
                  <Container fluid className="animated fadeIn">

                    <Row>
                      <Col lg={12}>
                        <ToolkitProvider
                            keyField="id"
                            data={this.props.collection.studies}
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

                  </Container>
                </Card.Body>

              </Card>
            </Col>

          </Row>
        </Container>
    );
  }

}

export default StudyCollectionDetails;