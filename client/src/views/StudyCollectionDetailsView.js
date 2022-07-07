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
import StudyCollectionDetails
  from "../components/collections/StudyCollectionDetails";
import swal from "sweetalert";
import {useSelector} from "react-redux";
import axios from "axios";

const StudyCollectionDetailsView = props => {
  
  const [state, setState] = useState({
    collectionId: props.match.params.collectionId,
    isLoaded: false,
    isError: false
  });
  const user = useSelector(s => s.user.value);

  const handleRemoveStudy = (id) => {
    swal({
      title: "Are you sure you want to remove this study from the collection?",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        fetch("/api/studycollection/" + state.collection.id + "/" + id, {
          method: 'DELETE',
          headers: {
            "Content-Type": "application/json"
          }
        }).then(response => {
          let collection = state.collection;
          collection.studies = collection.studies.filter(s => s.id === id);
          setState({ 
            ...state, 
            collection 
          });
        })
        .catch(error => {
          console.error(error);
        })
      }
    });

  }

  useEffect(() => {
    axios.get("/api/studycollection/" + state.collectionId)
    .then(response => {
      setState({
        ...state,
        collection: response.data,
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


  let content = <LoadingMessage/>;
  if (state.isError) {
    content = <ErrorMessage/>;
  } else if (state.isLoaded) {
    content = <StudyCollectionDetails
        collection={state.collection}
        user={user}
        handleRemoveStudy={handleRemoveStudy}
    />;
  }
  return (
      <StandardWrapper {...props}>
        {content}
      </StandardWrapper>
  );

}

export default StudyCollectionDetailsView;