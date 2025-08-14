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
import {Button, Card, Col, Container, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";
import { createColumnHelper } from "@tanstack/react-table";
import DataTable from "../../common/DataTable";

export const MyCollectionTable = ({collections}) => {

  const columnHelper = createColumnHelper();

  const myCollectionColumns = React.useMemo(() => [
    columnHelper.accessor(row => row, {
      id: "name",
      header: "Name",
      cell: (d) => {
        return (
          <a href={"/collection/" + d.getValue().id}>
            {d.getValue().name}
          </a>
        )
      },
      sortingFn: (a, b) => {
        return a.original.name.localeCompare(b.original.name);
      },
    }),
    {
      id: "description",
      header: "Description",
      accessorFn: (d) => d.description,
    },
    columnHelper.accessor(row => row, {
      id: "shared",
      header: "Visibility",
      cell: (d) => {
        if (d.getValue().shared) {
          return <div className="badge badge-success">Public</div>
        } else {
          return <div className="badge badge-warning">Private</div>
        }
      }
    }),
    {
      id: "updatedAt",
      header: "Last Updated",
      accessorFn: (d) => new Date(d.updatedAt).toLocaleDateString(),
      sortingFn: (a, b) => a.original.updatedAt - b.original.updatedAt,
    },
    {
      id: "noStudies",
      header: "No. Studies",
      accessorFn: (d) => d.studies.length
    }
  ], []);

  return (
    <DataTable data={collections} columns={myCollectionColumns} />
  )
}

MyCollectionTable.propTypes = {
  collections: PropTypes.array.isRequired
}

export const PublicCollectionsTable = ({collections}) => {

  const columnHelper = createColumnHelper();
  const publicCollectionColumns = React.useMemo(() => [
    columnHelper.accessor(row => row, {
      id: "name",
      header: "Name",
      cell: (d) => {
        return (
          <a href={"/collection/" + d.getValue().id}>
            {d.getValue().name}
          </a>
        )
      },
      sortingFn: (a, b) => {
        return a.original.name.localeCompare(b.original.name);
      },
    }),
    {
      id: "description",
      header: "Description",
      accessorFn: (d) => d.description
    },
    {
      id: "createdBy",
      header: "Created By",
      accessorFn: (d) => d.createdBy.displayName
    },
    {
      id: "updatedAt",
      header: "Last Updated",
      accessorFn: (d) => new Date(d.updatedAt).toLocaleDateString()
    },
    {
      id: "studies",
      header: "No. Studies",
      accessorFn: (d) => d.studies.length
    }
  ], []);

  return (
    <DataTable data={collections} columns={publicCollectionColumns} />
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