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
import {Edit, Info, User, UserPlus} from 'react-feather';
import {history} from "../../App";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";

class UserSettings extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      users: [],
      isLoaded: false,
      isError: false,
      showDetails: false,
      selectedUser: null,
      showModal: false
    };
    this.toggleModal = this.toggleModal.bind(this);
  }

  toggleModal(selected) {
    if (!!selected) {
      this.setState({
        showModal: true,
        selectedUser: selected
      });
    } else {
      this.setState({
        showModal: false
      })
    }
  }

  componentDidMount() {
    fetch("/api/user")
    .then(response => response.json())
    .then(async users => {
      this.setState({
        users: users,
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
        dataField: "username",
        text: "Username",
        sort: true,
        headerStyle: {width: '20%%'},
        formatter: (c, d, i, x) => <a href={"javascript:void(0)"} onClick={() => this.toggleModal(d)}>{d.username}</a>,
        sortFunc: (a, b, order, dataField, rowA, rowB) => {
          if (rowA.username > rowB.username) {
            return order === "desc" ? -1 : 1;
          }
          if (rowB.username > rowA.username) {
            return order === "desc" ? 1 : -1;
          }
          return 0;
        },
      },
      {
        dataField: "displayName",
        text: "Display Name",
        sort: true,
        headerStyle: {width: '25%%'},
        formatter: (cell, d, index, x) => d.displayName,
        sortFunc: (a, b, order, dataField, rowA, rowB) => {
          if (rowA.displayName > rowB.displayName) {
            return order === "desc" ? -1 : 1;
          }
          if (rowB.displayName > rowA.displayName) {
            return order === "desc" ? 1 : -1;
          }
          return 0;
        },
      },
      {
        dataField: "email",
        text: "Email",
        sort: true,
        headerStyle: {width: '25%'},
        formatter: (cell, d, index, x) => d.email
      },
      {
        dataField: "type",
        text: "Type",
        sort: true,
        headerStyle: {width: '10%'},
        formatter: (c, d, i, x) => {
          if (d.admin) {
            return (
                <div className="badge badge-danger">
                  Admin
                </div>
            )
          } else {
            return (
                <div className="badge badge-info">
                  User
                </div>
            )
          }
        }
      },
      {
        dataField: "status",
        text: "Status",
        sort: true,
        headerStyle: {width: '10%'},
        formatter: (c, d, i, x) => {
          if (d.locked) {
            return (
                <div className="badge badge-warning">
                  Locked
                </div>
            )
          } else if (d.active) {
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
        dataField: "controls",
        text: "Options",
        sort: false,
        headerStyle: {width: '10%'},
        formatter: (c, d, i, x) => {
          return (
              <React.Fragment>

                <a className="text-info" title={"View details"}
                   onClick={() => this.toggleModal(d)}>
                  <Info className="align-middle mr-1" size={18}/>
                </a>

                <a className="text-warning" title={"Edit user"}
                   onClick={() => history.push("/users/" + d.id + "/edit")}>
                  <Edit className="align-middle mr-1" size={18}/>
                </a>

                {/*<a className="text-danger" title={"Disable user"}*/}
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
                Registered Users
                <span className="float-right">
                  <Button
                      color={"primary"}
                      onClick={() => history.push("/users/new")}
                  >
                    New User
                    &nbsp;
                    <UserPlus className="feather align-middle ml-2 mb-1"/>
                  </Button>
                </span>
              </CardTitle>
            </CardHeader>
            <CardBody>
              <ToolkitProvider
                  keyField="id"
                  data={this.state.users}
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
                            dataField: "username",
                            order: "asc"
                          }]}
                          {...props.baseProps}
                      >
                      </BootstrapTable>
                    </div>
                )}
              </ToolkitProvider>
              <UserDetailsModal
                  toggle={this.toggleModal}
                  isOpen={this.state.showModal}
                  user={this.state.selectedUser}
              />
            </CardBody>
          </Card>

        </React.Fragment>
    );
  }

}

const UserDetailsModal = ({user, isOpen, toggle}) => {

  if (!user) {
    return "";
  }

  const attributes = Object.keys(user.attributes).map(k => {
    return (
        <tr key={"assay-type-attribute-" + k}>
          <td>{k}</td>
          <td>{user.attributes[k]}</td>
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
          User: <strong>{user.displayName}</strong> (<code>{user.username}</code>)
        </ModalHeader>
        <ModalBody>
          <Row>

            <Col md={6}>
              <h4>Name</h4>
              <p>{user.displayName}</p>
            </Col>
            <Col md={6}>
              <h4>Username</h4>
              <p>{user.username}</p>
            </Col>
            <Col md={6}>
              <h4>Email</h4>
              <p>{user.email}</p>
            </Col>
            <Col md={6}>
              <h4>Department</h4>
              <p>{user.department || 'n/a'}</p>
            </Col>
            <Col md={6}>
              <h4>Title</h4>
              <p>{user.title || 'n/a'}</p>
            </Col>

            <Col xs={12}>
              <hr/>
            </Col>

            <Col md={6}>
              <h4>Active</h4>
              <p>
                <TrueFalseLabel bool={user.active}/>
              </p>
            </Col>
            <Col md={6}>
              <h4>Admin</h4>
              <p>
                <TrueFalseLabel bool={user.admin}/>
              </p>
            </Col>
            <Col md={6}>
              <h4>Account Locked</h4>
              <p>
                <TrueFalseLabel bool={user.locked}/>
              </p>
            </Col>
            <Col md={6}>
              <h4>Account Expired</h4>
              <p>
                <TrueFalseLabel bool={user.expired}/>
              </p>
            </Col>
            <Col md={6}>
              <h4>Credentials Expired</h4>
              <p>
                <TrueFalseLabel bool={user.credentialsExpired}/>
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
                  onClick={() => history.push("/user/" + user.username)}>
            <User size={14} className="mb-1"/>
            &nbsp;
            View Profile
          </Button>
          <Button color="warning"
                  onClick={() => history.push("/users/" + user.id + "/edit")}>
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

export default UserSettings;