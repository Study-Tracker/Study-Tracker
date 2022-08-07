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

import React, {useEffect, useState} from "react";
import NoSidebarPageWrapper from "../../common/structure/NoSidebarPageWrapper";
import LoadingMessage from "../../common/structure/LoadingMessage";
import ErrorMessage from "../../common/structure/ErrorMessage";
import StudyForm from "./StudyForm";
import {useSelector} from "react-redux";
import axios from "axios";
import {useParams} from "react-router-dom";

const StudyFormView = props => {

  const params = useParams();
  const [state, setState] = useState({
    studyCode: params.studyCode || null,
    studyLoaded: false,
    programsLoaded: false,
    collaboratorsLoaded: false,
    keywordCategoriesLoaded: false,
    isError: false,
  });
  const user = useSelector(s => s.user.value);
  const features = useSelector(s => s.features.value);

  useEffect(() => {

    // Programs
    axios.get("/api/internal/program")
    .then(response => {
      setState(prevState => ({
        ...prevState,
        programs: response.data,
        programsLoaded: true
      }));
    }).catch(error => {
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });

    // Contacts
    axios.get("/api/internal/collaborator")
    .then(response => {
      setState(prevState => ({
        ...prevState,
        collaborators: response.data,
        collaboratorsLoaded: true
      }));
    }).catch(error => {
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });

    // Keyword categories
    axios.get("/api/internal/keyword-category")
    .then(response => {
      setState(prevState => ({
        ...prevState,
        keywordCategories: response.data.sort((a, b) => {
          if (a.name > b.name) {
            return 1;
          } else if (a.name < b.name) {
            return -1;
          } else {
            return 0;
          }
        }),
        keywordCategoriesLoaded: true
      }))
    }).catch(error => {
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });

    // Selected study
    if (!!state.studyCode) {
      axios.get("/api/internal/study/" + state.studyCode)
      .then(response => {
        console.debug(response.data);
        setState(prevState => ({
          ...prevState,
          study: response.data,
          studyLoaded: true
        }));
      })
      .catch(error => {
        setState(prevState => ({
          ...prevState,
          isError: true,
          error: error
        }));
      })
    } else {
      setState(prevState => ({
        ...prevState,
        studyLoaded: true
      }))
    }
  }, []);


  let content = <LoadingMessage/>;
  if (state.isError) {
    content = <ErrorMessage/>;
  } else if (
      !!user
      && state.studyLoaded
      && state.programsLoaded
      && state.collaboratorsLoaded
      && state.keywordCategoriesLoaded
  ) {
    content = <StudyForm
        study={state.study}
        programs={state.programs}
        externalContacts={state.collaborators}
        keywordCategories={state.keywordCategories}
        user={user}
        features={features}
    />;
  }
  return (
      <NoSidebarPageWrapper>
        {content}
      </NoSidebarPageWrapper>
  );

}

export default StudyFormView;
