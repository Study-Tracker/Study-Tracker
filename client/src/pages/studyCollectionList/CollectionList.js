/*
 * Copyright 2022 the original author or authors.
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
import {Button, Card, Col, Container, Row} from "react-bootstrap";
import {File} from "react-feather";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";

const myCollectionColumns = [
  {
    dataField: "name",
    text: "Name",
    sort: true,
    formatter: (cell, d) => {
      return (
          <a href={"/collection/" + d.id}>
            {d.name}
          </a>
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
    dataField: "description",
    text: "Description",
    sort: false,
    formatter: (cell, d) => d.description
  },
  {
    dataField: "shared",
    text: "Visibility",
    sort: true,
    formatter: (cell, d) => {
      if (!!d.shared) {
        return <div className="badge badge-success">Public</div>
      } else {
        return <div className="badge badge-warning">Private</div>
      }
    }
  },
  {
    dataField: "updatedAt",
    text: "Last Updated",
    sort: true,
    searchable: false,
    headerStyle: {width: '10%'},
    formatter: (c, d) => new Date(d.updatedAt).toLocaleDateString()
  },
  {
    dataField: "studies",
    text: "No. Studies",
    sort: true,
    formatter: (cell, d) => d.studies.length
  }
];

const publicCollectionColumns = [
  {
    dataField: "name",
    text: "Name",
    sort: true,
    formatter: (cell, d) => {
      return (
          <a href={"/collection/" + d.id}>
            {d.name}
          </a>
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
    dataField: "description",
    text: "Description",
    sort: false,
    formatter: (cell, d) => d.description
  },
  {
    dataField: "createdBy",
    text: "Created By",
    sort: true,
    formatter: (cell, d) => d.createdBy.displayName
  },
  {
    dataField: "updatedAt",
    text: "Last Updated",
    sort: true,
    searchable: false,
    headerStyle: {width: '10%'},
    formatter: (c, d) => new Date(d.updatedAt).toLocaleDateString()
  },
  {
    dataField: "studies",
    text: "No. Studies",
    sort: true,
    formatter: (cell, d) => d.studies.length
  }
];

const ExportToCsv = ({onExport}) => {
  const handleClick = () => {
    onExport();
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

ExportToCsv.propTypes = {
  onExport: PropTypes.func.isRequired
}

export const MyCollectionTable = ({collections}) => {
  return (
      <ToolkitProvider
          keyField="id"
          data={collections}
          columns={myCollectionColumns}
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

MyCollectionTable.propTypes = {
  collections: PropTypes.array.isRequired
}

export const PublicCollectionsTable = ({collections}) => {
  return (
      <ToolkitProvider
          keyField="id"
          data={collections}
          columns={publicCollectionColumns}
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

PublicCollectionsTable.propTypes = {
  collections: PropTypes.array.isRequired
}

export const CollectionList = ({collections, user}) => {

  const myCollections = collections.filter(c => c.createdBy.id === user.id && !c.shared);
  const publicCollections = collections.filter(c => !!c.shared);

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center mb-2">
          <Col xs={8}>
            <h3>Study Collections</h3>
          </Col>
          <Col xs={"auto"}>
            <Button variant="primary" href="/collections/new"
                    className="me-1 mb-1">
              <FontAwesomeIcon icon={faPlusCircle}/> New Collection
            </Button>
          </Col>
        </Row>

        <Row>
          <Col lg={12}>
            <Card className="details-card">
              <Card.Header>
                <Card.Title>
                  My Private Collections
                </Card.Title>
              </Card.Header>
              <Card.Body>
                <MyCollectionTable collections={myCollections}/>
              </Card.Body>
            </Card>
          </Col>
        </Row>

        <Row>
          <Col lg={12}>
            <Card className="details-card">
              <Card.Header>
                <Card.Title>
                  Public Collections
                </Card.Title>
              </Card.Header>
              <Card.Body>
                <PublicCollectionsTable collections={publicCollections}/>
              </Card.Body>
            </Card>
          </Col>
        </Row>

      </Container>
  )
}

CollectionList.propTypes = {
  collections: PropTypes.array.isRequired,
  user: PropTypes.object.isRequired
}