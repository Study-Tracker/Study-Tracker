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
import React, {useContext, useState} from 'react';
import {Download, File, Folder, FolderPlus, Link, MoreHorizontal} from "react-feather";
import BootstrapTable from "react-bootstrap-table-next";
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import paginationFactory from "react-bootstrap-table2-paginator";
import {Dropdown} from "react-bootstrap";
import PropTypes from "prop-types";
import NotyfContext from "../../context/NotyfContext";

const FileManagerTable = ({folder, handlePathChange, dataSource}) => {

  const notyf = useContext(NotyfContext);
  const [selectedItem, setSelectedItem] = useState(null);
  const [selectedItemIndex, setSelectedItemIndex] = useState(null);

  const handleItemClick = (item, index) => {
    setSelectedItem(item);
    setSelectedItemIndex(index);
    if (item.type === 'folder') {
      handlePathChange(item.path);
    }
  };

  const handleCopyS3Path = (d) => {
    const path = dataSource.name + "/" + d.path;
    navigator.clipboard.writeText(path);
    notyf.open({message: "Copied S3 path to clipboard", type: "success"});
  }

  const handleCopyUrl = (d) => {
    navigator.clipboard.writeText(d.url);
    notyf.open({message: "Copied URL clipboard", type: "success"});
  }

  const handleAddToStudy = (d) => {
    console.debug("Add to study", d);
  }

  const columns = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      headerStyle: {width: "40%"},
      formatter: (cell, d) => {
        if (d.type === 'folder') {
          return (
              <a
                  className="d-flex justify-content-start file-link"
                  onClick={() => handleItemClick(d)}
              >
                <div className="align-self-center">
                  <Folder size={24}/>
                </div>
                <div className="align-self-center">{d.name}</div>
              </a>
          )
        } else {
          if (d.url) {
            return (
                <a
                    className="d-flex justify-content-start file-link"
                    href={d.url}
                    target="_blank"
                    rel="noopener noreferrer"
                >
                  <div className="align-self-center">
                    <File size={24}/>
                  </div>
                  <div className="align-self-center">{d.name}</div>
                </a>
            )
          } else {
            return (
                <div className="d-flex justify-content-start file-link">
                  <div className="align-self-center">
                    <File size={24}/>
                  </div>
                  <div className="align-self-center">{d.name}</div>
                </div>
            )
          }
        }

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
        return d.lastModified ? new Date(d.lastModified).toLocaleString() : "-";
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
                  d.type === "file" && d.downloadable ? (
                    <Dropdown.Item onClick={() => window.open("/api/internal/data-files/download?locationId=" + dataSource.id + "&path=" + d.path)}>
                      <Download className="align-middle me-2" /> Download
                    </Dropdown.Item>
                    ) : ""
                }

                {
                  dataSource.storageDrive && dataSource.storageDrive.driveType && dataSource.storageDrive.driveType === "S3" ? (
                      <Dropdown.Item onClick={() => handleCopyS3Path(d)}>
                        <Link className="align-middle me-2" size={18} /> Copy S3 Path
                      </Dropdown.Item>
                  ) : ""
                }

                {
                  !!d.url ? (
                      <Dropdown.Item onClick={() => handleCopyUrl(d)}>
                        <Link className="align-middle me-2" size={18} /> Copy Link
                      </Dropdown.Item>
                  ) : ""
                }
                {
                  d.type === "folder" && (
                    <Dropdown.Item onClick={() => handleAddToStudy(d)}>
                      <FolderPlus className="align-middle me-2" size={18} /> Add to Study
                    </Dropdown.Item>
                  )
                }

                {/*<Dropdown.Item onClick={() => console.log("Click!")}>*/}
                {/*  <Info className="align-middle me-2" size={18} /> Details*/}
                {/*</Dropdown.Item>*/}

              </Dropdown.Menu>
            </Dropdown>
        )
      }
    }
  ];

  const formatFileSize = (size) => {
    if (!size) {
      return "-"
    } else if (size >= 1e9) {
      return (size / 1e9).toFixed(2) + " gb";
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

FileManagerTable.propTypes = {
  folder: PropTypes.object.isRequired,
  handlePathChange: PropTypes.func.isRequired,
  dataSource: PropTypes.object.isRequired
}

export default FileManagerTable;
