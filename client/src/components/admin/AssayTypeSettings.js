import React, {useEffect, useState} from "react";
import {
  Badge,
  Button,
  Card,
  Col,
  Dropdown,
  Modal,
  Row,
  Table
} from "react-bootstrap";
import {PlusCircle} from "react-feather";
import ToolkitProvider from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import {AssayTaskList} from "../assayTasks";
import {SettingsLoadingMessage} from "../loading";
import {SettingsErrorMessage} from "../errors";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
  faCheckCircle,
  faEdit,
  faInfoCircle,
  faTimesCircle
} from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import {useNavigate} from "react-router-dom";

const AssayTypeSettings = props => {
  
  const [state, setState] = useState({
    assayTypes: [],
    isModalOpen: false,
    isLoaded: false,
    isError: false
  });

  const showModal = (selected) => {
    if (!!selected) {
      setState(prevState => ({
        ...prevState,
        isModalOpen: true,
        selectedAssayType: selected
      }));
    } else {
      setState(prevState => ({
        ...prevState,
        isModalOpen: false
      }));
    }
  }

  const toggleActive = (selected) => {
    axios.patch("/api/assaytype/" + selected.id)
    .then(response => {
      if (response.status === 200) {
        let assayTypes = state.assayTypes;
        const i = assayTypes.findIndex(d => d.id === selected.id);
        assayTypes[i].active = !assayTypes[i].active;
        setState(prevState => ({
          ...prevState,
          assayTypes,
        }))
      }
    })
    .catch(error => {
      console.error(error);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });
  }

  useEffect(() => {
    axios.get("/api/assaytype")
    .then(response => {
      setState(prevState => ({
        ...prevState,
        assayTypes: response.data,
        isLoaded: true
      }));
    })
    .catch(error => {
      console.error(error);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });

  }, []);

  return (
      <React.Fragment>
        <AssayTypeListCard
            assayTypes={state.assayTypes}
            isModalOpen={state.isModalOpen}
            selectedAssayType={state.selectedAssayType}
            showModal={showModal}
            toggleActive={toggleActive}
            isLoaded={state.isLoaded}
            isError={state.isError}
        />
      </React.Fragment>
  )

}

export default AssayTypeSettings;

const AssayTypeListCard = ({
  assayTypes,
  isModalOpen,
  isLoaded,
  isError,
  selectedAssayType,
  showModal,
  toggleActive
}) => {

  const navigate = useNavigate();

  const columns = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      headerStyle: {width: '20%'},
      formatter: (c, d, i, x) => <Button variant="link"
                                         onClick={() => showModal(
                                             d)}>{d.name}</Button>,
      sortFunc: (a, b, order, dataField, rowA, rowB) => {
        if (rowA.name > rowB.name) {
          return order === "desc" ? -1 : 1;
        }
        if (rowB.name > rowA.name) {
          return order === "desc" ? 1 : -1;
        }
        return 0;
      }
    },
    {
      dataField: "description",
      text: "Description",
      sort: false,
      headerStyle: {width: '40%'},
      formatter: (c, d, i, x) => d.description
    },
    {
      dataField: "active",
      text: "Status",
      sort: false,
      headerStyle: {width: '20%'},
      formatter: (c, d, i, x) => {
        return !!d.active
            ? <Badge bg="success">Active</Badge>
            : <Badge bg="warning">Inactive</Badge>
      }
    },
    {
      dataField: "controls",
      text: "",
      sort: false,
      headerStyle: {width: '20%'},
      formatter: (c, d, i, x) => {
        return (
            <React.Fragment>

              <Dropdown>

                <Dropdown.Toggle variant={"outline-primary"}>
                  {/*<FontAwesomeIcon icon={faBars} />*/}
                  &nbsp;Options&nbsp;
                </Dropdown.Toggle>

                <Dropdown.Menu>

                  <Dropdown.Item onClick={() => showModal(d)}>
                    <FontAwesomeIcon icon={faInfoCircle}/>
                    &nbsp;&nbsp;
                    View Details
                  </Dropdown.Item>

                  {
                    d.name === "Generic" ? "" : (
                        <React.Fragment>
                          <Dropdown.Divider/>
                          <Dropdown.Item
                              onClick={() => navigate(
                                  "/assaytypes/" + d.id + "/edit")}
                          >
                            <FontAwesomeIcon icon={faEdit}/>
                            &nbsp;&nbsp;
                            Edit assay type
                          </Dropdown.Item>
                        </React.Fragment>
                    )
                  }

                  {
                    d.name === "Generic" ? "" : (
                        !!d.active ? (
                            <Dropdown.Item
                                className={"text-warning"}
                                onClick={() => toggleActive(d)}
                            >
                              <FontAwesomeIcon icon={faTimesCircle}/>
                              &nbsp;&nbsp;
                              Set Inactive
                            </Dropdown.Item>
                        ) : (
                            <Dropdown.Item
                                className={"text-warning"}
                                onClick={() => toggleActive(d)}
                            >
                              <FontAwesomeIcon icon={faCheckCircle}/>
                              &nbsp;&nbsp;
                              Set Active
                            </Dropdown.Item>
                        )
                    )
                  }

                </Dropdown.Menu>

              </Dropdown>

            </React.Fragment>
        )
      }
    }
  ];

  let content = <SettingsLoadingMessage/>
  if (!!isLoaded) {
    content = <AssayTypeTable columns={columns} assayTypes={assayTypes}/>
  } else if (!!isError) {
    content = <SettingsErrorMessage/>
  }

  return (
      <Card>

        <Card.Header>
          <Card.Title tag="h5" className="mb-0">
            Assay Types
            <span className="float-end">
              <Button
                  variant={"primary"}
                  href={"/assaytypes/new"}
              >
                New Assay Type
                &nbsp;
                <PlusCircle className="feather align-middle ms-2 mb-1"/>
              </Button>
            </span>
          </Card.Title>
        </Card.Header>

        <Card.Body>

          {content}

          <AssayTypeDetailsModal
              assayType={selectedAssayType}
              isOpen={isModalOpen}
              showModal={showModal}
          />

        </Card.Body>

      </Card>
  )
}

