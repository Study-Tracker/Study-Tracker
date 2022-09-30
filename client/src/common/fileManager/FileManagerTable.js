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
import {
  Download,
  File,
  Folder,
  Info,
  Link,
  MoreHorizontal
} from "react-feather";
import BootstrapTable from "react-bootstrap-table-next";
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import paginationFactory from "react-bootstrap-table2-paginator";
import {Dropdown} from "react-bootstrap";

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
            <a className="d-flex justify-content-start file-link" onClick={() => handleItemClick(d)}>
              <div className="align-self-center">
                {d.type === "folder" ? <Folder size={24}/> : <File size={24}/>}
              </div>
              <div className="align-self-center">{d.name}</div>
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
          return d.totalSize ? formatFileSize(d.totalSize) : "-";
        } else {
          return formatFileSize(d.size);
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
            <Dropdown className="actions-button">
              <Dropdown.Toggle variant="light">
                <MoreHorizontal size={18}/>
              </Dropdown.Toggle>
              <Dropdown.Menu>
                {
                  d.type === "file" ? (
                    <Dropdown.Item onClick={() => console.log("Click!")}>
                      <Download className="align-middle me-2" /> Download
                    </Dropdown.Item>
                    ) : ""
                }
                <Dropdown.Item onClick={() => console.log("Click!")}>
                  <Link className="align-middle me-2" size={18} /> Copy Link
                </Dropdown.Item>
                <Dropdown.Item onClick={() => console.log("Click!")}>
                  <Info className="align-middle me-2" size={18} /> Details
                </Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
        )
        // return (
        //     <>
        //       { d.type === "file" ? <Download className="align-middle me-1" size={18} />: "" }
        //       <Link className="align-middle" size={18} />
        //     </>
        // );
      }
    }
  ];

  // const getIcon = (file) => {
  //   if (file.type === "folder") {
  //     return <Folder />;
  //   } else if (file.type === "image") {
  //     return <Image />;
  //   } else {
  //     return <File />;
  //   }
  // }
  //
  // const getFileSize = (file) => {
  //   if (file.type === "folder") {
  //     return "";
  //   } else {
  //     return file.size + " bytes";
  //   }
  // }

  const formatFileSize = (size) => {
    if (size >= 1000000000) {
      return (size / 1000000000).toFixed(2) + " gb";
    } else if (size >= 1000000) {
      return (size / 1000000).toFixed(2) + " mb";
    } else if (size >= 1000) {
      return (size / 1000).toFixed(2) + " kb";
    } else {
      return size + " bytes";
    }
  };

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
          <div className="mt-3 file-manager-table">
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
                  sizePerPage: 10,
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
