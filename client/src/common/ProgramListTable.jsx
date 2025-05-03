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

import {Badge} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";
import DataTable from "./DataTable";
import { createColumnHelper } from "@tanstack/react-table";

const ProgramListTable = ({programs}) => {

  const columnHelper = createColumnHelper();

  const columns = React.useMemo(() => [
    {
      id: "name",
      header: "Name",
      accessorFn: (d) => {
        return (
            <a href={"/program/" + d.id}>{d.name}</a>
        )
      },
      sortingFn: (a, b) => {
        return a.original.name.localeCompare(b.original.name);
      },
    },
    {
      id: "code",
      header: "Code",
      accessorFn: (d) => d.code
    },
    columnHelper.accessor(row => row, {
      id: "active",
      header: "Active",
      cell: (d) => {
        if (d.active) {
          return (
              <Badge bg="success">
                Active
              </Badge>
          )
        } else {
          return (
              <Badge bg="warning">
                Inactive
              </Badge>
          )
        }
      }
    }),
  ], []);

  return (
      <DataTable data={programs} columns={columns} />
  );

}

ProgramListTable.propTypes = {
  programs: PropTypes.array.isRequired
}

export default ProgramListTable;
