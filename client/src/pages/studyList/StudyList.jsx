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
import PropTypes from "prop-types";
import { createColumnHelper } from "@tanstack/react-table";
import DataTable from "../../common/DataTable";

export const StudyListTable = ({studies}) => {

  const columnHelper = createColumnHelper();
  const columns = React.useMemo(() => [
    columnHelper.accessor(row => row, {
      id: "code",
      header: "Code",
      cell: (d) => {
        return (
          <a href={"/study/" + d.getValue().code}>
            {d.getValue().code}
          </a>
        )
      },
      sortingFn: (a, b) => {
        return a.original.code.localeCompare(b.original.code);
      },
    }),
    columnHelper.accessor(row => row, {
      id: "status",
      header: "Status",
      sortingFn: (a, b) => {
        return a.original.status.localeCompare(b.original.status);
      },
      cell: (d) => <StatusBadge status={d.getValue().status}/>
    }),
    {
      id: "program",
      header: "Program",
      sortingFn: (a, b) => {
        return a.original.program.name.localeCompare(b.original.program.name);
      },
      accessorFn: (d) => d.program.name,
    },
    {
      id: "name",
      header: "Name",
      accessorFn: (d) => d.name
    },
    {
      id: "owner",
      header: "Owner",
      accessorFn: (d) => d.owner.displayName,
    },
    {
      id: "startDate",
      header: "Start Date",
      accessorFn: (d) => new Date(d.startDate).toLocaleDateString(),
      sortingFn: (a, b) => a.original.startDate - b.original.startDate,
    },
    {
      id: "createdAt",
      header: "Created",
      accessorFn: (d) => new Date(d.createdAt).toLocaleDateString(),
      sortingFn: (a, b) => a.original.createdAt - b.original.createdAt,
    },
    {
      id: "updatedAt",
      header: "Last Updated",
      accessorFn: (d) => new Date(d.updatedAt).toLocaleDateString(),
      sortingFn: (a, b) => a.original.updatedAt - b.original.updatedAt,
    },
    columnHelper.accessor(row => row, {
      id: "cro",
      header: "CRO / Collaborator",
      sortingFn: (a, b) => {
        const da = a.original.collaborator ? a.original.collaborator.organizationName
          : '';
        const db = b.original.collaborator ? b.original.collaborator.organizationName
          : '';
        return da.localeCompare(db);
      },
      cell: (d) => d.getValue().collaborator && (
          <div>
            <p style={{fontWeight: 'bold', marginBottom: '0.2rem'}}>
              {d.getValue().collaborator.organizationName}
            </p>
            <p>
              {d.getValue().externalCode}
            </p>
          </div>
        )
    }),
  ], []);

  return (
    <DataTable data={studies} columns={columns} />
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
              user && (
                  <a href="/studies/new">
                    <Button color="primary" className="me-1 mb-1">
                      <FontAwesomeIcon icon={faPlusCircle}/> New Study
                    </Button>
                  </a>
              )
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