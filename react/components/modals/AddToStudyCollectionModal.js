import React from 'react';
import {
  Button,
  Col,
  Form,
  FormGroup,
  Label,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Row
} from "reactstrap";
import Select from "react-select";
import swal from "sweetalert";

class AddToStudyCollectionModal extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      collections: []
    }
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentDidMount() {
    fetch("/api/studycollection?visibleToMe=true")
    .then(response => response.json())
    .then(collections => {
      this.setState({collections});
    }).catch(e => {
      console.error(e);
    })
  }

  handleSubmit() {
    if (!!this.state.selected) {
      fetch("/api/studycollection/" + this.state.selected + "/" + this.props.study.id, {
        method: "POST"
      }).then(response => {
        if (response.ok) {
          this.props.toggle();
        } else {
          console.warn("Failed to add study to collection.")
          swal(
              "Something went wrong",
              "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
          );
        }
      })
    }
  }

  render() {

    const options = this.state.collections
    .sort((a, b) => {
      if (a.name > b.name) {
        return 1;
      } else if (a.name < b.name) {
        return -1;
      } else {
        return 0;
      }
    })
    .map(c => {
      return {
        value: c.id,
        label: c.name
      }
    });

    return (
        <Modal isOpen={this.props.isOpen}
               toggle={() => this.props.toggle()}
               size={"md"}>
          <ModalHeader toggle={() => this.props.toggle()}>
            Add Study to Collection
          </ModalHeader>
          <ModalBody>
            <Form>
              <Row form>
                <Col xs={12}>
                  <FormGroup>
                    <Label>Collection</Label>
                    <Select
                      className="react-select-container"
                      classNamePrefix="react-select"
                      value={options.filter(o => o.value === this.state.selected)}
                      options={options}
                      onChange={o => {
                        console.log(o);
                        this.setState({selected: o.value})
                      }}
                    />
                  </FormGroup>
                </Col>
              </Row>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button
                color="secondary"
                onClick={() => this.props.toggle()}
            >
              Cancel
            </Button>
            <Button
                color="primary"
                onClick={() => this.handleSubmit(this.state.selected)}
                disabled={!this.state.selected}
            >
              Submit
            </Button>
          </ModalFooter>
        </Modal>
    )
  }

}

export default AddToStudyCollectionModal;