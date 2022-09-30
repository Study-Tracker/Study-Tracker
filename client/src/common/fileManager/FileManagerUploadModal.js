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

import {useDropzone} from "react-dropzone";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFile} from "@fortawesome/free-solid-svg-icons";
import React, {useMemo} from "react";
import {Button, Col, Modal, Row} from "react-bootstrap";
import PropTypes from "prop-types";
import {DismissableAlert} from "../errors";

const baseStyle = {
  flex: 1,
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  padding: '20px',
  borderWidth: 2,
  borderRadius: 2,
  borderColor: '#eeeeee',
  borderStyle: 'dashed',
  backgroundColor: '#fafafa',
  color: '#bdbdbd',
  outline: 'none',
  transition: 'border .24s ease-in-out'
};

const activeStyle = {
  borderColor: '#2196f3'
};

const acceptStyle = {
  borderColor: '#00e676'
};

const rejectStyle = {
  borderColor: '#ff1744'
};

const FileManagerUploadModal = ({
  isOpen,
  setModalIsOpen,
  handleSubmit,
  error
}) => {

  const {
    acceptedFiles,
    getRootProps,
    getInputProps,
    isDragAccept,
    isDragActive,
    isDragReject
  } = useDropzone();

  const files = acceptedFiles.map(file => (
      <li key={file.path}>
        <FontAwesomeIcon icon={faFile}/> {file.path}
      </li>
  ));

  const style = useMemo(() => ({
    ...baseStyle,
    ...(isDragActive ? activeStyle : {}),
    ...(isDragAccept ? acceptStyle : {}),
    ...(isDragReject ? rejectStyle : {})
  }), [
    isDragActive, isDragReject
  ]);

  return (
      <Modal
          show={isOpen}
          onHide={() => setModalIsOpen(false)}
      >

        <Modal.Header closeButton>
          Upload Files
        </Modal.Header>

        <Modal.Body className="m-3">

          <Row>

            <Col sm={12}>
              <p>
                Uploaded files will be stored with your other study documents
                and
                will be accessible directly in the file system or through the
                Files
                tab.
              </p>
            </Col>

            <Col sm={12}>
              <div {...getRootProps({style})}>
                <input {...getInputProps()} />
                <p>
                  Drag-and-drop files to upload them, or click here to select
                  the files you would like to upload individually.
                </p>
              </div>
            </Col>

            <Col sm={12}></Col>

            <Col sm={12}>
              {files.length > 0 ? <h4 className="mt-3">To be uploaded:</h4> : ""}
              <ul>
                {files}
              </ul>
              {error && <DismissableAlert variant="danger" message={error}/>}
            </Col>

          </Row>

        </Modal.Body>

        <Modal.Footer>
          <Button variant={"secondary"} onClick={() => setModalIsOpen(false)}>
            Cancel
          </Button>
          <Button variant={"primary"}
                  onClick={() => handleSubmit(acceptedFiles)}>
            Upload
          </Button>
        </Modal.Footer>

      </Modal>
  )
};

FileManagerUploadModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setModalIsOpen: PropTypes.func.isRequired,
  handleSubmit: PropTypes.func.isRequired
};

export default FileManagerUploadModal;