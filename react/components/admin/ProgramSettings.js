import React from 'react';
import {
  Badge,
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
} from 'reactstrap';
import {Clipboard, Edit, FolderPlus, Info} from 'react-feather';
import {history} from "../../App";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import {RepairableStorageFolderLink} from "../files";
import {RepairableNotebookFolderLink} from "../eln";

const createMarkup = (content) => {
  return {__html: content};
};

class ProgramSettings extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      programs: [],
      isLoaded: false,
      isError: false,
      showDetails: false,
      selectedProgram: null,
      showModal: false
    };
    this.toggleModal = this.toggleModal.bind(this);
  }

  toggleModal(selected) {
    if (!!selected) {
      this.setState({
        showModal: true,
        selectedProgram: selected
      });
    } else {
      this.setState({
        showModal: false
      })
    }
  }

  componentDidMount() {
    fetch("/api/program?details=true")
    .then(response => response.json())
    .then(async programs => {
      this.setState({
        programs: programs,
        isLoaded: true
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

    const columns = [
      {
        dataField: "name",
        text: "Name",
        sort: true,
        // headerStyle: {width: '40%'},
        formatter: (c, d, i, x) => <a href={"javascript:void(0)"} onClick={() => this.toggleModal(d)}>{d.name}</a>,
        sortFunc: (a, b, order, dataField, rowA, rowB) => {
          if (rowA.name > rowB.name) {
            return order === "desc" ? -1 : 1;
          }
          if (rowB.name > rowA.name) {
            return order === "desc" ? 1 : -1;
          }
          return 0;
        },
      },
      {
        dataField: "code",
        text: "Code",
        sort: true,
        // headerStyle: {width: '20%'},
        formatter: (cell, d, index, x) => d.code,
      },
      {
        dataField: "createdAt",
        text: "Created",
        sort: true,
        // headerStyle: {width: '40%'},
        formatter: (c, d, i, x) => new Date(d.createdAt).toLocaleDateString()
      },
      {
        dataField: "status",
        text: "Status",
        sort: true,
        // headerStyle: {width: '10%'},
        formatter: (c, d, i, x) => {
          if (d.active) {
            return (
                <div className="badge badge-success">
                  Active
                </div>
            )
          } else {
            return (
                <div className="badge badge-danger">
                  Inactive
                </div>
            )
          }
        }
      },
      {
        dataField: "eln",
        text: "ELN",
        sort: false,
        // headerStyle: {width: '40%'},
        formatter: (c, d, i, x) => {
          if (!!d.notebookFolder) {
            if (!!d.notebookFolder.url && d.notebookFolder.url !== "ERROR") {
              return <a href={d.notebookFolder.url} target="_blank">ELN Folder</a>
            } else {
              return <span className="badge badge-warning">ERROR</span>
            }
          } else {
            return "n/a"
          }
        }
      },
      {
        dataField: "storage",
        text: "File Storage",
        sort: false,
        // headerStyle: {width: '40%'},
        formatter: (c, d, i, x) => {
          if (!!d.storageFolder) {
            if (!!d.storageFolder.url) {
              return <a href={d.storageFolder.url} target="_blank">Files Folder</a>
            } else {
              return <span className="badge badge-warning">ERROR</span>
            }
          } else {
            return "n/a"
          }
        }
      },
      {
        dataField: "controls",
        text: "Options",
        sort: false,
        // headerStyle: {width: '10%'},
        formatter: (c, d, i, x) => {
          return (
              <React.Fragment>

                <a className="text-info" title={"View details"}
                   onClick={() => this.toggleModal(d)}>
                  <Info className="align-middle mr-1" size={18}/>
                </a>

                <a className="text-warning" title={"Edit program"}
                   onClick={() => history.push("/program/" + d.id + "/edit")}>
                  <Edit className="align-middle mr-1" size={18}/>
                </a>

                {/*<a className="text-danger" title={"Disable program"}*/}
                {/*   onClick={() => console.log("click")}>*/}
                {/*  <Trash className="align-middle mr-1" size={18}/>*/}
                {/*</a>*/}

              </React.Fragment>
          )
        }
      }
    ];

    return (
        <React.Fragment>

          <Card>
            <CardHeader>
              <CardTitle tag="h5" className="mb-0">
                Registered Programs
                <span className="float-right">
                  <Button
                      color={"primary"}
                      onClick={() => history.push("/programs/new")}
                  >
                    New Program
                    &nbsp;
                    <FolderPlus className="feather align-middle ml-2 mb-1"/>
                  </Button>
                </span>
              </CardTitle>
            </CardHeader>
            <CardBody>
              <ToolkitProvider
                  keyField="id"
                  data={this.state.programs}
                  columns={columns}
                  search
                  exportCSV
              >
                {props => (
                    <div>
                      <div className="float-right">
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
              <ProgramDetailsModal
                  toggle={this.toggleModal}
                  isOpen={this.state.showModal}
                  program={this.state.selectedProgram}
              />
            </CardBody>
          </Card>

        </React.Fragment>
    );
  }

}

const ProgramDetailsModal = ({program, isOpen, toggle}) => {

  if (!program) {
    return "";
  }

  const attributes = Object.keys(program.attributes).map(k => {
    return (
        <tr key={"assay-type-attribute-" + k}>
          <td>{k}</td>
          <td>{program.attributes[k]}</td>
        </tr>
    )
  });

  return (
      <Modal
          isOpen={isOpen}
          toggle={() => toggle()}
          size={"lg"}
      >
        <ModalHeader toggle={() => toggle()}>
          Program: <strong>{program.name}</strong> (<code>{program.code}</code>)
        </ModalHeader>
        <ModalBody>
          <Row>

            <Col md={6}>
              <h4>Name</h4>
              <p>{program.name}</p>
            </Col>

            <Col md={6}>
              <h4>Code</h4>
              <p>{program.code}</p>
            </Col>

            <Col md={12}>
              <h4>Description</h4>
              <div dangerouslySetInnerHTML={createMarkup(
                  program.description)}/>
            </Col>

            <Col md={6}>
              <h4>Created</h4>
              <p>{new Date(program.createdAt).toLocaleString()} by {program.createdBy.displayName}</p>
            </Col>

            <Col md={6}>
              <h4>Last Updated</h4>
              <p>{new Date(program.createdAt).toLocaleString()} by {program.lastModifiedBy.displayName}</p>
            </Col>

            <Col md={6}>
              <h4>Active</h4>
              <p>
                <TrueFalseLabel bool={program.active}/>
              </p>
            </Col>

            <Col xs={12}>
              <hr/>
            </Col>

            <Col xs={6}>
              <h4>File Storage</h4>
              <p>
                <RepairableStorageFolderLink
                    folder={program.storageFolder}
                    repairUrl={"/api/program/" + program.id + "/storage"}
                />
              </p>
            </Col>

            <Col xs={6}>
              <h4>ELN Folder</h4>
              <p>
                <RepairableNotebookFolderLink
                    folder={program.notebookFolder}
                    repairUrl={"/api/program/" + program.id + "/notebook"}
                />
              </p>
            </Col>

            <Col xs={12}>
              <hr/>
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
          <Button color="info"
                  onClick={() => history.push("/program/" + program.id)}>
            <Clipboard size={14} className="mb-1"/>
            &nbsp;
            View Program
          </Button>
          <Button color="warning"
                  onClick={() => history.push("/program/" + program.id + "/edit")}>
            <Edit size={14} className="mb-1"/>
            &nbsp;
            Edit
          </Button>
          <Button color="secondary" onClick={() => toggle()}>
            Close
          </Button>
        </ModalFooter>
      </Modal>
  )

}

const TrueFalseLabel = ({bool}) => {
  if (!!bool) {
    return <Badge color={'success'}>True</Badge>
  } else {
    return <Badge color={'danger'}>False</Badge>
  }
}

export default ProgramSettings;