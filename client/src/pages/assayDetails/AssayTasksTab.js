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

import React, {useEffect, useRef, useState} from "react";
import {Badge, Card} from 'react-bootstrap'
import {LoadingMessageCard} from "../../common/loading";
import {DismissableAlert} from "../../common/errors";
import axios from "axios";
import PropTypes from "prop-types";
import AssayTasksContentPlaceholder from "./AssayTasksContentPlaceholder";
import AssayTaskFormModal from "./AssayTaskFormModal";

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
        <Badge className="me-2" color={color}>{label}</Badge>
    )

}

const AssayTasksTab = ({assay, user}) => {

  const [tasks, setTasks] = useState(null);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [error, setError] = useState(null);
  const [loadCounter, setLoadCounter] = useState(0);
  const formikRef = useRef();

  useEffect(() => {
    axios.get("/api/internal/assay/" + assay.code + "/tasks")
    .then(response => {
      setTasks(response.data);
    })
    .catch(e => {
      setError(e);
    })
  }, [loadCounter]);

  const handleFormSubmit = (values, {setSubmitting, resetForm}) => {
    console.debug("Form values", values);
    const isUpdate = !!values.id;
    const url = isUpdate
        ? "/api/internal/assay/" + assay.id + "/tasks/" + values.id
        : "/api/internal/assay/" + assay.id + "/tasks";
    axios({
      url: url,
      method: isUpdate ? "put" : "post",
      data: values
    })
    .then(response => {
      const json = response.data;
      console.debug("Task", json);
      resetForm();
    })
    .catch(e => {
      console.error(e);
    })
    .finally(() => {
      setSubmitting(false);
      setModalIsOpen(false);
      setLoadCounter(loadCounter + 1);
    });
  }

  const handleTaskUpdate = (task) => {

    console.log("Click!");

    // let tasks = assay.tasks;
    // let oldTasks = tasks;
    // let updatedTask = null;
    // for (let i = 0; i < tasks.length; i++) {
    //   if (tasks[i].order === task.order) {
    //     updatedTask = tasks[i];
    //     if (updatedTask.status === "TODO") {
    //       updatedTask.status = "COMPLETE";
    //     } else if (updatedTask.status
    //         === "COMPLETE") {
    //       updatedTask.status = "INCOMPLETE";
    //     } else if (updatedTask.status
    //         === "INCOMPLETE") {
    //       updatedTask.status = "TODO";
    //     }
    //     updatedTask.updatedAt = new Date().getTime();
    //   }
    // }
    //
    // // Update before the request
    // let updated = assay;
    // updated.tasks = tasks;
    // // setAssay(updated);
    //
    // axios.put("/api/internal/assay/" + assay.code + "/tasks", updatedTask)
    // .then(response => {
    //   console.log("Task successfully updated.");
    // })
    // .catch(e => {
    //   console.error("Failed to update assay tasks.");
    //   console.error(e);
    //   updated.tasks = oldTasks;
    //   // setAssay(updated);
    //   swal(
    //       "Task update failed",
    //       "Please try updating the task again and contact Study Tracker support if the problem persists."
    //   );
    // })

  }

  const taskCards = !tasks ? [] : tasks.sort((a, b) => {
    return a.order - b.order;
  })
  .map((task, index) => {
    return (
        <Card>
          <Card.Body>
            <p className="text-muted">Task #{index+1}</p>
            <h3>
              <TaskStatusBadge status={task.status} />
              {task.label}
            </h3>
          </Card.Body>
        </Card>
    )
  });

  return (
      <>

        {
          !error && !tasks && <LoadingMessageCard />
        }

        {
          !!error && <DismissableAlert color={'warning'}
                                       message={'Failed to load assay tasks.'}/>
        }

        {taskCards}

        {
          !error && taskCards.length === 0 && (
              <AssayTasksContentPlaceholder
                  handleClick={() => setModalIsOpen(true)}
              />
            )
        }

        <AssayTaskFormModal
            modalIsOpen={modalIsOpen}
            setModalIsOpen={setModalIsOpen}
            handleFormSubmit={handleFormSubmit}
            formikRef={formikRef}

        />

      </>
  )

}

AssayTasksTab.propTypes = {
  assay: PropTypes.object.isRequired
}

export default AssayTasksTab;