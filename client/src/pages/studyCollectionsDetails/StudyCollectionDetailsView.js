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

import React, {useContext, useEffect, useState} from "react";
import LoadingMessage from "../../common/structure/LoadingMessage";
import ErrorMessage from "../../common/structure/ErrorMessage";
import StandardWrapper from "../../common/structure/StandardWrapper";
import StudyCollectionDetails from "./StudyCollectionDetails";
import swal from "sweetalert";
import {useSelector} from "react-redux";
import axios from "axios";
import {useParams} from "react-router-dom";
import NotyfContext from "../../context/NotyfContext";

const StudyCollectionDetailsView = ({}) => {

  const params = useParams();
  const collectionId = params.collectionId;
  const [collection, setCollection] = useState(null);
  const [error, setError] = useState(null);
  const [loadCount, setLoadCount] = useState(0);
  const user = useSelector(s => s.user.value);
  const notyf = useContext(NotyfContext);

  const handleRemoveStudy = (id) => {
    swal({
      title: "Are you sure you want to remove this study from the collection?",
      icon: "warning",
      buttons: true
    })
    .then(val => {
      if (val) {
        axios.delete("/api/internal/studycollection/" + collection.id + "/" + id)
        .then(response => {
          setLoadCount(loadCount + 1);
        })
        .catch(error => {
          console.error(error);
          notyf.open({
            type: "error",
            message: "There was an error removing the study from the collection."
          })
        })
      }
    });
  }

  const handleUpdateCollection = (collection) => {
    axios.put("/api/internal/studycollection/" + collection.id, collection)
    .then(response => {
      setLoadCount(loadCount + 1)
      notyf.open({
        type: "success",
        message: "Successfully updated study collection."
      })
    })
    .catch(error => {
      console.error(error);
      notyf.open({
        type: "error",
        message: "There was an error updating the study collection."
      })
    })
  }

  useEffect(() => {
    axios.get("/api/internal/studycollection/" + collectionId)
    .then(response => {
      setCollection(response.data);
      console.debug("Study collection", response.data);
    })
    .catch(error => {
      console.error(error);
      setError(error);
      notyf.open({
        type: "error",
        message: "There was an error loading the study collection."
      })
    })
  }, [loadCount]);


  let content = <LoadingMessage/>;
  if (error) {
    content = <ErrorMessage/>;
  } else if (collection) {
    content = <StudyCollectionDetails
        collection={collection}
        user={user}
        handleUpdateCollection={handleUpdateCollection}
        handleRemoveStudy={handleRemoveStudy}
    />;
  }
  return (
      <StandardWrapper>
        {content}
      </StandardWrapper>
  );

}

export default StudyCollectionDetailsView;