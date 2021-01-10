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
import StudyFormStandard from "../components/forms/StudyFormStandard";
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
    fetch("/api/keyword/categories")
    .then(response => response.json())
    .then(keywordCategories => {
      this.setState({
        keywordCategories: keywordCategories.sort((a, b) => {
          if (a > b) {
            return 1;
          } else if (a < b) {
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

    // Selected study
    if (!!this.state.studyCode) {
      fetch("/api/study/" + this.state.studyCode)
      .then(response => response.json())
      .then(study => {
        console.log(study);
        if (!!study.startDate) {
          study.startDate = Date.parse(study.startDate);
        }
        if (!!study.endDate) {
          study.endDate = Date.parse(study.endDate);
        }
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
    } else if (!!this.props.user && this.state.studyLoaded
        && this.state.programsLoaded && this.state.collaboratorsLoaded
        && this.state.keywordCategoriesLoaded) {
      content = <StudyFormStandard
          study={this.state.study}
          programs={this.state.programs}
          externalContacts={this.state.collaborators}
          keywordCategories={this.state.keywordCategories}
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
