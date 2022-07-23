import React, {useEffect, useState} from "react";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import Select from "react-select";
import swal from "sweetalert";
import axios from "axios";
import PropTypes from "prop-types";

const AddToStudyCollectionModal = props => {

  const {showModal, isOpen, study} = props;
  const [collections, setCollections] = useState([]);
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    axios.get("/api/studycollection?visibleToMe=true")
    .then(response => setCollections(response.data))
    .catch(e => {
      console.error(e);
    })
  }, []);

  const handleSubmit = () => {
    if (!!selected) {
      axios.post("/api/studycollection/" + selected + "/" + study.id)
      .then(() => showModal(false))
      .catch(e => {
        console.error(e);
        console.warn("Failed to add study to collection.")
        swal(
            "Something went wrong",
            "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
        )
      })
    }
  }

  const options = collections
  .sort((a, b) => {
    if (a.name > b.name) {
      return 1;
    } else if (a.name < b.name) {
      return -1;
    } else {
      return 0;
    }
  })
  .map(c => {
    return {
      value: c.id,
      label: c.name
    }
  });

  return (
      <Modal show={isOpen}
             onHide={() => showModal(false)}
      >
        <Modal.Header closeButton>
          Add Study to Collection
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Row>
              <Col xs={12}>
                <Form.Group>
                  <Form.Label>Collection</Form.Label>
                  <Select
                      className="react-select-container"
                      classNamePrefix="react-select"
                      value={options.filter(
                          o => o.value === selected)}
                      options={options}
                      onChange={o => {
                        console.debug("Option", o);
                        setSelected(o.value);
                      }}
                  />
                </Form.Group>
              </Col>
            </Row>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button
              variant="secondary"
              onClick={() => showModal(false)}
          >
            Cancel
          </Button>
          <Button
              variant="primary"
              onClick={() => handleSubmit(selected)}
              disabled={!selected}
          >
            Submit
          </Button>
        </Modal.Footer>
      </Modal>
  )

}

AddToStudyCollectionModal.propTypes = {
  showModal: PropTypes.func.isRequired,
  isOpen: PropTypes.bool.isRequired,
  study: PropTypes.object.isRequired
}

export default AddToStudyCollectionModal;