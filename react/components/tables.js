import {Button} from "reactstrap";
import {File} from "react-feather";
import React from "react";

export const ExportToCsv = (props) => {
  const handleClick = () => {
    props.onExport();
  };
  return (
      <span>
        <Button color={'primary'} onClick={handleClick}>
          Export to CSV
          &nbsp;
          <File className="feather align-middle ml-2 mb-1"/>
        </Button>
      </span>
  );
};