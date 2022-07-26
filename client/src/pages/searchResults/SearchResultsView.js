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
import LoadingMessage from "../../common/structure/LoadingMessage";
import ErrorMessage from "../../common/structure/ErrorMessage";
import SideBar from "../../common/structure/SideBar";
import NavBar from "../../common/structure/NavBar";
import Footer from "../../common/structure/Footer";
import {SearchHits} from "../../common/search";
import {useSearchParams} from "react-router-dom";
import axios from "axios";

const SearchResultsView = props => {

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

      // const data = {};
      // data.cf = crossfilter(hits);
      // data.dimensions = {};
      // data.dimensions.allData = data.cf.dimension(d => d);
      // data.dimensions[filter.PROGRAM] = data.cf.dimension(d => d.program.id);
      // data.dimensions[filter.LEGACY] = data.cf.dimension(d => d.legacy);
      // data.dimensions[filter.EXTERNAL] = data.cf.dimension(
      //     d => !!d.collaborator);
      // data.dimensions[filter.MY_STUDY] = data.cf.dimension(
      //     d => props.user && d.owner.id === props.user.id);
      // data.dimensions[filter.STATUS] = data.cf.dimension(d => d.status);

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

      // Apply filters
      // console.log("Filters: ");
      // console.log(props.filters);
      //
      // for (let key of Object.keys(state.data.dimensions)) {
      //   state.data.dimensions[key].filterAll();
      //   if (props.filters.hasOwnProperty(key) && props.filters[key]
      //       != null) {
      //     if (Array.isArray(props.filters[key])) {
      //       state.data.dimensions[key].filter(
      //           d => props.filters[key].indexOf(d) > -1);
      //     } else {
      //       state.data.dimensions[key].filter(props.filters[key]);
      //     }
      //   }
      // }

      content = <SearchHits hits={state.hits}/>;

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
