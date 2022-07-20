import React from "react";
import {Button, Col, Form, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import {Trash} from "react-feather";
import {FormGroup} from "./common";

const mapToAttributeArray = (map) => {
  let array = [];
  for (let key of Object.keys(map)) {
    array.push({
      key: key,
      value: map[key]
    });
  }
  return array;
}

const arrayToAttributeMap = (array) => {
  let map = {};
  for (let obj of array) {
    map[obj.key] = obj.value;
  }
  return map;
}

const AttributeInputs = props => {

  const [attributes, setAttributes] = React.useState(mapToAttributeArray(props.attributes));

  const handleValueUpdate = (key, value, index) => {
    const updated = [...attributes];
    updated[index] = {key: key, value: value};
    setAttributes(updated);
    props.handleUpdate(arrayToAttributeMap(updated));
  };

  const handleAddAttributeClick = () => {
    const newAttributes = [
      ...attributes,
      {key: "New Attribute", value: ""}
    ];
    setAttributes(newAttributes);
    props.handleUpdate(arrayToAttributeMap(newAttributes));
  }

  const handleRemoveAttributeClick = (index) => {
    let updated = [...attributes];
    updated.splice(index, 1);
    setAttributes(updated);
    props.handleUpdate(arrayToAttributeMap(updated));
  }

  let inputs = attributes.map((a, i) => {
    return (
        <Row key={'attributes-inputs-' + i}>
          <Col xs={5}>
            <FormGroup>
              <Form.Label/>
              <Form.Control
                  type="text"
                  value={a.key}
                  onChange={(e) => handleValueUpdate(e.target.value,
                      a.value, i)}
              />
            </FormGroup>
          </Col>
          <Col xs={5}>
            <FormGroup>
              <Form.Label/>
              <Form.Control
                  type="text"
                  value={a.value}
                  onChange={(e) => handleValueUpdate(a.key,
                      e.target.value, i)}
              />
            </FormGroup>
          </Col>
          <Col xs={2}>
            <a
                className="text-danger"
                title={"Remove attribute"}
                onClick={() => handleRemoveAttributeClick(i)}
            >
              <Trash className="align-middle mt-3" size={18}/>
            </a>
          </Col>
        </Row>
    )
  });

  return (
      <React.Fragment>

        <Row>
          <Col xs={5}>
            <FormGroup>
              <Form.Label>Name</Form.Label>
            </FormGroup>
          </Col>
          <Col xs={5}>
            <FormGroup>
              <Form.Label>Value</Form.Label>
            </FormGroup>
          </Col>
          <Col xs={2}></Col>
        </Row>

        {inputs}

        <Row>
          <Col md={12} className="mt-2">
            <Button
                variant="info"
                onClick={handleAddAttributeClick}>
              <FontAwesomeIcon icon={faPlusCircle}/> Add Attribute
            </Button>
          </Col>
        </Row>

      </React.Fragment>
  );


}

export default AttributeInputs;