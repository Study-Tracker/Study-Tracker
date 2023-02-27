import React from "react";
import PropTypes from "prop-types";
import {Modal} from "react-bootstrap";
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