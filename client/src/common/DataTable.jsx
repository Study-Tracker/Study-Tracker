/*
 * Copyright 2019-2025 the original author or authors.
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
import {
  Button,
  ButtonGroup,
  Col,
  Form,
  FormGroup,
  Row,
  Table
} from "react-bootstrap";
import PropTypes from "prop-types";
import {
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable
} from "@tanstack/react-table";
import { ChevronDown, ChevronUp } from "react-feather";

const DataTable = ({
  data,
  columns,
  defaultSort,
  defaultPageSize = 10,
  className
}) => {

  const [pagination, setPagination] = React.useState({
    pageIndex: 0,
    pageSize: defaultPageSize,
  });
  const [sorting, setSorting] = React.useState(defaultSort ? [defaultSort] : []);

  const table = useReactTable({
    columns,
    data,
    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    enableSorting: true,
    state: {
      pagination,
      sorting,
    },
    onPaginationChange: setPagination,
    onSortingChange: setSorting,
    globalFilterFn: (row, dolId, filterValue) => {
      return JSON.stringify(row.original).toLowerCase().includes(filterValue.toLowerCase());
    },
  });

  return (
    <Row>
      <Col xs={"12"} className={"d-flex justify-content-end"}>
        <div>
          <FormGroup>
            <Form.Control
              type={"input"}
              onChange={(e) => table.setGlobalFilter(e.target.value)}
              placeholder={"Search..."}
              data-test-id={"search"}
            />
          </FormGroup>
        </div>
      </Col>

      <Col xs={12}>
        <Table hover className={className}>
          <thead>
          {table.getHeaderGroups().map((headerGroup) => (
            <tr key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <th colSpan={header.colSpan} key={header.id}>
                  { header.isPlaceholder ? null : (
                    <div
                      className={"data-table-header " + (header.column.getCanSort() ? "cursor-pointer" : "")}
                      onClick={header.column.getToggleSortingHandler()}
                      title={
                        header.column.getCanSort()
                          ? header.column.getNextSortingOrder() === 'asc'
                            ? 'Sort ascending'
                            : header.column.getNextSortingOrder() === 'desc'
                              ? 'Sort descending'
                              : 'Clear sort'
                          : undefined
                      }
                    >
                      {flexRender(header.column.columnDef.header, header.getContext())}
                      {{
                        asc: <ChevronUp size={18} className={"ms-1"} />,
                        desc: <ChevronDown size={18} className={"ms-1"} />,
                      }[header.column.getIsSorted()] ?? null }
                    </div>
                  )}
                </th>
              ))}
            </tr>
          ))}
          </thead>

          <tbody>
          {table.getRowModel().rows.map((row) => {
            return (
              <tr key={row.id} >
                {row.getVisibleCells().map((cell) => {
                  return (
                    <td key={cell.id}>
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </td>
                  );
                })}
              </tr>
            );
          })}
          </tbody>
        </Table>
      </Col>

      <Col xs={12} className={"d-flex justify-content-between mb-3"}>
        <span>
          <Form.Select
            value={pagination.pageSize}
            onChange={(e) => {
              table.setPageSize(Number(e.target.value));
            }}
            data-test-id={"page-size"}
          >
            {[5, 10, 20, 50].map((pageSize) => (
              <option key={pageSize} value={pageSize}>
                {pageSize} per page
              </option>
            ))}
          </Form.Select>
        </span>

        <span>
          <ButtonGroup>
            <Button
              type={"button"}
              size={"sm"}
              variant="light"
              onClick={() => setPagination({ ...pagination, pageIndex: 0 })}
              disabled={!table.getCanPreviousPage()}
              data-test-id={"first-page"}
            >
              {"<<"}
            </Button>

            <Button
              type={"button"}
              size={"sm"}
              variant="light"
              onClick={() => table.previousPage()}
              disabled={!table.getCanPreviousPage()}
              data-test-id={"previous-page"}
            >
              {"<"}
            </Button>
          </ButtonGroup>

          <span className={"ms-2 me-2"}>
            Page{" "}
            <strong>
              {pagination.pageIndex + 1} of {table.getPageCount() || 1}
            </strong>
          </span>

          <ButtonGroup>
            <Button
              type={"button"}
              size={"sm"}
              variant="light"
              onClick={() => table.nextPage()}
              disabled={!table.getCanNextPage()}
            >
              {">"}
            </Button>

            <Button
              type={"button"}
              size={"sm"}
              variant="light"
              onClick={() => setPagination({ ...pagination, pageIndex: table.getPageCount() - 1 })}
              disabled={!table.getCanNextPage()}
            >
              {">>"}
            </Button>
          </ButtonGroup>
        </span>
      </Col>
    </Row>
  );
};

DataTable.propTypes = {
  data: PropTypes.array.isRequired,
  columns: PropTypes.array.isRequired,
  defaultSort: PropTypes.object,
  className: PropTypes.string,
  defaultPageSize: PropTypes.number,
};

export default DataTable;
