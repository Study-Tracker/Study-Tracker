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
import SideBar from "../structure/SideBar";
import NavBar from "../structure/NavBar";
import Footer from "../structure/Footer";
import {useLocation} from "react-router-dom";
import ErrorMessage from "../structure/ErrorMessage";
import FrontPageTimeline from "../components/timeline/FrontPageTimeline";
import {useSelector} from "react-redux";

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

    fetch("/api/activity?sort=" + sort + "&page=" + page + "&size=" + size)
    .then(response => response.json())
    .then(activityPage => {
      fetch("/api/stats/frontpage")
      .then(response => response.json())
      .then(stats => {
        fetch("/api/stats/user")
        .then(response => response.json())
        .then(userStats => {
          setState({
            ...state,
            stats: stats,
            userStats: userStats,
            activity: activityPage.content,
            isLoaded: true,
            pageNumber: page,
            pageSize: size,
            pageSort: sort,
            totalItems: activityPage.numberOfElements,
            totalPages: activityPage.totalPages,
            hasNextPage: !activityPage.last,
            hasPreviousPage: page > 0
          })
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
