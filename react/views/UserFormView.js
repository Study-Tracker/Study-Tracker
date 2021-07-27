/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import NoSidebarPageWrapper from "../structure/NoSidebarPageWrapper";
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import {connect} from "react-redux";
import UserForm from "../components/forms/UserForm";

class UserFormView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      userId: props.match.params.userId || null,
      isLoaded: false,
      isError: false,
    };
  }

  componentDidMount() {

    // Programs
    fetch("/api/user")
    .then(response => response.json())
    .then(users => {
      if (!!this.state.userId) {
        const user = users.find(p => p.id === parseInt(this.state.userId));
        this.setState({
          selectedUser: user,
          users: users,
          isLoaded: true
        });
      } else {
        this.setState({
          users: users,
          isLoaded: true
        });
      }
    }).catch(error => {
      this.setState({
        isError: true,
        error: error
      });
    });

  }

  render() {
    let content = <LoadingMessage/>;
    if (this.state.isError) {
      content = <ErrorMessage/>;
    } else if (!!this.props.user && this.state.isLoaded) {
      content = <UserForm
          user={this.state.selectedUser}
          users={this.state.users}
      />;
    }
    return (
        <NoSidebarPageWrapper>
          {content}
        </NoSidebarPageWrapper>
    );
  }

}

export default connect(store => ({
  user: store.user
}))(UserFormView);
