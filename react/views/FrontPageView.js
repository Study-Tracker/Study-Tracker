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

import React from 'react';
import LoadingMessage from "../structure/LoadingMessage";
import ErrorMessage from "../structure/ErrorMessage";
import {connect} from 'react-redux';
import {compose} from 'redux';
import {withRouter} from 'react-router-dom';
import SideBar from "../structure/SideBar";
import NavBar from "../structure/NavBar";
import Footer from "../structure/Footer";
import FrontPageTimeline from "../components/timeline/FrontPageTimeline";

const qs = require('qs');

class FrontPageView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isLoaded: false,
      isError: false,
      activity: [],

    };
  }

  componentDidMount() {
    const params = qs.parse(this.props.location.search,
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
        this.setState({
          stats: stats,
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
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    });

  }

  render() {

    let content = <LoadingMessage/>;

    try {

      if (this.state.isError) {

        content = <ErrorMessage/>;

      } else if (this.state.isLoaded) {

        content = <FrontPageTimeline
            activity={this.state.activity}
            stats={this.state.stats}
            user={this.props.user}
            pageNumber={this.state.pageNumber}
            pageSize={this.state.pageSize}
            hasNextPage={this.state.hasNextPage}
            hasPreviousPage={this.state.hasPreviousPage}
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
              <NavBar user={this.props.user}/>
              <div className="content">
                {content}
              </div>
              <Footer/>
            </div>
          </div>
          {/*<StudyFilters/>*/}
        </React.Fragment>
    );

  }

}

export default compose(
    withRouter,
    connect(
        store => ({
          filters: store.filters,
          user: store.user
        })
    )
)(FrontPageView);
