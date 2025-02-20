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
import NoSidebarPageWrapper from "../../common/structure/NoSidebarPageWrapper";
import LoadingMessage from "../../common/structure/LoadingMessage";
import ErrorMessage from "../../common/structure/ErrorMessage";
import StudyForm from "./StudyForm";
import {useSelector} from "react-redux";
import axios from "axios";
import {useParams} from "react-router-dom";
import {useQuery} from "react-query";

const StudyFormView = () => {

  const params = useParams();
  const studyCode = params.studyCode || null;
  const user = useSelector(s => s.user.value);
  const features = useSelector(s => s.features.value);

  const {data: programs, isLoading: programsLoading, error: programsError} = useQuery(["programs"], () => {
    return axios.get("/api/internal/program")
    .then(response => response.data);
  })

  const {data: study, isLoading: studyLoading, error: studyError} = useQuery({
    queryKey: ["study", studyCode],
    queryFn: () => {
      return axios.get("/api/internal/study/" + studyCode)
      .then(response => response.data);
    },
    enabled: studyCode !== null
  });

  if (!user || programsLoading || studyLoading) {
    return (
        <NoSidebarPageWrapper>
          <LoadingMessage/>
        </NoSidebarPageWrapper>
    );
  }

  if (programsError || studyError) {
    return (
        <NoSidebarPageWrapper>
          <ErrorMessage/>
        </NoSidebarPageWrapper>
    );
  }

  return (
      <NoSidebarPageWrapper>
        <StudyForm
            study={study}
            programs={programs}
            user={user}
            features={features}
        />
      </NoSidebarPageWrapper>
  );

}

export default StudyFormView;
