import React, {useEffect, useState} from "react";
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import NoSidebarPageWrapper from "../structure/NoSidebarPageWrapper";
import StudyCollectionForm from "../components/forms/StudyCollectionForm";
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
    axios.get("/api/studycollection")
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