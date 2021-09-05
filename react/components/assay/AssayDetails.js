import React from 'react';
import {
  Breadcrumb,
  BreadcrumbItem,
  Button,
  Card,
  CardBody,
  CardHeader,
  CardTitle,
  Col,
  Container,
  DropdownItem,
  DropdownMenu,
  DropdownToggle,
  Nav,
  NavItem,
  NavLink,
  Row,
  TabContent,
  TabPane,
  UncontrolledDropdown
} from "reactstrap";
import {SelectableStatusButton, StatusButton} from "../status";
import {Book, Menu} from "react-feather";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faTrash} from "@fortawesome/free-solid-svg-icons";
import {history} from "../../App";
import {StudyTeam} from "../studyMetadata";
import AssayTimelineTab from "./AssayTimelineTab";
import AssayFilesTab from "./AssayFilesTab";
import AssayNotebookTab from "./AssayNotebookTab";
import swal from "sweetalert";
import {AssayTaskList} from "../assayTasks";
import {RepairableStorageFolderButton} from "../files";

const createMarkup = (content) => {
  return {__html: content};
};

const AssayDetailHeader = ({study, assay, user}) => {
  return (
      <Row className="justify-content-between align-items-center">

        <Col>
          <h5 className="text-muted">{assay.assayType.name} Assay</h5>
          <h1>{assay.name}</h1>
          <h4>{assay.code}</h4>
        </Col>

        <Col className="col-auto">

          {
            !assay.active ? <Button size="lg" className="mr-1 mb-1"
                                    color="danger">Inactive Assay</Button> : ''
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
  const assayTypeFields = assay.assayType.fields;
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

export default class AssayDetails extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      activeTab: "1",
      assay: props.assay
    }
    this.handleAssayDelete = this.handleAssayDelete.bind(this);
    this.handleTaskUpdate = this.handleTaskUpdate.bind(this);
    this.toggle = this.toggle.bind(this);
  }

  toggle(tab) {
    if (this.state.activeTab !== tab) {
      this.setState({
        activeTab: tab
      });
    }
  }

  handleTaskUpdate(task) {

    let tasks = this.state.assay.tasks;
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
    let assay = this.state.assay;
    assay.tasks = tasks;
    this.setState({
      assay: assay
    });

    const url = "/api/assay/" + this.state.assay.code + "/tasks";
    fetch(url, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(updatedTask)
    })
    .then(response => {
      if (response.ok) {
        console.log("Task successfully updated.");
      } else {
        throw Error("Failed to update assay tasks.");
      }
    })
    .catch(e => {
      console.error("Failed to update assay tasks.");
      console.error(e);
      let assay = this.state.assay;
      assay.tasks = oldTasks;
      this.setState({
        assay: assay
      });
      swal(
          "Task update failed",
          "Please try updating the task again and contact Study Tracker support if the problem persists."
      );
    })

  }

  handleAssayDelete() {
    swal({
      title: "Are you sure you want to remove this assay?",
      text: "Removed assays will be hidden from view, but their records will not be deleted. Assays can be recovered in the admin dashboard.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        fetch("/api/assay/" + this.state.assay.code, {
          method: 'DELETE',
          headers: {
            "Content-Type": "application/json"
          }
        }).then(response => {
          history.push("/assays")
        })
        .catch(error => {
          console.error(error);
          this.setState({
            modalError: "Failed to remove study. Please try again."
          });
        })
      }
    });
  }

  render() {

    const assay = this.state.assay;
    const study = this.props.study;

    return (
        <Container fluid className="animated fadeIn">

          <Row>
            <Col>
              <Breadcrumb>

                <BreadcrumbItem>
                  <a href={"/"}>Home</a>
                </BreadcrumbItem>

                <BreadcrumbItem>
                  <a href={"/study/" + study.code}>
                    Study {study.code}
                  </a>
                </BreadcrumbItem>

                <BreadcrumbItem>
                  Assay Detail
                </BreadcrumbItem>

              </Breadcrumb>
            </Col>
          </Row>

          <AssayDetailHeader assay={assay} study={study}
                             user={this.props.user}/>

          <Row>

            <Col lg={5}>
              <Card className="details-card">

                <CardHeader>
                  <div className="card-actions float-right">
                    <UncontrolledDropdown>
                      <DropdownToggle tag="a">
                        <Menu/>
                      </DropdownToggle>
                      <DropdownMenu right>
                        {/*<DropdownItem onClick={() => console.log("Share!")}>*/}
                        {/*  <FontAwesomeIcon icon={faShare}/>*/}
                        {/*  &nbsp;*/}
                        {/*  Share*/}
                        {/*</DropdownItem>*/}
                        {/*{*/}
                        {/*  !!this.props.user ? <DropdownItem divider/> : ''*/}
                        {/*}*/}
                        {
                          !!this.props.user ? (
                              <DropdownItem onClick={() => history.push(
                                  "/study/" + study.code + "/assay/"
                                  + assay.code + "/edit")}>
                                <FontAwesomeIcon icon={faEdit}/>
                                &nbsp;
                                Edit
                              </DropdownItem>
                          ) : ''
                        }
                        {
                          !!this.props.user ? (
                              <DropdownItem onClick={this.handleAssayDelete}>
                                <FontAwesomeIcon icon={faTrash}/>
                                &nbsp;
                                Delete
                              </DropdownItem>
                          ) : ''
                        }
                      </DropdownMenu>
                    </UncontrolledDropdown>
                  </div>
                  <CardTitle tag="h5" className="mb-0 text-muted">
                    Assay Overview
                  </CardTitle>
                </CardHeader>

                <CardBody>
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
                </CardBody>

                {
                  Object.keys(assay.fields).length > 0
                      ? (
                          <CardBody>
                            <CardTitle>{assay.assayType.name} Fields</CardTitle>
                            <AssayFieldData assay={assay}/>
                          </CardBody>
                      )
                      : ''
                }

                {
                  assay.tasks.length > 0
                      ? (
                          <CardBody>
                            <CardTitle>
                              Tasks
                              {
                                !!this.props.user
                                    ? (
                                        <small
                                            className="float-right text-muted font-italic">
                                          Click to toggle status
                                        </small>
                                    )
                                    : ''
                              }
                            </CardTitle>
                            <AssayTaskList
                                tasks={assay.tasks}
                                user={this.props.user}
                                handleUpdate={this.handleTaskUpdate}
                            />
                          </CardBody>
                      ) : ''
                }

                {
                  Object.keys(assay.attributes).length > 0
                      ? (
                          <CardBody>
                            <CardTitle>Attributes</CardTitle>
                            <AssayAttributes attributes={assay.attributes}/>
                          </CardBody>
                      )
                      : ''
                }

                <CardBody>
                  <Row>
                    <Col xs={12}>
                      <CardTitle>Assay Team</CardTitle>
                      <StudyTeam users={assay.users} owner={assay.owner}/>
                    </Col>
                  </Row>
                </CardBody>

                <CardBody>
                  <Row>
                    <Col xs={12}>
                      <CardTitle>Workspaces</CardTitle>
                      <RepairableStorageFolderButton
                          folder={assay.storageFolder}
                          repairUrl={"/api/assay/" + assay.id + "/storage"}
                      />
                      {
                        !!assay.notebookFolder
                            ? (
                                <a href={assay.notebookFolder.url}
                                   target="_blank"
                                   className="btn btn-outline-info mb-2 mr-2">
                                  Assay ELN Folder
                                  <Book
                                      className="feather align-middle ml-2 mb-1"/>
                                </a>
                            ) : ''
                      }

                    </Col>
                  </Row>
                </CardBody>

              </Card>
            </Col>

            <Col lg={7}>

              {/*Tabs*/}
              <div className="tab">
                <Nav tabs>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "1" ? "active" : ''}
                        onClick={() => {
                          this.toggle("1");
                        }}
                    >
                      Timeline
                    </NavLink>
                  </NavItem>

                  <NavItem>
                    <NavLink
                        className={this.state.activeTab === "2" ? "active" : ''}
                        onClick={() => {
                          this.toggle("2");
                        }}
                    >
                      Files
                    </NavLink>
                  </NavItem>

                  {
                    !!assay.notebookFolder ? (
                        <NavItem>
                          <NavLink
                              className={this.state.activeTab === "3" ? "active" : ''}
                              onClick={() => {
                                this.toggle("3");
                              }}
                          >
                            Notebook
                          </NavLink>
                        </NavItem>
                    ) : ""
                  }

                  {/*TODO*/}
                  {/*<NavItem>*/}
                  {/*  <NavLink*/}
                  {/*      className={this.state.activeTab === "3" ? "active" : ''}*/}
                  {/*      onClick={() => {*/}
                  {/*        this.toggle("3");*/}
                  {/*      }}*/}
                  {/*  >*/}
                  {/*    Results*/}
                  {/*  </NavLink>*/}
                  {/*</NavItem>*/}

                </Nav>

                {/*Tab content*/}
                <TabContent activeTab={this.state.activeTab}>

                  <TabPane tabId="1">
                    <AssayTimelineTab assay={assay} user={this.props.user}/>
                  </TabPane>

                  <TabPane tabId="2">
                    <AssayFilesTab assay={assay} user={this.props.user}/>
                  </TabPane>

                  {
                    !!assay.notebookFolder ? (
                        <TabPane tabId="3">
                          <AssayNotebookTab assay={assay} user={this.props.user}/>
                        </TabPane>
                    ) : ""
                  }

                  {/*TODO*/}
                  {/*<TabPane tabId="3">*/}
                  {/*  <p className="text-center">*/}
                  {/*    Results will go here.*/}
                  {/*  </p>*/}
                  {/*</TabPane>*/}

                </TabContent>
              </div>
            </Col>

          </Row>

        </Container>
    );
  }

}