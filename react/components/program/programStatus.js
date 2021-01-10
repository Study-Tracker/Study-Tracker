import {
  Button,
  DropdownItem,
  DropdownMenu,
  DropdownToggle,
  UncontrolledButtonDropdown
} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import {statuses} from "../../config/programActivityConstants";
import swal from "sweetalert";

export const ProgramStatusButton = ({active}) => {
  const config = !!active ? statuses.ACTIVE : statuses.INACTIVE;
  return (
      <Button size="lg" className="mr-1" color={config.color}>
        <FontAwesomeIcon icon={config.icon}
                         className="align-middle"/> {config.label}
      </Button>
  )
};

export class SelectableProgramStatusButton extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};
    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(e) {
    const active = e.target.dataset.value === statuses.ACTIVE;
    fetch("/api/program/" + this.props.programId + "/status?active=" + active, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      }
    })
    .then(async response => {
      if (response.ok) {
        window.location.reload();
      } else {
        throw new Error("Request failed");
      }
    }).catch(e => {
      swal(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    });
  }

  render() {
    const {active} = this.props;
    const config = !!active ? statuses.ACTIVE : statuses.INACTIVE;
    const options = [];
    for (const k in statuses) {
      const s = statuses[k];
      options.push(
          <DropdownItem
              key={'status-option-' + s.label}
              onClick={this.handleChange}
              data-value={s.value}
          >
            {s.label}
          </DropdownItem>
      );
    }
    return (
        <UncontrolledButtonDropdown className="mr-1 mb-1">
          <DropdownToggle caret size={"lg"} color={config.color}>
            <FontAwesomeIcon icon={config.icon}/>
            &nbsp;&nbsp;
            {config.label}
          </DropdownToggle>
          <DropdownMenu>
            {options}
          </DropdownMenu>
        </UncontrolledButtonDropdown>
    )
  }
}

export const ProgramStatusBadge = ({status}) => {
  const config = !!active ? statuses.ACTIVE : statuses.INACTIVE;
  return (
      <span className={"badge badge-" + config.color}>{config.label}</span>
  )
};