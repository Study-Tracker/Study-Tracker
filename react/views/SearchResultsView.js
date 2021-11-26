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
import {SearchHits} from "../components/search";

const qs = require('qs');

class SearchResultsView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isLoaded: false,
      isError: false,
      hits: {}
    };
  }

  componentDidMount() {
    const params = qs.parse(this.props.location.search,
        {ignoreQueryPrefix: true});
    let query = '';
    if (!!params.q) {
      query = "?keyword=" + params.q;
    }

    fetch("/api/search" + query)
    .then(response => response.json())
    .then(hits => {

      console.log(hits);

      // const data = {};
      // data.cf = crossfilter(hits);
      // data.dimensions = {};
      // data.dimensions.allData = data.cf.dimension(d => d);
      // data.dimensions[filter.PROGRAM] = data.cf.dimension(d => d.program.id);
      // data.dimensions[filter.LEGACY] = data.cf.dimension(d => d.legacy);
      // data.dimensions[filter.EXTERNAL] = data.cf.dimension(
      //     d => !!d.collaborator);
      // data.dimensions[filter.MY_STUDY] = data.cf.dimension(
      //     d => this.props.user && d.owner.id === this.props.user.id);
      // data.dimensions[filter.STATUS] = data.cf.dimension(d => d.status);

      this.setState({
        hits: hits,
        isLoaded: true
      });

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

        // Apply filters
        // console.log("Filters: ");
        // console.log(this.props.filters);
        //
        // for (let key of Object.keys(this.state.data.dimensions)) {
        //   this.state.data.dimensions[key].filterAll();
        //   if (this.props.filters.hasOwnProperty(key) && this.props.filters[key]
        //       != null) {
        //     if (Array.isArray(this.props.filters[key])) {
        //       this.state.data.dimensions[key].filter(
        //           d => this.props.filters[key].indexOf(d) > -1);
        //     } else {
        //       this.state.data.dimensions[key].filter(this.props.filters[key]);
        //     }
        //   }
        // }

        content = <SearchHits hits={this.state.hits} />;

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
)(SearchResultsView);
