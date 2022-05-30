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

import React from "react";
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import StandardWrapper from "../structure/StandardWrapper";
import {connect} from "react-redux";
import StudyDetails from '../components/study/StudyDetails';

class StudyDetailsView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      studyCode: props.match.params.studyCode,
      isLoaded: false,
      isError: false
    };
  }

  componentDidMount() {
    fetch("/api/study/" + this.state.studyCode)
    .then(response => response.json())
    .then(async study => {

      this.setState({
        study: study,
        isLoaded: true
      });
      console.log(study);

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
      content = <StudyDetails study={this.state.study} user={this.props.user}/>;
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
}))(StudyDetailsView);