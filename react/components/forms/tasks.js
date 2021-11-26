import React from 'react';
import {Button, Card, Col, Form, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import {XCircle} from "react-feather";
import dragula from 'react-dragula';
import {FormGroup} from "./common";

class TaskInputList extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};
  }

  handleContainerLoaded = container => {
    if (container) {
      this.props.onContainerLoaded(container);
    }
  }

  render() {

    return (
        <div id="task-input-container" ref={this.handleContainerLoaded}>
          {this.props.children}
        </div>
    )

  }

}

const TaskInputCard = ({task, index, handleTaskUpdate, handleRemoveTaskClick}) => {
  return (
      <Card className="mb-3 bg-light cursor-grab border">

        <Card.Header className="bg-light pt-0 pb-0">
          <div className="card-actions float-end">
            <a className="text-danger" title={"Remove field"}
               onClick={() => handleRemoveTaskClick(index)}>
              <XCircle className="align-middle mt-3" size={12}/>
            </a>
          </div>
        </Card.Header>

        <Card.Body className="pb-3 pr-3 pl-3 pt-0">
          <Row>

            <Col sm={12} md={6}>
              <FormGroup>
                <Form.Label>Label</Form.Label>
                <Form.Control
                    type="text"
                    value={task.label}
                    onChange={(e) => handleTaskUpdate({"label": e.target.value},
                        index)}
                />
              </FormGroup>
            </Col>

            <Col sm={12} md={6}>
              <FormGroup>
                <Form.Label>Status</Form.Label>
                <Form.Select
                    value={task.status}
                    onChange={(e) => {
                      handleTaskUpdate({"status": e.target.value}, index);
                    }}
                >
                  <option value="TODO">To Do</option>
                  <option value="COMPLETE">Complete</option>
                  <option value="INCOMPLETE">Incomplete</option>
                </Form.Select>
              </FormGroup>
            </Col>

          </Row>
        </Card.Body>
      </Card>
  )
};

export class TaskInputs extends React.Component {

  constructor(props) {
    super(props);
    this.state = {};

    this.containers = [];

    this.handleAddTaskClick = this.handleAddTaskClick.bind(this);
    this.handleRemoveTaskClick = this.handleRemoveTaskClick.bind(this);
    this.handleTaskUpdate = this.handleTaskUpdate.bind(this);
  }

  componentDidMount() {
    dragula(this.containers);
  }

  onContainerReady = container => {
    console.log(container);
    this.containers.push(container);
  };

  handleTaskUpdate(data, index) {
    let tasks = this.props.tasks;
    tasks[index] = {
      ...tasks[index],
      ...data
    };
    this.props.handleUpdate(tasks);
  }

  handleAddTaskClick() {
    const newTasks = [
      ...this.props.tasks,
      {label: "", status: "TODO"}
    ];
    this.props.handleUpdate(newTasks);
  }

  handleRemoveTaskClick(index) {
    let updated = this.props.tasks;
    updated.splice(index, 1);
    this.props.handleUpdate(updated);
  }

  render() {

    const cards = this.props.tasks
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
                  handleRemoveTaskClick={this.handleRemoveTaskClick}
                  handleTaskUpdate={this.handleTaskUpdate}
              />
            </Col>
          </Row>
      )
    });

    return (
        <React.Fragment>

          <TaskInputList onContainerLoaded={this.onContainerReady}>
            {cards}
          </TaskInputList>

          <Row>
            <Col md={12}>
              <Button
                  variant="info"
                  onClick={this.handleAddTaskClick}>
                <FontAwesomeIcon icon={faPlusCircle}/> Add Task
              </Button>
            </Col>
          </Row>

        </React.Fragment>
    );

  }

}