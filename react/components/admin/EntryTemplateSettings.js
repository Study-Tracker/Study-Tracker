import React from 'react';

import {
  Badge,
  Button,
  Card,
  Col,
  Dropdown,
  Form,
  Modal,
  Row
} from 'react-bootstrap';
import {CheckCircle, Edit, MinusCircle, PlusCircle, Star} from "react-feather";

import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import {Form as FormikForm, Formik} from "formik";
import Select from "react-select";
import swal from "sweetalert";
import {LoadingOverlay, SettingsLoadingMessage} from "../loading";
import {SettingsErrorMessage} from "../errors";

const defaultTemplate = {
  category: "STUDY",
  name: "",
  templateId: "",
  active: true,
  'default': false
};

export default class EntryTemplateSettings extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      registeredTemplates: [],
      availableTemplates: [],
      selectedTemplate: defaultTemplate,
      isError: false,
      isLoaded: false,
      isModalOpen: false
    };
    this.handleStatusChange = this.handleStatusChange.bind(this);
    this.updateTemplate = this.updateTemplate.bind(this);
    this.handleSelectedTemplateUpdate = this.handleSelectedTemplateUpdate.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentDidMount() {
    fetch('/api/notebookentrytemplate')
    .then(rs => rs.json())
    .then(registeredTemplates => {
      fetch("/api/eln/entrytemplate")
      .then(response => response.json())
      .then(availableTemplates => {
        this.setState({
          registeredTemplates,
          availableTemplates,
          isLoaded: true
        })
      })
    })
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    });
  }

  handleSelectedTemplateUpdate(template) {
    console.log(template);
    this.setState({
      selectedTemplate: !!template
          ? {
            ...template,
            active: true,
            'default': false
          }
          : defaultTemplate
    });
  }

  showModal(template) {
    if (!!template) {
      this.setState({
        selectedTemplate: {...template, referenceId: template.templateId},
        isModalOpen: true
      })
    } else {
      this.setState({
        isModalOpen: false,
        selectedTemplate: defaultTemplate
      })
    }
  }

  handleStatusChange = (template, statusToSet) => {
    fetch(`/api/notebookentrytemplate/${ template.id }/status?${ new URLSearchParams({ active: statusToSet }) }`, {
      method: 'POST',
    }).then(response => {
      if (response.ok) {
        swal("Entry Template Updated",
            "Refresh the page to view updated records.",
            "success");
      } else {
        throw new Error("Failed to update entry template status: " + response.statusText);
      }
    })
      .catch(error => {
        console.log(error);
        swal('Something went wrong', 'Template status change failed.');
      });
  };

  handleSetDefault = (template) => {
    fetch(`/api/notebookentrytemplate/${ template.id }/default`, {
      method: 'POST',
    }).then(response => {
      if (response.ok) {
        swal("Entry Template Updated",
            "Refresh the page to view updated records.",
            "success");
      } else {
        throw new Error("Failed to update entry template status: " + response.statusText);
      }
    })
    .catch(error => {
      console.log(error);
      swal('Something went wrong', 'Template status change failed.');
    });
  };

  updateTemplate(updatedTemplate) {
    fetch('/api/notebookentrytemplate/', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        ...updatedTemplate,
      }),
    })
    .catch(error => {
      console.log(error);
      swal('Something went wrong', 'Template update failed.');
    });
  }

  handleSubmit = (values, {setSubmitting}) => {
    console.log("Submitting new template...");
    console.log(values);
    let payload = {
      ...values,
      templateId: values.referenceId
    };
    console.log(payload);
    let url = "/api/notebookentrytemplate";
    let method = "POST";
    if (!!values.id) {
      url = url + "/" + values.id;
      method = "PUT";
    }
    fetch(url, {
      method: method,
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(payload)
    }).then(response => {
      if (response.status === 201) {
        this.showModal();
        swal("Entry Template Saved",
            "Refresh the page to view updated records.",
            "success")
        setSubmitting(false);
      } else {
        throw Error("Request failed: " + response.statusText)
      }
    }).catch(e => {
      console.error(e);
      swal("Request failed",
          "Failed to save the template record. Check your inputs and try again. "
          + "If the problem persist, contact Study Tracker support.",
          "warning");
      setSubmitting(false);
    })
  }

  handleValidation = values => {
    const errors = {};
    if (!values.name) errors.name = "You must select a template";
    if (!values.category) errors.category = "You must select a category";
    console.log(errors);
    return errors;
  }

  render() {

    const availableTemplateOptions = this.state.availableTemplates
    .sort((a, b) => {
      if (a.name < b.name) return -1;
      else if (a.name > b.name) return 1;
      else return 0;
    })
    .map(t => {
      return {
        label: t.name,
        value: t
      }
    });

    const studyTemplates = this.state.registeredTemplates.filter(t => t.category === "STUDY");
    const assayTemplates = this.state.registeredTemplates.filter(t => t.category === "ASSAY");

    let content = <SettingsLoadingMessage />;
    if (!!this.state.isLoaded) {
      content = <RegisteredEntryTemplateLists
          showModal={this.showModal}
          handleStatusChange={this.handleStatusChange}
          handleSetDefault={this.handleSetDefault}
          studyTemplates={studyTemplates}
          assayTemplates={assayTemplates}
      />;
    } else if (this.state.isError) {
      content = <SettingsErrorMessage />;
    }

    return (
        <Card>

          <Card.Header>
            <Card.Title>
              ELN Entry Templates
              <span className="float-end">
                <Button color={"primary"}
                        onClick={() => this.showModal(true)}>
                  Add Template
                  &nbsp;
                  <PlusCircle className="feather align-middle ms-2 mb-1"/>
                </Button>
              </span>
            </Card.Title>
          </Card.Header>

          <Card.Body>

            { content }

            {/* Modal Form */}
            <Modal
                show={this.state.isModalOpen}
                onHide={() => this.showModal()}
                size={"md"}
            >

              <Modal.Header closeButton>
                {
                  !!this.state.selectedTemplate
                  && !!this.state.selectedTemplate.id
                    ? "Edit Template" : "New Template"
                }
              </Modal.Header>

              <Modal.Body className={"m-3"}>
                <Formik
                    initialValues={this.state.selectedTemplate}
                    validate={this.handleValidation}
                    onSubmit={this.handleSubmit}
                    enableReinitialize
                >
                  {({
                    errors,
                    touched,
                    isSubmitting,
                    handleChange,
                    handleSubmit,
                    handleBlur,
                    values,
                    setFieldValue
                  }) => (
                      <FormikForm>

                        <LoadingOverlay
                            isVisible={isSubmitting}
                            message={"Saving entry template..."}
                        />

                        <Row>

                          {
                            !values.id ? (
                                <Col xs={12} className="mb-3">
                                  <Form.Group>
                                    <Form.Label>Available Templates</Form.Label>
                                    <Select
                                        className={"react-select-container " + !errors.name ? "is-invalid" : ''}
                                        classNamePrefix="react-select"
                                        options={availableTemplateOptions}
                                        onChange={selected => {
                                          this.handleSelectedTemplateUpdate(selected.value);
                                        }}
                                    />
                                    <div
                                        className="invalid-feedback"
                                        hidden={!errors.name}
                                    >
                                      {errors.name}
                                    </div>
                                  </Form.Group>
                                </Col>

                            ) : ''
                          }

                          <Col xs={12} className="mb-3">
                            <Form.Group>
                              <Form.Label>Name</Form.Label>
                              <Form.Control
                                  type="text"
                                  name="name"
                                  value={values.name}
                                  disabled={true}
                                  onChange={handleChange}
                              />
                            </Form.Group>
                          </Col>

                          <Col xs={12} className="mb-3">
                            <Form.Group>
                              <Form.Label>Template ID</Form.Label>
                              <Form.Control
                                  type="text"
                                  name="referenceId"
                                  value={values.referenceId}
                                  disabled={true}
                                  onChange={handleChange}
                              />
                            </Form.Group>
                          </Col>

                          <Col xs={12} className="mb-3">
                            <Form.Group>
                              <Form.Label>Category</Form.Label>
                              <Select
                                  className={"react-select-container " + !errors.category ? "is-invalid" : ''}
                                  classNamePrefix="react-select"
                                  name="category"
                                  defaultValue={values.category}
                                  options={[
                                    {label: 'Study', value: 'STUDY'},
                                    {label: 'Assay', value: 'ASSAY'}
                                  ]}
                                  onChange={(selected, e) => {
                                    setFieldValue("category", selected.value);
                                  }}
                              />
                              <div
                                  className="invalid-feedback"
                                  hidden={!errors.category}
                              >
                                {errors.category}
                              </div>
                            </Form.Group>
                          </Col>

                          <Col xs={12} className="mb-3">
                            <Form.Check
                                type="switch"
                                name="active"
                                onChange={handleChange}
                                defaultChecked={values.active}
                                label="Enable template. Disabled templates remain registered, but are not selectable."
                            />
                          </Col>

                          <Col xs={12} className="mb-3">
                            <Form.Check
                                type="switch"
                                name="default"
                                defaultChecked={values.default}
                                onChange={handleChange}
                                label="Make this template default for the selected category."
                            />
                          </Col>

                        </Row>

                        <Row>
                          <Col xs={12}>
                            <div className="text-center">
                              <Button
                                  className="mx-1"
                                  size="lg"
                                  variant="secondary"
                                  onClick={() => this.showModal()}
                              >
                                Cancel
                              </Button>
                              <Button
                                  className="mx-1"
                                  size="lg"
                                  variant="primary"
                                  type="submit"
                                  onClick={handleSubmit}
                              >
                                Submit
                              </Button>
                            </div>
                          </Col>
                        </Row>

                      </FormikForm>
                  )}
                </Formik>
              </Modal.Body>
            </Modal>

          </Card.Body>
        </Card>
    );

  }
}

