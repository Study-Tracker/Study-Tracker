/*
 * Copyright 2022 the original author or authors.
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

import React, {useEffect} from "react";
import dragula from "react-dragula";
import {Button, Col, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";
import TaskInputCard from "./TaskInputCard";
import TaskInputList from "./TaskInputList";

const TaskInputs = props => {

  const {tasks, handleUpdate, error} = props;

  const containers = [];

  useEffect(() => {
    dragula(containers);
  }, []);

  const onContainerReady = container => {
    console.debug("Task container", container);
    containers.push(container);
  };

  const handleTaskUpdate = (data, index) => {
    tasks[index] = {
      ...tasks[index],
      ...data
    };
    handleUpdate(tasks);
  }

  const handleAddTaskClick = () => {
    const newTasks = [
      ...tasks,
      {label: "", status: "TODO"}
    ];
    handleUpdate(newTasks);
  }

  const handleRemoveTaskClick = (index) => {
    let updated = tasks;
    updated.splice(index, 1);
    handleUpdate(updated);
  }

  const cards = tasks
  .sort((a, b) => {
    if (a.order > b.order) {
      return 1;
    } else if (b.order > a.order) {
      return -1;
    }
    return 0;
  })
  .map((task, index) => {
    return (
        <Row key={'task-inputs-' + index} data-index={index}>
          <Col md={12} lg={8} xl={6}>
            <TaskInputCard
                task={task}
                index={index}
                handleRemoveTaskClick={handleRemoveTaskClick}
                handleTaskUpdate={handleTaskUpdate}
            />
          </Col>
        </Row>
    )
  });

  return (
      <React.Fragment>

        <TaskInputList
            isInvalid={!!error}
            onContainerLoaded={onContainerReady}
        >
          {cards}
        </TaskInputList>

        {
          !!error
              ? (<div className={"invalid-feedback"}>{error}</div>)
              : ''
        }

        <Row>
          <Col md={12}>
            <Button
                variant="info"
                onClick={handleAddTaskClick}>
              <FontAwesomeIcon icon={faPlusCircle}/> Add Task
            </Button>
          </Col>
        </Row>

      </React.Fragment>
  );

}

TaskInputs.propTypes = {
  tasks: PropTypes.array.isRequired,
  handleUpdate: PropTypes.func.isRequired
}

export default TaskInputs;