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
import AssayFormStandard from "../components/forms/AssayFormStandard";
import {connect} from "react-redux";
import {Redirect} from 'react-router'

class AssayFormView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      studyCode: props.match.params.studyCode,
      assayCode: props.match.params.assayCode || null,
      isLoaded: false,
      isError: false,
    };
  }

  componentDidMount() {

    fetch("/api/study/" + this.state.studyCode)
    .then(response => response.json())
    .then(study => {

      // Selected assay
      if (!!this.state.assayCode) {
        fetch("/api/assay/" + this.state.assayCode)
        .then(response => response.json())
        .then(assay => {
          console.log(assay);
          if (!!assay.startDate) {
            assay.startDate = Date.parse(assay.startDate);
          }
          if (!!assay.endDate) {
            assay.endDate = Date.parse(assay.endDate);
          }
          this.setState({
            study: study,
            assay: assay,
            isLoaded: true
          });
        })
        .catch(error => {
          console.error(error);
          this.setState({
            isError: true,
            error: error
          });
        })
      } else {
        this.setState({
          isLoaded: true,
          study: study,
          assay: null
        })
      }
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
    } else if (!this.props.user) {
      content = <Redirect to="/login"/>
    } else if (this.state.isLoaded) {
      content = <AssayFormStandard
          study={this.state.study}
          assay={this.state.assay}
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
}))(AssayFormView);
