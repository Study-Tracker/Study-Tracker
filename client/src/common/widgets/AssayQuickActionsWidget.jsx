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
  faPersonRunning,
  faTrash,
  faTrashArrowUp,
  faTriangleExclamation
} from "@fortawesome/free-solid-svg-icons";
import React, {useState} from "react";
import PropTypes from "prop-types";
import {faSquareCheck} from "@fortawesome/free-regular-svg-icons";
import NotyfContext from "../../context/NotyfContext";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import swal from "sweetalert2";
import MoveAssayModal from "../modals/MoveAssayModal";

const AssayQuickActionsWidget = ({
    assay,
    study
}) => {

  const notyf = React.useContext(NotyfContext);
  const navigate = useNavigate();
  const [moveAssayModalIsOpen, setMoveAssayModalIsOpen] = useState(false);

  const handleStatusChange = (status) => {
    axios.post("/api/internal/assay/" + assay.id + "/status", {
      status: status
    })
    .then(() => window.location.reload())
    .catch(e => {
      notyf.open({
        type: 'error',
        message: 'Error changing status'
      });
      console.error(e);
    });
  }

  const handleAssayDelete = () => {
    swal({
      title: "Are you sure you want to remove this assay?",
      text: "Removed assays will be hidden from view, but their records will not be deleted. Assays can be recovered in the admin dashboard.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        axios.delete("/api/internal/assay/" + assay.code)
        .then(() => {
          navigate("/assays")
        })
        .catch(error => {
          console.error(error);
          notyf.open({
            type: "error",
            message: "Failed to remove assay. Please try again."
          });
        })
      }
    });
  }

  const handleAssayRestore = () => {
    if (!study.active) {
      notyf.open({
        type: 'error',
        message: 'Assay cannot be restored because parent study is not active.'
      });
      return;
    }
    axios.post("/api/internal/assay/" + assay.id + "/restore")
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
                  What's next?
                </h4>
                <br/>
                <Dropdown className="me-1 mb-1">
                  <Dropdown.Toggle variant={"outline-primary"}>
                    <FontAwesomeIcon icon={faBolt} className={"me-2"}/>
                    Actions
                  </Dropdown.Toggle>
                  <Dropdown.Menu>

                    {
                      (assay.status === "IN_PLANNING" || assay.status === "NEEDS_ATTENTION") && (
                            <Dropdown.Item onClick={() => handleStatusChange("ACTIVE")}>
                              <FontAwesomeIcon icon={faPersonRunning} className={"me-2"}/>
                              Set status to 'Active'
                            </Dropdown.Item>
                        )
                    }

                    {
                        assay.status === "ACTIVE" && (
                            <Dropdown.Item onClick={() => handleStatusChange("COMPLETE")}>
                              <FontAwesomeIcon icon={faSquareCheck} className={"me-2"}/>
                              Set status to 'Complete'
                            </Dropdown.Item>
                        )
                    }

                    {
                        assay.status === "COMPLETE" && (
                            <Dropdown.Item onClick={() => handleStatusChange("NEEDS_ATTENTION")}>
                              <FontAwesomeIcon icon={faTriangleExclamation} className={"me-2"}/>
                              Set status to 'Needs attention'
                            </Dropdown.Item>
                        )
                    }

                    <Dropdown.Divider/>

                    <Dropdown.Item onClick={() => setMoveAssayModalIsOpen(true)}>
                      <FontAwesomeIcon icon={faDolly} className={"me-2"} />
                      Move to another study
                    </Dropdown.Item>

                    <Dropdown.Item onClick={() => navigate("/study/" + study.code +
                        "/assay/" + assay.code + "/edit")}>
                      <FontAwesomeIcon icon={faEdit} className={"me-2"}/>
                      Edit
                    </Dropdown.Item>

                    {
                      assay.active ? (
                          <Dropdown.Item onClick={handleAssayDelete}>
                            <FontAwesomeIcon icon={faTrash} className={"me-2"}/>
                            Remove
                          </Dropdown.Item>
                      ) : (
                          <Dropdown.Item onClick={handleAssayRestore}>
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

        <MoveAssayModal
          assay={assay}
          study={study}
          isOpen={moveAssayModalIsOpen}
          setIsOpen={setMoveAssayModalIsOpen}
        />

      </Card>
  )
}

AssayQuickActionsWidget.propTypes = {
  assay: PropTypes.object.isRequired,
  study: PropTypes.object.isRequired
}

export default AssayQuickActionsWidget;
