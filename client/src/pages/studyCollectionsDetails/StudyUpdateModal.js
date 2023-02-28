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
import PropTypes from "prop-types";
import {Button, Modal} from "react-bootstrap";
import StudyInputs from "../../common/forms/StudyInputs";

const StudyUpdateModal = ({studies, isOpen, closeModal, handleUpdate}) => {

  return (
      <Modal
          show={isOpen}
          onHide={closeModal}
          size={"lg"}
      >
        <Modal.Header closeButton>
          <Modal.Title>Edit Studies</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <StudyInputs studies={studies} onChange={handleUpdate} />
        </Modal.Body>
        <Modal.Footer>
          <Button variant={"secondary"} onClick={closeModal}>Close</Button>
        </Modal.Footer>
      </Modal>
  );
}

StudyUpdateModal.propTypes = {
  studies: PropTypes.array.isRequired,
  isOpen: PropTypes.bool.isRequired,
  closeModal: PropTypes.func.isRequired,
  handleUpdate: PropTypes.func.isRequired
}

export default StudyUpdateModal;