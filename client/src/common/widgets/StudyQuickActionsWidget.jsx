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

import {Card, Col, Dropdown, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faBolt,
  faDolly,
  faEdit,
  faFolderPlus,
  faPersonRunning,
  faStopwatch,
  faTrash,
  faTrashArrowUp,
  faTriangleExclamation,
} from "@fortawesome/free-solid-svg-icons";
import {faPlusSquare, faSquareCheck} from "@fortawesome/free-regular-svg-icons";
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import NotyfContext from "../../context/NotyfContext";
import swal from "sweetalert2";
import PropTypes from "prop-types";
import {faGit} from "@fortawesome/free-brands-svg-icons";
import MoveStudyModal from "../modals/MoveStudyModal";

const StudyQuickActionsWidget = ({
    study,
    handleAddToCollection
}) => {

  const navigate = useNavigate();
  const notyf = React.useContext(NotyfContext);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [moveStudyModalIsOpen, setMoveStudyModalIsOpen] = useState(false);

  const handleStatusChange = (status) => {
    setIsSubmitting(true);
    axios.post("/api/internal/study/" + study.id + "/status", {
      status: status
    })
    .then(() => window.location.reload())
    .catch(e => {
      notyf.open({
        type: 'error',
        message: 'Error changing status'
      });
      console.error(e);
    })
    .finally(() => setIsSubmitting(false));
  }

  const handleNewGitRepository = () => {
    setIsSubmitting(true);
    axios.post("/api/internal/study/" + study.id + "/git")
    .then(() => {
      notyf.open({
        type: "success",
        message: "Git repository created successfully."
      })
      navigate("#overview");
      navigate(0);
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "Failed to create Git repository. Please try again."
      })
    })
    .finally(() => setIsSubmitting(false));
  }

  const handleStudyDelete = () => {
    swal.fire({
      title: "Are you sure you want to remove this study?",
      text: "Removed studies will be hidden from view, but their records will not be deleted. Studies can be recovered at a later date.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val.isConfirmed) {
        axios({
          url: "/api/internal/study/" + study.code,
          method: 'delete',
          headers: {
            "Content-Type": "application/json"
          }
        }).then(() => {
          navigate("/studies")
        })
        .catch(error => {
          console.error(error);
          notyf.open({
            type: 'error',
            message: 'Failed to remove study. Please try again.'
          })
        })
      }
    });
  }

  const handleStudyRestore = () => {
    axios.post("/api/internal/study/" + study.code + "/restore")
    .then(() => {
      navigate(0);
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: 'error',
        message: 'Failed to restore study. Please try again.'
      })
    })
  }

  return (
      <Card className="illustration flex-fill">
        <Card.Body className="p-0 d-flex flex-fill">
          <Row className="g-0 w-100">
            <Col xs={6} className="align-self-end text-end">
              <img
                  src={"/static/images/clip/user-interface.png"}
                  alt="Quick actions"
                  className="img-fluid illustration-img"
              />
            </Col>
            <Col xs="6">
              <div className="illustration-text p-3 m-1">
                <h4 className="illustration-text">
                  What&apos;s next?
                </h4>
                <br/>
                <Dropdown className="me-1 mb-1">
                  {
                    isSubmitting ? (
                        <Dropdown.Toggle variant={"outline-primary"} disabled={true}>
                          <FontAwesomeIcon icon={faStopwatch} className={"me-2"}/>
                          Working...
                        </Dropdown.Toggle>
                    ) : (
                        <Dropdown.Toggle variant={"outline-primary"}>
                          <FontAwesomeIcon icon={faBolt} className={"me-2"}/>
                          Actions
                        </Dropdown.Toggle>
                    )
                  }
                  <Dropdown.Menu>

                    {
                      (study.status === "IN_PLANNING" || study.status === "NEEDS_ATTENTION") && (
                            <Dropdown.Item onClick={() => handleStatusChange("ACTIVE")}>
                              <FontAwesomeIcon icon={faPersonRunning} className={"me-2"}/>
                              Set status to &apos;Active&apos;
                            </Dropdown.Item>
                      )
                    }

                    {
                        study.status === "ACTIVE" && (
                            <Dropdown.Item onClick={() => handleStatusChange("COMPLETE")}>
                              <FontAwesomeIcon icon={faSquareCheck} className={"me-2"}/>
                              Set status to &apos;Complete&apos;
                            </Dropdown.Item>
                        )
                    }

                    {
                        study.status === "COMPLETE" && (
                            <Dropdown.Item onClick={() => handleStatusChange("NEEDS_ATTENTION")}>
                              <FontAwesomeIcon icon={faTriangleExclamation} className={"me-2"}/>
                              Set status to &apos;Needs attention&apos;
                            </Dropdown.Item>
                        )
                    }

                    <Dropdown.Item onClick={() => navigate("/study/" + study.code + "/assays/new")}>
                      <FontAwesomeIcon icon={faPlusSquare} className={"me-2"}/>
                      New Assay
                    </Dropdown.Item>

                    <Dropdown.Item onClick={handleAddToCollection}>
                      <FontAwesomeIcon icon={faFolderPlus} className={"me-2"}/>
                      Add to Collection
                    </Dropdown.Item>

                    {
                      study.gitRepositories.length === 0 && (
                          <Dropdown.Item onClick={handleNewGitRepository}>
                            <FontAwesomeIcon icon={faGit} className={"me-2"} />
                            Create Git repository
                          </Dropdown.Item>
                      )
                    }

                    <Dropdown.Divider />

                    <Dropdown.Item onClick={() => setMoveStudyModalIsOpen(true)}>
                      <FontAwesomeIcon icon={faDolly} className={"me-2"} />
                      Move to another program
                    </Dropdown.Item>

                    <Dropdown.Item onClick={() => navigate("/study/" + study.code + "/edit")}>
                      <FontAwesomeIcon icon={faEdit} className={"me-2"}/>
                      Edit
                    </Dropdown.Item>

                    {
                      study.active ? (
                          <Dropdown.Item onClick={handleStudyDelete}>
                            <FontAwesomeIcon icon={faTrash} className={"me-2"}/>
                            Remove
                          </Dropdown.Item>
                      ) : (
                          <Dropdown.Item onClick={handleStudyRestore}>
                            <FontAwesomeIcon icon={faTrashArrowUp} className={"me-2"}/>
                            Restore
                          </Dropdown.Item>
                      )
                    }



                  </Dropdown.Menu>
                </Dropdown>
              </div>
            </Col>
          </Row>
        </Card.Body>

        <MoveStudyModal
          study={study}
          isOpen={moveStudyModalIsOpen}
          setIsOpen={setMoveStudyModalIsOpen}
        />

      </Card>
  )
}

StudyQuickActionsWidget.propTypes = {
  study: PropTypes.object.isRequired,
  handleAddToCollection: PropTypes.func.isRequired
};

export default StudyQuickActionsWidget;
