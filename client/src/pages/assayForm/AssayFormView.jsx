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

import React, { useContext } from "react";
import NoSidebarPageWrapper from "../../common/structure/NoSidebarPageWrapper";
import LoadingMessage from "../../common/structure/LoadingMessage";
import ErrorMessage from "../../common/structure/ErrorMessage";
import AssayForm from "./AssayForm";
import {useSelector} from "react-redux";
import {useParams} from "react-router-dom";
import axios from "axios";
import { useQuery } from "@tanstack/react-query";
import NotyfContext from "@/context/NotyfContext";

const AssayFormView = () => {

  const params = useParams();
  const notyf = useContext(NotyfContext);
  const user = useSelector(state => state.user.value);
  const features = useSelector(state => state.features.value);

  const { data: study, isLoading: isStudyLoading, error: studyError } = useQuery({
    queryKey: ["study", params.studyCode],
    queryFn: () => axios.get("/api/internal/study/" + params.studyCode)
    .then(response => response.data)
    .catch(e => {
      console.error(e);
      notyf.error("Failed to load study: " + e.message);
    }),
  });

  const { data: assay, isLoading: isAssayLoading, error: assayError } = useQuery({
    queryKey: ["assay", params.assayCode],
    queryFn: () => {
      return axios.get("/api/internal/assay/" + params.assayCode)
      .then(response => response.data)
      .catch(e => {
        console.error(e);
        notyf.error("Failed to load assay: " + e.message);
      });
    },
    enabled: !!params.assayCode, // Only run if assayCode is provided
  });

  const { data: assayTypes, isLoading: isAssayTypesLoading, error: assayTypesError } = useQuery({
    queryKey: ["assayTypes"],
    queryFn: () => {
      return axios.get("/api/internal/assaytype")
      .then(response => response.data.filter(t => t.active === true))
      .catch(e => {
        console.error(e);
        notyf.error("Failed to load assay types: " + e.message);
      });
    },
  });

  if (isStudyLoading || isAssayLoading || isAssayTypesLoading) {
    return (
      <NoSidebarPageWrapper>
        <LoadingMessage />
      </NoSidebarPageWrapper>
    );
  }

  if (studyError || assayError || assayTypesError) {
    return (
      <NoSidebarPageWrapper>
        <ErrorMessage message={`Error loading data: ${studyError?.message || assayError?.message || assayTypesError?.message}`} />
      </NoSidebarPageWrapper>
    );
  }

  return (
    <NoSidebarPageWrapper>
      <AssayForm
        study={study}
        assay={assay}
        user={user}
        assayTypes={assayTypes}
        features={features}
      />
    </NoSidebarPageWrapper>
  );

}

AssayFormView.propTypes = {
}

export default AssayFormView;
