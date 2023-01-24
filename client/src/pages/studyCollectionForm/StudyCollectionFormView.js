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
import NoSidebarPageWrapper from "../../common/structure/NoSidebarPageWrapper";
import StudyCollectionForm from "./StudyCollectionForm";
import {useSelector} from "react-redux";
import axios from "axios";
import {useParams} from "react-router-dom";

const StudyCollectionFormView = props => {

  const params = useParams();
  const [state, setState] = useState({
    collectionId: params.collectionId || null,
    isLoaded: false,
    isError: false
  });
  const user = useSelector(s => s.user.value);

  useEffect(() => {
    axios.get("/api/internal/studycollection")
    .then(response => {
      if (!!state.collectionId) {
        const collection = response.data.find(
            p => String(p.id) === state.collectionId);
        setState(prevState => ({
          ...prevState,
          collection,
          collections: response.data,
          isLoaded: true
        }));
      } else {
        setState(prevState => ({
          ...prevState,
          collections: response.data,
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
    content = <StudyCollectionForm
        collection={state.collection}
        collections={state.collections}
        user={user}
    />;
  }
  return (
      <NoSidebarPageWrapper>
        {content}
      </NoSidebarPageWrapper>
  );

}

export default StudyCollectionFormView;