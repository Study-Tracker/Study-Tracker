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

import React from "react";
import LoadingMessage from "../../common/structure/LoadingMessage";
import ErrorMessage from "../../common/structure/ErrorMessage";
import AssayDetails from "./AssayDetails";
import StandardWrapper from "../../common/structure/StandardWrapper";
import {useSelector} from "react-redux";
import {useParams} from "react-router-dom";
import axios from "axios";
import {useQuery} from "@tanstack/react-query";

const AssayDetailsView = props => {

  const params = useParams();
  const user = useSelector(state => state.user.value)
  const features = useSelector(state => state.features.value);
  const {studyCode, assayCode} = params;

  const {data: study, isLoading: studyIsLoading, error: studyError} = useQuery("study", () => {
    return axios.get("/api/internal/study/" + studyCode)
    .then(response => response.data);
  });

  const {data: assay, isLoading: assayIsLoading, error: assayError} = useQuery("assay", () => {
    return axios.get("/api/internal/assay/" + assayCode)
    .then(response => response.data);
  });

  if (studyIsLoading || assayIsLoading) {
    return (
        <StandardWrapper {...props}>
          <LoadingMessage/>
        </StandardWrapper>
    )
  }

  if (studyError || assayError) {
    return (
        <StandardWrapper {...props}>
          <ErrorMessage/>
        </StandardWrapper>
    )
  }

  return (
      <StandardWrapper {...props}>
        <AssayDetails
          study={study}
          assay={assay}
          user={user}
          features={features}
        />
      </StandardWrapper>
  )

}

AssayDetailsView.propTypes = {}

export default AssayDetailsView;