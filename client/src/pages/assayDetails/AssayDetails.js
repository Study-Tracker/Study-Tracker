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
import {
  Button,
  Card,
  Col,
  Container,
  Dropdown,
  Nav,
  Row,
  Tab
} from "react-bootstrap";
import {SelectableStatusButton, StatusButton} from "../../common/status";
import {Menu} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faTrash} from "@fortawesome/free-solid-svg-icons";
import {StudyTeam} from "../../common/studyMetadata";
import AssayTimelineTab from "./AssayTimelineTab";
import AssayFilesTab from "./AssayFilesTab";
import AssayNotebookTab from "./AssayNotebookTab";
import swal from "sweetalert";
import {AssayTaskList} from "../../common/assayTasks";
import {RepairableStorageFolderButton} from "../../common/files";
import {RepairableNotebookFolderButton} from "../../common/eln";
import {Breadcrumbs} from "../../common/common";
import PropTypes from "prop-types";
import {useNavigate} from "react-router-dom";
import axios from "axios";

const createMarkup = (content) => {
  return {__html: content};
};

const AssayDetailHeader = ({assay, user}) => {
  return (
      <Row className="justify-content-between align-items-center">

        <Col>
          <h5 className="text-muted">{assay.assayType.name} Assay</h5>
          <h3>{assay.name}</h3>
          <h4>{assay.code}</h4>
        </Col>

        <Col xs={"auto"}>

          {
            !assay.active
                ? (
                    <Button className="me-1 mb-1" variant="danger" disabled>
                      Inactive Assay
                    </Button>
                ) : ''
          }
          {
            !!user
                ? <SelectableStatusButton status={assay.status}
                                          assayId={assay.id}/>
                : <StatusButton status={assay.status}/>
          }

        </Col>
      </Row>
  );
};

const AssayFieldData = ({assay}) => {

  let fields = [];
  const assayTypeFields = assay.assayType.fields.sort((a, b) => {
    if (a.id > b.id) {
      return 1;
    } else if (a.id < b.id) {
      return -1;
    } else {
      return 0;
    }
  });
  const assayFields = assay.fields;
  for (let f of assayTypeFields) {
    if (assayFields.hasOwnProperty(f.fieldName)) {

      const value = assayFields[f.fieldName];

      if (["STRING", "INTEGER", "FLOAT"].indexOf(f.type) > -1) {
        fields.push(
            <div key={"assay-field-display-" + f.fieldName}>
              <h6 className="details-label">{f.displayName}</h6>
              <p>{value || 'n/a'}</p>
            </div>
        );
      } else if (f.type === "TEXT") {
        fields.push(
            <div key={"assay-field-display-" + f.fieldName}>
              <h6 className="details-label">{f.displayName}</h6>
              <div dangerouslySetInnerHTML={createMarkup(
                  !!value ? value : 'n/a')}/>
            </div>
        )
      } else if (f.type === "BOOLEAN") {
        fields.push(
            <div key={"assay-field-display-" + f.fieldName}>
              <h6 className="details-label">{f.displayName}</h6>
              <p>{!!value ? "True" : "False"}</p>
            </div>
        );
      } else if (f.type === "DATE") {
        fields.push(
            <div key={"assay-field-display-" + f.fieldName}>
              <h6 className="details-label">{f.displayName}</h6>
              <p>{!!value ? new Date(value).toLocaleString() : 'n/a'}</p>
            </div>
        );
      }

    }
  }

  return (
      <Row>
        <Col xs={12}>
          {fields}
        </Col>
      </Row>
  )

}

const AssayAttributes = ({attributes}) => {
  let items = [];
  for (let k of Object.keys(attributes)) {
    let v = attributes[k];
    items.push(
        <div key={"assay-attributes-" + k}>
          <h6 className="details-label">{k}</h6>
          <p>{v || "n/a"}</p>
        </div>
    )
  }
  return (
      <Row>
        <Col xs={12}>
          {items}
        </Col>
      </Row>
  )
};

