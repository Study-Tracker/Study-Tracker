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
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import StandardWrapper from "../structure/StandardWrapper";
import {connect} from 'react-redux';
import UserDetails from "../components/user/UserDetails";

class UserDetailsView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      userId: props.match.params.userId,
      isLoaded: false,
      isError: false
    };
  }

  componentDidMount() {
    fetch("/api/user/" + this.state.userId)
    .then(response => response.json())
    .then(async user => {
      fetch("/api/study?user=" + user.id)
      .then(response => response.json())
      .then(studies => {
        this.setState({
          user: user,
          studies: studies,
          isLoaded: true
        });
        console.log(user);
      })
    })
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    })
  }

  render() {
    let content = <LoadingMessage/>;
    if (this.state.isError) {
      content = <ErrorMessage/>;
    } else if (this.state.isLoaded) {
      content = <UserDetails targetUser={this.state.user}
                             studies={this.state.studies}
                             user={this.props.user}/>;
    }
    return (
        <StandardWrapper {...this.props}>
          {content}
        </StandardWrapper>
    );
  }

}

export default connect(store => ({
  user: store.user
}))(UserDetailsView);