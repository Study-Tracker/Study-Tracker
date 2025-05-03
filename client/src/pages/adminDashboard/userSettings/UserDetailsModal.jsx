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

import {Badge, Button, Col, Modal, Row, Table} from "react-bootstrap";
import {Edit, User} from "react-feather";
import React from "react";
import PropTypes from "prop-types";

const UserDetailsModal = ({user, isOpen, showModal}) => {

  if (!user) {
    return "";
  }

  const attributes = Object.keys(user.attributes).map(k => {
    return (
        <tr key={"assay-type-attribute-" + k}>
          <td>{k}</td>
          <td>{user.attributes[k]}</td>
        </tr>
    )
  });

  return (
      <Modal
          show={isOpen}
          onHide={() => showModal()}
          size={"lg"}
      >
        <Modal.Header closeButton>
          User:&nbsp;
          <strong>{user.displayName}</strong>&nbsp;(<code>{user.username}</code>)
        </Modal.Header>
        <Modal.Body>
          <Row>

            <Col md={6}>
              <h4>Name</h4>
              <p>{user.displayName}</p>
            </Col>
            <Col md={6}>
              <h4>Username</h4>
              <p>{user.username}</p>
            </Col>
            <Col md={6}>
              <h4>Email</h4>
              <p>{user.email}</p>
            </Col>
            <Col md={6}>
              <h4>Department</h4>
              <p>{user.department || 'n/a'}</p>
            </Col>
            <Col md={6}>
              <h4>Title</h4>
              <p>{user.title || 'n/a'}</p>
            </Col>

            <Col xs={12}>
              <hr/>
            </Col>

            <Col md={6}>
              <h4>Active</h4>
              <p>
                <TrueFalseLabel bool={user.active}/>
              </p>
            </Col>
            <Col md={6}>
              <h4>Admin</h4>
              <p>
                <TrueFalseLabel bool={user.admin}/>
              </p>
            </Col>
            <Col md={6}>
              <h4>Account Locked</h4>
              <p>
                <TrueFalseLabel bool={user.locked}/>
              </p>
            </Col>
            <Col md={6}>
              <h4>Account Expired</h4>
              <p>
                <TrueFalseLabel bool={user.expired}/>
              </p>
            </Col>
            <Col md={6}>
              <h4>Credentials Expired</h4>
              <p>
                <TrueFalseLabel bool={user.credentialsExpired}/>
              </p>
            </Col>

            <Col xs={12}>
              <hr/>
            </Col>

            <Col xs={12}>
              <h4>Attributes</h4>
              {
                attributes.length > 0
                    ? (
                        <Table style={{fontSize: "0.8rem"}}>
                          <thead>
                          <tr>
                            <th>Name</th>
                            <th>Value</th>
                          </tr>
                          </thead>
                          <tbody>
                          {attributes}
                          </tbody>
                        </Table>
                    ) : <p className="text-muted">n/a</p>
              }
            </Col>

          </Row>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="info" href={"/user/" + user.id}>
            <User size={14} className="mb-1"/>
            &nbsp;
            View Profile
          </Button>
          <Button variant="warning" href={"/users/" + user.id + "/edit"}>
            <Edit size={14} className="mb-1"/>
            &nbsp;
            Edit
          </Button>
          <Button variant="secondary" onClick={() => showModal()}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
  )

};

const TrueFalseLabel = ({bool}) => {
  if (!!bool) {
    return <Badge color={'success'}>True</Badge>
  } else {
    return <Badge color={'danger'}>False</Badge>
  }
};

UserDetailsModal.propTypes = {
  user: PropTypes.object,
  isOpen: PropTypes.bool,
  showModal: PropTypes.func
};


export default UserDetailsModal;
