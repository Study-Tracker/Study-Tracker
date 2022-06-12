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
import NoSidebarPageWrapper from "../structure/NoSidebarPageWrapper";
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import AssayForm from "../components/forms/AssayForm";
import {connect} from "react-redux";
import PropTypes from "prop-types";

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

  async componentDidMount() {

    const study = await fetch("/api/study/" + this.state.studyCode)
    .then(async response => await response.json())
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    });

    let assay = null;
    if (!!this.state.assayCode) {

      assay = await fetch("/api/assay/" + this.state.assayCode)
      .then(async response => await response.json())
      .catch(error => {
        console.error(error);
        this.setState({
          isError: true,
          error: error
        });
      });

    }

    const assayTypes = await fetch("/api/assaytype/")
    .then(async response => await response.json())
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    });

    const notebookTemplates = await fetch(
        "/api/notebookentrytemplate?category=ASSAY&active=true")
    .then(async response => await response.json())
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    });
    const defaultNotebookTemplate = notebookTemplates.find(
        o => o.default === true);

    this.setState({
      study: study,
      assay: assay,
      assayTypes: assayTypes,
      notebookTemplates: notebookTemplates,
      defaultNotebookTemplate,
      isLoaded: true
    });

  }

  render() {
    let content = <LoadingMessage/>;
    if (this.state.isError) {
      content = <ErrorMessage/>;
    } else if (!!this.props.user && this.state.isLoaded) {
      content = <AssayForm
          study={this.state.study}
          assay={this.state.assay}
          user={this.props.user}
          assayTypes={this.state.assayTypes}
          notebookTemplates={this.state.notebookTemplates}
          defaultNotebookTemplate={this.state.defaultNotebookTemplate}
          features={this.props.features}
      />;
    }
    return (
        <NoSidebarPageWrapper>
          {content}
        </NoSidebarPageWrapper>
    );
  }

}

AssayFormView.propTypes = {
  user: PropTypes.object.isRequired,
  features: PropTypes.object,
}

export default connect(store => ({
  user: store.user,
  features: store.features,
}))(AssayFormView);
