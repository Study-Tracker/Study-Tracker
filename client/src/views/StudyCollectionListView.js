import React, {useEffect, useState} from "react";
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import SideBar from "../structure/SideBar";
import NavBar from "../structure/NavBar";
import {CollectionList} from "../components/collections/CollectionList";
import axios from "axios";
import {useSelector} from "react-redux";

const StudyCollectionListView = props => {

  const user = useSelector(state => state.user.value);
  const [state, setState] = useState({
    isLoaded: false,
    isError: false
  });

  useEffect(() => {
    axios.get("/api/studycollection")
    .then(response => {
      setState({
        ...state,
        collections: response.data,
        isLoaded: true
      })
    })
    .catch(error => {
      console.error(error);
      setState({
        ...state,
        isError: true,
        error: error
      });
    });
  
  }, [user]);



  let content = <LoadingMessage/>;

  try {
    if (state.isError) {
      content = <ErrorMessage/>;
    } else if (state.isLoaded) {
      content = <CollectionList
          collections={state.collections}
          user={user}
      />;

    }
  } catch (e) {
    console.error(e);
    content = <ErrorMessage/>;
  }

  return (
      <React.Fragment>
        <div className="wrapper">
          <SideBar/>
          <div className="main">
            <NavBar />
            <div className="content">
              {content}
            </div>
          </div>
        </div>
      </React.Fragment>
  );

}

export default StudyCollectionListView;