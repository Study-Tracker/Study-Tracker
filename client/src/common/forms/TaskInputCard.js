import React from "react";
import {Card, Col, Form, Row} from "react-bootstrap";
import {XCircle} from "react-feather";
import {FormGroup} from "./common";
import PropTypes from "prop-types";

const TaskInputCard = ({
  task,
  index,
  handleTaskUpdate,
  handleRemoveTaskClick
}) => {
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

TaskInputCard.propTypes = {
  task: PropTypes.object.isRequired,
  index: PropTypes.number.isRequired,
  handleTaskUpdate: PropTypes.func.isRequired,
  handleRemoveTaskClick: PropTypes.func.isRequired
}

export default TaskInputCard;

