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
import crossfilter from "crossfilter2";
import SideBar from "../../common/structure/SideBar";
import NavBar from "../../common/structure/NavBar";
import Footer from "../../common/structure/Footer";
import AssayFilters, {
  labels as filter
} from "../../common/filters/assayFilters";
import AssayList from "../assayDetails/AssayList";
import {useSelector} from "react-redux";
import {useSearchParams} from "react-router-dom";
import axios from "axios";

const AssayListView = props => {

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

    axios.get("/api/internal/assay" + query)
    .then(response => {

      console.log(response.data);

      const data = {};
      data.cf = crossfilter(response.data);
      data.dimensions = {};
      data.dimensions.allData = data.cf.dimension(d => d);
      data.dimensions[filter.PROGRAM] = data.cf.dimension(
          d => d.study.program.id);
      data.dimensions[filter.LEGACY] = data.cf.dimension(d => d.study.legacy);
      data.dimensions[filter.EXTERNAL] = data.cf.dimension(
          d => !!d.study.collaborator);
      data.dimensions[filter.MY_ASSAY] = data.cf.dimension(
          d => props.user && d.owner.id === props.user.id);
      data.dimensions[filter.STATUS] = data.cf.dimension(d => d.status);
      data.dimensions[filter.ASSAY_TYPE] = data.cf.dimension(
          d => d.assayType.id);

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
      console.log("Filters: ");
      console.log(filters);

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
          <AssayList
              assays={state.data.dimensions.allData.top(Infinity)}
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
        <AssayFilters/>
      </React.Fragment>
  );

}

export default AssayListView;
