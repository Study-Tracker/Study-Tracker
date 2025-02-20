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
import {Col, Form, Row} from "react-bootstrap";
import AsyncSelect from "react-select/async";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimesCircle} from "@fortawesome/free-regular-svg-icons";
import {FormGroup} from "./common";
import axios from "axios";
import PropTypes from "prop-types";

const UserInputs = ({
    users,
    owner,
    onChange,
    isValid
}) => {

  const userAutocomplete = (input, callback) => {
    axios.get("/api/internal/autocomplete/user?q=" + input)
    .then(response => {
      const options = response.data
        .filter(user => !users.find(u => u.id === user.id))
        .map(user => {
          return {label: user.displayName, value: user.id, obj: user}
        })
        .sort((a, b) => {
          if (a.label < b.label) return -1;
          else if (a.label > b.label) return 1;
          else return 0;
        });
      callback(options);
    }).catch(e => {
      console.error(e);
    })
  }

  const handleUserSelect = (selected) => {
    const user = selected.obj;
    user.owner = users.length === 0;
    onChange("users", [...users, user]);
    if (user.owner) {
      onChange("owner", user);
    }
  }

  const handleOwnerChange = (e) => {
    let u = [];
    const selected = parseInt(e.target.dataset.id, 10);
    let o = null;
    users.forEach(user => {
      user.owner = user.id === selected;
      if (user.owner) {
        o = user;
      }
      u.push(user);
    });
    onChange("users", u);
    onChange("owner", o);
  }

  const handleRemoveUser = (e) => {
    const selected = parseInt(e.currentTarget.dataset.id, 10);
    const u = users.filter(user => user.id !== selected);
    let o = owner;
    if (selected.id === owner.id) {
      o = users.length ? users[0] : null;
    }
    onChange("users", u);
    onChange("owner", o);
  }

  // User list
  const selectedUsers = users.map(user => {
    return (
        <Row key={"user-" + user.id}>
          <Col xs={2}>
            <Form.Check
                id={"owner-radio-" + user.id}
                type="radio"
                name="owner"
                label=""
                className="mb-2"
                checked={user.id === owner.id}
                data-id={user.id}
                onChange={handleOwnerChange}
            />
          </Col>
          <Col xs={8}>
            <Form.Label>{user.displayName}</Form.Label>
          </Col>
          <Col xs={2}>
            <a
                onClick={handleRemoveUser}
                data-id={user.id}
            >
              <FontAwesomeIcon
                  icon={faTimesCircle}
                  className="align-middle me-2 text-danger"
              />
            </a>
          </Col>
        </Row>
    );
  });

  return (
      <Row>
        <Col sm={6}>
          <FormGroup>
            <Form.Label>Users</Form.Label>
            <AsyncSelect
                placeholder="Search-for and select team members..."
                className={"react-select-container" + (!isValid ? " is-invalid" : '')}
                classNamePrefix="react-select"
                loadOptions={userAutocomplete}
                onChange={handleUserSelect}
                controlShouldRenderValue={false}
                defaultOptions={true}
            />
            <Form.Control.Feedback type={"invalid"}>
              You must select at least one user.
            </Form.Control.Feedback>
          </FormGroup>
        </Col>
        <Col sm={6}>
          <Row>
            <Col xs={2}>
              <Form.Label>Owner</Form.Label>
            </Col>
            <Col xs={8}>
              <Form.Label>User</Form.Label>
            </Col>
            <Col xs={2}>
              <Form.Label>Remove</Form.Label>
            </Col>
          </Row>
          {selectedUsers}
        </Col>
      </Row>
  );

}

UserInputs.propsTypes = {
  users: PropTypes.array.isRequired,
  owner: PropTypes.object,
  onChange: PropTypes.func.isRequired,
  isValid: PropTypes.bool.isRequired
}

export default UserInputs;