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
import SideBar from "../../common/structure/SideBar";
import NavBar from "../../common/structure/NavBar";
import {CollectionList} from "./CollectionList";
import axios from "axios";
import {useSelector} from "react-redux";

const StudyCollectionListView = props => {

  const user = useSelector(state => state.user.value);
  const [state, setState] = useState({
    isLoaded: false,
    isError: false
  });

  useEffect(() => {
    axios.get("/api/internal/studycollection")
    .then(response => {
      setState(prevState => ({
        ...prevState,
        collections: response.data,
        isLoaded: true
      }))
    })
    .catch(error => {
      console.error(error);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
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