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

import React, {useContext, useEffect, useState} from 'react';
import {useDropzone} from "react-dropzone";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCircleCheck, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import {Button, Modal} from "react-bootstrap";
import PropTypes from "prop-types";
import {DismissableAlert} from "../errors";
import {FormGroup} from "../forms/common";
import NotyfContext from "../../context/NotyfContext";
import axios from "axios";
import {LoadingOverlay} from "../loading";

const QueuedFile = ({file, handleRemove}) => {
  return (
      <div className={"dropzone-item d-flex justify-content-between bg-light p-3 mt-2"} key={"file-" + file.name}>
        <div className={"dropzone-file"}>
          <div className={"dropzone-filename text-dark"}>
            {file.name}&nbsp;({file.size} bytes)
          </div>
          <div className={"dropzone-error mt-0"}></div>
        </div>
        <div className={"dropzone-progress"}></div>
        <div className={"dropzone-toolbar"}>
          <Button variant={"outline-danger"} onClick={() => handleRemove(file)}>
            <FontAwesomeIcon icon={faTrashAlt} />
          </Button>
        </div>
      </div>
  )
}

QueuedFile.propTypes = {
  file: PropTypes.object.isRequired,
  handleRemove: PropTypes.func.isRequired,
}

const SuccessFile = ({path}) => {
  return (
      <div className={"dropzone-item d-flex justify-content-between dropzone-success p-3 mt-2"} key={"file-" + path}>
        <div className={"dropzone-file"}>
          <div className={"dropzone-filename"}>
            {path}
          </div>
          <div className={"dropzone-error mt-0"}></div>
        </div>
        <div className={"dropzone-toolbar"}>
          <FontAwesomeIcon size={"xl"} icon={faCircleCheck} className={"text-success"}/>
        </div>
      </div>
  )
}

SuccessFile.propTypes = {
  path: PropTypes.string,
}

const FileManagerUploadModal = ({
  isOpen,
  setModalIsOpen,
  path,
  error,
  handleSuccess,
  folderId
}) => {

  const notyf = useContext(NotyfContext);
  const [queuedFiles, setQueuedFiles] = useState([]);
  const [successFiles, setSuccessFiles] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    getRootProps,
    getInputProps,
    open,
    acceptedFiles,
    fileRejections
  } = useDropzone({
    maxSize: 1024 * 1024 * 20,
    onDropRejected: (rejectedFiles) => notyf.open({
      type: 'error',
      message: `File ${rejectedFiles[0].file.name} is too large. The limit is 20 MB`
    }),
    noClick: true,
  })

  const handleCloseModal = () => {
    setQueuedFiles([]);
    setSuccessFiles([]);
    setModalIsOpen(false);
  }

  const updateQueuedFiles = (files) => {
    const existing = queuedFiles.map(f => f.name + "--" + f.size);
    const toAdd = files.filter(f => !existing.includes(f.name + "--" + f.size));
    setQueuedFiles([...queuedFiles, ...toAdd]);
  }

  useEffect(() => {
    console.debug('acceptedFiles', acceptedFiles);
    updateQueuedFiles(acceptedFiles);
  }, [acceptedFiles]);


  const handleUploadFiles = () => {
    console.debug("Files", queuedFiles);
    setIsSubmitting(true);
    const requests = queuedFiles.map(file => {
      const data = new FormData();
      data.append("file", file);
      data.append("folderId", folderId);
      data.append("path", path);
      return axios.post('/api/internal/data-files/upload', data)
      .then(() => {
        return {
          ...file,
          success: true
        }
      })
      .catch(err => {
        return {
          ...file,
          success: false,
          error: err
        }
      });
    });
    Promise.allSettled(requests)
    .then((results) => {
      console.debug("Result", results);
      const success = [];
      let failed = false;
      for (const r of results) {
        if (r.status === "fulfilled" && !r.value.error) {
          setQueuedFiles(queuedFiles.filter(f => f.path !== r.value.path));
          success.push(r.value.path);
        } else {
          failed = true;
        }
      }
      if (!failed) {
        setQueuedFiles([]);
        notyf.open({message: "Files uploaded successfully", type: "success"});
        handleSuccess();
      } else {
        setSuccessFiles(success);
        notyf.open({message: "One or more uploads failed. Please try again.", type: "error"});
      }
    })
    .finally(() => {
      setIsSubmitting(false);
    });
  }

  const handleRemoveFile = (file) => {
    const updated = queuedFiles.filter(f => f !== file);
    setQueuedFiles(updated);
  }

  return (
      <Modal
          show={isOpen}
          onHide={handleCloseModal}
      >

        <LoadingOverlay isVisible={isSubmitting} message={"Uploading files..."} />

        <Modal.Header closeButton>
          Upload Files
        </Modal.Header>

        <Modal.Body className="m-3">

          <FormGroup>
            <div className={"dropzone dropzone-queue mb-2"}>

              <div {...getRootProps()} className={"dropzone-panel d-flex"}>

                <input {...getInputProps()} />

                <p>
                  Drag-and-drop files here to queue them for upload.
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
                {
                  queuedFiles.map((f, i) => (
                      <QueuedFile key={`queued-file-${i}`} file={f} handleRemove={handleRemoveFile} />
                  ))
                }
              </div>

              <div className={"dropzone-items"}>
                {
                  successFiles.map((f, i) => (
                      <SuccessFile key={`success-file-${i}`} path={f} />
                  ))
                }
              </div>


              <div className={"dropzone-default dropzone-message"}>
                {error && <DismissableAlert variant="danger" message={error}/>}
              </div>

              <div className={"dropzone-controls d-flex justify-content-end mt-2"}>
                {
                    queuedFiles.length > 0 && (
                        <>
                          <Button
                              variant={"outline-warning"}
                              onClick={() => setQueuedFiles([])}
                              className={"me-2"}
                          >
                            Remove All
                          </Button>

                          <Button
                              variant={"primary"}
                              onClick={handleUploadFiles}
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

        </Modal.Body>

      </Modal>
  )
};

FileManagerUploadModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  setModalIsOpen: PropTypes.func.isRequired,
  path: PropTypes.string.isRequired,
  error: PropTypes.string,
  handleSuccess: PropTypes.func.isRequired,
  folderId: PropTypes.number,
};

export default FileManagerUploadModal;