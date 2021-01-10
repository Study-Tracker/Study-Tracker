import React from 'react';
import {
  Button,
  Card,
  CardBody,
  CardHeader,
  Col,
  FormGroup,
  Input,
  Label,
  Row
} from 'reactstrap'
import {XCircle} from 'react-feather'
import dragula from "react-dragula";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";

class AssayTypeFieldInputList extends React.Component {

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
        <div id="field-input-container" ref={this.handleContainerLoaded}>
          {this.props.children}
        </div>
    )

  }

}

const AssayTypeFieldInputCard = ({field, index, handleFieldUpdate, handleRemoveField}) => {
  return (
      <Card className="mb-3 bg-light cursor-grab border">

        <CardHeader className="bg-light pt-0 pb-0">
          <div className="card-actions float-right">
            <a className="text-danger" title={"Remove field"}
               onClick={() => handleRemoveField(index)}>
              <XCircle className="align-middle mt-3" size={12}/>
            </a>
          </div>
        </CardHeader>

        <CardBody className="pb-3 pr-3 pl-3 pt-0">
          <Row>

            <Col md={6} lg={3}>
              <FormGroup>
                <Label>Name *</Label>
                <Input
                    type="text"
                    value={field.displayName}
                    onChange={(e) => {
                      let val = e.target.value;
                      handleFieldUpdate({
                        "displayName": val,
                        "fieldName": val.replace(/[\W]+/g, "_")
                      }, index);
                    }}
                />
              </FormGroup>
            </Col>

            <Col md={6} lg={3}>
              <FormGroup>
                <Label>Type</Label>
                <Input
                    type="select"
                    value={field.type}
                    onChange={(e) => {
                      handleFieldUpdate({"type": e.target.value}, index);
                    }}
                >
                  <option value="STRING">Text String</option>
                  <option value="TEXT">Text Block</option>
                  <option value="INTEGER">Integer</option>
                  <option value="FLOAT">Float</option>
                  <option value="BOOLEAN">Boolean</option>
                  <option value="DATE">Date</option>
                </Input>
              </FormGroup>
            </Col>

            <Col md={12} lg={4}>
              <FormGroup>
                <Label>Description</Label>
                <Input
                    type="textarea"
                    size="4"
                    value={field.description}
                    onChange={(e) => handleFieldUpdate(
                        {"description": e.target.value}, index)}
                />
              </FormGroup>
            </Col>

            <Col md={6} lg={1}>
              <Label check className="ml-4 mt-3">
                <Input
                    type="checkbox"
                    onChange={(e) => {
                      handleFieldUpdate({"required": e.target.checked}, index)
                    }}
                    checked={field.required}
                /> Required
              </Label>
            </Col>

          </Row>
        </CardBody>
      </Card>
  )
};

export class AssayTypeFieldInputs extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      fields: props.fields || []
    }

    this.containers = [];

    this.handleAddFieldClick = this.handleAddFieldClick.bind(this);
    this.handleRemoveFieldClick = this.handleRemoveFieldClick.bind(this);
    this.handleFieldUpdate = this.handleFieldUpdate.bind(this);
  }

  componentDidMount() {
    dragula(this.containers);
  }

  onContainerReady = container => {
    console.log(container);
    this.containers.push(container);
  };

  handleFieldUpdate(data, index) {
    let fields = this.state.fields;
    fields[index] = {
      ...fields[index],
      ...data
    };
    this.setState({
      fields: fields
    });
    this.props.handleUpdate(fields);
  }

  handleAddFieldClick() {
    const newField = [
      ...this.state.fields,
      {
        displayName: "",
        fieldName: "",
        type: "STRING",
        description: "",
        required: false
      }
    ];
    this.setState({
      fields: newField
    });
    this.props.handleUpdate(newField);
  }

  handleRemoveFieldClick(index) {
    let updated = this.state.fields;
    updated.splice(index, 1);
    this.setState({
      fields: updated
    });
    this.props.handleUpdate(updated);
  }

  render() {

    const cards = this.props.fields.map((field, index) => {
      return (
          <Row form key={'field-inputs-' + index} data-index={index}>
            <Col xs={12}>
              <AssayTypeFieldInputCard
                  field={field}
                  index={index}
                  handleRemoveField={this.handleRemoveFieldClick}
                  handleFieldUpdate={this.handleFieldUpdate}
              />
            </Col>
          </Row>
      )
    });

    return (
        <React.Fragment>

          <AssayTypeFieldInputList onContainerLoaded={this.onContainerReady}>
            {cards}
          </AssayTypeFieldInputList>

          <Row form>
            <Col md={12}>
              <Button
                  size="lg"
                  color="info"
                  onClick={this.handleAddFieldClick}>
                <FontAwesomeIcon icon={faPlusCircle}/> Add Field
              </Button>
            </Col>
          </Row>

        </React.Fragment>
    );

  }

}