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

import React from "react";
import PropTypes from "prop-types";
import {Card, Col, Row} from "react-bootstrap";
import TeamMembers from "../../common/detailsPage/TeamMembers";
import AssayQuickActionsWidget
  from "../../common/widgets/AssayQuickActionsWidget";
import SummaryTimelineCard from "../../common/detailsPage/SummaryTimelineCard";
import {RepairableStorageFolderButton} from "../../common/files";
import {RepairableNotebookFolderButton} from "../../common/eln";
import {AssayTaskList} from "../../common/assayTasks";
import axios from "axios";
import swal from "sweetalert";
import AssayFieldDataTable from "./AssayFieldDataTable";

const createMarkup = (content) => {
  return {__html: content};
};

const AssayOverviewTab = ({
  assay,
  user,
  features
}) => {

  const handleTaskUpdate = (task) => {

    let tasks = assay.tasks;
    let oldTasks = tasks;
    let updatedTask = null;
    for (let i = 0; i < tasks.length; i++) {
      if (tasks[i].order === task.order) {
        updatedTask = tasks[i];
        if (updatedTask.status === "TODO") {
          updatedTask.status = "COMPLETE";
        } else if (updatedTask.status
            === "COMPLETE") {
          updatedTask.status = "INCOMPLETE";
        } else if (updatedTask.status
            === "INCOMPLETE") {
          updatedTask.status = "TODO";
        }
        updatedTask.updatedAt = new Date().getTime();
      }
    }

    // Update before the request
    let updated = assay;
    updated.tasks = tasks;
    // setAssay(updated);

    axios.put("/api/internal/assay/" + assay.code + "/tasks", updatedTask)
    .then(response => {
      console.log("Task successfully updated.");
    })
    .catch(e => {
      console.error("Failed to update assay tasks.");
      console.error(e);
      updated.tasks = oldTasks;
      // setAssay(updated);
      swal(
          "Task update failed",
          "Please try updating the task again and contact Study Tracker support if the problem persists."
      );
    })

  }

  return (
      <Row>

        <Col md={8}>

          <Card className={"details-card"}>
              <Card.Body>

                <Row>

                  <Col xs={12}>
                    <div className={"card-title h5"}>Summary</div>
                  </Col>

                  <Col xs={12}>
                    <div dangerouslySetInnerHTML={createMarkup(assay.description)}/>
                  </Col>

                </Row>

                <Row>

                  <Col sm={4}>
                    <h6 className={"details-label"}>Assay Type</h6>
                    <p>{assay.assayType.name}</p>
                  </Col>

                  <Col sm={4}>
                    <h6 className={"details-label"}>Code</h6>
                    <p>{assay.code}</p>
                  </Col>

                </Row>

                <Row>

                  <Col sm={4}>
                    <h6 className="details-label">Created</h6>
                    <p>{new Date(assay.createdAt).toLocaleString()}</p>
                  </Col>

                  <Col sm={4}>
                    <h6 className="details-label">Start Date</h6>
                    <p>{new Date(assay.startDate).toLocaleString()}</p>
                  </Col>

                  {
                    !!assay.endDate
                        ? (
                            <Col sm={4}>
                              <h6 className="details-label">End Date</h6>
                              <p>{new Date(assay.endDate).toLocaleString()}</p>
                            </Col>
                        ) : ""
                  }

                  <Col md={12}>
                    <h6 className="details-label">Study Team</h6>
                    <TeamMembers owner={assay.owner} users={assay.users} />
                  </Col>

                </Row>

              </Card.Body>
          </Card>

          {/* Assay fields */}
          {
            Object.keys(assay.fields).length > 0
                ? (
                    <Card>
                      <Card.Body>
                        <Row>
                          <Col xs={12}>
                            <Card.Title>{assay.assayType.name} Metadata</Card.Title>
                            {/*<AssayFieldData assay={assay}/>*/}
                            <AssayFieldDataTable assay={assay}/>
                          </Col>
                        </Row>
                      </Card.Body>
                    </Card>
                ) : ""
          }

          {
            assay.tasks.length > 0
                ? (
                    <Card>
                      <Card.Body>
                        <Card.Title>
                          Tasks
                          {
                            !!user
                                ? (
                                    <small
                                        className="float-end text-muted font-italic">
                                      Click to toggle status
                                    </small>
                                )
                                : ''
                          }
                        </Card.Title>
                        <AssayTaskList
                            tasks={assay.tasks}
                            user={user}
                            handleUpdate={handleTaskUpdate}
                        />
                      </Card.Body>
                    </Card>
                ) : ''
          }

        </Col>

        <Col md={4}>

          <AssayQuickActionsWidget assay={assay} />

          <SummaryTimelineCard assay={assay} />

          {/*Workspaces Card*/}
          <Card>
            <Card.Body>
              <Row>
                <Col xs={12}>

                  <Card.Title>Workspaces</Card.Title>

                  <div className={"d-flex flex-column align-items-center"}>

                    <RepairableStorageFolderButton
                        folder={assay.primaryStorageFolder}
                        repairUrl={"/api/internal/assay/" + assay.id + "/storage/repair"}
                    />

                    {
                      features
                      && features.notebook
                      && features.notebook.isEnabled ? (
                          <RepairableNotebookFolderButton
                              folder={assay.notebookFolder}
                              repairUrl={"/api/internal/assay/" + assay.id + "/notebook/repair"}
                          />
                      ) : ""
                    }

                  </div>

                </Col>
              </Row>
            </Card.Body>
          </Card>

        </Col>

      </Row>
  );

};

AssayOverviewTab.propTypes = {
  assay: PropTypes.object.isRequired,
}

export default AssayOverviewTab;
