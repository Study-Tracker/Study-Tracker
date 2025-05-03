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
import {Badge, Card, Col, Container, Row} from "react-bootstrap";
import { createColumnHelper } from "@tanstack/react-table";
import DataTable from "../../common/DataTable";
import PropTypes from "prop-types";

export const UserTable = ({users}) => {
  const columnHelper = createColumnHelper();
  const columns = React.useMemo(() => [
    columnHelper.accessor(row => row, {
      id: "displayName",
      header: "Name",
      cell: (d) => {
        return (
          <a href={"/user/" + d.id}>{d.displayName}</a>
        )
      },
      sortingFn: (a, b) => {
        return a.original.displayName.localeCompare(b.original.displayName);
      },
    }),
    {
      id: "email",
      header: "Email",
      accessorFn: (d) => d.email
    },
    {
      id: "department",
      header: "Department",
      accessorFn: (d) => d.department
    },
    columnHelper.accessor(row => row, {
      id: "admin",
      header: "Admin",
      cell: (d) => {
        if (d.admin) {
          return (
            <div className="badge badge-danger">
              Admin
            </div>
          )
        }
      }
    }),
    columnHelper.accessor(row => row, {
      id: "active",
      header: "Active",
      cell: (d) => {
        if (d.active) {
          return <Badge bg="success">Active</Badge>
        } else {
          return <Badge bg="warning">Inactive</Badge>
        }
      }
    })
  ], []);

  return (
    <DataTable data={users} columns={columns} />
  )
}

UserTable.propTypes = {
  users: PropTypes.array.isRequired,
}

const UserList = ({users}) => {

  return (
      <Container fluid className="animated fadeIn">

        <Row className="justify-content-between align-items-center mb-2">
          <Col>
            <h3>Users</h3>
          </Col>
        </Row>

        <Row>
          <Col lg="12">
            <Card>
              <Card.Body>
                <UserTable users={users}/>
              </Card.Body>
            </Card>
          </Col>
        </Row>

      </Container>
  );

}

UserList.propTypes = {
  users: PropTypes.array.isRequired,
};

export default UserList;