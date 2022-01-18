import React from 'react';
import {Badge, Button, Card, Col, Modal, Row, Table} from 'react-bootstrap';
import {Edit, Info, User, UserPlus} from 'react-feather';
import {history} from "../../App";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import {SettingsErrorMessage} from "../errors";
import {SettingsLoadingMessage} from "../loading";

class UserSettings extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      users: [],
      isLoaded: false,
      isError: false,
      showDetails: false,
      selectedUser: null,
      isModalOpen: false
    };
    this.showModal = this.showModal.bind(this);
  }

  showModal(selected) {
    if (!!selected) {
      this.setState({
        isModalOpen: true,
        selectedUser: selected
      });
    } else {
      this.setState({
        isModalOpen: false
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

    let content = '';
    if (!!this.state.isLoaded) {
      content = <UserTable users={this.state.users} showModal={this.showModal} />
    } else if (!!this.state.isError) {
      content = <SettingsErrorMessage />
    } else {
      content = <SettingsLoadingMessage />
    }

    return (
        <React.Fragment>

          <Card>
            <Card.Header>
              <Card.Title tag="h5" className="mb-0">
                Registered Users
                <span className="float-end">
                  <Button
                      color={"primary"}
                      onClick={() => history.push("/users/new")}
                  >
                    New User
                    &nbsp;
                    <UserPlus className="feather align-middle ms-2 mb-1"/>
                  </Button>
                </span>
              </Card.Title>
            </Card.Header>
            <Card.Body>

              {content}

              <UserDetailsModal
                  showModal={this.showModal}
                  isOpen={this.state.isModalOpen}
                  user={this.state.selectedUser}
              />

            </Card.Body>
          </Card>

        </React.Fragment>
    );
  }

}

const UserTable = ({users, showModal}) => {

  const columns = [
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
      dataField: "username",
      text: "Username",
      sort: true,
      headerStyle: {width: '20%%'},
      formatter: (c, d, i, x) => <Button variant={"link"} onClick={() => showModal(d)}>{d.username}</Button>,
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
          return <Badge bg="danger">Admin</Badge>
        } else {
          return <Badge bg="info">User</Badge>
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
          return <Badge bg="warning">Locked</Badge>
        } else if (d.active) {
          return <Badge bg="success">Active</Badge>
        } else {
          return <Badge bg="danger">Inactive</Badge>
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
                 onClick={() => showModal(d)}>
                <Info className="align-middle me-1" size={18}/>
              </a>

              <a className="text-warning" title={"Edit user"}
                 onClick={() => history.push("/users/" + d.id + "/edit")}>
                <Edit className="align-middle me-1" size={18}/>
              </a>

              {/*<a className="text-danger" title={"Disable user"}*/}
              {/*   onClick={() => console.log("click")}>*/}
              {/*  <Trash className="align-middle me-1" size={18}/>*/}
              {/*</a>*/}

            </React.Fragment>
        )
      }
    }
  ];

  return (
      <ToolkitProvider
          keyField="id"
          data={users}
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
                    dataField: "displayName",
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

const UserDetailsModal = ({user, isOpen, showModal}) => {

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
          show={isOpen}
          onHide={() => showModal()}
          size={"lg"}
      >
        <Modal.Header closeButton>
          User:&nbsp;<strong>{user.displayName}</strong>&nbsp;(<code>{user.username}</code>)
        </Modal.Header>
        <Modal.Body>
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
        </Modal.Body>
        <Modal.Footer>
          <Button variant="info" href={"/user/" + user.username}>
            <User size={14} className="mb-1"/>
            &nbsp;
            View Profile
          </Button>
          <Button variant="warning" href={"/users/" + user.id + "/edit"}>
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
    return <Badge color={'success'}>True</Badge>
  } else {
    return <Badge color={'danger'}>False</Badge>
  }
}

export default UserSettings;