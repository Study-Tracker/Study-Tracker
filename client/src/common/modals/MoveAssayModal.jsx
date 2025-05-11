/*
 * Copyright 2019-2024 the original author or authors.
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

import React, {useContext, useState} from "react";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import PropTypes from "prop-types";
import axios from "axios";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import {DismissableAlert} from "../errors";
import NotyfContext from "../../context/NotyfContext";
import {useNavigate} from "react-router-dom";
import AsyncSelect from "react-select/async";

const MoveAssayModal = ({ assay, study, isOpen, setIsOpen }) => {

  const queryClient = useQueryClient();
  const notyf = useContext(NotyfContext);
  const navigate = useNavigate();
  const [selectedStudy, setSelectedStudy] = useState(study);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const studyAutocomplete = (input) => {
    return axios.get(`/api/internal/autocomplete/study?q=${input}`)
    .then(response => {
      return response.data
      .filter(s => s.active)
      .filter(s => s.id !== study.id)
      .sort((a, b) => {
        const aLabel = a.code + ": " + a.name;
        const bLabel = b.code + ": " + b.name;
        if (aLabel < bLabel) {
          return -1;
        }
        if (aLabel > bLabel) {
          return 1;
        }
        return 0;
      })
      .map(s => {
        return {
          label: s.code + ": " + s.name,
          value: s.id,
          obj: s
        }
      });
    }).catch(e => {
      console.error(e);
    })
  }

  const changeStudyMutation = useMutation({
    mutationFn: (newStudy) => {
      return axios.put(`/api/internal/assay/${assay.id}/study`,
        { "studyId": newStudy.id });
    }
  })

  const handleSubmit = (s) => {
    setIsSubmitting(true);
    changeStudyMutation.mutate(s, {
      onSuccess: (data) => {
        console.debug("Updated assay", data);
        queryClient.invalidateQueries({queryKey: ["assays"]});
        queryClient.invalidateQueries({queryKey: ["assay"]});
        notyf.success("Assay moved successfully");
        setIsOpen(false);
        navigate(`/study/${s.code}/assay/${data.data.code}`);
      },
      onError: (e) => {
        console.error(e);
        console.warn("Failed to move assay.")
        notyf.error("Failed to move assay");
      },
      onSettled: () => {
        setIsSubmitting(false);
      }
    })
  }

  return (
    <Modal show={isOpen} onHide={() => setIsOpen(false)}>

      <Modal.Header closeButton>
        Move Assay
      </Modal.Header>

      <Modal.Body>
        <Form>
          <Row>

            <Col xs={12}>
              <DismissableAlert
                color={"info"}
                dismissable={false}
                message={"Select the study you would like to move the current assay to. Moved assays will receive a " +
                  "new assay code, storage folder, and notebook folder (if applicable). Existing storage and notebook " +
                  "folders will not be removed and will still be linked to the moved assay."}
              />
            </Col>

            <Col xs={12}>
              <AsyncSelect
                placeholder="Search-for and select a new study..."
                className={"react-select-container"}
                classNamePrefix="react-select"
                loadOptions={studyAutocomplete}
                onChange={(selected) => setSelectedStudy(selected.obj)}
                // controlShouldRenderValue={false}
                // defaultOptions={true}
              />
              <Form.Text className="text-muted">
                Select a new study to associate your assay with.
              </Form.Text>
            </Col>

          </Row>
        </Form>
      </Modal.Body>

      <Modal.Footer>
        <Button
          variant="secondary"
          onClick={() => setIsOpen(false)}
        >
          Cancel
        </Button>
        <Button
          variant="primary"
          onClick={() => handleSubmit(selectedStudy)}
          disabled={selectedStudy === null || selectedStudy?.id === study.id || isSubmitting}
        >
          {isSubmitting ? "Working..." : "Submit"}
        </Button>
      </Modal.Footer>

    </Modal>
  )

}

MoveAssayModal.propTypes = {
  assay: PropTypes.object.isRequired,
  study: PropTypes.object.isRequired,
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
}

export default MoveAssayModal;
