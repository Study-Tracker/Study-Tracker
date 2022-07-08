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
import ProgramForm from "../components/forms/ProgramForm";
import PropTypes from "prop-types";
import {useSelector} from "react-redux";
import axios from "axios";
import {useParams} from "react-router-dom";

const ProgramFormView = props => {

  const params = useParams();
  const user = useSelector(s => s.user.value);
  const features = useSelector(s => s.features.value);
  const [state, setState] = useState({
    programId: params.programId || null,
    isLoaded: false,
    isError: false,
  });
  
  useEffect(() => {

    // Programs
    axios.get("/api/program")
    .then(async response => {
      const programs = response.data;
      if (!!state.programId) {

        const program = await fetch("/api/program/" + state.programId)
        .then(async response => response.json());
        console.log(program);

        setState(prevState => ({
          ...prevState,
          program: program,
          programs: programs,
          isLoaded: true
        }));

      } else {
        setState(prevState => ({
          ...prevState,
          programs: programs,
          isLoaded: true
        }));
      }
    }).catch(error => {
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });

  }, []);

  let content = <LoadingMessage/>;
  if (state.isError) {
    content = <ErrorMessage/>;
  } else if (!!user && state.isLoaded) {
    content = <ProgramForm
        program={state.program}
        programs={state.programs}
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

ProgramFormView.propTypes = {
  features: PropTypes.object,
}

export default ProgramFormView;
