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
import SideBar from "../../common/structure/SideBar";
import NavBar from "../../common/structure/NavBar";
import Footer from "../../common/structure/Footer";
import {useLocation} from "react-router-dom";
import ErrorMessage from "../../common/structure/ErrorMessage";
import FrontPageTimeline from "./FrontPageTimeline";
import {useSelector} from "react-redux";
import axios from "axios";

const qs = require('qs');

const FrontPageView = props => {

  const defaultState = {
    isLoaded: false,
    isError: false,
    activity: []
  }
  const [state, setState] = useState(defaultState);
  const location = useLocation();
  const user = useSelector(state => state.user.value);

  useEffect(() => {
    const params = qs.parse(location.search,
        {ignoreQueryPrefix: true});
    console.log(params);
    let page = !!params.page ? parseInt(params.page) : 0;
    let size = !!params.size ? parseInt(params.size) : 20;
    let sort = "date,desc";
    let query = '';
    if (!!params.search) {
      query = "?search=" + params.search;
    }

    axios.get("/api/internal/activity?sort=" + sort + "&page=" + page + "&size=" + size)
    .then(r1 => {
      const activityPage = r1.data;
      axios.get("/api/internal/stats/frontpage")
      .then(r2 => {
        const stats = r2.data;
        axios.get("/api/internal/stats/user")
        .then(r3 => {
          setState(prevState => ({
            ...prevState,
            stats: stats,
            userStats: r3.data,
            activity: activityPage.content,
            isLoaded: true,
            pageNumber: page,
            pageSize: size,
            pageSort: sort,
            totalItems: activityPage.numberOfElements,
            totalPages: activityPage.totalPages,
            hasNextPage: !activityPage.last,
            hasPreviousPage: page > 0
          }))
        })
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

  }, []);

  let content = <LoadingMessage/>;

  try {

    if (state.isError) {

      content = <ErrorMessage/>;

    } else if (state.isLoaded) {

      content = <FrontPageTimeline
          activity={state.activity}
          stats={state.stats}
          userStats={state.userStats}
          user={user}
          pageNumber={state.pageNumber}
          pageSize={state.pageSize}
          hasNextPage={state.hasNextPage}
          hasPreviousPage={state.hasPreviousPage}
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
      </React.Fragment>
  );

}

export default FrontPageView;
