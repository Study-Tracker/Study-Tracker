/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import StudyDetails from './StudyDetails';
import PropTypes from "prop-types";
import {useSelector} from "react-redux";
import axios from "axios";
import {useParams} from "react-router-dom";

const StudyDetailsView = props => {

  const params = useParams();
  const [state, setState] = useState({
    studyCode: params.studyCode,
    isLoaded: false,
    isError: false
  });
  const user = useSelector(s => s.user.value);
  const features = useSelector(s => s.features.value);

  useEffect(() => {
    axios.get("/api/internal/study/" + state.studyCode)
    .then(response => {

      setState(prevState => ({
        ...prevState,
        study: response.data,
        isLoaded: true
      }));
      console.debug(response.data);

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