const RegisteredEntryTemplateLists = ({
  studyTemplates,
  assayTemplates,
  showModal,
  handleStatusChange,
  handleSetDefault
}) => {

  const columns = [
    {
      dataField: 'name',
      text: 'Name',
      sort: true
    },
    {
      dataField: 'templateId',
      text: 'Template ID',
    },
    {
      dataField: 'active',
      text: 'Active',
      formatter: (c, d, i, x) => {
        if (!d.active) {
          return <Badge bg="danger">Inactive</Badge>
        } else {
          return <Badge bg="success">Active</Badge>
        }
      }
    },
    {
      dataField: 'default',
      text: 'Default',
      sort: true,
      formatter: (c, d, i, x) => {
        if (d.default) {
          return <Badge bg="info">Default</Badge>
        } else {
          return ''
        }
      }
    },
    {
      dataField: "controls",
      text: "Options",
      sort: false,
      headerStyle: {width: '10%'},
      formatter: (c, d, i, x) => {
        return (
            <React.Fragment>
              <Dropdown className="d-inline me-2">
                <Dropdown.Toggle variant="light" className="bg-white shadow-sm">
                  Options
                </Dropdown.Toggle>
                <Dropdown.Menu>

                  <Dropdown.Item
                      onClick={() => showModal(d)}>
                    <Edit className="feather align-middle mt-n1" /> Edit template
                  </Dropdown.Item>

                  {
                    !d.active ? (
                        <Dropdown.Item
                            onClick={() => handleStatusChange(d, true)}>
                          <CheckCircle className="feather align-middle mt-n1" /> Set active
                        </Dropdown.Item>
                    ) : (
                        <Dropdown.Item
                            onClick={() => handleStatusChange(d, false)}>
                          <MinusCircle className="feather align-middle mt-n1" /> Set inactive
                        </Dropdown.Item>
                    )
                  }

                  {
                    !d.default ? (
                        <Dropdown.Item
                            onClick={() => handleSetDefault(d)}>
                          <Star className="feather align-middle mt-n1" /> Set as default
                        </Dropdown.Item>
                    ) : ''
                  }

                </Dropdown.Menu>
              </Dropdown>

            </React.Fragment>
        )
      }
    }
  ];

  return (
      <React.Fragment>
        {/* Study Templates */}
        <Row>
          <Col xs={12}>

            <h3>Study Templates</h3>

            <ToolkitProvider
                keyField="id"
                data={studyTemplates}
                columns={columns}
                search
            >
              {props => (
                  <div>
                    <div className="float-end">
                      <Search.SearchBar
                          {...props.searchProps}
                      />
                    </div>
                    <BootstrapTable
                        bootstrap4
                        keyField="id"
                        bordered={false}
                        pagination={paginationFactory({
                          sizePerPage: 10,
                          sizePerPageList: [10, 20, 40, 80]
                        })}
                        defaultSorted={[{
                          dataField: "name",
                          order: "asc"
                        }]}
                        {...props.baseProps}
                    >
                    </BootstrapTable>
                  </div>
              )}
            </ToolkitProvider>

          </Col>
        </Row>

        {/* Study Templates */}
        <Row className="mt-4">
          <Col xs={12}>

            <h3>Assay Templates</h3>

            <ToolkitProvider
                keyField="id"
                data={assayTemplates}
                columns={columns}
                search
            >
              {props => (
                  <div>
                    <div className="float-end">
                      <Search.SearchBar
                          {...props.searchProps}
                      />
                    </div>
                    <BootstrapTable
                        bootstrap4
                        keyField="id"
                        bordered={false}
                        pagination={paginationFactory({
                          sizePerPage: 10,
                          sizePerPageList: [10, 20, 40, 80]
                        })}
                        defaultSorted={[{
                          dataField: "name",
                          order: "asc"
                        }]}
                        {...props.baseProps}
                    >
                    </BootstrapTable>
                  </div>
              )}
            </ToolkitProvider>

          </Col>
        </Row>
      </React.Fragment>
  )
}