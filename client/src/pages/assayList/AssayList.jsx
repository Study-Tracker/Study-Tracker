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
import {Card, Col, Container, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import DataTable from "../../common/DataTable";
import { createColumnHelper } from "@tanstack/react-table";

const AssayList = ({assays}) => {

  const columnHelper = createColumnHelper();

  const columns = React.useMemo(
    () => [
      columnHelper.accessor(row => row, {
        id: "code",
        header: "Code",
        cell: (d) => {
          return (
            <a href={"/study/" + d.study.code + "/assay/" + d.code}>
              {d.code}
            </a>
          )
        },
        sortingFn: (a, b) => a.original.code.localeCompare(b.original.code),
      }),
      {
        id: "assayType",
        header: "Assay Type",
        sortingFn: (a, b) => a.original.assayType.name.localeCompare(b.original.assayType.name),
        accessorFn: (d) => d.assayType.name,
      },
      columnHelper.accessor(row => row, {
        id: "status",
        header: "Status",
        sortingFn: (a, b) => a.original.status.localeCompare(b.original.status),
        cell: (d) => <StatusBadge status={d.getValue().status} />,
      }),
      {
        id: "program",
        header: "Program",
        sortingFn: (a, b) => {
          return a.original.study.program.name.localeCompare(b.original.study.program.name);
        },
        accessorFn: (d) => d.study.program.name,
      },
      {
        id: "name",
        header: "Name",
        accessorFn: (d) => d.name,
        sortingFn: (a, b) => a.original.name.localeCompare(b.original.name),
      },
      {
        id: "owner",
        header: "Owner",
        accessorFn: (d) => d.owner.displayName,
        sortingFn: (a, b) => a.original.owner.displayName.localeCompare(b.original.owner.displayName),
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
        sortFunc: (a, b, order, dataField, rowA, rowB) => {
          const da = rowA.study.collaborator
            ? rowA.study.collaborator.organizationName
            : '';
          const db = rowB.study.collaborator
            ? rowB.study.collaborator.organizationName
            : '';
          if (da > db) {
            return order === "desc" ? -1 : 1;
          }
          if (db > da) {
            return order === "desc" ? 1 : -1;
          }
          return 0;
        },
        cell: (d) => d.study.collaborator
          ? (
            <div>
              <p style={{fontWeight: 'bold', marginBottom: '0.2rem'}}>
                {d.study.collaborator.organizationName}
              </p>
              <p>
                {d.study.externalCode}
              </p>
            </div>

          ) : ''
      }),
    ],
    []
  );

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center mb-2">
          <Col>
            <h3>Assays</h3>
          </Col>
        </Row>

        <Row>
          <Col lg={12}>
            <Card>
              <Card.Body>
                <DataTable data={assays} columns={columns} />
              </Card.Body>
            </Card>
          </Col>
        </Row>

      </Container>
  );

};

AssayList.propTypes = {
  assays: PropTypes.array.isRequired
}

export default AssayList;