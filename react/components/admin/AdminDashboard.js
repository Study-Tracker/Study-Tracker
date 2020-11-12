import React from 'react';
import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  CardHeader,
  CardTitle,
  Col,
  Container,
  ListGroup,
  ListGroupItem,
  Row
} from "reactstrap";
import UserSettings from "./UserSettings";
import AssayTypeSettings from "./AssayTypeSettings";

class AdminDashboard extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      active: props.active || "users"
    };
    this.setActiveSettings = this.setActiveSettings.bind(this);
  }

  setActiveSettings(id) {
    this.setState({active: id});
  }

  render() {

    let content = '';
    if (this.state.active === "users") {
      content = <UserSettings/>;
    } else if (this.state.active === "assay-types") {
      content =
          <AssayTypeSettings/>;
    }

    return (
        <Container fluid className="animated fadeIn">

          <Row>
            <Col>
              <Breadcrumb>
                <BreadcrumbItem>
                  <a href={"/"}>Home</a>
                </BreadcrumbItem>
                <BreadcrumbItem active>Admin Dashboard</BreadcrumbItem>
              </Breadcrumb>
            </Col>
          </Row>

          <Row className="justify-content-between align-items-center">
            <Col>
              <h1>Admin Dashboard</h1>
            </Col>
          </Row>

          <Row>

            <Col md="3" xl="2">
              <Card>
                <CardHeader>
                  <CardTitle tag="h5" className="mb-0">
                    Site Settings
                  </CardTitle>
                </CardHeader>
                <ListGroup flush>
                  <ListGroupItem tag="a" href="#" action
                                 active={this.state.active === "users"}
                                 onClick={() => this.setState(
                                     {active: "users"})}>
                    Users
                  </ListGroupItem>
                  <ListGroupItem tag="a" href="#" action
                                 active={this.state.active === "assay-types"}
                                 onClick={() => this.setState(
                                     {active: "assay-types"})}>
                    Assay Types
                  </ListGroupItem>
                </ListGroup>
              </Card>
            </Col>

            <Col md="9" xl="10">
              {content}
            </Col>

          </Row>

        </Container>
    );
  }

}

export default AdminDashboard;
