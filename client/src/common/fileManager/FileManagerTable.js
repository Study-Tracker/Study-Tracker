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
import React, {useState} from 'react';
import {Download, File, Folder, Image, Link} from "react-feather";
import BootstrapTable from "react-bootstrap-table-next";
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import paginationFactory from "react-bootstrap-table2-paginator";

const FileManagerTable = ({folder, handlePathChange}) => {
  const [selectedItem, setSelectedItem] = useState(null);
  const [selectedItemIndex, setSelectedItemIndex] = useState(null);

  const handleItemClick = (item, index) => {
    setSelectedItem(item);
    setSelectedItemIndex(index);
    if (item.type === 'folder') {
      handlePathChange(item.path);
    }
  };

  const columns = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      headerStyle: {width: "40%"},
      formatter: (cell, d) => {
        return (
            <a onClick={() => handleItemClick(d)}>
              {d.type === "folder" ? <Folder/> : <File/>} {d.name}
            </a>
        )
      }
    },
    {
      dataField: "size",
      text: "Size",
      sort: true,
      headerStyle: {width: "20%"},
      formatter: (cell, d) => {
        if (d.type === "folder") {
          return d.totalSize || "-";
        } else {
          return d.size + " bytes";
        }
      },
      sortFunc: (a, b) => {
        return a - b;
      }
    },
    {
      dataField: "lastModified",
      text: "Last Modified",
      sort: true,
      formatter: (cell, d) => {
        return new Date(d.lastModified).toLocaleDateString();
      },
      sortFunc: (a, b) => {
        return a - b;
      }
    },
    {
      dataField: "actions",
      text: "Actions",
      sort: false,
      searchable: false,
      formatter: (cell, d) => {
        return (
            <>
              <Download className="align-middle me-1" size={18} />
              <Link className="align-middle" size={18} />
            </>
        );
      }
    }
  ];

  const getIcon = (file) => {
    if (file.type === "folder") {
      return <Folder />;
    } else if (file.type === "image") {
      return <Image />;
    } else {
      return <File />;
    }
  }

  const getFileSize = (file) => {
    if (file.type === "folder") {
      return "";
    } else {
      return file.size + " bytes";
    }
  }

  const data = [
      ...folder.subFolders.map(f => ({...f, type: "folder"})),
      ...folder.files.map(f => ({...f, type: "file"}))
  ]

  return (
    <ToolkitProvider
      keyField={"name"}
      data={data}
      columns={columns}
      search
    >
      {props => (
          <div className="mt-3">
            <div className="d-flex justify-content-end">
              <Search.SearchBar
                  {...props.searchProps}
              />
            </div>
            <BootstrapTable
                bootstrap4
                keyField="name"
                bordered={false}
                pagination={paginationFactory({
                  sizePerPage: 20,
                  sizePerPageList: [10, 20, 40, 80]
                })}
                defaultSorted={[{
                  dataField: "name",
                  order: "asc"
                }]}
                {...props.baseProps}
            >
            </BootstrapTable>
          </div>
      )}
    </ToolkitProvider>
  );
}

export default FileManagerTable;
