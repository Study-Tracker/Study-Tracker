import {Button, Dropdown} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import {statuses} from "../../config/programActivityConstants";
import swal from "sweetalert";
import PropTypes from "prop-types";
import axios from "axios";

export const ProgramStatusButton = ({active}) => {
  const config = !!active ? statuses.ACTIVE : statuses.INACTIVE;
  return (
      <Button size="lg" className="me-1" variant={config.color}>
        <FontAwesomeIcon icon={config.icon}
                         className="align-middle"/> {config.label}
      </Button>
  )
};

export const SelectableProgramStatusButton = props => {

  const handleChange = (e) => {
    const active = e.target.dataset.value === statuses.ACTIVE.value;
    axios.post("/api/program/" + props.programId + "/status?active=" + active)
    .then(async response => {
      if (response.status === 200) {
        window.location.reload();
      } else {
        throw new Error("Request failed");
      }
    })
    .catch(e => {
      swal(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    });
  }

  const {active} = props;
  const config = !!active ? statuses.ACTIVE : statuses.INACTIVE;
  const options = [];
  for (const k in statuses) {
    const s = statuses[k];
    options.push(
        <Dropdown.Item
            key={'status-option-' + s.label}
            onClick={handleChange}
            data-value={s.value}
        >
          {s.label}
        </Dropdown.Item>
    );
  }
  return (
      <Dropdown className="me-1 mb-1">
        <Dropdown.Toggle size={"lg"} variant={config.color}>
          <FontAwesomeIcon icon={config.icon}/>
          &nbsp;&nbsp;
          {config.label}
        </Dropdown.Toggle>
        <Dropdown.Menu>
          {options}
        </Dropdown.Menu>
      </Dropdown>
  )

}

SelectableProgramStatusButton.propTypes = {
  programId: PropTypes.string.isRequired,
  active: PropTypes.bool.isRequired
}