const AssayDetails = props => {

  const {user, study, features} = props;
  const [assay, setAssay] = React.useState(props.assay);
  const [error, setError] = React.useState(null);
  const navigate = useNavigate();

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
    setAssay(updated);

    axios.put("/api/internal/assay/" + assay.code + "/tasks", updatedTask)
    .then(response => {
      console.log("Task successfully updated.");
    })
    .catch(e => {
      console.error("Failed to update assay tasks.");
      console.error(e);
      updated.tasks = oldTasks;
      setAssay(updated);
      swal(
          "Task update failed",
          "Please try updating the task again and contact Study Tracker support if the problem persists."
      );
    })

  }

  const handleAssayDelete = () => {
    swal({
      title: "Are you sure you want to remove this assay?",
      text: "Removed assays will be hidden from view, but their records will not be deleted. Assays can be recovered in the admin dashboard.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        axios.delete("/api/internal/assay/" + assay.code)
        .then(response => {
          navigate("/assays")
        })
        .catch(error => {
          console.error(error);
          setError("Failed to remove study. Please try again.");
        })
      }
    });
  }

  return (
      <Container fluid className="animated fadeIn">

        <Row>
          <Col>
            <Breadcrumbs crumbs={[
              {label: "Home", url: "/"},
              {label: "Study " + study.code, url: "/study/" + study.code},
              {label: "Assay Details"}
            ]}/>
          </Col>
        </Row>

        <AssayDetailHeader assay={assay} study={study} user={user}/>

        <Row>

          <Col lg={5}>
            <Card className="details-card">

              <Card.Header>
                <div className="card-actions float-end">
                  <Dropdown align="end">
                    <Dropdown.Toggle as="a" bsPrefix="-">
                      <Menu/>
                    </Dropdown.Toggle>
                    <Dropdown.Menu>

                      {
                        !!user ? (
                            <Dropdown.Item
                                href={"/study/" + study.code + "/assay/"
                                    + assay.code + "/edit"}>
                              <FontAwesomeIcon icon={faEdit}/>
                              &nbsp;
                              Edit
                            </Dropdown.Item>
                        ) : ''
                      }
                      {
                        !!user ? (
                            <Dropdown.Item onClick={handleAssayDelete}>
                              <FontAwesomeIcon icon={faTrash}/>
                              &nbsp;
                              Delete
                            </Dropdown.Item>
                        ) : ''
                      }
                    </Dropdown.Menu>
                  </Dropdown>
                </div>

                <Card.Title tag="h5" className="mb-0 text-muted">
                  Summary
                </Card.Title>

              </Card.Header>

              <Card.Body>
                <Row>
                  <Col xs="12">

                    <h6 className="details-label">Description</h6>
                    <div dangerouslySetInnerHTML={createMarkup(
                        assay.description)}/>

                    <h6 className="details-label">Created By</h6>
                    <p>{assay.createdBy.displayName}</p>

                    <h6 className="details-label">Last Updated</h6>
                    <p>{new Date(assay.updatedAt).toLocaleString()}</p>

                    <h6 className="details-label">Start Date</h6>
                    <p>{new Date(assay.startDate).toLocaleString()}</p>

                    <h6 className="details-label">End Date</h6>
                    <p>
                      {
                        !!assay.endDate
                            ? new Date(assay.endDate).toLocaleString()
                            : "n/a"
                      }
                    </p>

                  </Col>
                </Row>
              </Card.Body>

              {
                Object.keys(assay.fields).length > 0
                    ? (
                        <Card.Body>
                          <Card.Title>{assay.assayType.name} Fields</Card.Title>
                          <AssayFieldData assay={assay}/>
                        </Card.Body>
                    )
                    : ''
              }

              {
                assay.tasks.length > 0
                    ? (
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
                    ) : ''
              }

              {
                Object.keys(assay.attributes).length > 0
                    ? (
                        <Card.Body>
                          <Card.Title>Attributes</Card.Title>
                          <AssayAttributes attributes={assay.attributes}/>
                        </Card.Body>
                    )
                    : ''
              }

              <Card.Body>
                <Row>
                  <Col xs={12}>
                    <Card.Title>Assay Team</Card.Title>
                    <StudyTeam users={assay.users} owner={assay.owner}/>
                  </Col>
                </Row>
              </Card.Body>

              <Card.Body>
                <Row>
                  <Col xs={12}>

                    <Card.Title>Workspaces</Card.Title>

                    <RepairableStorageFolderButton
                        folder={assay.storageFolder}
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

                  </Col>
                </Row>
              </Card.Body>

            </Card>
          </Col>

          <Col lg={7}>

            {/*Tabs*/}
            <div className="tab">
              <Tab.Container defaultActiveKey="timeline">
                <Nav variant="tabs">

                  <Nav.Item>
                    <Nav.Link eventKey={"timeline"}>
                      Timeline
                    </Nav.Link>
                  </Nav.Item>

                  <Nav.Item>
                    <Nav.Link eventKey={"files"}>
                      Files
                    </Nav.Link>
                  </Nav.Item>

                  {
                    features
                    && features.notebook
                    && features.notebook.isEnabled
                    && assay.notebookFolder ? (
                        <Nav.Item>
                          <Nav.Link eventKey={"notebook"}>
                            Notebook
                          </Nav.Link>
                        </Nav.Item>
                    ) : ""
                  }

                </Nav>

                {/*Tab content*/}
                <Tab.Content>

                  <Tab.Pane eventKey={"timeline"}>
                    <AssayTimelineTab assay={assay} user={user}/>
                  </Tab.Pane>

                  <Tab.Pane eventKey={"files"}>
                    <AssayFilesTab assay={assay} user={user}/>
                  </Tab.Pane>

                  {
                    features
                    && features.notebook
                    && features.notebook.isEnabled
                    && assay.notebookFolder ? (
                        <Tab.Pane eventKey={"notebook"}>
                          <AssayNotebookTab assay={assay} user={user}/>
                        </Tab.Pane>
                    ) : ""
                  }

                </Tab.Content>
              </Tab.Container>
            </div>
          </Col>

        </Row>

      </Container>
  );

}

AssayDetails.propTypes = {
  user: PropTypes.object,
  study: PropTypes.object.isRequired,
  assay: PropTypes.object.isRequired,
  features: PropTypes.object,

}

export default AssayDetails;