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
import AssayTypeForm from "../components/forms/AssayTypeForm";
import {useSelector} from "react-redux";
import axios from "axios";

const AssayTypeFormView = props => {
  
  const [state, setState] = useState({
    assayTypeId: props.match.params.assayTypeId || null,
    isLoaded: false,
    isError: false,
  });
  const user = useSelector(s => s.user.value);

  useEffect(() => {

    axios.get("/api/assaytype/")
    .then(response => {
      const assayTypes = response.data;
      if (!!state.assayTypeId) {
        const assayType = assayTypes.find(
            p => p.id === parseInt(state.assayTypeId));
        setState({
          ...state,
          selectedAssayType: assayType,
          assayTypes: assayTypes,
          isLoaded: true
        });
      } else {
        setState({
          ...state,
          assayTypes: assayTypes,
          isLoaded: true
        });
      }
    }).catch(error => {
      setState({
        ...state,
        isError: true,
        error: error
      });
    });

  }, []);

  let content = <LoadingMessage/>;
  if (state.isError) {
    content = <ErrorMessage/>;
  } else if (user && state.isLoaded) {
    content = <AssayTypeForm
        assayType={state.selectedAssayType}
        assayTypes={state.assayTypes}
    />;
  }
  return (
      <NoSidebarPageWrapper>
        {content}
      </NoSidebarPageWrapper>
  );

}

export default AssayTypeFormView;
