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

import {Card, Dropdown, ListGroup} from "react-bootstrap";
import React, {useContext} from "react";
import PropTypes from "prop-types";
import {DataSourceIcon} from "./fileManagerUtils";
import {MoreHorizontal} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheck, faTrash} from "@fortawesome/free-solid-svg-icons";
import {useMutation, useQueryClient} from "react-query";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";
import swal from "sweetalert2";

const StudyFileManagerMenu = ({
  record,
  folders,
  handleFolderSelect,
  selectedFolder
}) => {

  const notyf = useContext(NotyfContext);
  const queryClient = useQueryClient();

  let type = "program";
  if (record.hasOwnProperty("programId")) {
    type = "study";
  } else if (record.hasOwnProperty("studyId")) {
    type = "assay";
  }

  console.debug("Storage folders", folders);

  const handleChangeDefaultFolderMutation = useMutation((folder) => {
    console.debug("Change default folder", folder);
    return axios.patch(`/api/internal/${type}/${record.id}/storage/${folder.id}`)
  }, {
    onSuccess: () => {
      notyf.success("Default folder updated successfully");
      queryClient.invalidateQueries(["storageFolders", record.id]);
    },
    onError: (error) => {
      console.error(error);
      notyf.error("Failed to update default folder: " + error.message);
    }
  });

  const handleRemoveFolder = (folder) => {
    swal.fire({
      title: "Are you sure you want to remove this folder?",
      text: "Your files will not be deleted and you can re-add this folder later from the File Manager.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Remove",
    }).then((results) => {
      if (results.isConfirmed) {
        removeFolderMutation.mutate(folder);
      }
    })
  }

  const removeFolderMutation = useMutation((folder) => {
    console.debug("Remove folder", folder);
    return axios.delete(`/api/internal/${type}/${record.id}/storage/${folder.id}`)
  }, {
    onSuccess: () => {
      notyf.success("Folder removed successfully");
      queryClient.invalidateQueries(["storageFolders", record.id]);
    },
    onError: (error) => {
      console.error(error);
      notyf.error("Failed to remove folder: " + error.message);
    }
  });

  return (
      <Card>

        <Card.Header>
          <Card.Title tag="h5" className="mb-0 text-muted">
            Storage Folders
          </Card.Title>
        </Card.Header>

        <ListGroup variant="flush">
          {
            folders
            .sort((a, b) => {
              if (a.storageDriveFolder.name > b.storageDriveFolder.name) return -1;
              else if (b.storageDriveFolder.name > a.storageDriveFolder.name) return 1;
              else return 0;
            })
            .map(f => {
              const isActive = selectedFolder && selectedFolder.id === f.storageDriveFolder.id;
              return (
                <ListGroup.Item
                  action
                  key={f.id}
                  active={isActive}
                  onClick={() => handleFolderSelect(f.storageDriveFolder)}
                >
                  <div>

                    <DataSourceIcon driveType={f.storageDriveFolder.storageDrive.driveType}/>

                    <span className={isActive ? '' : 'text-muted'}>
                      {f.storageDriveFolder.storageDrive.driveType}
                    </span>

                    <span className={"float-end"}>
                      <Dropdown align="end">

                        <Dropdown.Toggle as="a" bsPrefix="-" style={{color: "inherit"}}>
                          <MoreHorizontal />
                        </Dropdown.Toggle>

                        <Dropdown.Menu>

                          {
                            !f.primary && (
                              <Dropdown.Item onClick={() => handleChangeDefaultFolderMutation.mutate(f)}>
                                <FontAwesomeIcon icon={faCheck} className={"me-2"} />
                                Make default
                              </Dropdown.Item>
                            )
                          }

                          <Dropdown.Item disabled={f.primary} onClick={() => handleRemoveFolder(f)}>
                            <FontAwesomeIcon icon={faTrash} className={"me-2"} />
                            Remove
                          </Dropdown.Item>

                        </Dropdown.Menu>
                      </Dropdown>
                    </span>

                  </div>

                  <div className={isActive ? 'fw-bold' : ''}>
                    {f.storageDriveFolder.name}
                  </div>

                  {
                    f.primary && (
                      <div>
                        <span className="badge bg-info">default</span>
                      </div>
                    )
                  }

                </ListGroup.Item>
              )
            })
          }
        </ListGroup>

      </Card>
  );

}

StudyFileManagerMenu.propTypes = {
  record: PropTypes.object.isRequired,
  folders: PropTypes.array.isRequired,
  handleFolderSelect: PropTypes.func.isRequired,
  selectedFolder: PropTypes.object
}

export default StudyFileManagerMenu;