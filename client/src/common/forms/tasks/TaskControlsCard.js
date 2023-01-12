import {Card} from "react-bootstrap";
import {XCircle} from "react-feather";
import React from "react";
import TaskControls from "./TaskControls";
import PropTypes from "prop-types";

const TaskControlsCard = ({
    task,
    handleUpdate,
    errors,
    touched,
    handleRemoveTask
}) => {

  return (
      <Card className="mb-3 bg-light cursor-grab border">
        <Card.Header className="bg-light pt-0 pb-0 mt-3 d-flex justify-content-between">
          <div className="text-muted text-lg">#{task.order+1}</div>
          <div className="card-actions">
            <a className="text-danger" title={"Remove field"}
               onClick={handleRemoveTask}>
              <XCircle className="align-middle" size={12}/>
            </a>
          </div>
        </Card.Header>
        <Card.Body className="pb-3 pr-3 pl-3 pt-0">
          <TaskControls
              handleUpdate={handleUpdate}
              task={task}
              errors={errors}
              touched={touched}
              colWidth={6}
          />
        </Card.Body>
      </Card>
  )
}

TaskControlsCard.propTypes = {
  task: PropTypes.object.isRequired,
  handleUpdate: PropTypes.func.isRequired,
  errors: PropTypes.object.isRequired,
  touched: PropTypes.object.isRequired,
  handleRemoveTask: PropTypes.func.isRequired
}

export default TaskControlsCard;