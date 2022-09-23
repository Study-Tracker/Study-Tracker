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

import React from "react";
import {Button} from "react-bootstrap";
import {Folder as FolderIcon, RefreshCw} from "react-feather";
import swal from "sweetalert";
import axios from "axios";

const handleFolderRepairRequest = (url) => {
  swal({
    title: "Are you sure you want to repair this notebook folder?",
    text: "Folder repair could result in a loss of data.",
    icon: "warning",
    buttons: true
  })
  .then(val => {
    if (val) {
      axios.post(url)
      .then(response => {
        swal("Folder Repair Complete",
            "Refresh the page to view the updated storage folder information.",
            "success")
      })
      .catch(error => {
        swal("Request failed",
            "Check the server log for more information.",
            "warning");
      })
    }
  });
}

export const RepairableNotebookFolderLink = ({folder, repairUrl}) => {
  if (!!folder && !!folder.referenceId && !!folder.url && folder.url
      !== 'ERROR') {
    return <a href={folder.url} target="_blank" rel="noopener noreferrer">Notebook Folder</a>
  } else {
    return (
        <Button variant="warning"
                onClick={() => handleFolderRepairRequest(repairUrl)}>
          <RefreshCw size={14} className="mb-1"/>
          &nbsp;
          Repair Folder
        </Button>
    )
  }
}

export const RepairableNotebookFolderButton = ({folder, repairUrl}) => {
  if (!!folder && !!folder.referenceId && !!folder.url && folder.url
      !== 'ERROR') {
    return (
        <a href={folder.url}
           target="_blank"
           rel="noopener noreferrer"
           className="btn btn-outline-info mt-2 me-2">
          Notebook Folder
          <FolderIcon
              className="feather align-middle ms-2 mb-1"/>
        </a>
    )
  } else {
    return (
        <Button
            variant="warning"
            onClick={() => handleFolderRepairRequest(repairUrl)}
            className={"mt-2"}
        >
          <RefreshCw size={14} className="mb-1"/>
          &nbsp;
          Repair Folder
        </Button>
    )
  }
}