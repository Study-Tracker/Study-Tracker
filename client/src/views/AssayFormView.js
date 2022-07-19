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
import NoSidebarPageWrapper from "../structure/NoSidebarPageWrapper";
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import AssayForm from "../components/forms/AssayForm";
import {useSelector} from "react-redux";
import {useParams} from "react-router-dom";
import axios from "axios";

const AssayFormView = props => {

  const params = useParams();
  const user = useSelector(state => state.user.value);
  const features = useSelector(state => state.features.value);
  const [state, setState] = useState({
    studyCode: params.studyCode,
    assayCode: params.assayCode || null,
    isLoaded: false,
    isError: false,
  });

  useEffect(async () => {

    const study = await axios.get("/api/study/" + state.studyCode)
    .then(async response => await response.data)
    .catch(error => {
      console.error(error);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });

    let assay = null;
    if (!!state.assayCode) {

      assay = await axios.get("/api/assay/" + state.assayCode)
      .then(async response => await response.data)
      .catch(error => {
        console.error(error);
        setState(prevState => ({
          ...prevState,
          isError: true,
          error: error
        }));
      });

    }

    const assayTypes = await axios.get("/api/assaytype/")
    .then(async response => await response.data)
    .catch(error => {
      console.error(error);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });

    const notebookTemplates = await axios.get(
        "/api/notebookentrytemplate?category=ASSAY&active=true")
    .then(async response => await response.data)
    .catch(error => {
      console.error(error);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });
    const defaultNotebookTemplate = notebookTemplates.find(
        o => o.default === true);

    setState(prevState => ({
      ...prevState,
      study: study,
      assay: assay,
      assayTypes: assayTypes,
      notebookTemplates: notebookTemplates,
      defaultNotebookTemplate,
      isLoaded: true
    }));

  }, []);


  let content = <LoadingMessage/>;
  if (state.isError) {
    content = <ErrorMessage/>;
  } else if (!!user && state.isLoaded) {
    content = <AssayForm
        study={state.study}
        assay={state.assay}
        user={user}
        assayTypes={state.assayTypes}
        notebookTemplates={state.notebookTemplates}
        defaultNotebookTemplate={state.defaultNotebookTemplate}
        features={features}
    />;
  }
  return (
      <NoSidebarPageWrapper>
        {content}
      </NoSidebarPageWrapper>
  );

}

AssayFormView.propTypes = {
}

export default AssayFormView;