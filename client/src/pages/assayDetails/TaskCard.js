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

import {
  Badge,
  Card,
  Col,
  Dropdown,
  OverlayTrigger,
  Row,
  Tooltip
} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faCheckCircle,
  faCircleXmark,
  faEdit
} from "@fortawesome/free-regular-svg-icons";
import {faTrash, faUndoAlt} from "@fortawesome/free-solid-svg-icons";
import CustomFieldDataTable from "./CustomFieldDataTable";
import {FileText} from "react-feather";

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

                <span className="fw-bolder text-lg">
                  {task.label}
                  {
                    task.fields && task.fields.length > 0 && task.status !== "COMPLETE" && (
                      <OverlayTrigger placement={"top"} overlay={(
                          <Tooltip>This task requires input to complete.</Tooltip>
                      )}>
                        <FileText size={18} className="ms-2 text-info" />
                      </OverlayTrigger>
                    )
                  }
                </span>

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

          {
            task.status === "COMPLETE" && task.fields.length > 0 && (
                <Row className={"d-flex justify-content-center mt-4"}>
                  <Col sm={12} md={10}>
                    <Row>
                      <Col xs={12}>
                        <span className="text-muted">Task Data</span>
                      </Col>
                      <Col xs={12}>
                        <CustomFieldDataTable fields={task.fields} data={task.data} />
                      </Col>
                    </Row>
                  </Col>
                </Row>
            )
          }

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