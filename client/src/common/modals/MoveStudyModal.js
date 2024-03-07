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

  const {data: programs} = useQuery("programs", () => {
    return axios.get("/api/internal/program").then((res) => res.data);
  }, { placeholderData: []})

  const changeProgramMutation = useMutation((newProgram) => {
    return axios.put(`/api/internal/study/${study.id}/program`, {"programId": newProgram.id});
  })

  const handleSubmit = (program) => {
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
          disabled={!selectedProgram.id === study.program.id}
        >
          Submit
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
