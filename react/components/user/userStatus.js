import {Badge, Button, Dropdown} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import {statuses} from "../../config/userActivityConstants";
import swal from "sweetalert";

export const UserStatusButton = ({active}) => {
  const config = !!active ? statuses.ACTIVE : statuses.INACTIVE;
  return (
      <Button size="lg" className="me-1" variant={config.color}>
        <FontAwesomeIcon icon={config.icon}
                         className="align-middle"/> {config.label}
      </Button>
  )
};

export class SelectableUserStatusButton extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};
    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(e) {
    const active = e.target.dataset.value === statuses.ACTIVE;
    fetch("/api/user/" + this.props.userId + "/status?active=" + active, {
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
          <Dropdown.Item
              key={'status-option-' + s.label}
              onClick={this.handleChange}
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
}

export const UserStatusBadge = ({status}) => {
  const config = !!status ? statuses.ACTIVE : statuses.INACTIVE;
  return <Badge bg={config.color}>{config.label}</Badge>
};