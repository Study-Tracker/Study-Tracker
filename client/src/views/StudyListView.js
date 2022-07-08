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
import StudyList from "../components/study/StudyList";
import crossfilter from "crossfilter2";
import StudyFilters, {
  labels as filter
} from '../components/filters/studyFilters'
import SideBar from "../structure/SideBar";
import NavBar from "../structure/NavBar";
import Footer from "../structure/Footer";
import {useSelector} from "react-redux";
import {useSearchParams} from "react-router-dom";
import axios from "axios";

const StudyListView = props => {

  const user = useSelector(state => state.user.value);
  const filters = useSelector(state => state.filters.value);
  const [searchParams, setSearchParams] = useSearchParams();
  const [state, setState] = useState({
    isLoaded: false,
    isError: false,
    title: props.title,
    data: {}
  });

  useEffect(() => {
    let title = searchParams.has("title") ? searchParams.get("title") : state.title;
    let query = '';
    if (searchParams.has("search")) {
      query = "?search=" + searchParams.get("search");
      title = 'Search Results';
    }

    axios.get("/api/study" + query)
    .then(response => {

      console.debug(response.data);

      const data = {};
      data.cf = crossfilter(response.data);
      data.dimensions = {};
      data.dimensions.allData = data.cf.dimension(d => d);
      data.dimensions[filter.PROGRAM] = data.cf.dimension(d => d.program.id);
      data.dimensions[filter.LEGACY] = data.cf.dimension(d => d.legacy);
      data.dimensions[filter.EXTERNAL] = data.cf.dimension(
          d => !!d.collaborator);
      data.dimensions[filter.MY_STUDY] = data.cf.dimension(
          d => user && d.owner.id === user.id);
      data.dimensions[filter.STATUS] = data.cf.dimension(d => d.status);

      setState(prevState => ({
        ...prevState,
        data: data,
        isLoaded: true,
        title: title
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
      console.debug("Filters: ");
      console.debug(filters);

      for (let key of Object.keys(state.data.dimensions)) {
        state.data.dimensions[key].filterAll();
        if (filters.hasOwnProperty(key) && filters[key]
            != null) {
          if (Array.isArray(filters[key])) {
            state.data.dimensions[key].filter(
                d => filters[key].indexOf(d) > -1);
          } else {
            state.data.dimensions[key].filter(filters[key]);
          }
        }
      }

      content =
          <StudyList
              studies={state.data.dimensions.allData.top(Infinity)}
              title={state.title}
              filters={filters}
              user={user}
          />;

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
        <StudyFilters/>
      </React.Fragment>
  );

}

export default StudyListView;
