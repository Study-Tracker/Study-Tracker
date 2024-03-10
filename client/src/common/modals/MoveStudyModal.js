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
import {useMutation, useQuery, useQueryClient} from "react-query";
import PropTypes from "prop-types";
import axios from "axios";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import {DismissableAlert} from "../errors";
import ProgramDropdown from "../forms/ProgramDropdown";
import NotyfContext from "../../context/NotyfContext";
import {useNavigate} from "react-router-dom";

const MoveStudyModal = ({ study, isOpen, setIsOpen }) => {

  const queryClient = useQueryClient();
  const notyf = useContext(NotyfContext);
  const navigate = useNavigate();
  const [selectedProgram, setSelectedProgram] = useState(study.program);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {data: programs} = useQuery("programs", () => {
    return axios.get("/api/internal/program").then((res) => res.data);
  }, { placeholderData: []})

  const changeProgramMutation = useMutation((newProgram) => {
    return axios.put(`/api/internal/study/${study.id}/program`, {"programId": newProgram.id});
  })

  const handleSubmit = (program) => {
    setIsSubmitting(true);
    changeProgramMutation.mutate(program, {
      onSuccess: (data) => {
        console.debug("Updated study", data);
        queryClient.invalidateQueries({queryKey: "studies"});
        queryClient.invalidateQueries({queryKey: "study"});
        notyf.success("Study moved successfully");
        setIsOpen(false);
        navigate(`/study/${data.data.code}`);
      },
      onError: (e) => {
        console.error(e);
        console.warn("Failed to move study.")
        notyf.error("Failed to move study");
      },
      onSettled: () => {
        setIsSubmitting(false);
      }
    })
  }

  return (
    <Modal show={isOpen} onHide={() => setIsOpen(false)}>

      <Modal.Header closeButton>
        Move Study
      </Modal.Header>

      <Modal.Body>
        <Form>
          <Row>

            <Col xs={12}>
              <DismissableAlert
                color={"info"}
                dismissable={false}
                message={"Select the program you would like to move the current study to. Moved studies will receive a " +
                  "new study code, storage folder, and notebook folder (if applicable). Existing storage and notebook " +
                  "folders will not be removed and will still be linked to the moved study."}
              />
            </Col>

            <Col xs={12}>
              <ProgramDropdown
                programs={programs}
                selectedProgram={selectedProgram.id}
                onChange={setSelectedProgram}
                isInvalid={selectedProgram.id === study.program.id}
                description={"Select a new program to associate your study with."}
                error={"You must select a new program"}
              />
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
          onClick={() => handleSubmit(selectedProgram)}
          disabled={!selectedProgram.id === study.program.id || isSubmitting}
        >
          {isSubmitting ? "Working..." : "Submit"}
        </Button>
      </Modal.Footer>

    </Modal>
  )

}

MoveStudyModal.propTypes = {
  study: PropTypes.object.isRequired,
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
}

export default MoveStudyModal;
