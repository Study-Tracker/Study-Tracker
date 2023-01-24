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

import {Card} from "react-bootstrap";
import {XCircle} from "react-feather";
import React from "react";
import TaskControls from "./TaskControls";
import PropTypes from "prop-types";

const TaskControlsCard = ({
    task,
    index,
    handleUpdate,
    errors,
    touched,
    handleRemoveTask
}) => {

  return (
      <Card className="mb-3 bg-light cursor-grab border">

        <Card.Header className="bg-light pt-0 pb-0 mt-3 d-flex justify-content-between">
          <div className="text-muted text-lg">#{index+1}</div>
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
  index: PropTypes.number.isRequired,
  handleUpdate: PropTypes.func.isRequired,
  errors: PropTypes.object.isRequired,
  touched: PropTypes.object.isRequired,
  handleRemoveTask: PropTypes.func.isRequired
}

export default TaskControlsCard;