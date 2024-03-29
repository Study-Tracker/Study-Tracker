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

import React, {useContext, useEffect, useRef, useState} from "react";
import {LoadingMessageCard} from "../../common/loading";
import {DismissableAlert} from "../../common/errors";
import axios from "axios";
import PropTypes from "prop-types";
import AssayTasksContentPlaceholder from "./AssayTasksContentPlaceholder";
import AssayTaskFormModal from "./AssayTaskFormModal";
import TaskCard from "./TaskCard";
import NotyfContext from "../../context/NotyfContext";
import {Button, Col, Row} from "react-bootstrap";
import AssayTaskCompleteModal from "./AssayTaskCompleteModal";

const AssayTasksTab = ({assay, user}) => {

  const [tasks, setTasks] = useState(null);
  const [selectedTask, setSelectedTask] = useState(null);
  const [taskToComplete, setTaskToComplete] = useState(null);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [completeModalIsOpen, setCompleteModalIsOpen] = useState(false);
  const [error, setError] = useState(null);
  const [loadCounter, setLoadCounter] = useState(0);
  const formikRef = useRef();
  const completeFormikRef = useRef();
  const notyf = useContext(NotyfContext);

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
      notyf.open({
        type: "success",
        message: isUpdate ? "Task successfully updated." : "Task created"
      });
      setSubmitting(false);
      setModalIsOpen(false);
      setLoadCounter(loadCounter + 1);
    });
  }

  const handleCompleteFormSubmit = async (values, {setSubmitting, resetForm}) => {
    console.debug("Form values", values);
    setSubmitting(true);

    // Upload any files
    let uploadErrors = false;
    for (let i in values.fields) {
      const field = values.fields[i];
      if (field.type === "FILE") {
        const file = values.data[field.fieldName];
        if (file) {
          console.debug("Uploading file: ", file);
          const formData = new FormData();
          formData.append("file", file);
          const localPath = await axios.post(
              "/api/internal/data-files/temp-upload", formData)
          .then(response => response.data.filePath)
          .catch(e => {
            console.error(e);
            uploadErrors = true;
          });
          if (uploadErrors) break;
          values.data[field.fieldName] = localPath;
        }
      }
    }
    if (uploadErrors) {
      setSubmitting(false);
      notyf.open({
        type: "error",
        message: "Error uploading task files."
      });
      return;
    }

    axios({
      url: "/api/internal/assay/" + assay.id + "/tasks/" + values.id,
      method: "patch",
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
      notyf.open({
        type: "success",
        message: "Task successfully updated."
      });
      setSubmitting(false);
      setCompleteModalIsOpen(false);
      setLoadCounter(loadCounter + 1);
    });
  }

  const updateTask = (data) => {
    axios({
      url: "/api/internal/assay/" + assay.id + "/tasks/" + data.id,
      method: "patch",
      data: data
    })
    .then(response => {
      console.debug("Updated task", response.data);
      notyf.open({
        type: "success",
        message: "Task successfully updated."
      });
      setLoadCounter(loadCounter + 1);
    })
    .catch(e => {
      console.error(e);
      notyf.open({
        type: "error",
        message: "Failed to update task."
      });
    })
  }

  const handleTaskComplete = (task) => {
    const values = {...task, status: "COMPLETE"};
    if (task.fields && task.fields.length > 0) {
      setTaskToComplete(values);
      setCompleteModalIsOpen(true);
    } else {
      updateTask(values);
    }
  }

  const handleTaskIncomplete = (task) => {
    const values = {...task, status: "INCOMPLETE"};
    updateTask(values);
  }

  const handleTaskReset = (task) => {
    const values = {...task, status: "TODO", data: {}};
    updateTask(values);
  }

  const handleTaskEdit = (task) => {
    setSelectedTask(task);
    setModalIsOpen(true);
  }

  const handleNewTaskClick = () => {
    setSelectedTask(null);
    setModalIsOpen(true);
  }

  const handleTaskDelete = (task) => {
    axios({
      url: "/api/internal/assay/" + assay.id + "/tasks/" + task.id,
      method: "delete"
    })
    .then(response => {
      console.debug("Updated task", response.data);
      notyf.open({
        type: "success",
        message: "Task successfully removed."
      });
      setLoadCounter(loadCounter + 1);
    })
    .catch(e => {
      console.error(e);
      notyf.open({
        type: "error",
        message: "Failed to remove task."
      });
    })
  }

  return (
      <>

        {
          !error && !tasks && <LoadingMessageCard />
        }

        {
          !!error && (
              <DismissableAlert
                  color={'warning'}
                  message={'Failed to load assay tasks.'}
              />
            )
        }

        {
          tasks && (
              tasks.sort((a, b) => {
                return a.order - b.order;
              })
              .map((task, index) => {
                return (
                    <TaskCard
                        key={index + "-task-card"}
                        task={task}
                        index={index}
                        handleTaskComplete={handleTaskComplete}
                        handleTaskIncomplete={handleTaskIncomplete}
                        handleTaskEdit={handleTaskEdit}
                        handleTaskDelete={handleTaskDelete}
                        handleTaskReset={handleTaskReset}
                    />
                );
              })
            )
        }

        {
          !error && (!tasks || tasks.length === 0) && (
              <AssayTasksContentPlaceholder
                  handleClick={() => setModalIsOpen(true)}
              />
            )
        }

        {
          tasks && tasks.length > 0 && (
                <Row>
                  <Col className={"d-flex justify-content-center"}>
                    <Button
                        className={"ps-5 pe-5"}
                        variant={"primary"}
                        onClick={handleNewTaskClick}
                    >
                      Add Task
                    </Button>
                  </Col>
                </Row>
          )
        }

        <AssayTaskFormModal
            task={selectedTask}
            modalIsOpen={modalIsOpen}
            setModalIsOpen={setModalIsOpen}
            handleFormSubmit={handleFormSubmit}
            formikRef={formikRef}

        />

        <AssayTaskCompleteModal
            task={taskToComplete}
            modalIsOpen={completeModalIsOpen}
            setModalIsOpen={setCompleteModalIsOpen}
            handleFormSubmit={handleCompleteFormSubmit}
            formikRef={completeFormikRef}
        />

      </>
  )

}

AssayTasksTab.propTypes = {
  assay: PropTypes.object.isRequired
}

export default AssayTasksTab;