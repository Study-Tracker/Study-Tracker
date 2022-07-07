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
import AssayDetails from "../components/assay/AssayDetails";
import StandardWrapper from "../structure/StandardWrapper";
import PropTypes from "prop-types";
import {useSelector} from "react-redux";

const AssayDetailsView = props => {

  const user = useSelector(state => state.user.value)
  const features = useSelector(state => state.features.value);
  const [state, setState] = useState({
    studyCode: props.match.params.studyCode,
    assayCode: props.match.params.assayCode,
    isLoaded: false,
    isError: false
  });

  useEffect(() => {
    fetch("/api/study/" + state.studyCode)
    .then(response => response.json())
    .then(async study => {
      fetch("/api/study/" + state.studyCode + "/assays/"
          + state.assayCode)
      .then(response => response.json())
      .then(assay => {
        setState({
          ...state,
          study: study,
          assay: assay,
          isLoaded: true
        });
        console.log(assay);
      })
      .catch(error => {
        console.error(error);
        setState({
          ...state,
          isError: true,
          error: error
        });
      });
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


  let content = <LoadingMessage/>;
  if (state.isError) {
    content = <ErrorMessage/>;
  } else if (state.isLoaded) {
    content = <AssayDetails
        study={state.study}
        assay={state.assay}
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

AssayDetailsView.propTypes = {
  user: PropTypes.object,
  features: PropTypes.object,
}

export default AssayDetailsView;