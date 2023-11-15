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
    axios.get("/api/internal/studycollection?visibleToMe=true")
    .then(response => setCollections(response.data))
    .catch(e => {
      console.error(e);
    })
  }, []);

  const handleSubmit = () => {
    if (!!selected) {
      axios.post("/api/internal/studycollection/" + selected + "/" + study.id)
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
  .sort((a, b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()))
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