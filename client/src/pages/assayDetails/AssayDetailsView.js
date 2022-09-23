/*
 * Copyright 2022 the original author or authors.
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
import AssayDetails from "./AssayDetails";
import StandardWrapper from "../../common/structure/StandardWrapper";
import PropTypes from "prop-types";
import {useSelector} from "react-redux";
import {useParams} from "react-router-dom";
import axios from "axios";

const AssayDetailsView = props => {

  const params = useParams();
  const user = useSelector(state => state.user.value)
  const features = useSelector(state => state.features.value);
  const [state, setState] = useState({
    studyCode: params.studyCode,
    assayCode: params.assayCode,
    isLoaded: false,
    isError: false
  });

  useEffect(() => {
    axios.get("/api/internal/study/" + state.studyCode)
    .then(async response => {
      const study = response.data;
      axios.get("/api/internal/study/" + state.studyCode + "/assays/"
          + state.assayCode)
      .then(response2 => {
        const assay = response2.data;
        setState(prevState => ({
          ...prevState,
          study: study,
          assay: assay,
          isLoaded: true
        }));
        console.debug("Assay", assay);
      })
      .catch(error => {
        console.error(error);
        setState(prevState => ({
          ...prevState,
          isError: true,
          error: error
        }));
      });
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