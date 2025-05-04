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

import React from "react";
import {Card, Col, Row} from "react-bootstrap";
import {CheckSquare, Square, XSquare} from "react-feather";
import PropTypes from "prop-types";

export const AssayTaskCard = ({task, user, handleUpdate}) => {
  if (!!user && !!handleUpdate) {
    return (
        <Card className="mb-3 bg-light border">
          <Card.Body
              className={"p-3 cursor-pointer"}
              onClick={() => handleUpdate(task)}
          >
            <div>
              <TaskIcon status={task.status}/>
              {task.label}
            </div>
          </Card.Body>
        </Card>
    );
  } else {
    return (
        <Card className="mb-3 bg-light border">
          <Card.Body className={"p-3"}>
            <div>
              <TaskIcon status={task.status}/>
              {task.label}
            </div>
          </Card.Body>
        </Card>
    )
  }
}

AssayTaskCard.propTypes = {
  task: PropTypes.object.isRequired,
  user: PropTypes.object,
  handleUpdate: PropTypes.func
}

export const AssayTaskList = ({tasks, handleUpdate, user}) => {

  console.log(tasks);

  const cards = tasks.sort((a, b) => {
    if (a.order > b.order) {
      return 1;
    } else if (a.order < b.order) {
      return -1;
    } else {
      return 0;
    }
  })
  .map(task => {
    return (
        <Col xs={12} key={'assay-task-' + task.order}>
          <AssayTaskCard task={task} user={user} handleUpdate={handleUpdate}/>
        </Col>
    )
  });

  return (
      <Row>
        {cards}
      </Row>
  )

}

AssayTaskList.propTypes = {
  tasks: PropTypes.array.isRequired,
  handleUpdate: PropTypes.func,
  user: PropTypes.object
}

const TaskIcon = ({status}) => {
  if (status === "TODO") {
    return <Square className="me-3"/>;
  } else if (status === "COMPLETE") {
    return <CheckSquare
        className="me-3 text-success"/>;
  } else {
    return <XSquare className="me-3 text-danger"/>;
  }
}

TaskIcon.propTypes = {
  status: PropTypes.string.isRequired
}