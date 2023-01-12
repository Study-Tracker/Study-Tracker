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
    touched
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
        order: tasks.length
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
}

export default TaskControlsDraggableCardList;