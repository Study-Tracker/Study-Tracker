import React from "react";
import swal from "sweetalert";
import {history} from "../../App";
import {LoadingOverlay} from "../loading";
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import Select from "react-select";
import {StudyInputs} from "./studies";
import {Breadcrumbs} from "../common";
import {FormGroup} from "./common";

export default class StudyCollectionForm extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      collection: props.collection || {
        shared: false,
        studies: []
      },
      validation: {
        nameIsValid: true,
        descriptionIsValid: true
      },
      showLoadingOverlay: false
    }
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleFormUpdate = this.handleFormUpdate.bind(this);
    this.validateForm = this.validateForm.bind(this);
  }

  handleFormUpdate(data) {
    const collection = {
      ...this.state.collection,
      ...data
    };
    console.log(collection);
    this.setState({
      collection
    });
  }

  validateForm(program) {
    let isError = false;
    let validation = this.state.validation;

    // Name
    if (!program.name) {
      isError = true;
      validation.nameIsValid = false;
    } else {
      validation.nameIsValid = true;
    }

    // Description
    if (!program.description) {
      isError = true;
      validation.descriptionIsValid = false;
    } else {
      validation.descriptionIsValid = true;
    }

    this.setState({
      validation: validation
    });

    return isError;

  }

  handleSubmit() {

    let isError = this.validateForm(this.state.collection);
    console.log(this.state);

    if (isError) {
      swal("Looks like you forgot something...",
          "Check that all of the required inputs have been filled and then try again.",
          "warning");
      console.warn("Validation failed.");
    } else {

      const isUpdate = !!this.state.collection.id;
      const url = isUpdate
          ? "/api/studycollection/" + this.state.collection.id
          : "/api/studycollection";
      this.setState({showLoadingOverlay: true});

      fetch(url, {
        method: isUpdate ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(this.state.collection)
      })
      .then(async response => {

        const json = await response.json();
        console.log(json);
        if (response.ok) {
          history.push("/collection/" + json.id);
        } else {
          this.setState({showLoadingOverlay: false})
          swal("Something went wrong",
              !!json.message
                  ? "Error: " + json.message :
                  "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
          );
          console.error("Request failed.");
        }

      }).catch(e => {
        this.setState({showLoadingOverlay: false})
        swal(
            "Something went wrong",
            "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
        );
        console.error(e);
      });

    }

  }

  handleCancel() {
    swal({
      title: "Are you sure you want to leave the page?",
      text: "Any unsaved work will be lost.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        history.push("/collections");
      }
    });
  }

  render() {

    return (
        <Container fluid className="animated fadeIn max-width-1200">

          <LoadingOverlay
              isVisible={this.state.showLoadingOverlay}
              message={"Creating your collection..."}
          />

          <Row>
            <Col>
              {
                !!this.state.collection.id
                    ? (
                        <Breadcrumbs crumbs={[
                          {label: "Home", url: "/"},
                          {label: "Collection Details", url: "/collection/" + this.state.collection.id},
                          {label: "Edit Collection"}
                        ]} />
                    )
                    : (
                        <Breadcrumbs crumbs={[
                          {label: "Collections", url: "/collections"},
                          {label: "New Collection"}
                        ]} />
                    )
              }
            </Col>
          </Row>

          <Row className="justify-content-end align-items-center">
            <Col>
              <h3>{!!this.state.collection.id ? "Edit Collection"
                  : "New Collection"}</h3>
            </Col>
          </Row>

          <Row>
            <Col xs={12}>
              <Card>

                <Card.Header>
                  <Card.Title tag="h5">Collection Overview</Card.Title>
                  <h6 className="card-subtitle text-muted">
                    Provide a unique name and a brief description for your collection.
                    Collections are private and visible only to you, unless
                    the 'Public' option is set to true. Any user can add or remove
                    studies from public collections.
                  </h6>
                </Card.Header>

                <Card.Body>
                  <Form className="collection-form">

                    {/*Overview*/}
                    <Row>

                      <Col md={7}>
                        <FormGroup>
                          <Form.Label>Name *</Form.Label>
                          <Form.Control
                              type="text"
                              isInvalid={!this.state.validation.nameIsValid}
                              defaultValue={this.state.collection.name || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"name": e.target.value})}
                          />
                          <Form.Control.Feedback type={"invalid"}>Name must not be empty.</Form.Control.Feedback>
                        </FormGroup>
                      </Col>

                      <Col md={5}>
                        <FormGroup>
                          <Form.Label>Is this collection public?</Form.Label>
                          <Select
                              className="react-select-container"
                              classNamePrefix="react-select"
                              options={[
                                {
                                  value: true,
                                  label: "Yes"
                                },
                                {
                                  value: false,
                                  label: "No"
                                }
                              ]}
                              defaultValue={
                                this.state.collection.shared ?
                                    {
                                      value: true,
                                      label: "Yes"
                                    } : {
                                      value: false,
                                      label: "No"
                                    }
                              }
                              onChange={(selected) => this.handleFormUpdate(
                                  {"shared": selected.value})}
                          />
                        </FormGroup>
                      </Col>

                    </Row>

                    <Row>

                      <Col md={7}>
                        <FormGroup>
                          <Form.Label>Description *</Form.Label>
                          <Form.Control
                              as="textarea"
                              defaultValue={this.state.collection.description
                              || ''}
                              onChange={(e) => this.handleFormUpdate(
                                  {"description": e.target.value})}
                              rows={5}
                          />
                          <Form.Control.Feedback type={"invalid"}>
                            Description must not be empty.
                          </Form.Control.Feedback>
                          <Form.Text>
                            Provide a brief description of the study collection.
                          </Form.Text>
                        </FormGroup>
                      </Col>

                    </Row>

                    <Row>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    <Row>
                      <Col md={12}>
                        <h5 className="card-title">Studies</h5>
                        <h6 className="card-subtitle text-muted">
                          Search-for and add studies to your collection. You can
                          add as many studies as you like.
                        </h6>
                        <br/>
                      </Col>

                      <Col md={12}>
                        <StudyInputs
                            studies={this.state.collection.studies || []}
                            onChange={this.handleFormUpdate}
                        />
                      </Col>

                    </Row>

                    <Row>
                      <Col>
                        <hr/>
                      </Col>
                    </Row>

                    {/*Buttons*/}
                    <Row>
                      <Col className="text-center">
                        <FormGroup>
                          <Button size="lg" variant="primary"
                                  onClick={this.handleSubmit}>Submit</Button>
                          &nbsp;&nbsp;
                          <Button size="lg" variant="secondary"
                                  onClick={this.handleCancel}>Cancel</Button>
                        </FormGroup>
                      </Col>
                    </Row>

                  </Form>
                </Card.Body>
              </Card>
            </Col>
          </Row>

        </Container>
    )

  }

}