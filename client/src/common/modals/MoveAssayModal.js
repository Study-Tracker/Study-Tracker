import React, {useContext, useState} from "react";
import {useMutation, useQueryClient} from "react-query";
import PropTypes from "prop-types";
import axios from "axios";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import {DismissableAlert} from "../errors";
import NotyfContext from "../../context/NotyfContext";
import {useNavigate} from "react-router-dom";
import AsyncSelect from "react-select/async";

const MoveAssayModal = ({ assay, isOpen, setIsOpen }) => {

  const queryClient = useQueryClient();
  const notyf = useContext(NotyfContext);
  const navigate = useNavigate();
  const [selectedStudy, setSelectedStudy] = useState(assay.study);

  const studyAutocomplete = (input) => {
    return axios.get(`/api/internal/autocomplete/study?q=${input}`)
    .then(response => {
      return response.data
      .filter(study => study.active)
      .filter(study => study.id !== assay.study.id)
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
      .map(study => {
        return {
          label: study.code + ": " + study.name,
          value: study.id,
          obj: study
        }
      });
    }).catch(e => {
      console.error(e);
    })
  }

  const changeStudyMutation = useMutation((newStudy) => {
    return axios.put(`/api/internal/assay/${assay.id}/study`, {"studyId": newStudy.id});
  })

  const handleSubmit = (study) => {
    changeStudyMutation.mutate(study, {
      onSuccess: (data) => {
        console.debug("Updated assay", data);
        queryClient.invalidateQueries({queryKey: "assays"});
        queryClient.invalidateQueries({queryKey: "assay"});
        notyf.success("Assay moved successfully");
        setIsOpen(false);
        navigate(`/assay/${data.data.code}`);
      },
      onError: (e) => {
        console.error(e);
        console.warn("Failed to move assay.")
        notyf.error("Failed to move assay");
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
                controlShouldRenderValue={false}
                defaultOptions={true}
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
          disabled={selectedStudy === null || !selectedStudy.id === assay.study.id}
        >
          Submit
        </Button>
      </Modal.Footer>

    </Modal>
  )

}

MoveAssayModal.propTypes = {
  assay: PropTypes.object.isRequired,
  isOpen: PropTypes.bool.isRequired,
  setIsOpen: PropTypes.func.isRequired,
}

export default MoveAssayModal;
