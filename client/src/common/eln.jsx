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
import {Button} from "react-bootstrap";
import {Folder as FolderIcon, RefreshCw} from "react-feather";
import swal from "sweetalert2";
import axios from "axios";
import PropTypes from "prop-types";

const handleFolderRepairRequest = (url) => {
  swal.fire({
    title: "Are you sure you want to repair this notebook folder?",
    text: "Folder repair could result in a loss of data.",
    icon: "warning",
    buttons: true
  })
  .then(val => {
    if (val.isConfirmed) {
      axios.post(url)
      .then(() => {
        swal.fire({
          title: "Folder Repair Complete",
          text: "Refresh the page to view the updated storage folder information.",
          icon: "success"
        });
      })
      .catch(() => {
        swal.fire("Request failed",
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

RepairableNotebookFolderLink.propTypes = {
  folder: PropTypes.object,
  repairUrl: PropTypes.string
}

export const RepairableNotebookFolderButton = ({folder, repairUrl}) => {
  if (!!folder && !!folder.referenceId
    && !!folder.url && folder.url !== 'ERROR') {
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

RepairableNotebookFolderButton.propTypes = {
  folder: PropTypes.object,
  repairUrl: PropTypes.string
}