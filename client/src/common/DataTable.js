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
import {Button, Col, Form, FormGroup, Row, Table} from "react-bootstrap";
import PropTypes from "prop-types";
import {useGlobalFilter, usePagination, useSortBy, useTable} from "react-table";
import {ChevronDown, ChevronUp} from "react-feather";

const DataTable = ({data, columns, defaultSort, className}) => {

  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    page,
    pageCount,
    canNextPage,
    nextPage,
    canPreviousPage,
    previousPage,
    pageOptions,
    gotoPage,
    setPageSize,
    prepareRow,
    state,
    setGlobalFilter,
  } = useTable({
    columns,
    data,
    initialState: {
      pageIndex: 0,
      pageSize: 10,
      sortBy: !!defaultSort ? [defaultSort] : []
    }
  }, useGlobalFilter, useSortBy, usePagination);

  const {globalFilter, pageIndex, pageSize} = state;

  return (
      <Row>

        <Col xs={"12"} className={"d-flex justify-content-end"}>
          <div>
            <FormGroup>
              <Form.Control
                  type={"input"}
                  value={globalFilter || ""}
                  onChange={e => setGlobalFilter(e.target.value)}
                  placeholder={"Search..."}
              />
            </FormGroup>
          </div>
        </Col>

        <Col xs={12}>
          <Table striped hover {...getTableProps()} className={className}>

            <thead>
              {headerGroups.map(headerGroup => (
                  <tr {...headerGroup.getHeaderGroupProps()}>
                    {headerGroup.headers.map(column => (
                        <th {...column.getHeaderProps(column.getSortByToggleProps())}>
                          {column.render("Header")}
                          <span>
                        {
                            column.isSorted ? (column.isSortedDesc
                                ? <ChevronDown size={18} className={"ms-1"} />
                                : <ChevronUp size={18} className={"ms-1"} />
                            ) : ""
                        }
                      </span>
                        </th>
                    ))}
                  </tr>
              ))}
            </thead>

            <tbody {...getTableBodyProps()}>
              {page.map((row, i) => {
                prepareRow(row);
                return (
                    <tr {...row.getRowProps()}>
                      {row.cells.map(cell => {
                        return <td {...cell.getCellProps()}>{cell.render("Cell")}</td>;
                      })}
                    </tr>
                );
              })}
            </tbody>

          </Table>
        </Col>

        <Col xs={12} className={"d-flex justify-content-between mb-3"}>

          <span>
            {/*<Form.Group>*/}
            {/*<Form.Label>Records per page</Form.Label>*/}
            <Form.Select
                value={pageSize}
                onChange={e => {
                  setPageSize(Number(e.target.value))
                }}
            >
              {[5, 10, 20, 50].map(pageSize => (
                  <option key={pageSize} value={pageSize}>{pageSize} per page</option>
              ))}
            </Form.Select>
            {/*</Form.Group>*/}
          </span>

          <span>

            <Button
                type={"button"}
                size={"sm"}
                variant="outline-primary"
                onClick={() => gotoPage(0)}
                disabled={!canPreviousPage}
                className={"me-1"}
            >
              {"<<"}
            </Button>

            <Button
                type={"button"}
                size={"sm"}
                variant="outline-primary"
                onClick={() => previousPage()}
                disabled={!canPreviousPage}
                className={"me-1"}
            >
              {"<"}
            </Button>

            <span className={"ms-1 me-2"}>
              Page{" "}<strong>{pageIndex + 1} of {pageOptions.length}</strong>
            </span>

            <Button
                type={"button"}
                size={"sm"}
                variant="outline-primary"
                onClick={() => nextPage()}
                disabled={!canNextPage}
                className={"me-1"}
            >
              {">"}
            </Button>

            <Button
                type={"button"}
                size={"sm"}
                variant="outline-primary"
                onClick={() => gotoPage(pageCount - 1)}
                disabled={!canNextPage}
                className={"ms-1"}
            >
              {">>"}
            </Button>
          </span>

        </Col>
      </Row>
  )

};

DataTable.propTypes = {
  data: PropTypes.array.isRequired,
  columns: PropTypes.array.isRequired,
  defaultSort: PropTypes.object
}

export default DataTable;
