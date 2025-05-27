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
import {useQuery} from "@tanstack/react-query";
import axios from "axios";
import {LoadingMessageCard} from "../../common/loading";
import {CardErrorMessage} from "../../common/errors";
import PropTypes from "prop-types";
import {StatusBadge} from "../../common/status";
import { createColumnHelper } from "@tanstack/react-table";
import DataTable from "../../common/DataTable";

const ProgramStudiesTab = ({program}) => {

  const {data: studies, isLoading, error} = useQuery({
    queryKey: ["programStudies", program.id],
    queryFn: () => {
      return axios.get(`/api/internal/study?program=${program.id}`)
      .then(response => response.data);
    }
  });

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
    {
      id: "status",
      header: "Status",
      sortingFn: (a, b) => {
        return a.original.status.localeCompare(b.original.status);
      },
      accessorFn: (d) => <StatusBadge status={d.status}/>
    },
    {
      id: "updatedAt",
      header: "Last Updated",
      accessorFn: (d) => new Date(d.updatedAt).toLocaleDateString(),
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
              <DataTable data={studies} columns={columns} />
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