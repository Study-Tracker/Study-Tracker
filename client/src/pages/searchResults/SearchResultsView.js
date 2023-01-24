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
import Footer from "../../common/structure/Footer";
import SearchHits from "./SearchHits";
import {useSearchParams} from "react-router-dom";
import axios from "axios";

const SearchResultsView = () => {

  const [searchParams, setSearchParams] = useSearchParams();
  const [state, setState] = useState({
    isLoaded: false,
    isError: false,
    hits: {}
  });

  useEffect(() => {
    let query = '';
    if (searchParams.has("q")) {
      query = "?keyword=" + searchParams.get("q");
    }

    axios.get("/api/internal/search" + query)
    .then(response => {

      console.debug(response.data);

      setState(prevState => ({
        ...prevState,
        hits: response.data,
        isLoaded: true
      }));

    })
    .catch(error => {
      console.error(error);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: error
      }));
    });
  }, []);

  let content = <LoadingMessage/>;

  try {

    if (state.isError) {

      content = <ErrorMessage/>;

    } else if (state.isLoaded) {

      content = <SearchHits hits={state.hits.hits}/>;

    }

  } catch (e) {
    console.error(e);
    content = <ErrorMessage/>
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
            <Footer/>
          </div>
        </div>
      </React.Fragment>
  );

}

export default SearchResultsView;
