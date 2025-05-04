/*
 * Copyright 2019-2025 the original author or authors.
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
import {RepairableStorageFolderLink} from "../../../common/files";
import {RepairableNotebookFolderLink} from "../../../common/eln";
import {Clipboard, Edit} from "react-feather";
import React from "react";
import PropTypes from "prop-types";

const createMarkup = (content) => {
  return {__html: content};
};

const ProgramDetailsModal = ({program, isOpen, showModal}) => {

  if (!program) {
    return "";
  }

  const attributes = Object.keys(program.attributes).map(k => {
    return (
        <tr key={"assay-type-attribute-" + k}>
          <td>{k}</td>
          <td>{program.attributes[k]}</td>
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
          Program Details
        </Modal.Header>
        <Modal.Body>
          <Row>

            <Col md={6}>
              <h4>Name</h4>
              <p>{program.name}</p>
            </Col>

            <Col md={6}>
              <h4>Code</h4>
              <p>{program.code}</p>
            </Col>

            <Col md={12}>
              <h4>Description</h4>
              <div dangerouslySetInnerHTML={createMarkup(
                  program.description)}/>
            </Col>

            <Col md={6}>
              <h4>Created</h4>
              <p>{new Date(
                  program.createdAt).toLocaleString()} by {program.createdBy.displayName}</p>
            </Col>

            <Col md={6}>
              <h4>Last Updated</h4>
              <p>{new Date(
                  program.createdAt).toLocaleString()} by {program.lastModifiedBy.displayName}</p>
            </Col>

            <Col md={6}>
              <h4>Active</h4>
              <p>
                {
                  program.active
                      ? <Badge bg={'success'}>True</Badge>
                      : <Badge bg={'danger'}>False</Badge>
                }
              </p>
            </Col>

            <Col xs={12}>
              <hr/>
            </Col>

            <Col xs={6}>
              <h4>File Storage</h4>
              <p>
                <RepairableStorageFolderLink
                    folder={program.primaryStorageFolder}
                    repairUrl={"/api/internal/program/" + program.id + "/storage/repair"}
                />
              </p>
            </Col>

            <Col xs={6}>
              <h4>ELN Folder</h4>
              <p>
                <RepairableNotebookFolderLink
                    folder={program.notebookFolder}
                    repairUrl={"/api/internal/program/" + program.id + "/notebook/repair"}
                />
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
          <Button variant="info"
                  href={"/program/" + program.id}>
            <Clipboard size={14} className="mb-1"/>
            &nbsp;
            View Program
          </Button>
          <Button variant="warning"
                  href={"/program/" + program.id + "/edit"}>
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

ProgramDetailsModal.propTypes = {
  program: PropTypes.object,
  isOpen: PropTypes.bool.isRequired,
  showModal: PropTypes.func.isRequired
};

export default ProgramDetailsModal;