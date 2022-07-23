import React from "react";
import swal from "sweetalert";
import {LoadingOverlay} from "../../common/loading";
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import Select from "react-select";
import StudyInputs from "./StudyInputs";
import {Breadcrumbs} from "../../common/common";
import {FormGroup} from "../../common/forms/common";
import {useNavigate} from "react-router-dom";
import {Form as FormikForm, Formik} from "formik";
import * as yup from "yup";
import PropTypes from "prop-types";
import axios from "axios";

const StudyCollectionForm = props => {

  const navigate = useNavigate();

  const collectionDefaults = {
    name: "",
    description: "",
    shared: false,
    studies: []
  };
  const collectionSchema = yup.object().shape({
    name: yup.string()
      .required("Name is required")
      .max(255, "Name must be less than 255 characters"),
    description: yup.string().required("Description is required"),
    shared: yup.boolean(),
    studies: yup.array(),
  });

  const handleFormSubmit = (values, {setSubmitting}) => {

    const isUpdate = !!values.id;
    const url = isUpdate
        ? "/api/studycollection/" + values.id
        : "/api/studycollection";

    axios({
      url: url,
      method: isUpdate ? "PUT" : "POST",
      data: values
    })
    .then(response => {
      setSubmitting(false);
      const json = response.data;
      console.debug("Collection submit response", json);
      navigate("/collection/" + json.id);
    })
    .catch(e => {
      setSubmitting(false);
      swal(
          "Something went wrong",
          "The request failed. Please check your inputs and try again. If this error persists, please contact Study Tracker support."
      );
      console.error(e);
    });

  }

  const handleCancel = () => {
    swal({
      title: "Are you sure you want to leave the page?",
      text: "Any unsaved work will be lost.",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        navigate(-1);
      }
    });
  }

  return (
      <Formik
          initialValues={props.collection || collectionDefaults}
          validationSchema={collectionSchema}
          onSubmit={handleFormSubmit}
          validateOnBlur={false}
          validateOnChange={false}
      >
        {({
          values,
          errors,
          touched,
          handleChange,
          handleBlur,
          handleSubmit,
          isSubmitting,
          setFieldValue,
        }) => (

            <Container fluid className="animated fadeIn max-width-1200">

              <LoadingOverlay
                  isVisible={isSubmitting}
                  message={"Saving collection..."}
              />

              <Row>
                <Col>
                  {
                    !!values.id
                        ? (
                            <Breadcrumbs crumbs={[
                              {label: "Home", url: "/"},
                              {
                                label: "Collection Details",
                                url: "/collection/" + values.id
                              },
                              {label: "Edit Collection"}
                            ]}/>
                        )
                        : (
                            <Breadcrumbs crumbs={[
                              {label: "Collections", url: "/collections"},
                              {label: "New Collection"}
                            ]}/>
                        )
                  }
                </Col>
              </Row>

              <Row className="justify-content-end align-items-center">
                <Col>
                  <h3>
                    {!!values.id ? "Edit Collection" : "New Collection"}
                  </h3>
                </Col>
              </Row>

              <Row>
                <Col xs={12}>
                  <Card>

                    <Card.Header>
                      <Card.Title tag="h5">Collection Overview</Card.Title>
                      <h6 className="card-subtitle text-muted">
                        Provide a unique name and a brief description for your
                        collection.
                        Collections are private and visible only to you, unless
                        the 'Public' option is set to true. Any user can add or
                        remove
                        studies from public collections.
                      </h6>
                    </Card.Header>

                    <Card.Body>
                      <FormikForm className="collection-form" autoComplete={"off"}>

                        {/*Overview*/}
                        <Row>

                          <Col md={7}>
                            <FormGroup>
                              <Form.Label>Name *</Form.Label>
                              <Form.Control
                                  type="text"
                                  name="name"
                                  className={(errors.name && touched.name) ? "is-invalid" : ""}
                                  isInvalid={!touched.name && errors.name}
                                  value={values.name}
                                  onChange={handleChange}
                              />
                              <Form.Control.Feedback type={"invalid"}>
                                {errors.name}
                              </Form.Control.Feedback>
                            </FormGroup>
                          </Col>

                          <Col md={5}>
                            <FormGroup>
                              <Form.Label>Is this collection public?</Form.Label>
                              <Select
                                  className="react-select-container"
                                  classNamePrefix="react-select"
                                  name={"shared"}
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
                                  value={
                                    values.shared ?
                                        {
                                          value: true,
                                          label: "Yes"
                                        } : {
                                          value: false,
                                          label: "No"
                                        }
                                  }
                                  onChange={(selected) => setFieldValue("shared", selected.value)}
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
                                  name={"description"}
                                  className={(errors.description && touched.description) ? "is-invalid" : ""}
                                  value={values.description}
                                  onChange={handleChange}
                                  rows={5}
                              />
                              <Form.Control.Feedback type={"invalid"}>
                                {errors.description}
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
                                studies={values.studies}
                                onChange={(studies) => setFieldValue("studies", studies)}
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

                              <Button
                                  size="lg"
                                  variant="primary"
                                  type={"submit"}
                              >
                                Submit
                              </Button>

                              &nbsp;&nbsp;

                              <Button
                                  size="lg"
                                  variant="secondary"
                                  onClick={handleCancel}
                              >
                                Cancel
                              </Button>

                            </FormGroup>
                          </Col>
                        </Row>

                      </FormikForm>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>

            </Container>

        )}
      </Formik>
  )

}

StudyCollectionForm.propTypes = {
  collection: PropTypes.object,
}

export default StudyCollectionForm;