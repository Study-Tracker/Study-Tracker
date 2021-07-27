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
import ProgramForm from "../components/forms/ProgramForm";
import {connect} from "react-redux";

class ProgramFormView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      programId: props.match.params.programId || null,
      isLoaded: false,
      isError: false,
    };
  }

  componentDidMount() {

    // Programs
    fetch("/api/program")
    .then(response => response.json())
    .then(programs => {
      if (!!this.state.programId) {
        const program = programs.find(p => String(p.id) === this.state.programId);
        this.setState({
          program: program,
          programs: programs,
          isLoaded: true
        });
      } else {
        this.setState({
          programs: programs,
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
      content = <ProgramForm
          program={this.state.program}
          programs={this.state.programs}
          user={this.props.user}
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
}))(ProgramFormView);
