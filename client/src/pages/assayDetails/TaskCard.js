import {Badge, Card, Col, Dropdown, Row} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faCheckCircle,
  faCircleXmark,
  faEdit
} from "@fortawesome/free-regular-svg-icons";
import {faTrash, faUndoAlt} from "@fortawesome/free-solid-svg-icons";

const TaskStatusBadge = ({status}) => {

  let color = 'secondary';
  let label = 'Unknown';
  if (status === 'TODO') {
    color = 'info';
    label = "To Do";
  } else if (status === 'COMPLETE') {
    color = 'success';
    label = "Complete";
  } else if (status === 'INCOMPLETE') {
    color = 'danger';
    label = "Incomplete";
  }

  return (
      <Badge className="me-2" bg={color}>{label}</Badge>
  )

}

const TaskCard = ({
    task,
    index,
    handleTaskComplete,
    handleTaskIncomplete,
    handleTaskEdit,
    handleTaskDelete,
    handleTaskReset
}) => {

  return (
      <Card>
        <Card.Body>
          <Row>

            <Col xs={1} className={"d-flex align-items-center"}>
              <span className="text-muted text-lg">#{index+1}</span>
            </Col>

            <Col xs={5} className={"d-flex align-items-center"}>
              <div>
                <span className="fw-bolder">{task.label}</span>
                {
                    task.dueDate && task.status !== "COMPLETE" && (
                        <>
                          <br />
                          <span className="text-muted">Due {new Date(task.dueDate).toLocaleDateString()}</span>
                        </>
                    )
                }
                {
                    task.status === "COMPLETE" && (
                        <>
                          <br />
                          <span className="text-muted">Completed {new Date(task.updatedAt).toLocaleDateString()}</span>
                        </>
                    )
                }
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <div>
                <span className="text-muted">Status</span>
                <br />
                <TaskStatusBadge status={task.status} />
              </div>
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              {
                task.status === "COMPLETE" ? (
                    <div>
                      <span className="text-muted">Completed by</span>
                      <br />
                      <span className={"fw-bolder"}>{task.lastModifiedBy.displayName}</span>
                    </div>
                ) : (
                    <div>
                      <span className="text-muted">Assigned to</span>
                      <br />
                      {
                        task.assignedTo
                            ? <span className={"fw-bolder"}>{task.assignedTo.displayName}</span>
                            : "n/a"
                      }
                    </div>
                )
              }
            </Col>

            <Col xs={2} className={"d-flex align-items-center"}>
              <Dropdown>
                <Dropdown.Toggle variant="outline-primary">
                  Actions
                </Dropdown.Toggle>
                <Dropdown.Menu>

                  {
                      task.status !== "TODO" && (
                          <Dropdown.Item onClick={() => handleTaskReset(task)}>
                            <FontAwesomeIcon icon={faUndoAlt} className={"me-1"} />
                            Reset
                          </Dropdown.Item>
                      )
                  }

                  {
                      task.status !== "COMPLETE" && (
                          <Dropdown.Item onClick={() => handleTaskComplete(task)}>
                            <FontAwesomeIcon icon={faCheckCircle} className={"me-1"} />
                            Mark as complete
                          </Dropdown.Item>
                      )
                  }

                  {
                      task.status !== "INCOMPLETE" && (
                          <Dropdown.Item onClick={() => handleTaskIncomplete(task)}>
                            <FontAwesomeIcon icon={faCircleXmark} className={"me-1"} />
                            Mark as incomplete
                          </Dropdown.Item>
                      )
                  }


                  <Dropdown.Divider />

                  <Dropdown.Item onClick={() => handleTaskEdit(task)}>
                    <FontAwesomeIcon icon={faEdit} className={"me-1"} />
                    Edit
                  </Dropdown.Item>

                  <Dropdown.Item onClick={() => handleTaskDelete(task)}>
                    <FontAwesomeIcon icon={faTrash} className={"me-1"} />
                    Delete
                  </Dropdown.Item>

                </Dropdown.Menu>
              </Dropdown>
            </Col>

          </Row>
        </Card.Body>
      </Card>
  )

}

TaskCard.propTypes = {
  task: PropTypes.object.isRequired,
  index: PropTypes.number.isRequired,
  handleTaskComplete: PropTypes.func.isRequired,
  handleTaskIncomplete: PropTypes.func.isRequired,
  handleTaskEdit: PropTypes.func.isRequired,
  handleTaskDelete: PropTypes.func.isRequired
}

export default TaskCard;