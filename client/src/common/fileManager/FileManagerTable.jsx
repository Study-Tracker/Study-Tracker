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
import {
  Download,
  Edit,
  File,
  Folder,
  FolderPlus,
  Link,
  MoreHorizontal
} from "react-feather";
import {Dropdown} from "react-bootstrap";
import PropTypes from "prop-types";
import NotyfContext from "../../context/NotyfContext";
import FileManagerAddToStudyModal from "./FileManagerAddToStudyModal";
import FileManagerRenameFolderModal from "./FileManagerRenameFolderModal";
import axios from "axios";
import { createColumnHelper } from "@tanstack/react-table";
import DataTable from "../DataTable";

const FileManagerTable = ({
  folder,
  handlePathChange,
  rootFolder,
}) => {

  const notyf = useContext(NotyfContext);
  const [addToStudyModalIsOpen, setAddToStudyModalIsOpen] = useState(false);
  const [addToAssayModalIsOpen, setAddToAssayModalIsOpen] = useState(false);
  const [addToProgramModalIsOpen, setAddToProgramModalIsOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [renameFolderModalIsOpen, setRenameFolderModalIsOpen] = useState(false);

  const handleItemClick = (item) => {
    setSelectedItem(item);
    if (item.type === 'folder') {
      handlePathChange(item.path);
    }
  };

  const handleCopyS3Path = (d) => {
    const path = "s3://" + rootFolder.storageDrive.details.bucketName + "/" + d.path;
    navigator.clipboard.writeText(path);
    notyf.open({message: "Copied S3 path to clipboard", type: "success"});
  }

  const handleCopyUrl = (d) => {
    navigator.clipboard.writeText(d.url);
    notyf.open({message: "Copied URL clipboard", type: "success"});
  }

  const handleRenameFolder = (values, { setSubmitting, resetForm }) => {
    setSubmitting(true);
    const data = new FormData();
    data.append("folderId", rootFolder.id);
    data.append("path", values.path);
    data.append("name", values.name);
    axios.post("/api/internal/data-files/rename-folder", data)
    .then(() => {
      notyf.open({message: "Folder renamed successfully.", type: "success"});
    })
    .catch(e => {
      console.error(e);
      console.error("Failed to rename folder");
      notyf.open({message: "Failed to rename folder. Please try again.", type: "error"});
    })
    .finally(() => {
      setSubmitting(false);
      resetForm();
      setRenameFolderModalIsOpen(false);
    });
  }

  const columnHelper = createColumnHelper();

  const columns = React.useMemo(() => [
    columnHelper.accessor(row => row, {
      id: "name",
      header: "Name",
      cell: (d) => {
        if (d.getValue().type === 'folder') {
          return (
              <a
                  className="d-flex justify-content-start file-link"
                  onClick={() => handleItemClick(d.getValue())}
              >
                <div className="align-self-center">
                  <Folder size={24}/>
                </div>
                <div className="align-self-center">{d.getValue().name}</div>
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
    }),
    columnHelper.accessor(row => row, {
      id: "size",
      header: "Size",
      cell: (d) => {
        if (d.getValue().type === "folder") {
          return d.getValue().totalSize ? formatFileSize(d.getValue().totalSize) : "-";
        } else {
          return formatFileSize(d.getValue().size);
        }
      },
      sortingFn: (a, b) => {
        return a - b;
      }
    }),
    {
      id: "lastModified",
      header: "Last Modified",
      accessorFn: (d) => {
        return d.lastModified ? new Date(d.lastModified).toLocaleString() : "-";
      },
      sortingFn: (a, b) => {
        return a - b;
      }
    },
    columnHelper.accessor(row => row, {
      id: "actions",
      header: "Actions",
      cell: (cell) => {
        const d = cell.getValue();
        return (
            <Dropdown className="actions-button">
              <Dropdown.Toggle variant="light">
                <MoreHorizontal size={18}/>
              </Dropdown.Toggle>
              <Dropdown.Menu>

                {
                  d.type === "file" && d.downloadable ? (
                    <Dropdown.Item onClick={() => window.open("/api/internal/data-files/download?folderId=" + rootFolder.id + "&path=" + d.path)}>
                      <Download className="align-middle me-2" /> Download
                    </Dropdown.Item>
                    ) : ""
                }

                {
                  rootFolder.storageDrive && rootFolder.storageDrive.driveType && rootFolder.storageDrive.driveType === "S3" ? (
                      <Dropdown.Item onClick={() => handleCopyS3Path(d)}>
                        <Link className="align-middle me-2" size={18} /> Copy S3 Path
                      </Dropdown.Item>
                  ) : ""
                }

                {
                  d.url && (
                      <Dropdown.Item onClick={() => handleCopyUrl(d)}>
                        <Link className="align-middle me-2" size={18} /> Copy Link
                      </Dropdown.Item>
                  )
                }
                {
                  d.type === "folder" && (
                    <>

                      {
                        rootFolder.storageDrive && rootFolder.storageDrive.driveType
                        && ["ONEDRIVE", "LOCAL"].includes(rootFolder.storageDrive.driveType) ? (
                            <Dropdown.Item onClick={() => {
                                setSelectedItem(d);
                                setRenameFolderModalIsOpen(true);
                            }}>
                              <Edit className="align-middle me-2" size={18} />
                              Rename Folder
                            </Dropdown.Item>
                        ) : ""
                      }

                      <Dropdown.Item onClick={() => {
                        setSelectedItem(d);
                        setAddToProgramModalIsOpen(true);
                      }}>
                        <FolderPlus className="align-middle me-2" size={18} />
                        Add to Program
                      </Dropdown.Item>

                      <Dropdown.Item onClick={() => {
                        setSelectedItem(d);
                        setAddToStudyModalIsOpen(true);
                      }}>
                        <FolderPlus className="align-middle me-2" size={18} />
                        Add to Study
                      </Dropdown.Item>

                      <Dropdown.Item onClick={() => {
                        setSelectedItem(d);
                        setAddToAssayModalIsOpen(true);
                      }}>
                        <FolderPlus className="align-middle me-2" size={18} />
                        Add to Assay
                      </Dropdown.Item>

                    </>
                  )
                }

              </Dropdown.Menu>
            </Dropdown>
        )
      }
    }),
  ], []);

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
    <>

      <DataTable data={data} columns={columns} />

      <FileManagerAddToStudyModal
          setModalIsOpen={setAddToProgramModalIsOpen}
          isOpen={addToProgramModalIsOpen}
          folder={selectedItem}
          rootFolder={rootFolder}
          type={"program"}
      />

      <FileManagerAddToStudyModal
        setModalIsOpen={setAddToStudyModalIsOpen}
        isOpen={addToStudyModalIsOpen}
        folder={selectedItem}
        rootFolder={rootFolder}
        type={"study"}
      />

      <FileManagerAddToStudyModal
        setModalIsOpen={setAddToAssayModalIsOpen}
        isOpen={addToAssayModalIsOpen}
        folder={selectedItem}
        rootFolder={rootFolder}
        type={"assay"}
      />

      <FileManagerRenameFolderModal
          folder={selectedItem}
          setModalIsOpen={setRenameFolderModalIsOpen}
          isOpen={renameFolderModalIsOpen}
          handleFormSubmit={handleRenameFolder}
      />

    </>
  );
}

FileManagerTable.propTypes = {
  folder: PropTypes.object.isRequired,
  handlePathChange: PropTypes.func.isRequired,
  rootFolder: PropTypes.object.isRequired,
}

export default FileManagerTable;
