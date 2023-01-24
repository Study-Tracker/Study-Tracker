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
import crossfilter from "crossfilter2";
import SideBar from "../../common/structure/SideBar";
import NavBar from "../../common/structure/NavBar";
import Footer from "../../common/structure/Footer";
import UserList from "./UserList";
import UserFilters, {labels as filter} from "../../common/filters/UserFilters";
import {useSearchParams} from "react-router-dom";
import {useSelector} from "react-redux";
import axios from "axios";

const UserListView = props => {

  const [searchParams, setSearchParams] = useSearchParams();
  const user = useSelector(state => state.user.value);
  const filters = useSelector(state => state.filters.value);
  const [state, setState] = useState({
    isLoaded: false,
    isError: false,
    data: {}
  });

  const indexUsers = (users) => {
    console.log(users);
    const data = {};
    data.cf = crossfilter(users);
    data.dimensions = {};
    data.dimensions.allData = data.cf.dimension(d => d);
    data.dimensions[filter.ACTIVE] = data.cf.dimension(d => d.active)
    data.dimensions[filter.INACTIVE] = data.cf.dimension(d => !d.active)
    data.dimensions[filter.ADMIN] = data.cf.dimension(d => d.admin)

    setState(prevState => ({
      ...prevState,
      data: data,
      isLoaded: true
    }));
  }

  const applyFilters = (filters) => {
    for (let key of Object.keys(state.data.dimensions)) {
      state.data.dimensions[key].filterAll();
      if (filters.hasOwnProperty(key) && filters[key] != null) {
        if (Array.isArray(filters[key])) {
          state.data.dimensions[key].filter(
              d => filters[key].indexOf(d) > -1);
        } else {
          state.data.dimensions[key].filter(filters[key]);
        }
      }
    }
  }

  useEffect(() => {

    axios.get("/api/internal/user?type=STANDARD_USER")
    .then(async response => {
      indexUsers(response.data);
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

      console.debug("Filters: ");
      console.debug(filters);
      applyFilters(filters);

      content =
          <UserList
              users={state.data.dimensions.allData.top(Infinity)}
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
        <UserFilters/>
      </React.Fragment>
  );

}

export default UserListView;
