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

import {Button, Col, Modal, Row, Table} from "react-bootstrap";
import {AssayTaskList} from "../../../common/assayTasks";
import React from "react";
import PropTypes from "prop-types";

const AssayTypeDetailsModal = ({assayType, isOpen, showModal}) => {

  if (!assayType) {
    return "";
  }

  const fields = assayType.fields.map(f => {
    return (
        <tr key={"assay-type-field-" + f.fieldName}>
          <td>{f.displayName}</td>
          <td><code>{f.fieldName}</code></td>
          <td>{f.type}</td>
          <td>{!!f.required ? "Yes" : "No"}</td>
          <td>{f.description}</td>
        </tr>
    )
  });

  const attributes = Object.keys(assayType.attributes).map(k => {
    return (
        <tr key={"assay-type-attribute-" + k}>
          <td>{k}</td>
          <td>{assayType.attributes[k]}</td>
        </tr>
    )
  })

  return (
      <Modal
          show={isOpen}
          onHide={() => showModal()}
          size={"lg"}
      >
        <Modal.Header closeButton>
          Assay Type: {assayType.name}
        </Modal.Header>
        <Modal.Body className="m-3">
          <Row>

            <Col xs={12}>
              <h4>Description</h4>
              <p>{assayType.description}</p>
            </Col>

            <Col xs={12}>
              <h4>Fields</h4>
              {
                fields.length > 0
                    ? (
                        <Table style={{fontSize: "0.8rem"}}>
                          <thead>
                          <tr>
                            <th>Display Name</th>
                            <th>Field Name</th>
                            <th>Data Type</th>
                            <th>Required</th>
                            <th>Description</th>
                          </tr>
                          </thead>
                          <tbody>
                          {fields}
                          </tbody>
                        </Table>
                    ) : (
                        <p className="text-muted">n/a</p>
                    )
              }
            </Col>

            <Col xs={12}>
              <h4>Default Tasks</h4>
              {
                !!assayType.tasks && assayType.tasks.length > 0
                    ? <AssayTaskList tasks={assayType.tasks}/>
                    : <p className="text-muted">n/a</p>
              }

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
          <Button color="secondary" onClick={() => showModal()}>
            Close
          </Button>
        </Modal.Footer>

      </Modal>
  )
}

AssayTypeDetailsModal.propTypes = {
  assayType: PropTypes.object,
  isOpen: PropTypes.bool.isRequired,
  showModal: PropTypes.func.isRequired
}

export default AssayTypeDetailsModal;