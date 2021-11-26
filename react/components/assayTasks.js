import React from 'react';
import {Card, Col, Row} from "react-bootstrap";
import {CheckSquare, Square, XSquare} from "react-feather";

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