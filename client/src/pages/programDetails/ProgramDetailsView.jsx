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
import StandardWrapper from "../../common/structure/StandardWrapper";
import ProgramDetails from "./ProgramDetails";
import {useSelector} from "react-redux";
import {useParams} from "react-router-dom";
import axios from "axios";
import {useQuery} from "@tanstack/react-query";

const ProgramDetailsView = props => {

  const {programId} = useParams();
  const user = useSelector(s => s.user.value);

  const {data: program, isLoading, error} = useQuery({
    queryKey: ["program", programId],
    queryFn: () => {
      return axios.get(`/api/internal/program/${programId}`)
      .then(response => response.data);
    }
  });

  if (isLoading) return (
    <StandardWrapper {...props}>
      <LoadingMessage/>
    </StandardWrapper>
  );

  if (error) return (
    <StandardWrapper {...props}>
      <ErrorMessage error={error}/>
    </StandardWrapper>
  );

  return (
      <StandardWrapper {...props}>
        <ProgramDetails program={program} user={user} />
      </StandardWrapper>
  );

}

export default ProgramDetailsView;