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

import {DragDropContext, Draggable, Droppable} from "react-beautiful-dnd";
import TaskControlsCard from "./TaskControlsCard";
import React from "react";
import PropTypes from "prop-types";
import {Button, Col, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";

const TaskControlsDraggableCardList = ({
    tasks,
    handleUpdate,
    errors,
    touched,
    showAssignments = false,
}) => {

  const handleUpdateTask = (data, index) => {
    console.debug("Updating task data", data, index);
    let updated = Array.from(tasks);
    updated[index] = {
      ...updated[index],
      ...data
    };
    console.debug("Updated tasks", updated);
    handleUpdate(updated);
  }

  const handleAddTask = () => {
    const newTasks = [
      ...tasks,
      {
        label: "",
        status: "TODO",
        order: tasks.length,
        fields: []
      }
    ];
    handleUpdate(newTasks);
  }

  const handleRemoveTask = (index) => {
    let updated = tasks;
    updated.splice(index, 1);
    handleUpdate(updated);
  }

  const onDragEnd = (result) => {
    console.debug("Drag end", result);
    if (!result.destination) {
      return;
    }
    if (result.destination.index === result.source.index) {
      return;
    }
    const updated = Array.from(tasks);
    const [removed] = updated.splice(result.source.index, 1);
    updated.splice(result.destination.index, 0, removed);
    for (let i = 0; i < updated.length; i++) {
      updated[i].order = i;
    }
    handleUpdate(updated);
  }

  return (
      <>

        <DragDropContext onDragEnd={onDragEnd}>
          <Droppable droppableId="draggable-task-list">
            {(provided) => (
              <div
                  ref={provided.innerRef}
                  {...provided.droppableProps}
              >
                {
                  tasks.sort((a, b) => a.order - b.order)
                  .map((task, index) => (
                      <Draggable
                          index={index}
                          draggableId={"draggable-task-" + index}
                          key={"draggable-task-" + index}
                      >
                        {(provided) => (
                            <div
                                ref={provided.innerRef}
                                {...provided.draggableProps}
                                {...provided.dragHandleProps}
                            >
                              <TaskControlsCard
                                  task={task}
                                  index={index}
                                  handleUpdate={(key, value) => handleUpdateTask({[key]: value}, index)}
                                  handleRemoveTask={() => handleRemoveTask(index)}
                                  errors={errors}
                                  touched={touched}
                                  showAssignments={showAssignments}
                              />
                            </div>
                        )}
                      </Draggable>
                  ))
                }
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        </DragDropContext>

        {
          !!errors.tasks
              ? (<div className={"invalid-feedback"}>{errors.tasks}</div>)
              : ''
        }

        <Row>
          <Col md={12}>
            <Button
                variant="info"
                onClick={handleAddTask}
                className={"ps-5 pe-5"}
            >
              <FontAwesomeIcon icon={faPlusCircle}/> Add Task
            </Button>
          </Col>
        </Row>

      </>
  )
}

TaskControlsDraggableCardList.propTypes = {
  tasks: PropTypes.array.isRequired,
  handleUpdate: PropTypes.func.isRequired,
  errors: PropTypes.object.isRequired,
  touched: PropTypes.object.isRequired,
  showAssignments: PropTypes.bool,
}

export default TaskControlsDraggableCardList;