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
import StudyForm from "../components/forms/StudyForm";
import {connect} from "react-redux";

class StudyFormView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      studyCode: props.match.params.studyCode || null,
      studyLoaded: false,
      programsLoaded: false,
      collaboratorsLoaded: false,
      keywordCategoriesLoaded: false,
      notebookTemplatesLoaded: false,
      isError: false,
    };
  }

  componentDidMount() {

    // Programs
    fetch("/api/program")
    .then(response => response.json())
    .then(programs => {
      this.setState({
        programs: programs,
        programsLoaded: true
      });
    }).catch(error => {
      this.setState({
        isError: true,
        error: error
      });
    });

    // Contacts
    fetch("/api/collaborator")
    .then(response => response.json())
    .then(collaborators => {
      this.setState({
        collaborators: collaborators,
        collaboratorsLoaded: true
      });
    }).catch(error => {
      this.setState({
        isError: true,
        error: error
      });
    });

    // Keyword categories
    fetch("/api/keyword-category")
    .then(response => response.json())
    .then(keywordCategories => {
      this.setState({
        keywordCategories: keywordCategories.sort((a, b) => {
          if (a.name > b.name) {
            return 1;
          } else if (a.name < b.name) {
            return -1;
          } else {
            return 0;
          }
        }),
        keywordCategoriesLoaded: true
      })
    }).catch(error => {
      this.setState({
        isError: true,
        error: error
      });
    });

    // Entry Templates
    fetch("/api/notebookentrytemplate?category=STUDY&active=true")
    .then(response => response.json())
    .then(templates => {
      const defaultNotebookTemplate = templates.find(o => o.default === true);
      this.setState({
        notebookTemplates: templates,
        defaultNotebookTemplate: defaultNotebookTemplate,
        notebookTemplatesLoaded: true
      });
    })
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    });

    // Selected study
    if (!!this.state.studyCode) {
      fetch("/api/study/" + this.state.studyCode)
      .then(response => response.json())
      .then(study => {
        console.log(study);
        this.setState({
          study: study,
          studyLoaded: true
        });
      })
      .catch(error => {
        this.setState({
          isError: true,
          error: error
        });
      })
    } else {
      this.setState({
        studyLoaded: true
      })
    }
  }

  render() {
    let content = <LoadingMessage/>;
    if (this.state.isError) {
      content = <ErrorMessage/>;
    } else if (
        !!this.props.user
        && this.state.studyLoaded
        && this.state.programsLoaded
        && this.state.collaboratorsLoaded
        && this.state.keywordCategoriesLoaded
        && this.state.notebookTemplatesLoaded
    ) {
      content = <StudyForm
          study={this.state.study}
          programs={this.state.programs}
          externalContacts={this.state.collaborators}
          keywordCategories={this.state.keywordCategories}
          notebookTemplates={this.state.notebookTemplates}
          defaultNotebookTemplate={this.state.defaultNotebookTemplate}
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
}))(StudyFormView);