const AssayTypeTable = ({columns, assayTypes}) => {
  return (
      <ToolkitProvider
          keyField="id"
          data={assayTypes}
          columns={columns}
      >
        {props => (
            <div>
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
  )
}

const AssayTypeDetailsModal = ({assayType, isOpen, showModal}) => {

  if (!assayType) {
    return "";
  }

  const fields = assayType.fields.map(f => {
    return (
        <tr key={"assay-type-field-" + f.fieldName}>
          <td>{f.displayName}</td>
          <td><code>{f.fieldName}</code></td>
          <td>{f.type}</td>
          <td>{!!f.required ? "Yes" : "No"}</td>
          <td>{f.description}</td>
        </tr>
    )
  });

  const attributes = Object.keys(assayType.attributes).map(k => {
    return (
        <tr key={"assay-type-attribute-" + k}>
          <td>{k}</td>
          <td>{assayType.attributes[k]}</td>
        </tr>
    )
  })

  return (
      <Modal
          show={isOpen}
          onHide={() => showModal()}
          size={"lg"}
      >
        <Modal.Header closeButton>
          Assay Type: {assayType.name}
        </Modal.Header>
        <Modal.Body className="m-3">
          <Row>

            <Col xs={12}>
              <h4>Description</h4>
              <p>{assayType.description}</p>
            </Col>

            <Col xs={12}>
              <h4>Fields</h4>
              {
                fields.length > 0
                    ? (
                        <Table style={{fontSize: "0.8rem"}}>
                          <thead>
                          <tr>
                            <th>Display Name</th>
                            <th>Field Name</th>
                            <th>Data Type</th>
                            <th>Required</th>
                            <th>Description</th>
                          </tr>
                          </thead>
                          <tbody>
                          {fields}
                          </tbody>
                        </Table>
                    ) : (
                        <p className="text-muted">n/a</p>
                    )
              }
            </Col>

            <Col xs={12}>
              <h4>Default Tasks</h4>
              {
                !!assayType.tasks && assayType.tasks.length > 0
                    ? <AssayTaskList tasks={assayType.tasks}/>
                    : <p className="text-muted">n/a</p>
              }

            </Col>

            <Col xs={12}>
              <h4>Attributes</h4>
              {
                attributes.length > 0
                    ? (
                        <Table style={{fontSize: "0.8rem"}}>
                          <thead>
                          <tr>
                            <th>Name</th>
                            <th>Value</th>
                          </tr>
                          </thead>
                          <tbody>
                          {attributes}
                          </tbody>
                        </Table>
                    ) : <p className="text-muted">n/a</p>
              }
            </Col>

          </Row>
        </Modal.Body>
        <Modal.Footer>
          <Button color="secondary" onClick={() => showModal()}>
            Close
          </Button>
        </Modal.Footer>

      </Modal>
  )
}