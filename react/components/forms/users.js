/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import {Col, CustomInput, FormGroup, Label, Row} from "reactstrap";
import AsyncSelect from "react-select/async";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimesCircle} from "@fortawesome/free-regular-svg-icons";

export class UserInputs extends React.Component {

  constructor(props) {
    super(props);
    this.handleUserSelect = this.handleUserSelect.bind(this);
    this.handleOwnerChange = this.handleOwnerChange.bind(this);
    this.handleRemoveUser = this.handleRemoveUser.bind(this);
  }

  userAutocomplete(input, callback) {
    if (input.length < 1) {
      return;
    }
    fetch("/api/autocomplete/user?q=" + input)
    .then(response => response.json())
    .then(json => {
      const options = json.map(user => {
        return {label: user.displayName, value: user.id, obj: user}
      });
      callback(options);
    }).catch(e => {
      console.error(e);
    })
  }

  handleUserSelect(selected) {
    const user = selected.obj;
    user.owner = this.props.users.length === 0;
    if (user.owner) {
      this.props.onChange({
        users: [
          ...this.props.users,
          user
        ],
        owner: user
      })
    } else {
      this.props.onChange({
        users: [
          ...this.props.users,
          user
        ]
      })
    }
  }

  handleOwnerChange(e) {
    let users = [];
    const selected = e.target.dataset.id;
    let owner = null;
    this.props.users.forEach(user => {
      user.owner = user.id === selected;
      if (user.owner) {
        owner = user;
      }
      users.push(user);
    });
    this.props.onChange({
      users: users,
      owner: owner
    });
  }

  handleRemoveUser(e) {
    const selected = parseInt(e.currentTarget.dataset.id);
    const users = this.props.users.filter(user => user.id !== selected);
    let owner = this.props.owner;
    if (selected.id === owner.id) {
      owner = users.length ? users[0] : null;
    }
    this.props.onChange({
      users: users,
      owner: owner
    });
  }

  render() {

    // User list
    const selectedUsers = this.props.users.map(user => {
      return (
          <Row key={"user-" + user.id}>
            <Col xs="2">
              <CustomInput
                  id={"owner-radio-" + user.id}
                  type="radio"
                  name="owner"
                  label=""
                  className="mb-2"
                  checked={user.id === this.props.owner.id}
                  data-id={user.id}
                  onChange={this.handleOwnerChange}
              />
            </Col>
            <Col xs="8">
              <Label>{user.displayName}</Label>
            </Col>
            <Col xs="2">
              <a
                  onClick={this.handleRemoveUser}
                  data-id={user.id}
              >
                <FontAwesomeIcon
                    icon={faTimesCircle}
                    className="align-middle mr-2 text-danger"
                />
              </a>
            </Col>
          </Row>
      );
    });

    return (
        <Row form>
          <Col sm="6">
            <FormGroup>
              <Label>Users</Label>
              <AsyncSelect
                  placeholder="Search-for and select team members..."
                  className={"react-select-container" + (!this.props.isValid
                      ? " is-invalid" : '')}
                  classNamePrefix="react-select"
                  loadOptions={this.userAutocomplete}
                  onChange={this.handleUserSelect}
                  controlShouldRenderValue={false}
              />
            </FormGroup>
          </Col>
          <Col sm="6">
            <Row>
              <Col xs="2">
                <Label>Owner</Label>
              </Col>
              <Col xs="8">
                <Label>User</Label>
              </Col>
              <Col xs="2">
                <Label>Remove</Label>
              </Col>
            </Row>
            {selectedUsers}
          </Col>
        </Row>
    );
  }

}