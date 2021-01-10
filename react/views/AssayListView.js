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
import crossfilter from "crossfilter2";
import {withRouter} from 'react-router-dom';
import SideBar from "../structure/SideBar";
import NavBar from "../structure/NavBar";
import Footer from "../structure/Footer";
import AssayFilters, {labels as filter} from "../components/filters/assayFilters";
import AssayList from "../components/assay/AssayList";

const qs = require('qs');

class AssayListView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isLoaded: false,
      isError: false,
      title: this.props.title,
      data: {}
    };
  }

  componentDidMount() {
    const params = qs.parse(this.props.location.search,
        {ignoreQueryPrefix: true});
    let title = params.title || this.state.title;
    let query = '';
    if (!!params.search) {
      query = "?search=" + params.search;
      title = 'Search Results';
    }

    fetch("/api/assay" + query)
    .then(response => response.json())
    .then(assays => {

      console.log(assays);

      const data = {};
      data.cf = crossfilter(assays);
      data.dimensions = {};
      data.dimensions.allData = data.cf.dimension(d => d);
      data.dimensions[filter.PROGRAM] = data.cf.dimension(
          d => d.study.program.id);
      data.dimensions[filter.LEGACY] = data.cf.dimension(d => d.study.legacy);
      data.dimensions[filter.EXTERNAL] = data.cf.dimension(
          d => !!d.study.collaborator);
      data.dimensions[filter.MY_ASSAY] = data.cf.dimension(
          d => this.props.user && d.owner.id === this.props.user.id);
      data.dimensions[filter.STATUS] = data.cf.dimension(d => d.status);
      data.dimensions[filter.ASSAY_TYPE] = data.cf.dimension(
          d => d.assayType.id);

      this.setState({
        data: data,
        isLoaded: true,
        title: title
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
        console.log("Filters: ");
        console.log(this.props.filters);

        for (let key of Object.keys(this.state.data.dimensions)) {
          this.state.data.dimensions[key].filterAll();
          if (this.props.filters.hasOwnProperty(key) && this.props.filters[key]
              != null) {
            if (Array.isArray(this.props.filters[key])) {
              this.state.data.dimensions[key].filter(
                  d => this.props.filters[key].indexOf(d) > -1);
            } else {
              this.state.data.dimensions[key].filter(this.props.filters[key]);
            }
          }
        }

        content =
            <AssayList
                assays={this.state.data.dimensions.allData.top(Infinity)}
                title={this.state.title}
                filters={this.props.filters}
                user={this.props.user}
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
          <AssayFilters/>
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
)(AssayListView);
