import React from 'react';
import {Button} from "react-bootstrap";
import {Folder as FolderIcon, RefreshCw} from "react-feather";
import swal from "sweetalert";

const handleFolderRepairRequest = (url) => {
  swal({
    title: "Are you sure you want to repair this notebook folder?",
    text: "Folder repair could result in a loss of data.",
    icon: "warning",
    buttons: true
  })
  .then(val => {
    if (val) {
      fetch(url, {
        method: 'PATCH',
        headers: {
          "Content-Type": "application/json"
        }
      }).then(response => {
        if (response.ok) {
          swal("Folder Repair Complete",
              "Refresh the page to view the updated storage folder information.",
              "success")
        } else {
          swal("Request failed",
              "Check the server log for more information.",
              "warning");
        }
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
  if (!!folder && !!folder.referenceId && !!folder.url && folder.url !== 'ERROR') {
    return <a href={folder.url} target="_blank">Notebook Folder</a>
  } else {
    return (
        <Button variant="warning" onClick={() => handleFolderRepairRequest(repairUrl)}>
          <RefreshCw size={14} className="mb-1"/>
          &nbsp;
          Repair Folder
        </Button>
    )
  }
}

export const RepairableNotebookFolderButton = ({folder, repairUrl}) => {
  if (!!folder && !!folder.referenceId && !!folder.url && folder.url !== 'ERROR') {
    return (
        <a href={folder.url}
           target="_blank"
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