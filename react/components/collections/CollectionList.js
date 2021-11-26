import React from 'react';
import {Button, Card, Col, Container, Row} from "react-bootstrap";
import {File} from "react-feather";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";

const myCollectionColumns = [
  {
    dataField: "name",
    text: "Name",
    sort: true,
    formatter: (cell, d, index, x) => {
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
    formatter: (cell, d, index, x) => d.description
  },
  {
    dataField: "shared",
    text: "Visibility",
    sort: true,
    formatter: (cell, d, index, x) => {
      if (!!d.shared) return <div className="badge badge-success">Public</div>
      else return <div className="badge badge-warning">Private</div>
    }
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
    dataField: "studies",
    text: "No. Studies",
    sort: true,
    formatter: (cell, d, index, x) => d.studies.length
  }
];

const publicCollectionColumns = [
  {
    dataField: "name",
    text: "Name",
    sort: true,
    formatter: (cell, d, index, x) => {
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
    formatter: (cell, d, index, x) => d.description
  },
  {
    dataField: "createdBy",
    text: "Created By",
    sort: true,
    formatter: (cell, d, index, x) => d.createdBy.displayName
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
    dataField: "studies",
    text: "No. Studies",
    sort: true,
    formatter: (cell, d, index, x) => d.studies.length
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

export const CollectionList = ({collections, user}) => {

  const myCollections = collections.filter(c => c.createdBy.id === user.id);
  const publicCollections = collections.filter(c => c.createdBy.id !== user.id && !!c.shared);

  return (
    <Container fluid className="animated fadeIn">

      <Row className="justify-content-between align-items-center mb-2">
        <Col xs={8}>
          <h3>Study Collections</h3>
        </Col>
        <Col xs={"auto"}>
          <Button variant="primary" href="/collections/new" className="me-1 mb-1">
            <FontAwesomeIcon icon={faPlusCircle}/> New Collection
          </Button>
        </Col>
      </Row>

      <Row>
        <Col lg={12}>
          <Card className="details-card">
            <Card.Header>
              <Card.Title>
                My Collections
              </Card.Title>
            </Card.Header>
            <Card.Body>
                <MyCollectionTable collections={myCollections} />
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
              <PublicCollectionsTable collections={publicCollections} />
            </Card.Body>
          </Card>
        </Col>
      </Row>

    </Container>
  )
}