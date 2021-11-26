import React from 'react';
import {Badge, Button, Card, Col, Modal, Row, Table} from 'react-bootstrap';
import {Clipboard, Edit, FolderPlus, Info} from 'react-feather';
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
      isModalOpen: false
    };
    this.showModal = this.showModal.bind(this);
  }

  showModal(selected) {
    if (!!selected) {
      this.setState({
        isModalOpen: true,
        selectedProgram: selected
      });
    } else {
      this.setState({
        isModalOpen: false
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
        formatter: (c, d, i, x) => <Button variant="link" onClick={() => this.showModal(d)}>{d.name}</Button>,
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
            return <Badge bg="success">Active</Badge>
          } else {
            return <Badge bg="danger">Inactive</Badge>
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
              return <Badge bg="warning">ERROR</Badge>
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
              return <Badge bg="warning">ERROR</Badge>
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
                   onClick={() => this.showModal(d)}>
                  <Info className="align-middle me-1" size={18}/>
                </a>

                <a className="text-warning" title={"Edit program"}
                   href={"/program/" + d.id + "/edit"}>
                  <Edit className="align-middle me-1" size={18}/>
                </a>

                {/*<a className="text-danger" title={"Disable program"}*/}
                {/*   onClick={() => console.log("click")}>*/}
                {/*  <Trash className="align-middle me-1" size={18}/>*/}
                {/*</a>*/}

              </React.Fragment>
          )
        }
      }
    ];

    return (
        <React.Fragment>

          <Card>
            <Card.Header>
              <Card.Title tag="h5" className="mb-0">
                Registered Programs
                <span className="float-end">
                  <Button
                      variant={"primary"}
                      href={"/programs/new"}
                  >
                    New Program
                    &nbsp;
                    <FolderPlus className="feather align-middle ms-2 mb-1"/>
                  </Button>
                </span>
              </Card.Title>
            </Card.Header>
            <Card.Body>
              <ToolkitProvider
                  keyField="id"
                  data={this.state.programs}
                  columns={columns}
                  search
                  exportCSV
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
              <ProgramDetailsModal
                  showModal={this.showModal}
                  isOpen={this.state.isModalOpen}
                  program={this.state.selectedProgram}
              />
            </Card.Body>
          </Card>

        </React.Fragment>
    );
  }

}

const ProgramDetailsModal = ({program, isOpen, showModal}) => {

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
          open={isOpen}
          onHide={() => showModal()}
          size={"lg"}
      >
        <Modal.Header closeButton>
          Program: <strong>{program.name}</strong> (<code>{program.code}</code>)
        </Modal.Header>
        <Modal.Body>
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
        </Modal.Body>
        <Modal.Footer>
          <Button variant="info"
                  href={"/program/" + program.id}>
            <Clipboard size={14} className="mb-1"/>
            &nbsp;
            View Program
          </Button>
          <Button variant="warning"
                  href={"/program/" + program.id + "/edit"}>
            <Edit size={14} className="mb-1"/>
            &nbsp;
            Edit
          </Button>
          <Button variant="secondary" onClick={() => showModal()}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
  )

}

const TrueFalseLabel = ({bool}) => {
  if (!!bool) {
    return <Badge bg={'success'}>True</Badge>
  } else {
    return <Badge bg={'danger'}>False</Badge>
  }
}

export default ProgramSettings;