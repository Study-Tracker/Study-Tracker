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
import LoadingMessage from "../../common/structure/LoadingMessage";
import ErrorMessage from "../../common/structure/ErrorMessage";
import StandardWrapper from "../../common/structure/StandardWrapper";
import ProgramDetails from "./ProgramDetails";
import {useSelector} from "react-redux";
import {useParams} from "react-router-dom";

const ProgramDetailsView = props => {

  const params = useParams();
  const user = useSelector(s => s.user.value);
  const [state, setState] = useState({
    programId: params.programId,
    isLoaded: false,
    isError: false
  });

  useEffect(() => {
    fetch("/api/program/" + state.programId)
    .then(response => response.json())
    .then(async program => {
      fetch("/api/study?program=" + program.id)
      .then(response => response.json())
      .then(studies => {
        setState(prevState => ({
          ...prevState,
          program: program,
          studies: studies,
          isLoaded: true
        }));
        console.log(program);
      })
    })
    .catch(error => {
      console.error(error);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    })
  }, []);

  let content = <LoadingMessage/>;
  if (state.isError) {
    content = <ErrorMessage/>;
  } else if (state.isLoaded) {
    content = <ProgramDetails program={state.program}
                              studies={state.studies}
                              user={user}/>;
  }
  return (
      <StandardWrapper {...props}>
        {content}
      </StandardWrapper>
  );

}

export default ProgramDetailsView;