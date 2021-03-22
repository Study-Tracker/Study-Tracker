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

import {
  Button,
  Col,
  Media,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Row
} from "reactstrap";
import React from "react";
import ReactQuill from "react-quill";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faPlusCircle,} from "@fortawesome/free-solid-svg-icons";
import {Clipboard} from "react-feather";

/**
 * Displays the study conclusions or a placeholder.
 *
 * @param conclusions
 * @param toggleModal
 * @param isSignedIn
 * @returns {*}
 * @constructor
 */
export const Conclusions = ({conclusions, toggleModal, isSignedIn}) => {

  if (!!conclusions) {

    const createMarkup = (content) => {
      return {__html: content};
    };

    return (

        <Media className="assay-card">

          <Clipboard
              size={36}
              className="align-middle text-primary mr-4"
          />

          <Media body>

            <Row>

              <Col xs={12}>
                <div className="mb-3" dangerouslySetInnerHTML={createMarkup(
                    conclusions.content)}/>
              </Col>

            </Row>

            <Row className="mt-2">

              <Col sm={6}>
                <h6 className="details-label">Created By</h6>
                <p>{conclusions.createdBy.displayName}</p>
              </Col>

              <Col sm={6}>
                <h6 className="details-label">Date Added</h6>
                <p>
                  {new Date(conclusions.createdAt).toLocaleDateString()}
                </p>
              </Col>

            </Row>

            <Row className="mt-2">

              {
                !!conclusions.lastModifiedBy
                    ? (
                        <Col sm={6}>
                          <h6 className="details-label">Updated By</h6>
                          <p>{conclusions.lastModifiedBy.displayName}</p>
                        </Col>
                    )
                    : ''
              }

              {
                !!conclusions.updatedAt
                    ? (
                        <Col sm={6}>
                          <span>
                            <h6 className="details-label">Last Updated</h6>
                            <p>
                              {new Date(
                                  conclusions.updatedAt).toLocaleDateString()}
                            </p>
                          </span>
                        </Col>
                    ) : ''
              }

            </Row>

            <Row className="mt-2">
              {
                isSignedIn ? (
                    <Col sm={12}>
                      <Button color={'info'} onClick={() => toggleModal()}>
                        Edit
                        &nbsp;&nbsp;
                        <FontAwesomeIcon icon={faEdit}/>
                      </Button>
                    </Col>
                ) : ''
              }
            </Row>

          </Media>
        </Media>
    );
  } else {
    return (
        <Row>
          <Col sm={12}>
            <div className={"text-center"}>
              <h4>No conclusions have been added.</h4>
              {
                isSignedIn
                    ? (
                        <div>
                          <Button color={"primary"} onClick={() => toggleModal()}>
                            Add Conclusions
                            &nbsp;&nbsp;
                            <FontAwesomeIcon icon={faPlusCircle}/>
                          </Button>
                        </div>
                    ) : ''
              }
            </div>
          </Col>
        </Row>
    );
  }

};

/**
 * Modal for adding conclusions.
 */
export const ConclusionsModal = ({
  conclusions,
  isOpen,
  toggleModal,
  handleUpdate,
  handleSubmit
}) => {

  console.log(conclusions)
  return (
      <Modal
          isOpen={isOpen}
          toggle={() => toggleModal()}
          size={"lg"}
      >

        <ModalHeader toggle={() => toggleModal()}>
          Add Conclusions
        </ModalHeader>

        <ModalBody className="m-3">

          <Row form>

            <Col sm={12}>
              <p>
                Add a brief summary of your study's conclusions. Supporting
                documents may be uploaded as attachments.
              </p>
            </Col>

            <Col sm={12}>
              <ReactQuill
                  theme="snow"
                  defaultValue={conclusions.content}
                  onChange={(content, delta, source, editor) => {
                    handleUpdate(content);
                  }}
              />
            </Col>

          </Row>

        </ModalBody>

        <ModalFooter>
          <Button color={"secondary"}
                  onClick={() => toggleModal()}>
            Cancel
          </Button>
          <Button color={"primary"} onClick={() => handleSubmit()}>
            Save
          </Button>
        </ModalFooter>

      </Modal>
  );

}