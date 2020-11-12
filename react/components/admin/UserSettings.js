import React from 'react';
import {Button, Card, CardBody, CardHeader, CardTitle} from 'reactstrap';
import AdminUserTable from "./AdminUserTable";
import {UserPlus} from 'react-feather';
import {history} from "../../App";

class UserSettings extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      users: [],
      isLoaded: false,
      isError: false,
      showDetails: false,
      selectedUser: null
    };
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
              <AdminUserTable users={this.state.users}/>
            </CardBody>
          </Card>

        </React.Fragment>
    );
  }

}

export default UserSettings;