import React, {useEffect, useState} from "react";
import dragula from "react-dragula";
import {Button, Col, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import AssayTypeFieldInputCard from "./AssayTypeFieldInputCard";
import AssayTypeFieldInputList from "./AssayTypeFieldInputList";
import PropTypes from "prop-types";

const AssayTypeFieldInputs = props => {

  const {handleUpdate} = props;
  const [fields, setFields] = useState(props.fields || []);
  const containers = [];

  // constructor(props) {
  //   super(props);
  //   this.state = {
  //     fields: props.fields || []
  //   }
  //
  //   this.containers = [];
  //
  //   this.handleAddFieldClick = this.handleAddFieldClick.bind(this);
  //   this.handleRemoveFieldClick = this.handleRemoveFieldClick.bind(this);
  //   this.handleFieldUpdate = this.handleFieldUpdate.bind(this);
  // }

  useEffect(() => {
    dragula(containers);
  }, []);

  const onContainerReady = container => {
    console.debug("Container", container);
    containers.push(container);
  };

  const handleFieldUpdate = (data, index) => {
    let f = fields;
    f[index] = {
      ...f[index],
      ...data
    };
    setFields(f)
    handleUpdate(fields);
  }

  const handleAddFieldClick = () => {
    const newField = [
      ...fields,
      {
        displayName: "",
        fieldName: "",
        type: "STRING",
        description: "",
        required: false
      }
    ];
    setFields(newField);
    handleUpdate(newField);
  }

  const handleRemoveFieldClick = (index) => {
    let updated = fields;
    updated.splice(index, 1);
    setFields(updated);
    handleUpdate(updated);
  }

  const cards = fields.map((field, index) => {
    return (
        <Row key={'field-inputs-' + index} data-index={index}>
          <Col xs={12}>
            <AssayTypeFieldInputCard
                field={field}
                index={index}
                handleRemoveField={handleRemoveFieldClick}
                handleFieldUpdate={handleFieldUpdate}
            />
          </Col>
        </Row>
    )
  });

  return (
      <React.Fragment>

        <AssayTypeFieldInputList onContainerLoaded={onContainerReady}>
          {cards}
        </AssayTypeFieldInputList>

        <Row>
          <Col md={12}>
            <Button
                variant="info"
                onClick={handleAddFieldClick}>
              <FontAwesomeIcon icon={faPlusCircle}/> Add Field
            </Button>
          </Col>
        </Row>

      </React.Fragment>
  );

}

AssayTypeFieldInputs.propTypes = {
  fields: PropTypes.array,
  handleUpdate: PropTypes.func.isRequired
}

export default AssayTypeFieldInputs;