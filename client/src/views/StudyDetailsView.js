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
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import StandardWrapper from "../structure/StandardWrapper";
import StudyDetails from '../components/study/StudyDetails';
import PropTypes from "prop-types";
import {useSelector} from "react-redux";
import axios from "axios";

const StudyDetailsView = props => {
  
  const [state, setState] = useState({
    studyCode: props.match.params.studyCode,
    isLoaded: false,
    isError: false
  });
  const user = useSelector(s => s.user.value);
  const features = useSelector(s => s.features.value);

  useEffect(() => {
    axios.get("/api/study/" + state.studyCode)
    .then(async response => {

      setState({
        ...state,
        study: response.data,
        isLoaded: true
      });
      console.debug(response.data);

    })
    .catch(error => {
      console.error(error);
      setState({
        ...state,
        isError: true,
        error: error
      });
    })
  }, []);


  console.debug(features);
  let content = <LoadingMessage/>;
  if (state.isError) {
    content = <ErrorMessage/>;
  } else if (state.isLoaded) {
    content = <StudyDetails
        study={state.study}
        user={user}
        features={features}
    />;
  }
  return (
      <StandardWrapper {...props}>
        {content}
      </StandardWrapper>
  );

}

StudyDetailsView.propTypes = {
  user: PropTypes.object,
  features: PropTypes.object
}

export default StudyDetailsView;