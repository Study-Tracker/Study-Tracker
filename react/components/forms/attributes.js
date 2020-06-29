import React from 'react';
import {Button, Col, FormGroup, Input, Label, Row} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimesCircle} from "@fortawesome/free-regular-svg-icons";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";

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

class Attributes extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      attributes: mapToAttributeArray(props.attributes)
    }

    this.handleAddAttributeClick = this.handleAddAttributeClick.bind(this);
    this.handleRemoveAttributeClick = this.handleRemoveAttributeClick.bind(
        this);
    this.handleValueUpdate = this.handleValueUpdate.bind(this);
  }

  handleValueUpdate(key, value, index) {
    let attributes = this.state.attributes;
    attributes[index] = {key: key, value: value};
    this.setState({
      attributes: attributes
    });
    this.props.handleUpdate(arrayToAttributeMap(attributes));
  }

  handleAddAttributeClick() {
    const newAttributes = [
      ...this.state.attributes,
      {key: "New Attribute", value: ""}
    ];
    this.setState({
      attributes: newAttributes
    });
    this.props.handleUpdate(arrayToAttributeMap(newAttributes));
  }

  handleRemoveAttributeClick(index) {
    let updated = this.state.attributes;
    updated.splice(index, 1);
    this.setState({
      attributes: updated
    });
    this.props.handleUpdate(arrayToAttributeMap(updated));
  }

  render() {

    let inputs = this.state.attributes.map((a, i) => {
      return (
          <Row key={'attributes-inputs-' + i}>
            <Col xs={5}>
              <FormGroup>
                <Label></Label>
                <Input
                    type="text"
                    value={a.key}
                    onChange={(e) => this.handleValueUpdate(e.target.value,
                        a.value, i)}
                />
              </FormGroup>
            </Col>
            <Col xs={5}>
              <FormGroup>
                <Label></Label>
                <Input
                    type="text"
                    value={a.value}
                    onChange={(e) => this.handleValueUpdate(a.key,
                        e.target.value, i)}
                />
              </FormGroup>
            </Col>
            <Col xs={2}>
              <a
                  title={"Remove attribute"}
                  onClick={() => this.handleRemoveAttributeClick(i)}
              >
                <FontAwesomeIcon
                    icon={faTimesCircle}
                    className="align-middle mr-2 mt-3 text-danger"
                />
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
                <Label>Attribute Name</Label>
              </FormGroup>
            </Col>
            <Col xs={5}>
              <FormGroup>
                <Label>Attribute Value</Label>
              </FormGroup>
            </Col>
            <Col xs={2}></Col>
          </Row>

          {inputs}

          <Row>
            <Col md={12}>
              <Button
                  size="lg"
                  color="info"
                  onClick={this.handleAddAttributeClick}>
                <FontAwesomeIcon icon={faPlusCircle}/> Add Attribute
              </Button>
            </Col>
          </Row>

        </React.Fragment>
    );

  }

}

export default Attributes;