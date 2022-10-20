/*
 * Copyright 2022 the original author or authors.
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

import Dropzone from "react-dropzone";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlay, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import {Button, Modal} from "react-bootstrap";
import PropTypes from "prop-types";
import {DismissableAlert} from "../errors";
import {FormGroup} from "../forms/common";

const FileManagerUploadModal = ({
  isOpen,
  setModalIsOpen,
  handleSubmit,
  error
}) => {

  const renderFilesToUpload = (files) => {
    return files.map((f, i) => (
        <div className={"dropzone-item d-flex justify-content-between bg-light p-3 mt-2"} key={"file-" + i}>
          <div className={"dropzone-file"}>
            <div className={"dropzone-filename text-dark"}>
              {f.name}&nbsp;({f.size} bytes)
            </div>
            <div className={"dropzone-error mt-0"}></div>
          </div>
          <div className={"dropzone-progress"}></div>
          <div className={"dropzone-toolbar"}>
            <Button variant={"outline-info"} className={"me-2"}>
              <FontAwesomeIcon icon={faPlay} />
            </Button>
            <Button variant={"outline-danger"}>
              <FontAwesomeIcon icon={faTrashAlt} />
            </Button>
          </div>
        </div>
    ))
  }

  return (
      <Modal
          show={isOpen}
          onHide={() => setModalIsOpen(false)}
      >

        <Modal.Header closeButton>
          Upload Files
        </Modal.Header>

        <Modal.Body className="m-3">

          <Dropzone maxSize={1024*1024*20}>
            {({
              getRootProps,
              getInputProps,
              acceptedFiles,
              open
            }) => (
                <FormGroup>
                  <div className={"dropzone dropzone-queue mb-2"}>

                    <div {...getRootProps()} className={"dropzone-panel d-flex"}>

                      <input {...getInputProps()} />

                      <p>
                        Drag-and-drop files here to upload them.
                      </p>

                      <Button
                          variant={"outline-primary"}
                          onClick={open}
                          className={"me-2"}
                      >
                        Select Files
                      </Button>

                    </div>

                    <div className={"dropzone-items"}>
                      {renderFilesToUpload(acceptedFiles)}
                    </div>

                    <div className={"dropzone-default dropzone-message"}>
                      {error && <DismissableAlert variant="danger" message={error}/>}
                    </div>

                    <div className={"dropzone-controls d-flex justify-content-end mt-2"}>
                      {
                          acceptedFiles.length > 0 && (
                              <>
                                <Button
                                    variant={"outline-warning"}
                                    onClick={() => console.log("Remove")}
                                    className={"me-2"}
                                >
                                  Remove All
                                </Button>

                                <Button
                                    variant={"primary"}
                                    onClick={() => handleSubmit(acceptedFiles)}
                                >
                                  Upload All
                                </Button>
                              </>
                          )
                      }
                    </div>

                  </div>

                  <span className={"text-muted"}>Max file size: 20MB</span>
                </FormGroup>
            )}
          </Dropzone>

        </Modal.Body>

      </Modal>
  )
};

FileManagerUploadModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setModalIsOpen: PropTypes.func.isRequired,
  handleSubmit: PropTypes.func.isRequired
};

export default FileManagerUploadModal;