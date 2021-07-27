import React from 'react';
import {
  Button,
  Card,
  CardBody,
  CardHeader,
  CardTitle,
  Col,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Row,
  Table
} from "reactstrap";
import {history} from "../../App";
import {CheckCircle, Edit, Info, PlusCircle, Trash} from "react-feather";
import ToolkitProvider from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import {AssayTaskList} from "../assayTasks";

class AssayTypeSettings extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      assayTypes: [],
      showModal: false
    };
    this.toggleModal = this.toggleModal.bind(this);
    this.toggleActive = this.toggleActive.bind(this);
  }

  toggleModal(selected) {
    if (!!selected) {
      this.setState({
        showModal: true,
        selectedAssayType: selected
      });
    } else {
      this.setState({
        showModal: false
      })
    }
  }

  toggleActive(selected) {
    fetch("/api/assaytype/" + selected.id, {
      method: "PATCH"
    })
    .then(response => {
      if (response.ok) {
        let assayTypes = this.state.assayTypes;
        const i = assayTypes.findIndex(d => d.id === selected.id);
        assayTypes[i].active = !assayTypes[i].active;
        this.setState({assayTypes})
      }
    })
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    });
  }

  componentDidMount() {
    fetch("/api/assaytype")
    .then(response => response.json())
    .then(json => {
      this.setState({
        assayTypes: json
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

  render() {
    return (
        <React.Fragment>
          <AssayTypeListCard
              assayTypes={this.state.assayTypes}
              showModal={this.state.showModal}
              selectedAssayType={this.state.selectedAssayType}
              toggleModal={this.toggleModal}
              toggleActive={this.toggleActive}
          />
        </React.Fragment>
    )
  }

}

export default AssayTypeSettings;

const AssayTypeListCard = ({
  assayTypes,
  showModal,
  selectedAssayType,
  toggleModal,
  toggleActive
}) => {

  const columns = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      headerStyle: {width: '20%'},
      formatter: (c, d, i, x) => <a href="javascript:void(0)"
                                    onClick={() => toggleModal(d)}>{d.name}</a>,
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
            ? <span className="badge badge-success">Active</span>
            : <span className="badge badge-warning">Inactive</span>
      }
    },
    {
      dataField: "controls",
      text: "Options",
      sort: false,
      headerStyle: {width: '20%'},
      formatter: (c, d, i, x) => {
        return (
            <React.Fragment>
              <a className="text-info" title={"Details"}
                 onClick={() => toggleModal(d)}>
                <Info className="align-middle mr-1" size={18}/>
              </a>
              {
                d.name === "Generic" ? "" : (
                    <a className="text-warning" title={"Edit assay type"}
                       onClick={() => history.push("/assaytypes/" + d.id + "/edit")}>
                      <Edit className="align-middle mr-1" size={18}/>
                    </a>
                )
              }
              {
                d.name === "Generic" ? "" : (
                  !!d.active
                      ? (
                          <a className="text-danger" title={"Set inactive"}
                             onClick={() => toggleActive(d)}>
                            <Trash className="align-middle mr-1" size={18}/>
                          </a>
                      )
                      : (
                          <a className="text-info" title={"Set active"}
                             onClick={() => toggleActive(d)}>
                            <CheckCircle className="align-middle mr-1" size={18}/>
                          </a>
                      )
                )
              }
            </React.Fragment>
        )
      }
    }
  ];

  return (
      <Card>
        <CardHeader>
          <CardTitle tag="h5" className="mb-0">
            Assay Types
            <span className="float-right">
                  <Button
                      color={"primary"}
                      onClick={() => history.push("/assaytypes/new")}
                  >
                    New Assay Type
                    &nbsp;
                    <PlusCircle className="feather align-middle ml-2 mb-1"/>
                  </Button>
                </span>
          </CardTitle>
        </CardHeader>
        <CardBody>
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
          <AssayTypeDetailsModal
              assayType={selectedAssayType}
              isOpen={showModal}
              toggle={toggleModal}
          />
        </CardBody>
      </Card>
  )
}

const AssayTypeDetailsModal = ({assayType, isOpen, toggle}) => {

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
          isOpen={isOpen}
          toggle={() => toggle()}
          size={"lg"}
      >
        <ModalHeader toggle={() => toggle()}>
          Assay Type: {assayType.name}
        </ModalHeader>
        <ModalBody>
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
        </ModalBody>
        <ModalFooter>
          <Button color="secondary" onClick={() => toggle()}>
            Close
          </Button>
        </ModalFooter>

      </Modal>
  )
}