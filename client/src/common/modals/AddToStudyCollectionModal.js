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

import React, {useState} from "react";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import Select from "react-select";
import swal from "sweetalert";
import axios from "axios";
import PropTypes from "prop-types";
import {useMutation, useQuery, useQueryClient} from "react-query";

const AddToStudyCollectionModal = ({showModal, isOpen, study}) => {

  // const [collections, setCollections] = useState([]);
  const queryClient = useQueryClient();
  const [selected, setSelected] = useState(null);

  const {data: collections} = useQuery("collections", async () => {
    return axios.get("/api/internal/studycollection?visibleToMe=true")
    .then(response => response.data)
  });

  const mutation = useMutation(async (collectionId) => {
    return axios.post("/api/internal/studycollection/" + collectionId + "/" + study.id);
  });

  const handleSubmit = () => {
    if (!!selected) {
      mutation.mutate(selected, {
        onSuccess: () => {
          queryClient.invalidateQueries("studyCollections");
          setSelected(null);
          showModal(false);
        },
        onError: (e) => {
          console.error(e);
          console.warn("Failed to add study to collection.")
          swal(
            "Something went wrong",
            "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
          );
        }
      })
    }
  }

  const options = (collections || [])
  .sort((a, b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()))
  .map(c => {
    return {
      value: c.id,
      label: c.name,
      public: c.shared
    }
  });

  const groupedOptions = [
    {
      label: "Public Collections",
      options: options.filter(o => o.public)
    },
    {
      label: "Private Collections",
      options: options.filter(o => !o.public)
    }
  ];

  const formatGroupLabel = (data) => {
    return (
        <div className={"react-select-group"}>
          <span>{data.label}</span>
          <span className={"react-select-group-badge"}>{data.options.length}</span>
        </div>
    );
  }

  return (
      <Modal show={isOpen} onHide={() => showModal(false)}>
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
                      options={groupedOptions}
                      onChange={o => {
                        console.debug("Option", o);
                        setSelected(o.value);
                      }}
                      formatGroupLabel={formatGroupLabel}
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